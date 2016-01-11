package kr.pe.kwonnam.spymemcached.extratranscoders.integrationtest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.serializers.EnumNameSerializer;
import kr.pe.kwonnam.spymemcached.extratranscoders.kryo.KryoTranscoder;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.transcoders.Transcoder;
import org.junit.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class KryoTranscoderIntegrationTest extends AbstractMemcachedIntegrationTest {
    private Logger log = getLogger(KryoTranscoderIntegrationTest.class);

    @Override
    protected Transcoder<Object> initTranscoder() {
        KryoFactory kryoFactory = new KryoFactory() {
            @Override
            public Kryo create() {
                Kryo kryo = new Kryo();
                kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
                kryo.register(FakePost.class);
                kryo.register(Date.class);
                kryo.register(FakePost.PostType.class);
                kryo.addDefaultSerializer(Enum.class, EnumNameSerializer.class);
                return kryo;
            }
        };
        return new KryoTranscoder<>(kryoFactory);
    }

    @Test
    public void get_set() throws Exception {
        FakePost post = new FakePost("Hello KryoTranscoder Integration Test.", new Date(), FakePost.PostType.LONG);
        post.setContents(TEST_DATA);

        final OperationFuture<Boolean> set = memcachedClient.set("kryo_post_02", 500, post);
        log.debug("kryo_post_02 result : {}", set.get());

        final FakePost postFromMemcached = (FakePost) memcachedClient.get("kryo_post_02");
        log.debug("kryo_post_02 from memcached :{}", postFromMemcached);

        assertThat(postFromMemcached).isEqualToComparingFieldByField(post);
    }
}
