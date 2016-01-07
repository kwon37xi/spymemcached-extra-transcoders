package kr.pe.kwonnam.spymemcached.extratranscoders.lz4;

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
public class Lz4CompressWrapperTranscoderTest {
    public static final int TEST_COMPRESSION_THRESHOLD_BYTE_LENGTH = 10240;
    private static final String TEST_DATA = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam semper felis lorem. Fusce accumsan tellus sed erat venenatis, sit amet rutrum augue pulvinar. Nunc ultricies mauris dui, non ultricies ipsum rhoncus non. Nam tortor lacus, aliquam sed ex vel, aliquet semper erat. Phasellus a dignissim odio, sit amet hendrerit metus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed ut sapien vehicula, consectetur justo id, lacinia lacus. Sed et justo sed nulla accumsan porta quis ut purus.\n" +
            "Maecenas tempor velit dui, eget pellentesque nisl porta eu. Pellentesque maximus quam sed ipsum ultricies pulvinar. Sed sollicitudin tristique metus, sed molestie ligula dictum ac. Nunc accumsan dui sed tellus faucibus, at dapibus tellus mattis. Curabitur sodales, eros sed pellentesque porttitor, justo elit porta dui, vitae dictum dolor lectus cursus purus. Fusce arcu neque, cursus sit amet faucibus in, bibendum vitae nibh. Sed ullamcorper ultrices semper. Maecenas luctus sed ligula condimentum sagittis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.\n" +
            "Cras fermentum felis sit amet tincidunt luctus. Pellentesque ultricies augue ut lacus mollis placerat eu at ante. Sed nisi est, sagittis vel neque a, convallis sollicitudin diam. Duis ultricies elit vitae diam varius auctor. Aenean quis diam viverra, tempus felis ut, rutrum risus. Nulla vehicula nisl et feugiat viverra. Mauris vel semper lectus, vitae tempus urna.\n" +
            "Vivamus hendrerit libero blandit interdum tincidunt. Sed cursus diam sem, ac fringilla sem suscipit sit amet. Donec eu metus molestie, pharetra erat sit amet, cursus nibh. Morbi ornare volutpat aliquet. Sed arcu elit, consequat consectetur velit sed, volutpat egestas leo. Duis orci lacus, varius fringilla cursus sed, facilisis ut tortor. Etiam ornare sodales tortor non fermentum. In hac habitasse platea dictumst. Lorem ipsum dolor sit amet, consectetur adipiscing elit. In pellentesque molestie euismod. Aliquam lacinia aliquet magna non finibus. Vivamus hendrerit varius ante a consequat. Nunc fermentum, odio id efficitur laoreet, elit ex pellentesque elit, eu pretium urna metus volutpat nisl.\n" +
            "Suspendisse in congue turpis. Mauris vehicula, ex eu malesuada efficitur, dolor neque tristique est, ac commodo diam sapien ut nunc. Aenean auctor ex lacus, ac venenatis lorem ultrices sed. Phasellus eget felis ut dui faucibus lacinia. Duis luctus felis at mauris dapibus, et laoreet diam rutrum. Suspendisse vehicula quam ac tellus rhoncus congue. Suspendisse eget massa vel diam pretium fringilla. Etiam viverra sem in eros gravida cursus.\n" +
            "Ut id ultricies mauris. Maecenas sed odio dapibus, tempus lorem ut, luctus tellus. Maecenas eu pretium eros. Sed dignissim fermentum massa at porttitor. Fusce at hendrerit quam, eget varius nunc. In non venenatis est. Phasellus ornare libero quis massa luctus, semper vulputate massa pellentesque. Fusce tempor placerat mattis. Quisque quis viverra est. Donec vel nulla id turpis luctus pharetra.\n" +
            "Ut eu varius mi, at vestibulum velit. Fusce at bibendum ante. Nunc quis finibus mi, eget posuere augue. Ut elit erat, rutrum nec dapibus ac, porttitor vitae augue. Nullam ut viverra massa. Vivamus dapibus massa id semper dictum. Vestibulum ultrices euismod scelerisque.\n" +
            "Cras vel tellus aliquet, pharetra libero in, malesuada diam. Maecenas ultrices, dui sed aliquam volutpat, felis velit convallis leo, id pellentesque metus mauris vitae neque. Proin finibus, nisl ac euismod semper, enim enim ultricies lectus, at mollis ligula dui id quam. Donec volutpat sollicitudin nisl sit amet accumsan. Proin orci urna, pellentesque at viverra in, maximus nec felis. Etiam posuere augue ornare erat accumsan, et tristique lectus mattis. Nam eu consequat leo. Nullam hendrerit quam non scelerisque ornare. Praesent nec vulputate sem, ac viverra lectus. Aenean tortor ante, iaculis vel euismod sit amet, ullamcorper feugiat felis. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur dapibus mauris ac arcu blandit, a finibus enim fermentum. Curabitur egestas in magna in pellentesque. Nam aliquet augue non massa feugiat lobortis. Proin non mi eget purus molestie gravida in at leo. Nunc commodo nunc urna, sed elementum tortor vulputate ut.\n" +
            "Maecenas dapibus tempus massa, et semper sem mollis vitae. Cras ante neque, mollis tempus dolor quis, blandit sagittis magna. Ut cursus dolor eget tincidunt tristique. Vivamus iaculis, magna in elementum laoreet, lectus lacus tristique orci, vel sagittis enim lacus et ipsum. Vivamus quis massa vitae tortor efficitur posuere. Fusce luctus felis eget massa pretium faucibus. Praesent ex sapien, faucibus a accumsan vitae, pharetra a nibh. Aliquam ut suscipit nulla. Phasellus id leo vitae lacus commodo tincidunt. Etiam auctor erat nec tempus lacinia. Ut bibendum, ipsum at suscipit feugiat, lacus elit porttitor lorem, et consectetur nisl lectus sit amet turpis.\n" +
            "Fusce non ultrices tortor. Aenean condimentum nibh quis sodales blandit. Proin ut viverra dui. Integer ac rutrum lacus. Integer accumsan nunc ut lorem malesuada, imperdiet pretium diam eleifend. Mauris at est nisl. Mauris ipsum mi, porttitor nec nibh vel, rhoncus volutpat ante. In laoreet mauris tellus, ac ornare tellus vehicula eu. Morbi semper mi nunc, at gravida eros cursus eget. Phasellus ac dignissim sapien, sit amet commodo urna. Proin semper risus quis erat efficitur mollis. Maecenas lobortis, velit a varius ultricies, elit libero faucibus nisl, quis consequat tortor nunc sed odio. In odio dolor, ornare in ante egestas, bibendum condimentum libero. Nulla et nisi quis ipsum ultrices lacinia.\n" +
            "Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nulla justo sem, rutrum malesuada ultricies lobortis, luctus in risus. In a erat posuere, dignissim justo ac, sagittis diam. Nullam lacus ex, semper nec aliquet ut, sodales vel arcu. Sed condimentum venenatis mauris, at faucibus arcu molestie in. Phasellus rhoncus tortor ut massa aliquam, vel fringilla est fermentum. Nullam faucibus leo et finibus mattis. Praesent maximus rutrum suscipit. Quisque accumsan mi sapien, a laoreet neque vestibulum ut. Proin in lacus finibus, rutrum nunc in, porttitor ante. Pellentesque ut pretium justo. Aliquam tempus aliquet elementum. Quisque pulvinar laoreet ex, eget facilisis felis tempus non.\n" +
            "Interdum et malesuada fames ac ante ipsum primis in faucibus. Nunc lacus lacus, rutrum at ex malesuada, euismod consequat eros. Cras feugiat dolor non ex posuere iaculis. Sed congue et odio ac ultrices. Proin euismod semper dui non gravida. Curabitur hendrerit, erat a feugiat consectetur, mauris quam consectetur nisl, et consequat augue erat eu tortor. Vestibulum eleifend, augue sed bibendum maximus, urna metus interdum orci, ac vehicula lorem nulla sed elit. Praesent ultrices tristique nisi vel feugiat. Cras id nunc ultricies, varius neque ut, finibus diam. Duis pulvinar non ante eu ultricies. Donec augue ante, posuere tempor justo at, condimentum aliquam quam. Donec tempor quam vitae malesuada pharetra. Pellentesque elit sapien, ornare eget eros sit amet, volutpat mollis elit. Phasellus convallis massa eget mauris interdum molestie. Morbi eget leo nunc. Ut molestie fringilla nulla ut tempor.\n" +
            "Aenean mollis commodo pulvinar. Nulla et risus eu risus dignissim tempus. Sed ullamcorper quis nunc id malesuada. Sed sollicitudin nunc nec mauris sodales pretium. Nullam viverra justo ante, ac accumsan lorem accumsan quis. Nulla eu lectus bibendum, sodales metus malesuada, ultrices lacus. In enim metus, scelerisque vel nulla eget, aliquam efficitur nibh. In vestibulum interdum feugiat. Integer at arcu in elit imperdiet congue. Sed at sapien aliquam, maximus lorem at, bibendum ligula. Ut aliquam eget mauris pellentesque auctor. Cras ultricies dictum enim nec porttitor. Ut in hendrerit neque, sed varius dolor. Integer dignissim, felis sed mollis pellentesque, quam velit pretium lectus, congue aliquam lectus mauris at odio. Suspendisse sed neque quis quam consectetur tincidunt.\n" +
            "Vivamus risus risus, elementum sit amet mi mattis, eleifend consequat mi. Cras varius vulputate ornare. Vivamus feugiat ex non eros dignissim dapibus. Aenean nec iaculis ante. Duis sit amet ligula finibus, varius elit nec, porta quam. Cras ullamcorper nec ligula non lobortis. Phasellus accumsan nisl sagittis tempor lobortis. Proin quis ex eget libero molestie dapibus nec sed purus. Aliquam in risus interdum, bibendum eros at, euismod sapien. Quisque euismod ullamcorper ligula, a vehicula diam posuere posuere. Etiam mollis nisi sit amet nisi efficitur, id laoreet massa mattis.\n" +
            "Nulla consectetur pellentesque odio, a congue magna elementum vitae. Quisque malesuada scelerisque elit sed consequat. Vivamus elementum sollicitudin nisl vitae semper. Etiam a pharetra mauris. Integer molestie, nisl non commodo laoreet, diam sapien luctus augue, id eleifend velit justo non lorem. Praesent tempor consequat erat, vitae egestas dui lobortis quis. Quisque sit amet massa odio. Aenean malesuada, nunc sit amet ultrices tristique, purus mauris suscipit felis, vel aliquet libero odio id eros. Curabitur consectetur arcu eget accumsan elementum. Aenean mattis vel leo eu rhoncus. Nam ullamcorper lobortis nibh non viverra. Curabitur sit amet magna faucibus, rhoncus felis sit amet, congue urna. Nam nec nisi sit amet quam auctor tempor eu sed neque. Phasellus non dui iaculis nisl aliquam aliquet.\n" +
            "Suspendisse dignissim laoreet libero, non vehicula velit viverra vitae. Ut convallis aliquet lacus ut facilisis. Sed a finibus lacus. Vestibulum sagittis a risus vitae iaculis. Etiam nec ex ultrices, semper dolor vel, laoreet velit. Quisque nisi sapien, vulputate nec convallis in, consectetur a dolor. Maecenas dignissim porta nulla eget luctus. Nullam tempor et lorem sit amet dignissim. Phasellus et leo eget mi ullamcorper tincidunt. Donec rutrum dignissim libero, eu tincidunt leo pharetra nec. Ut pellentesque ultrices lobortis. Proin vel iaculis metus, vitae tempor leo.\n" +
            "In hac habitasse platea dictumst. Fusce vestibulum tellus massa, id venenatis tellus ornare ac. Phasellus in lacus elit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut egestas diam nisl, nec venenatis augue malesuada non. Mauris dignissim, orci et sagittis tempus, ipsum odio pulvinar ex, vitae volutpat lacus est sed nisi. Phasellus sollicitudin vulputate odio sed suscipit. Suspendisse ullamcorper, odio eu bibendum posuere, massa purus interdum lorem, eu interdum mauris nulla eget nibh. Mauris ac semper urna. Nunc ac laoreet ante, ac accumsan justo. Nam porttitor tortor id tortor facilisis pellentesque. Ut dolor sem, commodo sit amet mauris sed, bibendum malesuada dui. Aenean euismod augue mauris, ut varius odio malesuada ut.\n" +
            "Nam feugiat odio varius suscipit fringilla. Ut nibh tellus, sollicitudin vitae nisl eget, eleifend mattis sapien. Vestibulum sagittis sed metus porttitor hendrerit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi felis purus, consequat quis lectus eu, ultricies vestibulum est. Cras ac enim vel justo convallis aliquet. Vivamus commodo turpis eget scelerisque consequat. Sed viverra ipsum ut ex pulvinar imperdiet. Nulla sed pharetra lacus, vel pretium justo. Pellentesque maximus magna et felis euismod imperdiet. Aenean quis est feugiat augue hendrerit placerat. Sed pretium mattis ornare. Nam fringilla orci odio, volutpat gravida odio tempus et.\n" +
            "Pellentesque tempus neque id ultrices tempor. Nunc sed massa cursus, porta lacus bibendum, pretium tortor. Praesent egestas odio libero, sed tempor est cursus eget. Vestibulum placerat sem orci, in imperdiet ipsum placerat sed. Praesent sit amet eleifend felis. Praesent maximus sem mi, in sagittis nibh semper eget. Nullam ut nulla consectetur, gravida ligula eget, molestie massa. Donec facilisis congue magna, in hendrerit lorem fringilla vel. Integer eget viverra elit.\n" +
            "Etiam eu sem ornare, fringilla ex id, blandit ex. Quisque mollis enim eget ligula mattis, et facilisis mauris lobortis. Proin vel ipsum luctus, convallis eros quis, volutpat dui. Donec ultrices magna a elementum gravida. Sed mi arcu, hendrerit eu mauris non, sagittis gravida justo. Curabitur convallis tincidunt tellus, nec molestie tortor sagittis at. Duis nisi odio, dapibus in dictum ut, consequat sed magna. Donec dignissim porta sapien, ac consequat purus tristique sit amet. Nunc maximus, leo a ullamcorper interdum, tellus ex mollis libero, varius pellentesque turpis nulla quis neque. In luctus consequat nisl, nec ultricies eros porta sed. Donec efficitur ornare maximus. Cras porttitor ligula orci, non ornare libero venenatis at. ";

