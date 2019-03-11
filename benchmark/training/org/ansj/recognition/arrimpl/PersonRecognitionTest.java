package org.ansj.recognition.arrimpl;


import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;


public class PersonRecognitionTest {
    @Test
    public void test() {
        String[] tests = new String[]{ "????????16???11.4?2.9??6???", "?????????nr", "????????????????????", "?????????????", "?????,????,????,?????", "????????", "?????????????????", "??????????", "???????????????", "???????????", "????????????????", "???????????????????????????????", "???????????????????", "?????????", "?????????", "??????,?????", "???????????????"// fix #408
        , "??????"// fix #284
        , "?????????", "??????????????????????????????????????????????????????????????????????????, ???????????????", "????"// #154
        , "??????????? ???"// #113
        , "???????????????????????3?8???????????????????????18917023639????"// #50
        , "?????"// #101
        , "???????????????????????????", "??????????????????????", "????????????????????????????????????????????????", "????????????", "????????", "???????????", "\u76f4\u64ad\u542711\u670824\u65e5\u8baf\uff0c\u4eca\u5929\u662f\u7f8e\u56fd\u7684\u611f\u6069\u8282\uff0c\u7bee\u7f51\u7403\u5458\u4e01\u5a01\u8fea\u5bf9\u4e8e\u7bee\u7f51\u7ed9\u4ed6\u673a\u4f1a\u8ba9\u4ed6\u9996\u53d1\u8868\u793a\u611f\u8c22\u3002\n" + ((("\u201c\u8fd9\u4e00\u5207\u90fd\u8981\u5f52\u529f\u4e8e\u52a9\u7406\u6559\u7ec3\u548c\u4e3b\u6559\u7ec3\u963f\u7279\u91d1\u68ee\u7684\u5de5\u4f5c\u3002\u201d\u4e01\u5a01\u8fea\u8bf4\u9053\uff1a\u201c\u8bf4\u5b9e\u8bdd\uff0c\u4ed6\u4eec\u5c31\u662f\u544a\u8bc9\u6211\uff0c\u5f53\u6211\u5728\u573a\u4e0a\u7684\u65f6\u5019\u8981\u4fdd\u6301\u4fb5\u7565\u6027\u3002\u4ed6\u4eec\u5c31\u662f\u5e0c\u671b\u6211\u53bb\u4e3a\u6bd4\u8d5b\u5b9a\u4e0b\u4e00\u79cd\u8282\u594f\uff0c\u66f4\u591a\u7684\u51fa\u573a\u65f6\u95f4\u610f\u5473\u7740\u66f4\u591a\u7684\u673a\u4f1a\u3002\u201d\n" + "\u201c\u8fd9\u662f\u4e00\u79cd\u6069\u8d50\uff0c\u6211\u5f88\u5e78\u8fd0\u3002\u65e0\u6cd5\u7528\u8bed\u8a00\u6765\u8868\u8fbe\u3002\u6211\u5f88\u5e78\u8fd0\u80fd\u591f\u53bb\u6295\u90a3\u4e9b\u6295\u7bee\uff0c\u80fd\u591f\u53bb\u6253\u6bd4\u8d5b\u3002\u80fd\u5904\u4e8e\u8fd9\u79cd\u73af\u5883\u8fd9\u662f\u4e00\u79cd\u6069\u8d50\uff0c\u968f\u7740\u7ecf\u9a8c\u7684\u7d2f\u79ef\u4f60\u4f1a\u611f\u5230\u8d8a\u6765\u8d8a\u8212\u9002\u3002\u8fd9\u771f\u7684\u662f\u8fd9\u6837\u3002\u201d\n") + "\u201c\u968f\u7740\u6bd4\u8d5b\u7684\u8fdb\u884c\uff0c\u4ed6\u53d8\u5f97\u8d8a\u6765\u8d8a\u597d\u3002\u4ed6\u6253\u5f97\u66f4\u8212\u9002\uff0c\u66f4\u6709\u4fb5\u7565\u6027\u3002\u201d\u963f\u7279\u91d1\u68ee\u8bf4\u9053\uff1a\u201c\u4e00\u5f00\u59cb\uff0c\u5f53\u6211\u4eec\u7b2c\u4e00\u6b21\u89c1\u5230\u4ed6\u7684\u65f6\u5019\uff0c\u4ed6\u7f3a\u5c11\u4fb5\u7565\u6027\u548c\u4fe1\u5fc3\u3002\u4ee5\u524d\u4ed6\u5728\u505a\u6bcf\u4ef6\u4e8b\u60c5\u4e0a\u90fd\u5f88\u80c6\u602f\u3002\u73b0\u5728\u4ed6\u5f88\u68d2\u3002\u89c2\u770b\u4e00\u4e2a\u5e74\u8f7b\u7403\u5458\u7684\u6210\u957f\uff0c\u5173\u4e8e\u4ed6\u7684\u53d1\u5c55\u3002\u4f60\u53ef\u4ee5\u770b\u5230\uff0c\u4ed6\u73b0\u5728\u975e\u5e38\u6709\u4fe1\u5fc3\u3002\u4ed6\u5728\u6bd4\u8d5b\u4e2d\u4e0d\u65ad\u7684\u8fdb\u6b65\u3002\u6211\u4eec\u9700\u8981\u4ed6\u5728\u9632\u5b88\u7aef\u6301\u7eed\u6ce8\u5165\u4e00\u80a1\u529b\u91cf\u3002\u8fd9\u9700\u8981\u627f\u62c5\u5f88\u591a\u7684\u8d23\u4efb\uff0c\u4f46\u662f\u4ee5\u4ed6\u7684\u4f53\u578b\u548c\u8fd0\u52a8\u80fd\u529b\uff0c\u4ed6\u53ef\u4ee5\u5728\u8fdb\u653b\u548c\u9632\u5b88\u4e24\u7aef\u90fd\u505a\u5230\u8fd9\u4e00\u70b9\u3002\u8fd9\u5bf9\u4e8e\u4ed6\u6765\u8bf4\u662f\u4e00\u4e2a\u6311\u6218\u3002\u201d\n") + "????????16???11.4?2.9??6???"), "????????????????? ,????????????????????1990???????????????????????????????????????????????????????????????????????", "???Facebook?CEO????????????HTML5??Facebook????????HTML5??????????", "????????????" };
        for (String str : tests) {
            for (Term term : ToAnalysis.parse(str).getTerms()) {
                System.out.print((term + " "));
            }
            System.out.println();
        }
    }
}

