package com.iota.iri.crypto;


import Curl.HASH_LENGTH;
import SpongeFactory.Mode;
import com.iota.iri.utils.Converter;
import com.iota.iri.utils.Pair;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static Curl.HASH_LENGTH;


/**
 * Created by paul on 4/15/17.
 */
public class CurlTest {
    static final Random seed = new Random();

    final String trytes = "RSWWSFXPQJUBJROQBRQZWZXZJWMUBVIVMHPPTYSNW9YQIQQF9RCSJJCVZG9ZWITXNCSBBDHEEKDRBHVTWCZ9SZOOZHVBPCQNPKTWFNZAWGCZ9QDIMKRVINMIRZBPKRKQAIPGOHBTHTGYXTBJLSURDSPEOJ9UKJECUKCCPVIQQHDUYKVKISCEIEGVOQWRBAYXWGSJUTEVG9RPQLPTKYCRAJ9YNCUMDVDYDQCKRJOAPXCSUDAJGETALJINHEVNAARIPONBWXUOQUFGNOCUSSLYWKOZMZUKLNITZIFXFWQAYVJCVMDTRSHORGNSTKX9Z9DLWNHZSMNOYTU9AUCGYBVIITEPEKIXBCOFCMQPBGXYJKSHPXNUKFTXIJVYRFILAVXEWTUICZCYYPCEHNTK9SLGVL9RLAMYTAEPONCBHDXSEQZOXO9XCFUCPPMKEBR9IEJGQOPPILHFXHMIULJYXZJASQEGCQDVYFOM9ETXAGVMSCHHQLFPATWOSMZIDL9AHMSDCE9UENACG9OVFAEIPPQYBCLXDMXXA9UBJFQQBCYKETPNKHNOUKCSSYLWZDLKUARXNVKKKHNRBVSTVKQCZL9RY9BDTDTPUTFUBGRMSTOTXLWUHDMSGYRDSZLIPGQXIDMNCNBOAOI9WFUCXSRLJFIVTIPIAZUK9EDUJJ9B9YCJEZQQELLHVCWDNRH9FUXDGZRGOVXGOKORTCQQA9JXNROLETYCNLRMBGXBL9DQKMOAZCBJGWLNJLGRSTYBKLGFVRUF9QOPZVQFGMDJA9TBVGFJDBAHEVOLW9GNU9NICLCQJBOAJBAHHBZJGOFUCQMBGYQLCWNKSZPPBQMSJTJLM9GXOZHTNDLGIRCSIJAZTENQVQDHFSOQM9WVNWQQJNOPZMEISSCLOADMRNWALBBSLSWNCTOSNHNLWZBVCFIOGFPCPRKQSRGKFXGTWUSCPZSKQNLQJGKDLOXSBJMEHQPDZGSENUKWAHRNONDTBLHNAKGLOMCFYRCGMDOVANPFHMQRFCZIQHCGVORJJNYMTORDKPJPLA9LWAKAWXLIFEVLKHRKCDG9QPQCPGVKIVBENQJTJGZKFTNZHIMQISVBNLHAYSSVJKTIELGTETKPVRQXNAPWOBGQGFRMMK9UQDWJHSQMYQQTCBMVQKUVGJEAGTEQDN9TCRRAZHDPSPIYVNKPGJSJZASZQBM9WXEDWGAOQPPZFLAMZLEZGXPYSOJRWL9ZH9NOJTUKXNTCRRDO9GKULXBAVDRIZBOKJYVJUSHIX9F9O9ACYCAHUKBIEPVZWVJAJGSDQNZNWLIWVSKFJUMOYDMVUFLUXT9CEQEVRFBJVPCTJQCORM9JHLYFSMUVMFDXZFNCUFZZIKREIUIHUSHRPPOUKGFKWX9COXBAZMQBBFRFIBGEAVKBWKNTBMLPHLOUYOXPIQIZQWGOVUWQABTJT9ZZPNBABQFYRCQLXDHDEX9PULVTCQLWPTJLRSVZQEEYVBVY9KCNEZXQLEGADSTJBYOXEVGVTUFKNCNWMEDKDUMTKCMRPGKDCCBDHDVVSMPOPUBZOMZTXJSQNVVGXNPPBVSBL9WWXWQNMHRMQFEQYKWNCSW9URI9FYPT9UZMAFMMGUKFYTWPCQKVJ9DIHRJFMXRZUGI9TMTFUQHGXNBITDSORZORQIAMKY9VRYKLEHNRNFSEFBHF9KXIQAEZEJNQOENJVMWLMHI9GNZPXYUIFAJIVCLAGKUZIKTJKGNQVTXJORWIQDHUPBBPPYOUPFAABBVMMYATXERQHPECDVYGWDGXFJKOMOBXKRZD9MCQ9LGDGGGMYGUAFGMQTUHZOAPLKPNPCIKUNEMQIZOCM9COAOMZSJ9GVWZBZYXMCNALENZ9PRYMHENPWGKX9ULUIGJUJRKFJPBTTHCRZQKEAHT9DC9GSWQEGDTZFHACZMLFYDVOWZADBNMEM9XXEOMHCNJMDSUAJRQTBUWKJF9RZHK9ACGUNI9URFIHLXBXCEODONPXBSCWP9WNAEYNALKQHGULUQGAFL9LB9NBLLCACLQFGQMXRHGBTMI9YKAJKVELRWWKJAPKMSYMJTDYMZ9PJEEYIRXRMMFLRSFSHIXUL9NEJABLRUGHJFL9RASMSKOI9VCFRZ9GWTMODUUESIJBHWWHZYCLDENBFSJQPIOYC9MBGOOXSWEMLVU9L9WJXKZKVDBDMFSVHHISSSNILUMWULMVMESQUIHDGBDXROXGH9MTNFSLWJZRAPOKKRGXAAQBFPYPAAXLSTMNSNDTTJQSDQORNJS9BBGQ9KQJZYPAQ9JYQZJ9B9KQDAXUACZWRUNGMBOQLQZUHFNCKVQGORRZGAHES9PWJUKZWUJSBMNZFILBNBQQKLXITCTQDDBV9UDAOQOUPWMXTXWFWVMCXIXLRMRWMAYYQJPCEAAOFEOGZQMEDAGYGCTKUJBS9AGEXJAFHWWDZRYEN9DN9HVCMLFURISLYSWKXHJKXMHUWZXUQARMYPGKRKQMHVR9JEYXJRPNZINYNCGZHHUNHBAIJHLYZIZGGIDFWVNXZQADLEDJFTIUTQWCQSX9QNGUZXGXJYUUTFSZPQKXBA9DFRQRLTLUJENKESDGTZRGRSLTNYTITXRXRGVLWBTEWPJXZYLGHLQBAVYVOSABIVTQYQM9FIQKCBRRUEMVVTMERLWOK";

