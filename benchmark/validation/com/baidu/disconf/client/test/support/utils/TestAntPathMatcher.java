package com.baidu.disconf.client.test.support.utils;


import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;


/**
 * TestAntPathMatcher
 * ???????spring ??????
 */
public class TestAntPathMatcher {
    @Test
    public void ResourceLoaderTest() throws Exception {
        /* ???????
        classpath:????????????jar?zip?????
        classpath*:??????????????????????????????
        file:??????????????????????
        http:// ??????
        ftp:// ??????

        ant??????????
         ?:??????
         *:??????
         **:??????
         */
        ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver();
        Resource[] rs = rpr.getResources("classpath:testXml.xml");
        for (Resource one : rs) {
            showResourceInfo(one, true);
        }
        System.out.println("=============================");
        // file:????????? ? ???????
        // ???? ???FileSystemResource
        rs = rpr.getResources("file:src/test/resources/res/testXml.xml");
        // ???? ?????????
        // rs=rpr.getResources("file:src/aop.xml");
        for (Resource one : rs) {
            showResourceInfo(one, true);
        }
        System.out.println("=============================");
        // http:??
        rs = rpr.getResources("http://www.baidu.com/img/bdlogo.gif");
        // ??????????????????
        byte[] gifByte = IOUtils.toByteArray(rs[0].getInputStream());
        FileUtils.writeByteArrayToFile(new File("tmp/bdlogo1.gif"), gifByte);
    }
}

