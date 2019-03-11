package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions15 extends IntegrationTest {
    public static class TestCls {
        private static boolean test(final String name) {
            if (TestConditions15.TestCls.isEmpty(name)) {
                return false;
            }
            if (((((((((((((((((((((((((((((("1".equals(name)) || ("2".equals(name))) || ("3".equals(name))) || ("4".equals(name))) || ("5".equals(name))) || ("6".equals(name))) || ("7".equals(name))) || ("8".equals(name))) || ("9".equals(name))) || ("10".equals(name))) || ("11".equals(name))) || ("12".equals(name))) || ("13".equals(name))) || ("14".equals(name))) || ("15".equals(name))) || ("16".equals(name))) || ("17".equals(name))) || ("18".equals(name))) || ("19".equals(name))) || ("20".equals(name))) || ("22".equals(name))) || ("22".equals(name))) || ("23".equals(name))) || ("24".equals(name))) || ("25".equals(name))) || ("26".equals(name))) || ("27".equals(name))) || ("28".equals(name))) || ("29".equals(name))) || ("30".equals(name))) {
                return false;
            } else {
                return true;
            }
        }

        private static boolean isEmpty(String name) {
            return name.isEmpty();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions15.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("\"1\".equals(name)"));
        Assert.assertThat(code, JadxMatchers.containsOne("\"30\".equals(name)"));
    }
}

