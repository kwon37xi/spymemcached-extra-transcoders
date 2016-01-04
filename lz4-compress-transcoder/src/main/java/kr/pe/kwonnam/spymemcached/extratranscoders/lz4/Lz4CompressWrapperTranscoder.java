package kr.pe.kwonnam.spymemcached.extratranscoders.lz4;

import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.AbstractCompressionWrapperTranscoder;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Compressor;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Decompressor;
import net.spy.memcached.transcoders.Transcoder;

public class Lz4CompressWrapperTranscoder<T> extends AbstractCompressionWrapperTranscoder<T> implements Compressor, Decompressor {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 20; // 20kb
    private static final int DECOMPRESSED_SIZE_STORE_BYTES = 4;

    public Lz4CompressWrapperTranscoder(Transcoder<T> wrappedTranscoder) {
        setWrappedTranscoder(wrappedTranscoder);
        setCompressor(this);
        setDecompressor(this);
    }

    @Override
    public byte[] compress(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return new byte[0];
    }
}
