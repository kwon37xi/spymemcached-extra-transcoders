package kr.pe.kwonnam.spymemcached.extratranscoders.fst;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.nustaq.serialization.FSTConfiguration;

/**
 * <a href="http://ruedigermoeller.github.io/fast-serialization/">FST(fast-serialization)</a> based spymemcached transcoder.<br>
 */
public class FSTTranscoder<T> implements Transcoder<T> {

    /**
     * Default FSTConfiguration.
     * <br>
     * It requires objects implement {@link java.io.Serializable} or {@link java.io.Externalizable}.
     */
    public static final FSTConfigurationFactory DEFAULT_FSTCONFIGURATION_FACTORY = new FSTConfigurationFactory() {
        @Override
        public FSTConfiguration create() {
            return FSTConfiguration.createDefaultConfiguration();
        }
    };

    private final FSTConfigurationFactory fstConfigurationFactory;

    private final ThreadLocal<FSTConfiguration> fstConfigurationContainer;

    public FSTTranscoder() {
        this(DEFAULT_FSTCONFIGURATION_FACTORY);
    }

    public FSTTranscoder(final FSTConfigurationFactory fstConfigurationFactory) {
        this.fstConfigurationFactory = fstConfigurationFactory;

        fstConfigurationContainer = new ThreadLocal<FSTConfiguration>() {
            @Override
            protected FSTConfiguration initialValue() {
                return fstConfigurationFactory.create();
            }
        };
    }

    /**
     * return {@link FSTConfigurationFactory} instance.
     *
     * @return FSTConfigurationFactory which used by this transcoder to create FSTConfiguration instances.
     */
    public FSTConfigurationFactory getFstConfigurationFactory() {
        return fstConfigurationFactory;
    }

    @Override
    public CachedData encode(T object) {
        final FSTConfiguration fstConfiguration = fstConfigurationContainer.get();
        final byte[] serializedBytes = fstConfiguration.asByteArray(object);
        return new CachedData(0B0000, serializedBytes, getMaxSize());
    }

    @Override
    public T decode(CachedData cachedData) {
        final FSTConfiguration fstConfiguration = fstConfigurationContainer.get();

        @SuppressWarnings("unchecked")
        final T deserialized = (T) fstConfiguration.asObject(cachedData.getData());
        return deserialized;
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