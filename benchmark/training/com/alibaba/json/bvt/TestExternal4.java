package com.alibaba.json.bvt;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;


public class TestExternal4 extends TestCase {
    ParserConfig confg = ParserConfig.global;

    public void test_0() throws Exception {
        TestExternal4.ExtClassLoader classLoader = new TestExternal4.ExtClassLoader();
        Class<?> clazz = classLoader.loadClass("external.VO2");
        Method method = clazz.getMethod("setName", new Class[]{ String.class });
        Method methodSetValue = clazz.getMethod("setValue", new Class[]{ Serializable.class });
        Object obj = clazz.newInstance();
        method.invoke(obj, "jobs");
        methodSetValue.invoke(obj, obj);
        {
            String text = JSON.toJSONString(obj);
            System.out.println(text);
        }
        String text = JSON.toJSONString(obj, WriteClassName);
        System.out.println(text);
        JSON.parseObject(text, clazz, confg);
        String clazzName = JSON.parse(text, confg).getClass().getName();
        Assert.assertEquals(clazz.getName(), clazzName);
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("external/VO2.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("external.VO2", bytes, 0, bytes.length);
            }
        }
    }
}

