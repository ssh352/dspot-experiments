package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import junit.framework.TestCase;


public class AmplAbstractClassTest {
    abstract static class AbstractFoo {
        public abstract String getValue();
    }

    static class Foo extends AmplAbstractClassTest.AbstractFoo {
        @Override
        public String getValue() {
            return "I am Foo";
        }
    }

    static class Bar extends AmplAbstractClassTest.AbstractFoo {
        @Override
        public String getValue() {
            return "I am Bar";
        }
    }

    static class Container {
        public final AmplAbstractClassTest.AbstractFoo foo;

        public Container(final AmplAbstractClassTest.AbstractFoo foo) {
            this.foo = foo;
        }
    }

    public void testReadmeSerial_add12536_remove13484_add16638() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add12536__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeSerial_add12536_remove13484_add16638__16 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536_remove13484_add16638__16);
        String o_testReadmeSerial_add12536__14 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536__14);
        String String_30 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_30);
        boolean boolean_31 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536_remove13484_add16638__16);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_30);
    }
}
