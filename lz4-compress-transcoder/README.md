# LZ4 Compress Transcoder
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/lz4-compress-transcoder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/lz4-compress-transcoder)

Compresses serialized data with [lz4-java](https://github.com/jpountz/lz4-java) compression algorithm.
This is a wrapper transcoder, it requires a serialization transcoder.

## Dependency Configuration
### Gradle
```groovy
compile "kr.pe.kwonnam.spymemcached-extra-transcoders:lz4-compress-transcoder:${version}"
```

### Maven
```xml
<dependency>
    <groupId>kr.pe.kwonnam.spymemcached-extra-transcoders</groupId>
    <artifactId>lz4-compress-transcoder</artifactId>
    <version>${version}</version>
</dependency>
```


## Usage
```java
Transcoder<Object> transcoder = .. configure serialization transcoder ..;
Lz4CompressWrapperTranscoder<Object> lz4CompressWrapperTranscoder = 
    new Lz4CompressWrapperTranscoder<>(transcoder);

// Spymemcached configuration

ConnectionFactoryBuilder  connectionFactoryBuilder = new ConnectionFactoryBuilder();
connectionFactoryBuilder.setTranscoder(lz4CompressWrapperTranscoder);
  // ... configure etc ...

MemcachedClient memcachedClient = 
    new MemcachedClient(connectionFactoryBuilder.build(), AddrUtil.getAddresses("memcachedhost:port"));
```

## Other configuration properties
* `Lz4CompressWrapperTranscoder.setCompressionThresholdByteLength` : Only when the decoded byte length is over `compressionThresholdByteLength`, compression proceeds or just passes the data to spymemcached.
* `Lz4CompressWrapperTranscoder.setCompressionFlag(int)` : set Memcached flag for lz4 Compression. 
   When compressing the data, this flag is also set. 
   When decoding the cached data, this flag is checked and if it is set decompression proceeds or just passes the cached data to the wrapped transcoder.