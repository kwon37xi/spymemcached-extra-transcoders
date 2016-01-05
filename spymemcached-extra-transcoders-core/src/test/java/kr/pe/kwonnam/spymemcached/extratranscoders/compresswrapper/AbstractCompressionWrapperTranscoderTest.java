package kr.pe.kwonnam.spymemcached.extratranscoders.compresswrapper;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCompressionWrapperTranscoderTest {
    public static class CachedDataMatcher extends ArgumentMatcher<CachedData> {
        private int expectedFlags;
        private byte[] expectedBytes;

        public CachedDataMatcher(int expectedFlags, byte[] expectedBytes) {
            this.expectedFlags = expectedFlags;
            this.expectedBytes = expectedBytes;
        }

        @Override
        public boolean matches(Object argument) {
            CachedData cachedData = (CachedData) argument;
            return cachedData.getFlags() == expectedFlags && Arrays.equals(cachedData.getData(), expectedBytes);
        }
    }

    public static final int TEST_FLAG = 0B1000;

    public static final int TEST_COMPRESSION_THRESHOLD_BYTE_LENGTH = 20;

    public static final String LESSTHAN_THRESHOLD_DATA = "ABCDEFGHIGJKLMNOPQR";

    public static final String GREATERTHAN_THRESHOLD_DATA = LESSTHAN_THRESHOLD_DATA + "S";

    @Mock
    private Compressor compressor;

    @Mock
    private Decompressor decompressor;

    @Mock
    private Transcoder<Object> wrappedTranscoder;

    private AbstractCompressionWrapperTranscoder<Object> compressionWrapperTranscoder;

    @Before
    public void setUp() throws Exception {
        compressionWrapperTranscoder = new AbstractCompressionWrapperTranscoder<Object>() {
        };

        compressionWrapperTranscoder.setWrappedTranscoder(wrappedTranscoder);
        compressionWrapperTranscoder.setCompressor(compressor);
        compressionWrapperTranscoder.setDecompressor(decompressor);
        compressionWrapperTranscoder.setCompressionFlag(TEST_FLAG);
        compressionWrapperTranscoder.setCompressionThresholdByteLength(TEST_COMPRESSION_THRESHOLD_BYTE_LENGTH);
    }

    @Test
    public void getWrappedTranscoder() throws Exception {
        assertThat(compressionWrapperTranscoder.getWrappedTranscoder()).isSameAs(wrappedTranscoder);
    }

    @Test
    public void getCompressor() throws Exception {
        assertThat(compressionWrapperTranscoder.getCompressor()).isSameAs(compressor);
    }

    @Test
    public void getDecompressor() throws Exception {
        assertThat(compressionWrapperTranscoder.getDecompressor()).isSameAs(decompressor);
    }

    @Test
    public void getCompressionFalg() throws Exception {
        assertThat(compressionWrapperTranscoder.getCompressionFlag()).isEqualTo(TEST_FLAG);
    }

    @Test
    public void getCompressionThresholdByteLength() throws Exception {
        assertThat(compressionWrapperTranscoder.getCompressionThresholdByteLength()).isEqualTo(TEST_COMPRESSION_THRESHOLD_BYTE_LENGTH);
    }

    @Test
    public void getMaxSize() throws Exception {
        assertThat(compressionWrapperTranscoder.getMaxSize()).isEqualTo(CachedData.MAX_SIZE);
    }

    @Test
    public void asyncDecode() throws Exception {
        assertThat(compressionWrapperTranscoder.asyncDecode(null)).isFalse();
        assertThat(compressionWrapperTranscoder.asyncDecode(new CachedData(1, new byte[]{}, CachedData.MAX_SIZE))).isFalse();
    }

    @Test
    public void encode_lessthan_threshold() throws Exception {
        final CachedData wrappedTranscoderEncode = new CachedData(0B0000, LESSTHAN_THRESHOLD_DATA.getBytes("UTF-8"), CachedData.MAX_SIZE);
        when(wrappedTranscoder.encode(LESSTHAN_THRESHOLD_DATA)).thenReturn(wrappedTranscoderEncode);

        final CachedData compressionWrapperEncode = compressionWrapperTranscoder.encode(LESSTHAN_THRESHOLD_DATA);

        assertThat(compressionWrapperEncode).isSameAs(wrappedTranscoderEncode);
        verifyZeroInteractions(compressor);
    }

    @Test
    public void encode_greaterthan_threshold() throws Exception {
        final byte[] encodedBytes = GREATERTHAN_THRESHOLD_DATA.getBytes("UTF-8");
        final CachedData wrappedTranscoderEncode = new CachedData(0B0001, encodedBytes, CachedData.MAX_SIZE);
        final byte[] compressedBytes = {1, 2, 3, 4, 5};

        when(wrappedTranscoder.encode(GREATERTHAN_THRESHOLD_DATA)).thenReturn(wrappedTranscoderEncode);
        when(compressor.compress(encodedBytes)).thenReturn(compressedBytes);
        final CachedData compressionWrapperEncode = compressionWrapperTranscoder.encode(GREATERTHAN_THRESHOLD_DATA);

        assertThat(compressionWrapperEncode.getFlags()).isEqualTo(0B1001);
        assertThat(compressionWrapperEncode.getData()).isEqualTo(compressedBytes);
    }

    @Test
    public void decode_not_compressed() throws Exception {
        final byte[] cachedBytes = LESSTHAN_THRESHOLD_DATA.getBytes("UTF-8");
        CachedData cachedData = new CachedData(0B0001, cachedBytes, CachedData.MAX_SIZE);
        when(wrappedTranscoder.decode(argThat(new CachedDataMatcher(0B0001, cachedBytes)))).thenReturn(LESSTHAN_THRESHOLD_DATA);

        final Object decoded = compressionWrapperTranscoder.decode(cachedData);

        assertThat(decoded).isEqualTo(LESSTHAN_THRESHOLD_DATA);
        verifyZeroInteractions(decompressor);
    }

    @Test
    public void decode_compressed() throws Exception {
        final byte[] compressedBytes = new byte[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        final byte[] decompressedBytes = GREATERTHAN_THRESHOLD_DATA.getBytes("UTF-8");

        CachedData cachedData = new CachedData(0B1001, compressedBytes, CachedData.MAX_SIZE);

        when(decompressor.decompress(compressedBytes)).thenReturn(decompressedBytes);
        when(wrappedTranscoder.decode(argThat(new CachedDataMatcher(0B1001, decompressedBytes)))).thenReturn(GREATERTHAN_THRESHOLD_DATA);

        final Object decoded = compressionWrapperTranscoder.decode(cachedData);
        assertThat(decoded).isEqualTo(GREATERTHAN_THRESHOLD_DATA);
    }
}