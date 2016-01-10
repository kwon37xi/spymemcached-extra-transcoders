# FST Serialization Transcoder
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/fst-transcoder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/fst-transcoder)

Serialize objects with [FST](http://ruedigermoeller.github.io/fast-serialization/) serializer.

## Features
`FSTConfiguration` is thread-safe but synchronized. Because of synchronization, it is slow when shared among many threads.
`FSTTranscoder` instantiates `FSTConfiguration` for each thread so there is no shared instances.
Thanks to this, there is no performance loss for synchronization.

## Dependency Configuration
### Gradle
```groovy
compile "kr.pe.kwonnam.spymemcached-extra-transcoders:fst-transcoder:${version}"
```

### Maven
```xml
<dependency>
    <groupId>kr.pe.kwonnam.spymemcached-extra-transcoders</groupId>
    <artifactId>fst-transcoder</artifactId>
    <version>${version}</version>
</dependency>
```

## Usage
```java
FstTranscoder<Object> fstTranscoder = new FstTranscoder<>(); // use DEFAULT_FSTCONFIGURATION_FACTORY

// Spymemcached configuration

ConnectionFactoryBuilder  connectionFactoryBuilder = new ConnectionFactoryBuilder();
connectionFactoryBuilder.setTranscoder(fstTranscoder); // you can wrap this with xxx-compress-transcoder
  // ... configure etc ...

MemcachedClient memcachedClient = 
    new MemcachedClient(connectionFactoryBuilder.build(), AddrUtil.getAddresses("memcachedhost:port"));
```

## Other configuration properties
### `FSTConfigurationFactory` implementation
If you want to use your own `FSTConfiguration` configuration, implement `kr.pe.kwonnam.spymemcached.extratranscoders.fst.FSTConfigurationFactory` and pass to the constructor of `FSTTranscoder`.
```java
FSTConfigurationFactory myConfigFactory = new FSTConfigurationFactory() {
    public FSTConfiguration create() {
        FSTConfiguration = FSTConfiguration.createDefaultConfiguration();
        // set your own configurations.
        return fstConfiguration;
    }
}

FstTranscoder<Object> fstTranscoder = new FstTranscoder<>(myConfigFactory);
...

```