    final String hash = "TIXEPIEYMGURTQ9ABVYVQSWMNGCVQFASMFAEQWUZCLIWLCDIGYVXOEJBBEMZOIHAYSUQMEFOGZBXUMHQW";

    @Test
    public void normalHashWorks() {
        int size = 8019;
        byte[] in_trits = new byte[size];
        Converter.trits(trytes, in_trits, 0);
        byte[] hash_trits = new byte[HASH_LENGTH];
        Sponge curl;
        curl = new Curl(Mode.CURLP81);
        curl.absorb(in_trits, 0, in_trits.length);
        curl.squeeze(hash_trits, 0, HASH_LENGTH);
        String out_trytes = Converter.trytes(hash_trits);
        Assert.assertEquals(hash, out_trytes);
    }

    @Test
    public void pairHashWorks() {
        int size = 8019;
        byte[] in_trits = new byte[size];
        Converter.trits(trytes, in_trits, 0);
        Pair<long[], long[]> hashPair = new Pair(new long[HASH_LENGTH], new long[HASH_LENGTH]);
        Curl curl;
        curl = new Curl(true, Mode.CURLP81);
        curl.absorb(Converter.longPair(in_trits), 0, in_trits.length);
        curl.squeeze(hashPair, 0, HASH_LENGTH);
        byte[] hash_trits = Converter.trits(hashPair.low, hashPair.hi);
        String out_trytes = Converter.trytes(hash_trits);
        Assert.assertEquals(hash, out_trytes);
    }

    @Test
    public void pairHashIsFasterThanNormalHash() {
        int size = 8019;
        long start1;
        long diff1;
        long start2;
        long diff2;
        byte[] in_trits = new byte[size];
        Converter.trits(trytes, in_trits, 0);
        final byte[] hash_trits = new byte[HASH_LENGTH];
        Curl curl;
        Curl curl1;
        curl = new Curl(true, Mode.CURLP81);
        curl1 = new Curl(Mode.CURLP81);
        Pair<long[], long[]> in_pair = Converter.longPair(in_trits);
        Pair<long[], long[]> hashPair = new Pair(new long[HASH_LENGTH], new long[HASH_LENGTH]);
        int iteration = 0;
        while ((iteration++) < 10) {
            curl.absorb(in_pair, 0, in_trits.length);
            curl.squeeze(hashPair, 0, HASH_LENGTH);
            curl.reset(true);
            curl1.absorb(in_trits, 0, in_trits.length);
            curl1.squeeze(hash_trits, 0, HASH_LENGTH);
            curl1.reset();
        } 
        int serialCount = 64;
        start1 = System.nanoTime();
        while ((serialCount--) > 0) {
            curl1.absorb(in_trits, 0, in_trits.length);
            curl1.squeeze(hash_trits, 0, HASH_LENGTH);
            curl1.reset();
        } 
        diff1 = (System.nanoTime()) - start1;
        hashPair = new Pair(new long[HASH_LENGTH], new long[HASH_LENGTH]);
        start2 = System.nanoTime();
        curl.absorb(in_pair, 0, in_trits.length);
        curl.squeeze(hashPair, 0, HASH_LENGTH);
        diff2 = (System.nanoTime()) - start2;
        System.arraycopy(Converter.trits(hashPair.low, hashPair.hi), 0, hash_trits, 0, HASH_LENGTH);
        String out_trytes = Converter.trytes(hash_trits);
        Assert.assertEquals(hash, out_trytes);
        System.out.println(diff1);
        System.out.println(diff2);
        System.out.println((diff1 / diff2));
        Assert.assertTrue((diff2 < diff1));
    }
}

