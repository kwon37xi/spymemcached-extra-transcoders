package kr.pe.kwonnam.spymemcached.extratranscoders.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.EnumNameSerializer;
import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * <a href="https://github.com/EsotericSoftware/kryo">Kryo</a> based spymemcached transcoder.
 *
 * @param <T> Object Type
 */
public class KryoTranscoder<T> implements Transcoder<T> {
    /**
     * Default KryoFactory - supports non-constructor call object instantiation, field change compatibility and name based enum serialization.
     */
    public static final KryoFactory DEFAULT_KRYO_FACTORY = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();

            // First, try to instantiate an object with no-args constructor,
            // Second, if first fails then try to instantiate an object without constructor calls.
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

            // field change compatibility support.
            kryo.setDefaultSerializer(CompatibleFieldSerializer.class);

            // enum named based serializer
            kryo.addDefaultSerializer(Enum.class, EnumNameSerializer.class);
            return kryo;
        }
    };

    /**
     * Default Kryo Output bufferSize
     */
    public static final int DEFAULT_BUFFER_SIZE = 8096;

    /**
     * Default Kryo Output maxBufferSize - Unlimited
     */
    public static final int DEFAULT_MAX_BUFFER_SIZE_UNLIMITED = -1;

    private final KryoFactory kryoFactory;

    private final KryoPool kryoPool;

    private int bufferSize = DEFAULT_BUFFER_SIZE;

    private int maxBufferSize = DEFAULT_MAX_BUFFER_SIZE_UNLIMITED;

    public KryoTranscoder() {
        this(DEFAULT_KRYO_FACTORY);
    }

    /**
     * Constructor with {@link KryoFactory}.
     *
     * @param kryoFactory kryoFactory which build {@link Kryo} instances.
     */
    public KryoTranscoder(KryoFactory kryoFactory) {
        this.kryoFactory = kryoFactory;
        this.kryoPool = createKryoPool();
    }

    /**
     * Default KryoPool - pool of {@link Kryo} instances which are instantiated by {@link KryoFactory}.
     * <br>
     * This can be overridden.
     *
     * @return kryoPool
     */
    protected KryoPool createKryoPool() {
        return new KryoPool.Builder(kryoFactory).softReferences().build();
    }

    /**
     * get KryoFactory instance.
     *
     * @return kryoFactory
     */
    public KryoFactory getKryoFactory() {
        return kryoFactory;
    }

    /**
     * get Kryo buffer size
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * set Kryo Output buffer size
     * Default value is {@link #DEFAULT_BUFFER_SIZE}.
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
     * Default value is {@link #DEFAULT_MAX_BUFFER_SIZE_UNLIMITED}
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
                @SuppressWarnings("unchecked")
                final T deserialized = (T) kryo.readClassAndObject(input);
                return deserialized;
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
