package kr.pe.kwonnam.spymemcached.extratranscoders.integrationtest;

import kr.pe.kwonnam.spymemcached.extratranscoders.fst.FSTTranscoder;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.transcoders.Transcoder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class FSTTranscoderIntegrationTest extends AbstractMemcachedIntegrationTest {
    private Logger log = LoggerFactory.getLogger(FSTTranscoderIntegrationTest.class);

    @Override
    protected Transcoder<Object> initTranscoder() {
        return new FSTTranscoder<>();
    }

    @Test
    public void get_set() throws Exception {
        FakePost post = new FakePost("Hello FSTTranscoder Integration Test.", new Date(), FakePost.PostType.LONG);
        post.setContents(TEST_DATA);

        final OperationFuture<Boolean> set = memcachedClient.set("fst_post_01", 100, post);
        log.debug("post_01 result : {}", set.get());

        final FakePost postFromMemcached = (FakePost) memcachedClient.get("fst_post_01");
        log.debug("post from memcached :{}", postFromMemcached);

        assertThat(postFromMemcached).isEqualToComparingFieldByField(post);
    }
}