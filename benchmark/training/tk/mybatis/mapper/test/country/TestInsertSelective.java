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
package tk.mybatis.mapper.test.country;


import java.util.List;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.CountryMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.Country;


/**
 * ?????????????null???
 *
 * @author liuzh
 */
public class TestInsertSelective {
    /**
     * ?????,id???null,???
     */
    @Test(expected = PersistenceException.class)
    public void testDynamicInsertAll() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            insertSelective(new Country());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????null
     */
    @Test(expected = PersistenceException.class)
    public void testDynamicInsertSelectiveAllByNull() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.insertSelective(null);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????
     */
    @Test
    public void testDynamicInsertSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("??");
            Assert.assertEquals(1, insertSelective(country));
            // ??CN??2?
            country = new Country();
            country.setCountrycode("CN");
            List<Country> list = select(country);
            Assert.assertEquals(2, list.size());
            // ???????,???????????
            Assert.assertEquals(1, deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * Countrycode???HH
     */
    @Test
    public void testDynamicInsertSelectiveNull() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setId(10086);
            country.setCountryname("??");
            Assert.assertEquals(1, insertSelective(country));
            // ??CN??2?
            country = new Country();
            country.setId(10086);
            List<Country> list = select(country);
            Assert.assertEquals(1, list.size());
            // ???
            Assert.assertNotNull(list.get(0).getCountrycode());
            Assert.assertEquals("HH", list.get(0).getCountrycode());
            // ???????,???????????
            Assert.assertEquals(1, deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }
}
