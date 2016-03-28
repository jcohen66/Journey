package caching

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.concurrent.duration._

trait Config {
  private val config = ConfigFactory.load()
  val simpleCacheConfig = config.getConfig("scalasnippets.akkasnippets.simplecache")
  def askTimeout = Duration(simpleCacheConfig.getDuration("ask-timeout", SECONDS), SECONDS)
  def cacheDuration = Duration(simpleCacheConfig.getDuration("cache-duration", MINUTES), MINUTES)
}

trait ActorCaching extends ActorLogging with Config { this: Actor ⇒
  import akka.pattern.pipe
  import context.dispatcher

  private case object ExpireCache
  private case object NoCache
  private type Cache = Map[Any, Any]
  private var cache: Cache = Map.empty

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit =
    if (cacheEnabled) context.system.scheduler.schedule(cacheDuration, cacheDuration, self, ExpireCache)

  override def receive: Receive = {
    case msg if cache.contains(msg) ⇒ sender ! cache(msg)
    case (NoCache, msg)             ⇒ sender ! msg
    case (msg, response)            ⇒ onMessageWithResponse(msg, response)
    case ExpireCache                ⇒ onExpireCache()
  }

  protected final def respond[T](f: Future[T]): Unit = f.map(NoCache -> _).pipeTo(self)(sender())

  protected final def cacheAndRespond[T](msg: Any, f: Future[T]): Unit = f.map(msg -> _).pipeTo(self)(sender())

  private def cacheEnabled: Boolean = cacheDuration != Duration.Zero

  private def onMessageWithResponse(msg: Any, response: Any): Unit = {
    if (cacheEnabled) cache = cache + (msg -> response)
    sender ! response
  }

  private def onExpireCache(): Unit = {
    log.debug(s"Expiring cache. Cache duration is set to $cacheDuration")
    cache = Map.empty
  }
}