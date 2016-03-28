package caching

import net.sf.ehcache._
import net.sf.ehcache.config._

class DataCache {
  // create CacheManager
  var cacheManager: net.sf.ehcache.CacheManager = CacheManager.newInstance("src/main/resources/ehcache.xml")

  //create the configuration of the cache . Here "testCache" is the name of the cache region
  val cacheConfiguration = new CacheConfiguration("testCache", 1000)
    .eternal(true)
    //.persistence(newPersistenceConfiguration().strategy(Strategy.LOCALRESTARTABLE))

  // create cache region
  //val cacheRegion = new Cache(cacheConfiguration)

  // add cache region in cacheManager
  cacheManager.addCache("datacache")

  // get the cache region by the name "testCache"
  val testCacheRegion = cacheManager.getCache("testCache")

  // insert the value in cache
  testCacheRegion.put(new Element("key", "testCacheValue"))

  // get the value from the cache region
  val getValue = testCacheRegion.get("key").getValue()
  println(" value from the cache :" , getValue)

  // You will get "testCachevalue"
}
