package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class StackTraceElementTest2 extends TestCase {
    public void test_stackTrace2() throws Exception {
        String text = "{\"@type\":\"java.lang.StackTraceElement\",\"className\":\"java.lang.Thread\",\"fileName\":\"Thread.java\",\"lineNumber\":1503,\"methodName\":\"getStackTrace\",\"nativeMethod\":false}";
        JSON.parseObject(text, StackTraceElement.class);
    }
}