    public static final int TEST_COMPRESSION_FLAG = 0B1000;

    private Logger log = LoggerFactory.getLogger(Lz4CompressWrapperTranscoderTest.class);

    @Mock
    private Transcoder<Object> wrappedTranscoder;

    private Lz4CompressWrapperTranscoder<Object> lz4CompressWrapperTranscoder;

    private byte[] dataBytes;

    @Before
    public void setUp() throws Exception {
        lz4CompressWrapperTranscoder = new Lz4CompressWrapperTranscoder<>(wrappedTranscoder);
        lz4CompressWrapperTranscoder.setCompressionThresholdByteLength(TEST_COMPRESSION_THRESHOLD_BYTE_LENGTH);
        lz4CompressWrapperTranscoder.setCompressionFlag(TEST_COMPRESSION_FLAG);
        dataBytes = TEST_DATA.getBytes("UTF-8");
    }

    @Test
    public void encode_decode() throws Exception {
        when(wrappedTranscoder.encode(TEST_DATA)).thenReturn(new CachedData(0B0001, dataBytes, CachedData.MAX_SIZE));
        final CachedData encodedCachedData = lz4CompressWrapperTranscoder.encode(TEST_DATA);

        log.debug("Original size : {}, lz4 compressed size : {}", dataBytes.length, encodedCachedData.getData().length);
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

        final Object decoded = lz4CompressWrapperTranscoder.decode(encodedCachedData);
        log.debug("Decoded : {}", decoded);
        assertThat(decoded).isEqualTo(TEST_DATA);
    }
}