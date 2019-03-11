/**
 * *****************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * *****************************************************************************
 */
package org.springside.modules.mapper;


import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Test;
import org.springside.modules.web.CollectionExtractor;


public class CollectionExtractorTest {
    @Test
    public void convertElementPropertyToString() {
        CollectionExtractorTest.TestBean3 bean1 = new CollectionExtractorTest.TestBean3();
        bean1.setId(1);
        CollectionExtractorTest.TestBean3 bean2 = new CollectionExtractorTest.TestBean3();
        bean2.setId(2);
        List list = Lists.newArrayList(bean1, bean2);
        assertThat(CollectionExtractor.extractToString(list, "id", ",")).isEqualTo("1,2");
    }

    @Test
    public void convertElementPropertyToList() {
        CollectionExtractorTest.TestBean3 bean1 = new CollectionExtractorTest.TestBean3();
        bean1.setId(1);
        CollectionExtractorTest.TestBean3 bean2 = new CollectionExtractorTest.TestBean3();
        bean2.setId(2);
        List list = Lists.newArrayList(bean1, bean2);
        List result = CollectionExtractor.extractToList(list, "id");
        assertThat(result).containsOnly(1, 2);
    }

    @Test
    public void convertCollectionToString() {
        List<String> list = Lists.newArrayList("aa", "bb");
        String result = CollectionExtractor.convertToString(list, ",");
        assertThat(result).isEqualTo("aa,bb");
        result = CollectionExtractor.convertToString(list, "<li>", "</li>");
        assertThat(result).isEqualTo("<li>aa</li><li>bb</li>");
    }

    public static class TestBean3 {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

