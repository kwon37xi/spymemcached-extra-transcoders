package kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper;

import net.spy.memcached.CachedData;
import net.spy.memcached.compat.SpyObject;
import net.spy.memcached.transcoders.Transcoder;

/**
 * Abstract Super class for Compression Wrapper Transcoders
 *
 * @param <T> cached data type
 */
public abstract class AbstractCompressionWrapperTranscoder<T> extends SpyObject implements Transcoder<T> {
    public static final int DEFAULT_COMPRESSION_THRESHOLD_BYTES_LENGTH = 1024 * 10;
    public static final int DEFAULT_COMPRESSION_FLAG = 0B0100;

    private long compressionThresholdByteLength = DEFAULT_COMPRESSION_THRESHOLD_BYTES_LENGTH;

    private int compressionFlag = DEFAULT_COMPRESSION_FLAG;

    private Transcoder<T> wrappedTranscoder;

    private Compressor compressor;

    private Decompressor decompressor;

    public long getCompressionThresholdByteLength() {
        return compressionThresholdByteLength;
    }

    public void setCompressionThresholdByteLength(long compressionThresholdByteLength) {
        this.compressionThresholdByteLength = compressionThresholdByteLength;
    }

    public int getCompressionFlag() {
        return compressionFlag;
    }

    public void setCompressionFlag(int compressionFlag) {
        this.compressionFlag = compressionFlag;
    }

    public Transcoder<T> getWrappedTranscoder() {
        return wrappedTranscoder;
    }

    public void setWrappedTranscoder(Transcoder<T> wrappedTranscoder) {
        this.wrappedTranscoder = wrappedTranscoder;
    }

    public Compressor getCompressor() {
        return compressor;
    }

    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }

    public Decompressor getDecompressor() {
        return decompressor;
    }

    public void setDecompressor(Decompressor decompressor) {
        this.decompressor = decompressor;
    }

    @Override
    public CachedData encode(T object) {
        final CachedData encodedCachedData = wrappedTranscoder.encode(object);
        final byte[] encodedBytes = encodedCachedData.getData();

        if (encodedBytes.length < compressionThresholdByteLength) {
            return encodedCachedData;
        }

        final byte[] compressed = compressor.compress(encodedBytes);

        final int flags = encodedCachedData.getFlags() | compressionFlag;
        return new CachedData(flags, compressed, getMaxSize());
    }

    @Override
    public T decode(CachedData cachedData) {
        int flags = cachedData.getFlags();
        boolean compressed = (flags & compressionFlag) > 0;

        byte[] decodedBytes = cachedData.getData();

        if (compressed) {
            decodedBytes = decompressor.decompress(cachedData.getData());
        }
        return wrappedTranscoder.decode(new CachedData(flags, decodedBytes, getMaxSize()));
    }

    @Override
    public int getMaxSize() {
        return CachedData.MAX_SIZE;
    }

    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }
}
