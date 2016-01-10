# spymemcached-extra-transcoders
Extra transcoders for [spymemcached](https://github.com/couchbase/spymemcached).

## Requirements
* Java 7+

## Compression Wrapper Transcoders
* [lz4-compress-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/lz4-compress-transcoder)
* [snappy-compress-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/snappy-compress-transcoder)
* [gzip-compress-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/gzip-compress-transcoder)

### Choosing Compression Algorithms
* Refer to many benchmark tests about JVM Compression algorithms.
* [Performance of various general compression algorithms - some of them are unbelievably fast!  - Java Performance Tuning Guide](http://java-performance.info/performance-general-compression/)

## Serialization Transcoders
* [kryo-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/kryo-transcoder)
* [fst-transcoder](https://github.com/kwon37xi/spymemcached-extra-transcoders/tree/master/fst-transcoder)
