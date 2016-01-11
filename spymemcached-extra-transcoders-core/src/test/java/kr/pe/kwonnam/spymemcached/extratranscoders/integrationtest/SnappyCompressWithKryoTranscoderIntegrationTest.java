package kr.pe.kwonnam.spymemcached.extratranscoders.integrationtest;

import kr.pe.kwonnam.spymemcached.extratranscoders.kryo.KryoTranscoder;
import kr.pe.kwonnam.spymemcached.extratranscoders.snappy.SnappyCompressWrapperTranscoder;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.transcoders.Transcoder;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class SnappyCompressWithKryoTranscoderIntegrationTest extends AbstractMemcachedIntegrationTest {
    private Logger log = getLogger(SnappyCompressWithKryoTranscoderIntegrationTest.class);

    @Override
    protected Transcoder<Object> initTranscoder() {
        return new SnappyCompressWrapperTranscoder<>(new KryoTranscoder<>());
    }

    @Test
    public void get_set_not_compressed() throws Exception {
        FakePost post = new FakePost("Hello SnappyCompressWrapperTranscoder Integration Test - small", new Date(), FakePost.PostType.SHORT);
        post.setContents("small data.");

        final OperationFuture<Boolean> set = memcachedClient.set("snappy_kryo_post_small", 500, post);
        log.debug("SnappyCompressWrapperTranscoder snappy_kryo_post_small result : {}", set.get());

        final FakePost postFromMemcached = (FakePost) memcachedClient.get("snappy_kryo_post_small");
        log.debug("SnappyCompressWrapperTranscoder snappy_kryo_post_small from memcached :{}", postFromMemcached);

        assertThat(postFromMemcached).isEqualToComparingFieldByField(post);
    }

    @Test
    public void get_set_compressed() throws Exception {
        FakePost post = new FakePost("Hello SnappyCompressWrapperTranscoder Integration Test - big", new Date(), FakePost.PostType.LONG);
        post.setContents(TEST_DATA);

        final OperationFuture<Boolean> set = memcachedClient.set("snappy_kryo_post_big", 500, post);
        log.debug("SnappyCompressWrapperTranscoder snappy_kryo_post_big result : {}", set.get());

        final FakePost postFromMemcached = (FakePost) memcachedClient.get("snappy_kryo_post_big");
        log.debug("SnappyCompressWrapperTranscoder snappy_kryo_post_big from memcached :{}", postFromMemcached);

        assertThat(postFromMemcached).isEqualToComparingFieldByField(post);
    }
}
