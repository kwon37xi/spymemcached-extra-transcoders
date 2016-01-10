# Snappy Compress Transcoder
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/snappy-compress-transcoder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/snappy-compress-transcoder)

Compresses serialized data with [snappy-java](https://github.com/xerial/snappy-java) compression algorithm.
This is a wrapper transcoder, it requires a serialization transcoder.

## Dependency Configuration
### Gradle
```groovy
compile "kr.pe.kwonnam.spymemcached-extra-transcoders:snappy-compress-transcoder:${version}"
```

### Maven
```xml
<dependency>
    <groupId>kr.pe.kwonnam.spymemcached-extra-transcoders</groupId>
    <artifactId>snappy-compress-transcoder</artifactId>
    <version>${version}</version>
</dependency>
```

## Usage
```java
Transcoder<Object> transcoder = .. configure serialization transcoder ..;
SnappyCompressWrapperTranscoder<Object> snappyCompressWrapperTranscoder = 
    new SnappyCompressWrapperTranscoder<>(transcoder);

// Spymemcached configuration

ConnectionFactoryBuilder  connectionFactoryBuilder = new ConnectionFactoryBuilder();
connectionFactoryBuilder.setTranscoder(snappyCompressWrapperTranscoder);
  // ... configure etc ...

MemcachedClient memcachedClient = 
    new MemcachedClient(connectionFactoryBuilder.build(), AddrUtil.getAddresses("memcachedhost:port"));
```


## Other configuration properties
* `SnappyCompressWrapperTranscoder.setCompressionThresholdByteLength(long)` : Only when the decoded byte length is over `compressionThresholdByteLength`, compression proceeds or just passes the data to spymemcached.
* `SnappyCompressWrapperTranscoder.setCompressionFlag(int)` : set Memcached flag for Snappy Compression. When compressing the data, this flag is also set. When decoding the cached data, this flag is checked and if it is set decompression proceeds or just passes the cached data to the wrapped transcoder.
