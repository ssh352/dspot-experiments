/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.pagehelper.test.basic;


import com.github.pagehelper.IPage;
import com.github.pagehelper.Page;
import com.github.pagehelper.mapper.CountryMapper;
import com.github.pagehelper.model.Country;
import com.github.pagehelper.model.CountryQueryModel;
import com.github.pagehelper.util.MybatisHelper;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


public class IPageTest {
    public static class CountryIPage extends CountryQueryModel implements IPage {}

    @Test
    public void testIPage() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            IPageTest.CountryIPage queryModel = new IPageTest.CountryIPage();
            queryModel.setPageNum(1);
            queryModel.setPageSize(10);
            queryModel.setOrderBy("id desc");
            List<Country> list = countryMapper.selectByQueryModel(queryModel);
            Assert.assertEquals(10, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            queryModel.setPageNum(2);
            queryModel.setOrderBy(null);
            list = countryMapper.selectByQueryModel(queryModel);
            Assert.assertEquals(10, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
            queryModel.setPageNum(null);
            queryModel.setPageSize(null);
            queryModel.setOrderBy("id asc");
            list = countryMapper.selectByQueryModel(queryModel);
            Assert.assertEquals(1, list.get(0).getId());
            Assert.assertEquals(183, list.size());
            Assert.assertEquals(183, ((Page<?>) (list)).getTotal());
        } finally {
            sqlSession.close();
        }
    }
}

