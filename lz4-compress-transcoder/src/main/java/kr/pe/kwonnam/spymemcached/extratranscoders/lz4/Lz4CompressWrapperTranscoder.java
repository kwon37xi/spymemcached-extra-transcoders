package kr.pe.kwonnam.spymemcached.extratranscoders.lz4;

import kr.pe.kwonnam.spymemcached.extratranscoders.IntToBytesUtils;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.AbstractCompressionWrapperTranscoder;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Compressor;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Decompressor;
import net.spy.memcached.transcoders.Transcoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Lz4CompressWrapperTranscoder<T> extends AbstractCompressionWrapperTranscoder<T> implements Compressor, Decompressor {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 20; // 20kb

    public Lz4CompressWrapperTranscoder(Transcoder<T> wrappedTranscoder) {
        setWrappedTranscoder(wrappedTranscoder);
        setCompressor(this);
        setDecompressor(this);
    }

    @Override
    public byte[] compress(byte[] bytes) {
        final int originalBytesLength = bytes.length;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(originalBytesLength)) {
            baos.write(IntToBytesUtils.intToBytes(originalBytesLength));

            byte[] compressedBytes = Lz4CompressUtils.compress(bytes);
            baos.write(compressedBytes);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to lz4-compress.", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        final int decompressedSize = IntToBytesUtils.bytesToInt(Arrays.copyOf(bytes, IntToBytesUtils.INT_TO_BYTES_LENGTH));
        return Lz4CompressUtils.decompressFast(bytes, IntToBytesUtils.INT_TO_BYTES_LENGTH, decompressedSize);
    }
}
