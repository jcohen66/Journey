package multithreading

import akka.actor._
import akka.routing.BalancingPool
import deathwatch.Reaper.WatchMe

import scalafx.application.{JFXApp, Platform}
import scalafx.scene.Scene
import scalafx.scene.image._
import scalafx.scene.paint.Color

/**
  * Created by jcohe_000 on 3/27/2016.
  */
object MandelbrotActors extends JFXApp {
  val MaxCount = 10000
  val ImageSize = 600
  val XMin = -1.5
  val XMax = 0.5
  val YMin = -1.0
  val YMax = 1.0

  case class Complex(real: Double, imag: Double) {
    def +(that: Complex) = Complex(real + that.real, imag + that.imag)
    def *(that: Complex) = Complex(real * that.real - imag * that.imag,
      real * that.imag + imag * that.real)
    def mag = math.sqrt(real * real + imag * imag)
  }

  case class Line(row: Int, y: Double)

  class LineActor(pw: PixelWriter, r: ActorRef) extends Actor {

    r ! WatchMe(self)

    def receive = {
      case Line(row, y) =>
        for(j <- 0 until ImageSize) {
          val x = XMin + j*(XMax-XMin)/ImageSize

          // This takes the processor time.
          val cnt = mandelCount(Complex(x,y))

          //
          Platform.runLater {
            pw.setColor(j, row, if (cnt == MaxCount) Color.Black
            else {
              val scale = 10 * math.sqrt(cnt.toDouble / MaxCount) min 1.0
              Color(scale, 0, 0, 1)
            })
            self ! PoisonPill
          }
        }
    }
  }

  def mandelCount(c: Complex): Int = {
    var cnt = 0
    var z = Complex(0, 0)
    while (cnt < MaxCount && z.mag < 1) {
      z = z * z + c
      cnt += 1
    }
    cnt
  }


  val system = ActorSystem("mandel-system")

  val reaper = system.actorOf(Props[ProductionReaper], "deathwatch")

  val cores = 4;


  stage = new JFXApp.PrimaryStage {
    title = "Actor Mandelbrot"
    scene = new Scene(ImageSize, ImageSize) {
      val image = new WritableImage(ImageSize, ImageSize)
      content = new ImageView(image)

      val router = system.actorOf(BalancingPool(cores).props(Props(new LineActor(image.pixelWrit, reaper))), "pool")

      for(i <- 0 until ImageSize) {
        val y = YMin + i*(YMax-YMin)/ImageSize
        router ! Line(i, y)
      }

    }
  }

}
