package kr.pe.kwonnam.spymemcached.extratranscoders.fst;

import net.spy.memcached.CachedData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class FSTTranscoderTest {
    private final Logger log = getLogger(FSTTranscoderTest.class);

    private FSTTranscoder<Object> fstTranscoder;

    @Before
    public void setUp() throws Exception {
        fstTranscoder = new FSTTranscoder<>();
    }

    @Test
    public void constructor_with_FSTConfigurationFactory() throws Exception {
        final FSTConfigurationFactory anotherFSTConfigurationFactory = new FSTConfigurationFactory() {
            @Override
            public FSTConfiguration create() {
                final FSTConfiguration defaultConfiguration = FSTConfiguration.createDefaultConfiguration();
                defaultConfiguration.setName("Config name");
                return defaultConfiguration;
            }
        };

        fstTranscoder = new FSTTranscoder<>(anotherFSTConfigurationFactory);

        assertThat(fstTranscoder.getFstConfigurationFactory()).isSameAs(anotherFSTConfigurationFactory);
    }

    @Test
    public void encode_decode() throws Exception {
        final Post post = new Post("Introducing FSTTranscoder", new Date(), PostType.LONG);
        post.setContents("Hello FSTTranscoder!!");

        // encode
        final CachedData encodedData = fstTranscoder.encode(post);
        log.debug("Encoded Post flags : {}, data : {}", encodedData.getFlags(), encodedData.getData());
        assertThat(encodedData.getFlags()).isEqualTo(0);
        assertThat(encodedData.getData()).isNotEmpty();

        // decode
        final Post decodedPost = (Post) fstTranscoder.decode(encodedData);
        log.debug("Decoded Post : {}", decodedPost);
        assertThat(decodedPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(decodedPost.getCreatedAt()).isEqualTo(post.getCreatedAt());
        assertThat(decodedPost.getPostType()).isEqualTo(post.getPostType());
        assertThat(decodedPost.getContents()).isEqualTo(post.getContents());
    }

    @Test
    public void asyncDecode() throws Exception {
        assertThat(fstTranscoder.asyncDecode(new CachedData(0B0000, new byte[]{1, 2, 3}, CachedData.MAX_SIZE))).isFalse();
    }

    @Test
    public void getMaxSize() throws Exception {
        assertThat(fstTranscoder.getMaxSize()).isEqualTo(CachedData.MAX_SIZE);
    }

    /**
     * Test User data class.
     * No zero-args constructor.
     */
    public static class Post implements Serializable {
        private String title;
        private Date createdAt;
        private PostType postType;
        private String contents;

        public Post(String title, Date createdAt, PostType postType) {
            this.title = title;
            this.createdAt = createdAt;
            this.postType = postType;
        }

        public String getTitle() {
            return title;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public PostType getPostType() {
            return postType;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        @Override
        public String toString() {
            return "Post{" +
                    "title='" + title + '\'' +
                    ", createdAt=" + createdAt +
                    ", postType=" + postType +
                    ", contents='" + contents + '\'' +
                    '}';
        }
    }

    public enum PostType {
        SHORT, LONG
    }
}