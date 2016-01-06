package kr.pe.kwonnam.spymemcached.extratranscoders.gzip;

import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.AbstractCompressionWrapperTranscoder;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Compressor;
import kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper.Decompressor;
import net.spy.memcached.transcoders.Transcoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * {@link java.util.zip.GZIPInputStream}/{@link java.util.zip.GZIPOutputStream} based compression wrapper spymemcached transcoder.
 */
public class GZIPCompressWrapperTranscoder<T> extends AbstractCompressionWrapperTranscoder<T> implements Compressor, Decompressor {
    public static final int DEFAULT_UBFFER_SIZE = 8192; // 8k

    private int bufferSize = DEFAULT_UBFFER_SIZE;

    public GZIPCompressWrapperTranscoder(Transcoder<T> wrappedTranscoder) {
        setWrappedTranscoder(wrappedTranscoder);
        setCompressor(this);
        setDecompressor(this);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public byte[] compress(byte[] bytes) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(bytes);
            gzos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to compress with gzip.", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             GZIPInputStream gzis = new GZIPInputStream(bais)) {

            byte[] buffer = new byte[bufferSize];
            int readByteLength;
            while ((readByteLength = gzis.read(buffer)) > 0) {
                baos.write(buffer, 0, readByteLength);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to decompress with gzip.", e);
        }
    }
}
