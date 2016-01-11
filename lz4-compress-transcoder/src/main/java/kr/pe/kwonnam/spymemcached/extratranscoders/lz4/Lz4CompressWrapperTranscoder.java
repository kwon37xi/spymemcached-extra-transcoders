package kr.pe.kwonnam.spymemcached.extratranscoders.lz4;

import kr.pe.kwonnam.spymemcached.extratranscoders.IntToBytesUtils;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.AbstractCompressionWrapperTranscoder;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Compressor;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Decompressor;
import net.spy.memcached.transcoders.Transcoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * <a href="https://github.com/jpountz/lz4-java">lz4-java</a> based compression wrapper spymemcached transcoder.
 */
public class Lz4CompressWrapperTranscoder<T> extends AbstractCompressionWrapperTranscoder<T> implements Compressor, Decompressor {
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
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(String.format("lz4-compression original-size : %d compressed-size : %d", bytes.length, compressedBytes.length));
            }
            baos.write(compressedBytes);
            baos.flush();
            baos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to compress with lz4.", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        final int decompressedSize = IntToBytesUtils.bytesToInt(Arrays.copyOf(bytes, IntToBytesUtils.INT_TO_BYTES_LENGTH));
        return Lz4CompressUtils.decompressFast(bytes, IntToBytesUtils.INT_TO_BYTES_LENGTH, decompressedSize);
    }
}
