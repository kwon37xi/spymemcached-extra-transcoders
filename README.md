# spymemcached-extra-transcoders
Extra transcoders for [spymemcached](https://github.com/couchbase/spymemcached).

## Requirements
* Java 7+

## Compression transcoders
* lz4-compress-transcoder : [lz4-java](https://github.com/jpountz/lz4-java)
* snappy-compress-transcoder : [snappy-java](https://github.com/xerial/snappy-java)
* gzip-compress-transcoder

### What to choose
* Refer to many benchmark tests about JVM Compression algorithms.
* [Performance of various general compression algorithms - some of them are unbelievably fast!  - Java Performance Tuning Guide](http://java-performance.info/performance-general-compression/)

## Serialize Transcoders
* kryo-transcoder : [Kryo](https://github.com/EsotericSoftware/kryo)
* fst-transcoder : [FST](http://ruedigermoeller.github.io/fast-serialization/)
