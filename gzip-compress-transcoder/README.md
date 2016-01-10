# GZIP Compress Transcoder
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/gzip-compress-transcoder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/gzip-compress-transcoder)

Compresses serialized data with GZIP compression algorithm([GZIPInputStream](https://docs.oracle.com/javase/7/docs/api/java/util/zip/GZIPInputStream.html), [GZIPOutputStream](https://docs.oracle.com/javase/7/docs/api/java/util/zip/GZIPOutputStream.html)).
This is a wrapper transcoder, it requires a serialization transcoder.

## Dependency Configuration
### Gradle
```groovy
compile "kr.pe.kwonnam.spymemcached-extra-transcoders:gzip-compress-transcoder:${version}"
```

### Maven
```xml
<dependency>
    <groupId>kr.pe.kwonnam.spymemcached-extra-transcoders</groupId>
    <artifactId>gzip-compress-transcoder</artifactId>
    <version>${version}</version>
</dependency>
```

## Usage
```java
Transcoder<Object> transcoder = .. configure serialization transcoder ..;
GZIPCompressWrapperTranscoder<Object> gzipCompressWrapperTranscoder = 
    new GZIPCompressWrapperTranscoder<>(transcoder);

// Spymemcached configuration

ConnectionFactoryBuilder  connectionFactoryBuilder = new ConnectionFactoryBuilder();
connectionFactoryBuilder.setTranscoder(gzipCompressWrapperTranscoder);
  // ... configure etc ...

MemcachedClient memcachedClient = 
    new MemcachedClient(connectionFactoryBuilder.build(), AddrUtil.getAddresses("memcachedhost:port"));
```

## Other configuration properties
* `GZIPCompressWrapperTranscoder.setCompressionThresholdByteLength` : Only when the decoded byte length is over `compressionThresholdByteLength`, compression proceeds or just passes the data to spymemcached.
* `GZIPCompressWrapperTranscoder.setCompressionFlag(int)` : set Memcached flag for GZIP Compression. When compressing the data, this flag is also set. When decoding the cached data, this flag is checked and if it is set decompression proceeds or just passes the cached data to the wrapped transcoder.
* `GZIPCompressWrapperTranscoder.setBufferSize(int)` : set GZIP buffer size in bytes. Default `8192` bytes.
