package kr.pe.kwonnam.spymemcached.extratranscoders.integrationtest;

import kr.pe.kwonnam.spymemcached.extratranscoders.gzip.GZIPCompressWrapperTranscoder;
import kr.pe.kwonnam.spymemcached.extratranscoders.kryo.KryoTranscoder;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.transcoders.Transcoder;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class GZIPComressWithKryoTranscoderIntegrationTest extends AbstractMemcachedIntegrationTest {
    private Logger log = getLogger(GZIPComressWithKryoTranscoderIntegrationTest.class);
    @Override
    protected Transcoder<Object> initTranscoder() {
        return new GZIPCompressWrapperTranscoder<>(new KryoTranscoder<>());
    }

    @Test
    public void get_set_not_compressed() throws Exception {
        FakePost post = new FakePost("Hello GZIPCompressWrapperTranscoder Integration Test - small", new Date(), FakePost.PostType.SHORT);
        post.setContents("small data.");

        final OperationFuture<Boolean> set = memcachedClient.set("gzip_kryo_post_small", 500, post);
        log.debug("GZIPCompressWrapperTranscoder gzip_kryo_post_small result : {}", set.get());

        final FakePost postFromMemcached = (FakePost) memcachedClient.get("gzip_kryo_post_small");
        log.debug("GZIPCompressWrapperTranscoder gzip_kryo_post_small from memcached :{}", postFromMemcached);

        assertThat(postFromMemcached).isEqualToComparingFieldByField(post);
    }

    @Test
    public void get_set_compressed() throws Exception {
        FakePost post = new FakePost("Hello GZIPCompressWrapperTranscoder Integration Test - big", new Date(), FakePost.PostType.LONG);
        post.setContents(TEST_DATA);

        final OperationFuture<Boolean> set = memcachedClient.set("gzip_kryo_post_big", 500, post);
        log.debug("GZIPCompressWrapperTranscoder gzip_kryo_post_big result : {}", set.get());

        final FakePost postFromMemcached = (FakePost) memcachedClient.get("gzip_kryo_post_big");
        log.debug("GZIPCompressWrapperTranscoder gzip_kryo_post_big from memcached :{}", postFromMemcached);

        assertThat(postFromMemcached).isEqualToComparingFieldByField(post);
    }
}
