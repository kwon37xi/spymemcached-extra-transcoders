package kr.pe.kwonnam.spymemcached.extratranscoders.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import net.spy.memcached.CachedData;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class KryoTranscoderTest {
    private Logger log = LoggerFactory.getLogger(KryoTranscoderTest.class);

    private KryoTranscoder<Object> kryoTranscoder = null;

    @Before
    public void setUp() throws Exception {
        kryoTranscoder = new KryoTranscoder<>();
    }

    @Test
    public void constructor_with_KryoFactory() throws Exception {
        final KryoFactory kryoFactory = new KryoFactory() {
            @Override
            public Kryo create() {
                return new Kryo();
            }
        };

        kryoTranscoder = new KryoTranscoder<>(kryoFactory);

        assertThat(kryoTranscoder.getKryoFactory()).isSameAs(kryoFactory);
    }

    @Test
    public void setGetBufferSize() throws Exception {
        kryoTranscoder.setBufferSize(123456);
        assertThat(kryoTranscoder.getBufferSize()).isEqualTo(123456);
    }

    @Test
    public void setGetMaxBufferSize() throws Exception {
        kryoTranscoder.setMaxBufferSize(Integer.MAX_VALUE);

        assertThat(kryoTranscoder.getMaxBufferSize()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void encode_decode() throws Exception {
        final User user = new User("Kryo Transcoder", new Date(), Sex.FEMALE);

        // encode
        final CachedData encodedData = kryoTranscoder.encode(user);
        log.debug("Encoded User flags : {}, data : {}", encodedData.getFlags(), encodedData.getData());
        assertThat(encodedData.getFlags()).isEqualTo(0);
        assertThat(encodedData.getData()).isNotEmpty();

        // decode
        final User decodedUser = (User) kryoTranscoder.decode(encodedData);
        log.debug("DecodedUser : {}", decodedUser);
        assertThat(decodedUser.getName()).isEqualTo(user.getName());
        assertThat(decodedUser.getBirthday()).isEqualTo(user.getBirthday());
        assertThat(decodedUser.getSex()).isEqualTo(user.getSex());
    }

    @Test
    public void asyncDecode() throws Exception {
        assertThat(kryoTranscoder.asyncDecode(new CachedData(0B000, new byte[] {1,2,3}, CachedData.MAX_SIZE))).isFalse();
    }

    /**
     * Test User data class.
     * No zero-args constructor.
     */
    public static class User {
        private String name;
        private Date birthday;
        private Sex sex;

        public User(String name, Date birthday, Sex sex) {
            this.name = name;
            this.birthday = birthday;
            this.sex = sex;
        }

        public String getName() {
            return name;
        }

        public Date getBirthday() {
            return birthday;
        }

        public Sex getSex() {
            return sex;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", birthday=" + birthday +
                    ", sex=" + sex +
                    '}';
        }
    }

    private enum Sex {
        MALE, FEMALE
    }
}