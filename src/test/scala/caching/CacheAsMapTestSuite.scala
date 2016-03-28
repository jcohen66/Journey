package caching

import org.scalatest.FunSuite


/**
  * Created by vinay on 10/21/13.
  */
class CacheAsMapTestSuite extends FunSuite {

  test("add, remove and get on a cache") {
    val m = new CacheAsMap[Int, String]
    //add key+value pair using the following syntax
    m(1) = "a"
    //or
    m += (2 -> "b")
    //or, since its mutable
    m+(3->"c")
    assert(m.size===3, "Did not find 3 KV pairs")
    //add to cache maps
    val n = new CacheAsMap[Int,String]()
    n(4)="d"
    //or, add the entire cache map over
    n++=m
    assert(n.size===4,"addition of 2 caches did not work")
    assert(m.size===3,"addition affected one of the caches being added")

    //remove
    m -= 2
    assert( m.size==2,"remove on cache did not work")
    //or, remove more than one key at a time
    n -=(1,2)
    assert(n.size===2,"remove with more than one keys did not work")
    //get
    assert("a" === m(1),"get did not work")


  }

  test("filter on a cache") {
    val m = new CacheAsMap[String, String]()
    m("apple") = "fruit"
    m("potato") = "vegetable"
    m("grapes") = "fruit"
    val filteredResults=m.filter { case(key,value) => value.equals("fruit") }
    assert(filteredResults.size === 2,"filter did not work")
  }

  test("transform on a cache using a function"){
    val m = new CacheAsMap[Int,Int]()
    m(1)=1
    m(2)=2
    m(3)=3
    assert(1===m(1),"hget did not work")
    m transform ((k,v)=>v+1)
    assert(2===m(1),"transform did not work")

  }

  test("other common map ops on cache"){
    val m = new CacheAsMap[String,String]()
    //getOrElse
    val thingType=m getOrElse("mango","fruit")
    assert("fruit"===thingType,"getOrElse did not work")
    m("pineapple")="fruits"
    assert(m contains "pineapple","contains ops did not work")
    m+("orange"->"fruits")
    assert(2===m.size,"simple size doesnot work")
  }

  test("create a cache given constraints") {
    val m = new CacheAsMap[Int, String](new CacheBuilder(name = "myCache", maxEntriesInHeap = 1000))
    for (i <- 1 to 2000) {
      m(i) = "value-" + i
    }
    //not a real check since the cache can expand to beyond 1000 and stay that way for a while or
    // at least until it feels the memory pressure
    assert(m.size >= 1000 && m.size < 2000,"cache constraints set were not honored")
  }

  /** Requires Terracotta BigMemory license for offheap support
    * Add terracotta-license.key & add direct memory to the JVM to allocate appropriate offheap space for use
    */
  /*
  test("create a off-heap cache given constraints") {
    val m = new CacheAsMap[Int, String](new CacheBuilder(name = "myCache", maxEntriesInHeap = 1000, maxBytesLocalOffHeapInMB = 256))
    for (i <- 1 to 2000) {
      m(i) = "value-" + i
    }
    assert(m.size >= 1000)
  }
  */

}