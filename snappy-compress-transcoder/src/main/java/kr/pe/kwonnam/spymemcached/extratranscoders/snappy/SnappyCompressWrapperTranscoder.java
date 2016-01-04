package kr.pe.kwonnam.spymemcached.extratranscoders.snappy;

import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.AbstractCompressionWrapperTranscoder;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Compressor;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Decompressor;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * <a href="https://github.com/xerial/snappy-java">snappy-java</a> based compression wrapper spymemcached transcoder.
 */
public class SnappyCompressWrapperTranscoder<T> extends AbstractCompressionWrapperTranscoder<T> implements Compressor, Decompressor {

    @Override
    public byte[] compress(byte[] bytes) {
        try {
            return Snappy.compress(bytes);
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
