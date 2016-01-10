# spymemcached-extra-transcoders
Extra transcoders for [spymemcached](https://github.com/couchbase/spymemcached).

## Requirements
* Java 7+

## Compression Wrapper Transcoders
* [lz4-compress-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/lz4-compress-transcoder) : [lz4-java](https://github.com/jpountz/lz4-java)
* [snappy-compress-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/snappy-compress-transcoder) : [snappy-java](https://github.com/xerial/snappy-java)
* [gzip-compress-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/gzip-compress-transcoder)

* Refer to many benchmark tests about JVM Compression algorithms.
* [Performance of various general compression algorithms - some of them are unbelievably fast!  - Java Performance Tuning Guide](http://java-performance.info/performance-general-compression/)

## Serialization Transcoders
* [kryo-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/kryo-transcoder) : [Kryo](https://github.com/EsotericSoftware/kryo)
* [fst-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/fst-transcoder) : [FST](http://ruedigermoeller.github.io/fast-serialization/)
