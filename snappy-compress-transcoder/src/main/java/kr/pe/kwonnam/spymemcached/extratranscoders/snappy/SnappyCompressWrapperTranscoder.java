package kr.pe.kwonnam.spymemcached.extratranscoders.snappy;

import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.AbstractCompressionWrapperTranscoder;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Compressor;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Decompressor;
import net.spy.memcached.transcoders.Transcoder;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * <a href="https://github.com/xerial/snappy-java">snappy-java</a> based compression wrapper spymemcached transcoder.
 */
public class SnappyCompressWrapperTranscoder<T> extends AbstractCompressionWrapperTranscoder<T> implements Compressor, Decompressor {

    public SnappyCompressWrapperTranscoder(Transcoder<T> wrappedTranscoder) {
        setWrappedTranscoder(wrappedTranscoder);
        setCompressor(this);
        setDecompressor(this);
    }

    @Override
    public byte[] compress(byte[] bytes) {
        try {
            final byte[] compressedBytes = Snappy.compress(bytes);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(String.format("snappy-compression original-size : %d compressed-size : %d", bytes.length, compressedBytes.length));
            }
            return compressedBytes;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to compress with snappy.", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        try {
            return Snappy.uncompress(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to decompress with snappy.", e);
        }
    }
}
