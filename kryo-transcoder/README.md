# Kryo Serialization Spymemcached Transcoder
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/kryo-transcoder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/kr.pe.kwonnam.spymemcached-extra-transcoders/kryo-transcoder)

Serialize objects with [Kryo](https://github.com/EsotericSoftware/kryo) serializer.

## Features
`Kryo` instance is NOT thread-safe. So you must not share the object among many threads.

Kryo 3 introduced `KryoFactory` and `KryoPool` configuration.
`KryoPool` provides pooled `Kryo` instances which is created by `KryoFactory` to each thread. This makes `Kryo` instances to be reused without being shared in many threads.
Thanks to this, there is no performance loss for creating many `Kryo` instances for each request.

### `DEFAULT_KRYO_FACTORY`
`KryoFactory` instantiates `Kryo` objects.
`DEFAULT_KRYO_FACTORY` instantiates `Kryo` objects with the following configuraiton.

* Can serialize/deserialize without constructor calls. Because of this Kryo can serialize/deserialize the classes without no zero-argument constructor. 
* Field change compatibility support with `CompatibleFieldSerializer`
* Enum name based serialization with `EnumNameSerializer` - If you use ordinal based enum serialization, you must not change the enum items's order.


## Dependency Configuration
### Gradle
```groovy
compile "kr.pe.kwonnam.spymemcached-extra-transcoders:kryo-transcoder:${version}"
```

### Maven
```xml
<dependency>
    <groupId>kr.pe.kwonnam.spymemcached-extra-transcoders</groupId>
    <artifactId>kryo-transcoder</artifactId>
    <version>${version}</version>
</dependency>
```

## Usage
```java
KryoTranscoder<Object> kryoTranscoder = new KryoTranscoder<>(); // use DEFAULT_KRYO_FACTORY

// Spymemcached configuration

ConnectionFactoryBuilder  connectionFactoryBuilder = new ConnectionFactoryBuilder();
connectionFactoryBuilder.setTranscoder(kryoTranscoder); // you can wrap this with xxx-compress-transcoder
  // ... configure etc ...

MemcachedClient memcachedClient = 
    new MemcachedClient(connectionFactoryBuilder.build(), AddrUtil.getAddresses("memcachedhost:port"));
```

## Other configuration properties
* `KryoTranscoder.setBufferSize(int)` : set Kryo Buffer size. Default `8096` bytes.
* `KryoTranscoder.setMaxBufferSize(int)` : set Kryo Max Buffer size. Default `-1` (unlimited). 
 
### `KryoFactory` implementation
If you want to use your own `Kryo` configuration, implement `com.esotericsoftware.kryo.pool.KryoFactory`
and pass to the constructor of `KryoTranscoder`.

```java
KryoFactory myKryoFactory = new KryoFactory() {
    @Override
    public Kryo create() {
        Kryo kryo = new Kryo();
        // your own configuration...
        return kryo;
    }
};

KryoTranscoder<Object> kryoTranscoder = new KryoTranscoder<>(myKryoFactory);

```

### Override `KryoPool` instantiation
If you want to change `KryoPool` configuration, subclass `KryoTranscoder` and override `protected KryoPool createKryoPool()` by yourself.