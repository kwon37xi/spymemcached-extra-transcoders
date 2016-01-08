package kr.pe.kwonnam.spymemcached.extratranscoders.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.EnumNameSerializer;
import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * <a href="https://github.com/EsotericSoftware/kryo">Kryo</a> based Object Serialization/Deserialization.
 *
 * @param <T> Object Type
 */
public class KryoTranscoder<T> implements Transcoder<T> {
    /**
     * Default KryoFactory - supports non-constructor call object  instantiation and name based enum serialization.
     */
    public static final KryoFactory DEFAULT_KRYO_FACTORY = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();

            // First, try to instantiate an object with no-args constructor,
            // Second, if first fails then try to instantiate an object without constructor calls.
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

            // enum named based serializer
            kryo.addDefaultSerializer(Enum.class, EnumNameSerializer.class);
            return kryo;
        }
    };

    /**
     * Default KryoPool - pool of {@link Kryo} instances which are instantiated by {@link KryoFactory}.
     *
     * @see KryoPool
     * @see KryoFactory
     * @see Kryo
     */
    public static final KryoPool DEFAULT_KRYO_POOL = new KryoPool.Builder(DEFAULT_KRYO_FACTORY).softReferences().build();

    /**
     * Default Kryo Output bufferSize
     */
    public static final int DEFAULT_BUFFER_SIZE = 8096;

    /**
     * Default Kryo Output maxBufferSize - Unlimited
     */
    public static final int DEFUALT_MAX_BUFFER_SIZE_UNLIMITED = -1;

    private KryoPool kryoPool = DEFAULT_KRYO_POOL;

    private int bufferSize = DEFAULT_BUFFER_SIZE;

    private int maxBufferSize = DEFUALT_MAX_BUFFER_SIZE_UNLIMITED;

    public KryoTranscoder() {
    }

    public KryoTranscoder(KryoPool kryoPool) {
        setKryoPool(kryoPool);
    }

    /**
     * return kryoPool
     *
     * @return kryoPool
     */
    public KryoPool getKryoPool() {
        return kryoPool;
    }

    /**
     * set kryoPool
     *
     * @param kryoPool a KryoPool instance.
     */
    public void setKryoPool(KryoPool kryoPool) {
        this.kryoPool = kryoPool;
    }

    /**
     * get Kryo buffer size
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * set Kryo Output buffer size
     *
     * @param bufferSize buffer size(bytes)
     * @see Output
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * get Kryo Output max buffer size
     *
     * @return maxBufferSize
     */
    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    /**
     * set Kryo Output max buffer size
     *
     * @param maxBufferSize max buffer size(bytes)
     * @see Output
     */
    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    @Override
    public CachedData encode(final T object) {
        final byte[] serializedBytes = kryoPool.run(new KryoCallback<byte[]>() {
            @Override
            public byte[] execute(Kryo kryo) {
                Output output = new Output(bufferSize, maxBufferSize);
                kryo.writeClassAndObject(output, object);
                output.close();
                return output.toBytes();
            }
        });

        return new CachedData(0B0000, serializedBytes, getMaxSize());
    }

    @Override
    public T decode(final CachedData cachedData) {
        return kryoPool.run(new KryoCallback<T>() {
            @Override
            public T execute(Kryo kryo) {
                final Input input = new Input(cachedData.getData());
                return (T) kryo.readClassAndObject(input);
            }
        });
    }

    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }

    @Override
    public int getMaxSize() {
        return CachedData.MAX_SIZE;
    }
}
