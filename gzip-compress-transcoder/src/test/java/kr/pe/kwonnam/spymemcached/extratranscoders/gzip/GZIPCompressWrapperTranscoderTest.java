package kr.pe.kwonnam.spymemcached.extratranscoders.gzip;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GZIPCompressWrapperTranscoderTest {
    public static final int TEST_COMPRESSION_THRESHOLD_BYTE_LENGTH = 20;
    public static final int TEST_BUFFER_SIZE = 4096;
    private static final String TEST_DATA = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent at iaculis erat. Aliquam vestibulum quam at erat finibus, vel dictum massa placerat. Nunc sit amet erat odio. Mauris congue urna tortor, et semper leo sollicitudin at. Curabitur id risus urna. Duis aliquet justo in orci pulvinar placerat. Integer imperdiet quam nec laoreet fermentum. Sed sollicitudin magna id sem viverra, et finibus arcu hendrerit. Nulla vel scelerisque massa. Vivamus commodo laoreet elit, a tempus massa rutrum a.\n" +
            "Mauris sit amet diam a lacus dignissim tempus et at elit. Donec diam nulla, rutrum quis iaculis eu, pretium nec elit. Ut et consequat dolor. Maecenas risus sem, pellentesque nec malesuada ut, porta a ligula. Vivamus volutpat dui sed nibh laoreet tincidunt. Quisque laoreet est lorem, in gravida libero pretium at. Aenean ut facilisis nulla. Sed semper pharetra quam, eu scelerisque tortor rutrum eu. Duis sodales metus lacus, eu fringilla odio volutpat et. Nulla et sagittis nunc, in placerat dolor. Maecenas vel risus elit. Cras nec semper purus, tempus ultrices dui.\n" +
            "Nullam ultricies cursus tellus, in venenatis quam semper non. Vivamus tellus elit, vulputate nec maximus sit amet, posuere eget felis. Phasellus ut libero libero. Nullam et risus elementum, condimentum lacus id, eleifend orci. Maecenas velit nibh, interdum eget vehicula at, finibus sed metus. Morbi vel arcu lorem. Pellentesque non placerat nunc, dignissim fermentum sapien. Integer enim tellus, viverra eget ligula at, elementum rutrum justo. Duis id rutrum orci. Curabitur a felis ultrices augue feugiat sodales eu vitae leo. Curabitur eleifend ornare molestie.\n" +
            "Donec ullamcorper hendrerit massa in porta. Cras scelerisque vestibulum facilisis. Ut libero dolor, condimentum ut consectetur id, ullamcorper vitae mi. Pellentesque sit amet nunc eu nibh sodales consectetur. Integer scelerisque nisi at nisl ultricies aliquam. Phasellus accumsan ante ut nisl elementum, in congue ipsum mattis. Vivamus posuere ultrices ex, eget tempor mauris sollicitudin ut. Nulla in sapien ac metus convallis dignissim et sit amet elit.\n" +
            "Suspendisse quis iaculis mi, a rutrum ex. In rutrum non libero id sollicitudin. Morbi sollicitudin eget orci a pulvinar. Nulla iaculis massa at dictum dignissim. Duis eget lacinia lectus, et lacinia sapien. Cras vitae turpis scelerisque, fringilla tortor vitae, tristique eros. In vel ante metus. Curabitur ut lectus eget magna malesuada consequat ac id ante. ";
    public static final int TEST_COMPRESSION_FLAG = 0B1000;

    private Logger log = LoggerFactory.getLogger(GZIPCompressWrapperTranscoderTest.class);

    @Mock
    private Transcoder<Object> wrappedTranscoder;

    private GZIPCompressWrapperTranscoder<Object> gzipCompressWrapperTranscoder;

    private byte[] dataBytes;

    @Before
    public void setUp() throws Exception {
        gzipCompressWrapperTranscoder = new GZIPCompressWrapperTranscoder<>(wrappedTranscoder);
        gzipCompressWrapperTranscoder.setBufferSize(TEST_BUFFER_SIZE);
        gzipCompressWrapperTranscoder.setCompressionThresholdByteLength(TEST_COMPRESSION_THRESHOLD_BYTE_LENGTH);
        gzipCompressWrapperTranscoder.setCompressionFlag(TEST_COMPRESSION_FLAG);
        dataBytes = TEST_DATA.getBytes("UTF-8");
    }

    @Test
    public void getBufferSize() throws Exception {
        assertThat(gzipCompressWrapperTranscoder.getBufferSize()).isEqualTo(4096);
    }
//
//    @Test
//    public void compress_decompress() throws Exception {
//        final byte[] compressed = gzipCompressWrapperTranscoder.compress(dataBytes);
//
//        log.debug("Original size : {}, Compressed size : {}", dataBytes.length, compressed.length);
//        assertThat(compressed.length).isLessThan(dataBytes.length);
//
//        //decompress
//        final byte[] decompressed = gzipCompressWrapperTranscoder.decompress(compressed);
//        String decompressedString = new String(decompressed, "UTF-8");
//
//        log.debug("Decompressed size : {} data - {}", decompressed.length, decompressedString);
//        assertThat(decompressed.length).isEqualTo(dataBytes.length);
//        assertThat(decompressedString).isEqualTo(TEST_DATA);
//    }

    @Test
    public void encode_decode() throws Exception {
        when(wrappedTranscoder.encode(TEST_DATA)).thenReturn(new CachedData(0B0001, dataBytes, CachedData.MAX_SIZE));
        final CachedData encodedCachedData = gzipCompressWrapperTranscoder.encode(TEST_DATA);

        log.debug("Original size : {}, Compressed size : {}", dataBytes.length, encodedCachedData.getData().length);
        assertThat(encodedCachedData.getFlags()).isEqualTo(0B1001);
        assertThat(encodedCachedData.getData().length).isLessThan(dataBytes.length);

        // decode
        when(wrappedTranscoder.decode(any(CachedData.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                byte[] encodedBytes = ((CachedData) invocation.getArguments()[0]).getData();
                return new String(encodedBytes, "UTF-8");
            }
        });

        final Object decoded = gzipCompressWrapperTranscoder.decode(encodedCachedData);
        log.debug("Decoded : {}", decoded);
        assertThat(decoded).isEqualTo(TEST_DATA);
    }
}