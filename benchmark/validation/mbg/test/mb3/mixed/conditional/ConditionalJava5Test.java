/**
 * Copyright 2006-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package mbg.test.mb3.mixed.conditional;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import mbg.test.mb3.AbstractTest;
import mbg.test.mb3.generated.mixed.conditional.mapper.AwfulTableMapper;
import mbg.test.mb3.generated.mixed.conditional.mapper.FieldsblobsMapper;
import mbg.test.mb3.generated.mixed.conditional.mapper.FieldsonlyMapper;
import mbg.test.mb3.generated.mixed.conditional.mapper.PkblobsMapper;
import mbg.test.mb3.generated.mixed.conditional.mapper.PkfieldsMapper;
import mbg.test.mb3.generated.mixed.conditional.mapper.PkfieldsblobsMapper;
import mbg.test.mb3.generated.mixed.conditional.mapper.PkonlyMapper;
import mbg.test.mb3.generated.mixed.conditional.model.AwfulTable;
import mbg.test.mb3.generated.mixed.conditional.model.AwfulTableExample;
import mbg.test.mb3.generated.mixed.conditional.model.Fieldsblobs;
import mbg.test.mb3.generated.mixed.conditional.model.FieldsblobsExample;
import mbg.test.mb3.generated.mixed.conditional.model.FieldsblobsWithBLOBs;
import mbg.test.mb3.generated.mixed.conditional.model.Fieldsonly;
import mbg.test.mb3.generated.mixed.conditional.model.FieldsonlyExample;
import mbg.test.mb3.generated.mixed.conditional.model.Pkblobs;
import mbg.test.mb3.generated.mixed.conditional.model.PkblobsExample;
import mbg.test.mb3.generated.mixed.conditional.model.Pkfields;
import mbg.test.mb3.generated.mixed.conditional.model.PkfieldsExample;
import mbg.test.mb3.generated.mixed.conditional.model.PkfieldsKey;
import mbg.test.mb3.generated.mixed.conditional.model.Pkfieldsblobs;
import mbg.test.mb3.generated.mixed.conditional.model.PkfieldsblobsExample;
import mbg.test.mb3.generated.mixed.conditional.model.PkfieldsblobsKey;
import mbg.test.mb3.generated.mixed.conditional.model.PkonlyExample;
import mbg.test.mb3.generated.mixed.conditional.model.PkonlyKey;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Jeff Butler
 */
public class ConditionalJava5Test extends AbstractMixedConditionalTest {
    @Test
    public void testFieldsOnlyInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria().andIntegerfieldEqualTo(5);
            List<Fieldsonly> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            Fieldsonly returnedRecord = answer.get(0);
            Assertions.assertEquals(record.getIntegerfield(), returnedRecord.getIntegerfield());
            Assertions.assertEquals(record.getDoublefield(), returnedRecord.getDoublefield());
            Assertions.assertEquals(record.getFloatfield(), returnedRecord.getFloatfield());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsOnlySelectByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(44.55);
            record.setFloatfield(66.77);
            record.setIntegerfield(8);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(88.99);
            record.setFloatfield(100.111);
            record.setIntegerfield(9);
            mapper.insert(record);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria().andIntegerfieldGreaterThan(5);
            List<Fieldsonly> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            example = new FieldsonlyExample();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsOnlySelectByExampleNoCriteria() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(44.55);
            record.setFloatfield(66.77);
            record.setIntegerfield(8);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(88.99);
            record.setFloatfield(100.111);
            record.setIntegerfield(9);
            mapper.insert(record);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria();
            List<Fieldsonly> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsOnlyDeleteByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(44.55);
            record.setFloatfield(66.77);
            record.setIntegerfield(8);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(88.99);
            record.setFloatfield(100.111);
            record.setIntegerfield(9);
            mapper.insert(record);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria().andIntegerfieldGreaterThan(5);
            int rows = mapper.deleteByExample(example);
            Assertions.assertEquals(2, rows);
            example = new FieldsonlyExample();
            List<Fieldsonly> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsOnlyCountByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(44.55);
            record.setFloatfield(66.77);
            record.setIntegerfield(8);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(88.99);
            record.setFloatfield(100.111);
            record.setIntegerfield(9);
            mapper.insert(record);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria().andIntegerfieldGreaterThan(5);
            long rows = mapper.countByExample(example);
            Assertions.assertEquals(2, rows);
            example.clear();
            rows = mapper.countByExample(example);
            Assertions.assertEquals(3, rows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKOnlyInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            PkonlyKey key = new PkonlyKey();
            key.setId(1);
            key.setSeqNum(3);
            mapper.insert(key);
            PkonlyExample example = new PkonlyExample();
            List<PkonlyKey> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            PkonlyKey returnedRecord = answer.get(0);
            Assertions.assertEquals(key.getId(), returnedRecord.getId());
            Assertions.assertEquals(key.getSeqNum(), returnedRecord.getSeqNum());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKOnlyDeleteByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            PkonlyKey key = new PkonlyKey();
            key.setId(1);
            key.setSeqNum(3);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(5);
            key.setSeqNum(6);
            mapper.insert(key);
            PkonlyExample example = new PkonlyExample();
            List<PkonlyKey> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            key = new PkonlyKey();
            key.setId(5);
            key.setSeqNum(6);
            int rows = mapper.deleteByPrimaryKey(key);
            Assertions.assertEquals(1, rows);
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKOnlyDeleteByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            PkonlyKey key = new PkonlyKey();
            key.setId(1);
            key.setSeqNum(3);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(5);
            key.setSeqNum(6);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(7);
            key.setSeqNum(8);
            mapper.insert(key);
            PkonlyExample example = new PkonlyExample();
            example.createCriteria().andIdGreaterThan(4);
            int rows = mapper.deleteByExample(example);
            Assertions.assertEquals(2, rows);
            example = new PkonlyExample();
            List<PkonlyKey> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKOnlySelectByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            PkonlyKey key = new PkonlyKey();
            key.setId(1);
            key.setSeqNum(3);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(5);
            key.setSeqNum(6);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(7);
            key.setSeqNum(8);
            mapper.insert(key);
            PkonlyExample example = new PkonlyExample();
            example.createCriteria().andIdGreaterThan(4);
            List<PkonlyKey> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKOnlySelectByExampleNoCriteria() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            PkonlyKey key = new PkonlyKey();
            key.setId(1);
            key.setSeqNum(3);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(5);
            key.setSeqNum(6);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(7);
            key.setSeqNum(8);
            mapper.insert(key);
            PkonlyExample example = new PkonlyExample();
            example.createCriteria();
            List<PkonlyKey> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKOnlyCountByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            PkonlyKey key = new PkonlyKey();
            key.setId(1);
            key.setSeqNum(3);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(5);
            key.setSeqNum(6);
            mapper.insert(key);
            key = new PkonlyKey();
            key.setId(7);
            key.setSeqNum(8);
            mapper.insert(key);
            PkonlyExample example = new PkonlyExample();
            example.createCriteria().andIdGreaterThan(4);
            long rows = mapper.countByExample(example);
            Assertions.assertEquals(2, rows);
            example.clear();
            rows = mapper.countByExample(example);
            Assertions.assertEquals(3, rows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setDatefield(new Date());
            record.setDecimal100field(10L);
            record.setDecimal155field(new BigDecimal("15.12345"));
            record.setDecimal30field(((short) (3)));
            record.setDecimal60field(6);
            record.setFirstname("Jeff");
            record.setId1(1);
            record.setId2(2);
            record.setLastname("Butler");
            record.setTimefield(new Date());
            record.setTimestampfield(new Date());
            mapper.insert(record);
            PkfieldsKey key = new PkfieldsKey();
            key.setId1(1);
            key.setId2(2);
            Pkfields returnedRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertNotNull(returnedRecord);
            Assertions.assertTrue(datesAreEqual(record.getDatefield(), returnedRecord.getDatefield()));
            Assertions.assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
            Assertions.assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
            Assertions.assertEquals(record.getDecimal30field(), returnedRecord.getDecimal30field());
            Assertions.assertEquals(record.getDecimal60field(), returnedRecord.getDecimal60field());
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getLastname(), returnedRecord.getLastname());
            Assertions.assertTrue(timesAreEqual(record.getTimefield(), returnedRecord.getTimefield()));
            Assertions.assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsUpdateByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            int rows = mapper.updateByPrimaryKey(record);
            Assertions.assertEquals(1, rows);
            PkfieldsKey key = new PkfieldsKey();
            key.setId1(1);
            key.setId2(2);
            Pkfields record2 = mapper.selectByPrimaryKey(key);
            Assertions.assertEquals(record.getFirstname(), record2.getFirstname());
            Assertions.assertEquals(record.getLastname(), record2.getLastname());
            Assertions.assertEquals(record.getId1(), record2.getId1());
            Assertions.assertEquals(record.getId2(), record2.getId2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsUpdateByPrimaryKeySelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setDecimal60field(5);
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            Pkfields newRecord = new Pkfields();
            newRecord.setId1(1);
            newRecord.setId2(2);
            newRecord.setFirstname("Scott");
            newRecord.setDecimal60field(4);
            int rows = mapper.updateByPrimaryKeySelective(newRecord);
            Assertions.assertEquals(1, rows);
            PkfieldsKey key = new PkfieldsKey();
            key.setId1(1);
            key.setId2(2);
            Pkfields returnedRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertTrue(datesAreEqual(record.getDatefield(), returnedRecord.getDatefield()));
            Assertions.assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
            Assertions.assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
            Assertions.assertEquals(record.getDecimal30field(), returnedRecord.getDecimal30field());
            Assertions.assertEquals(newRecord.getDecimal60field(), returnedRecord.getDecimal60field());
            Assertions.assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getLastname(), returnedRecord.getLastname());
            Assertions.assertTrue(timesAreEqual(record.getTimefield(), returnedRecord.getTimefield()));
            Assertions.assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKfieldsDeleteByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            PkfieldsKey key = new PkfieldsKey();
            key.setId1(1);
            key.setId2(2);
            int rows = mapper.deleteByPrimaryKey(key);
            Assertions.assertEquals(1, rows);
            PkfieldsExample example = new PkfieldsExample();
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(0, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsDeleteByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bob");
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            PkfieldsExample example = new PkfieldsExample();
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            example = new PkfieldsExample();
            example.createCriteria().andLastnameLike("J%");
            int rows = mapper.deleteByExample(example);
            Assertions.assertEquals(1, rows);
            example = new PkfieldsExample();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bob");
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            PkfieldsKey key = new PkfieldsKey();
            key.setId1(3);
            key.setId2(4);
            Pkfields newRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertNotNull(newRecord);
            Assertions.assertEquals(record.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), newRecord.getLastname());
            Assertions.assertEquals(record.getId1(), newRecord.getId1());
            Assertions.assertEquals(record.getId2(), newRecord.getId2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByExampleLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Fred");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Wilma");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Pebbles");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Barney");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Betty");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bamm Bamm");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andFirstnameLike("B%");
            example.setOrderByClause("ID1, ID2");
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
            Pkfields returnedRecord = answer.get(0);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByExampleNotLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Fred");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Wilma");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Pebbles");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Barney");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Betty");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bamm Bamm");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andFirstnameNotLike("B%");
            example.setOrderByClause("ID1, ID2");
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
            Pkfields returnedRecord = answer.get(0);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByExampleComplexLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Fred");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Wilma");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Pebbles");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Barney");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Betty");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bamm Bamm");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andFirstnameLike("B%").andId2EqualTo(3);
            example.or(example.createCriteria().andFirstnameLike("Wi%"));
            example.setOrderByClause("ID1, ID2");
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            Pkfields returnedRecord = answer.get(0);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByExampleIn() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Fred");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Wilma");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Pebbles");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Barney");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Betty");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bamm Bamm");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            List<Integer> ids = new ArrayList<>();
            ids.add(1);
            ids.add(3);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andId2In(ids);
            example.setOrderByClause("ID1, ID2");
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(4, answer.size());
            Pkfields returnedRecord = answer.get(0);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(3);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByExampleBetween() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Fred");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Wilma");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Pebbles");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Barney");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Betty");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bamm Bamm");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andId2Between(1, 3);
            example.setOrderByClause("ID1, ID2");
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(6, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByExampleNoCriteria() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Fred");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Wilma");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Pebbles");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Barney");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Betty");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bamm Bamm");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria();
            example.setOrderByClause("ID1, ID2");
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(6, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByExampleEscapedFields() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Fred");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            record.setWierdField(11);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Wilma");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            record.setWierdField(22);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Pebbles");
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            record.setWierdField(33);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Barney");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            record.setWierdField(44);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Betty");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            record.setWierdField(55);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bamm Bamm");
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            record.setWierdField(66);
            mapper.insert(record);
            List<Integer> values = new ArrayList<>();
            values.add(11);
            values.add(22);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andWierdFieldLessThan(40).andWierdFieldIn(values);
            example.setOrderByClause("ID1, ID2");
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsSelectByExampleDateTimeFields() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2009);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 15);
        calendar.set(Calendar.HOUR, 10);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.SECOND, 10);
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setId1(1);
            record.setId2(1);
            record.setDatefield(calendar.getTime());
            record.setTimefield(calendar.getTime());
            mapper.insert(record);
            record = new Pkfields();
            record.setId1(1);
            record.setId2(2);
            calendar.set(Calendar.DAY_OF_MONTH, 16);
            calendar.set(Calendar.MINUTE, 11);
            record.setDatefield(calendar.getTime());
            record.setTimefield(calendar.getTime());
            mapper.insert(record);
            record = new Pkfields();
            record.setId1(1);
            record.setId2(3);
            calendar.set(Calendar.DAY_OF_MONTH, 17);
            calendar.set(Calendar.MINUTE, 12);
            record.setDatefield(calendar.getTime());
            record.setTimefield(calendar.getTime());
            mapper.insert(record);
            record = new Pkfields();
            record.setId1(2);
            record.setId2(1);
            calendar.set(Calendar.DAY_OF_MONTH, 18);
            calendar.set(Calendar.MINUTE, 13);
            record.setDatefield(calendar.getTime());
            record.setTimefield(calendar.getTime());
            mapper.insert(record);
            record = new Pkfields();
            record.setId1(2);
            record.setId2(2);
            calendar.set(Calendar.DAY_OF_MONTH, 19);
            calendar.set(Calendar.MINUTE, 14);
            record.setDatefield(calendar.getTime());
            record.setTimefield(calendar.getTime());
            mapper.insert(record);
            record = new Pkfields();
            record.setId1(2);
            record.setId2(3);
            calendar.set(Calendar.DAY_OF_MONTH, 20);
            calendar.set(Calendar.MINUTE, 15);
            record.setDatefield(calendar.getTime());
            record.setTimefield(calendar.getTime());
            mapper.insert(record);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andDatefieldEqualTo(calendar.getTime());
            example.setOrderByClause("ID1, ID2");
            List<Pkfields> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            example.clear();
            example.createCriteria().andDatefieldLessThan(calendar.getTime());
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(5, answer.size());
            calendar.set(Calendar.MINUTE, 12);
            example.clear();
            example.createCriteria().andTimefieldEqualTo(calendar.getTime());
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            example.clear();
            example.createCriteria().andTimefieldGreaterThan(calendar.getTime());
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsCountByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bob");
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andLastnameLike("J%");
            long rows = mapper.countByExample(example);
            Assertions.assertEquals(1, rows);
            example.clear();
            rows = mapper.countByExample(example);
            Assertions.assertEquals(2, rows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            PkblobsExample example = new PkblobsExample();
            List<Pkblobs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            Pkblobs returnedRecord = answer.get(0);
            Assertions.assertEquals(record.getId(), returnedRecord.getId());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsUpdateByPrimaryKeyWithBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            int rows = mapper.updateByPrimaryKeyWithBLOBs(record);
            Assertions.assertEquals(1, rows);
            Pkblobs newRecord = mapper.selectByPrimaryKey(3);
            Assertions.assertNotNull(newRecord);
            Assertions.assertEquals(record.getId(), newRecord.getId());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsUpdateByPrimaryKeySelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            Pkblobs newRecord = new Pkblobs();
            newRecord.setId(3);
            newRecord.setBlob2(generateRandomBlob());
            mapper.updateByPrimaryKeySelective(newRecord);
            Pkblobs returnedRecord = mapper.selectByPrimaryKey(3);
            Assertions.assertNotNull(returnedRecord);
            Assertions.assertEquals(record.getId(), returnedRecord.getId());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(newRecord.getBlob2(), returnedRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsDeleteByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            PkblobsExample example = new PkblobsExample();
            List<Pkblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            int rows = mapper.deleteByPrimaryKey(3);
            Assertions.assertEquals(1, rows);
            example = new PkblobsExample();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(0, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsDeleteByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            PkblobsExample example = new PkblobsExample();
            List<Pkblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            example = new PkblobsExample();
            example.createCriteria().andIdLessThan(4);
            int rows = mapper.deleteByExample(example);
            Assertions.assertEquals(1, rows);
            example = new PkblobsExample();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsSelectByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            Pkblobs newRecord = mapper.selectByPrimaryKey(6);
            Assertions.assertNotNull(newRecord);
            Assertions.assertEquals(record.getId(), newRecord.getId());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsSelectByExampleWithoutBlobs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdGreaterThan(4);
            List<Pkblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            Pkblobs key = answer.get(0);
            Assertions.assertEquals(6, key.getId().intValue());
            Assertions.assertNull(key.getBlob1());
            Assertions.assertNull(key.getBlob2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsSelectByExampleWithoutBlobsNoCriteria() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            PkblobsExample example = new PkblobsExample();
            example.createCriteria();
            List<Pkblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsSelectByExampleWithBlobs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdGreaterThan(4);
            List<Pkblobs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            Pkblobs newRecord = answer.get(0);
            Assertions.assertEquals(record.getId(), newRecord.getId());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsCountByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdLessThan(4);
            long rows = mapper.countByExample(example);
            Assertions.assertEquals(1, rows);
            example.clear();
            rows = mapper.countByExample(example);
            Assertions.assertEquals(2, rows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            List<Pkfieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            Pkfieldsblobs returnedRecord = answer.get(0);
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), returnedRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByPrimaryKeyWithBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            Pkfieldsblobs updateRecord = new Pkfieldsblobs();
            updateRecord.setId1(3);
            updateRecord.setId2(4);
            updateRecord.setFirstname("Scott");
            updateRecord.setLastname("Jones");
            updateRecord.setBlob1(generateRandomBlob());
            int rows = mapper.updateByPrimaryKeyWithBLOBs(updateRecord);
            Assertions.assertEquals(1, rows);
            PkfieldsblobsKey key = new PkfieldsblobsKey();
            key.setId1(3);
            key.setId2(4);
            Pkfieldsblobs newRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertEquals(updateRecord.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(updateRecord.getLastname(), newRecord.getLastname());
            Assertions.assertEquals(record.getId1(), newRecord.getId1());
            Assertions.assertEquals(record.getId2(), newRecord.getId2());
            Assertions.assertTrue(blobsAreEqual(updateRecord.getBlob1(), newRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByPrimaryKeyWithoutBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            Pkfieldsblobs updateRecord = new Pkfieldsblobs();
            updateRecord.setId1(3);
            updateRecord.setId2(4);
            updateRecord.setFirstname("Scott");
            updateRecord.setLastname("Jones");
            int rows = mapper.updateByPrimaryKey(updateRecord);
            Assertions.assertEquals(1, rows);
            PkfieldsblobsKey key = new PkfieldsblobsKey();
            key.setId1(3);
            key.setId2(4);
            Pkfieldsblobs newRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertEquals(updateRecord.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(updateRecord.getLastname(), newRecord.getLastname());
            Assertions.assertEquals(record.getId1(), newRecord.getId1());
            Assertions.assertEquals(record.getId2(), newRecord.getId2());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByPrimaryKeySelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            Pkfieldsblobs updateRecord = new Pkfieldsblobs();
            updateRecord.setId1(3);
            updateRecord.setId2(4);
            updateRecord.setLastname("Jones");
            int rows = mapper.updateByPrimaryKeySelective(updateRecord);
            Assertions.assertEquals(1, rows);
            PkfieldsblobsKey key = new PkfieldsblobsKey();
            key.setId1(3);
            key.setId2(4);
            Pkfieldsblobs returnedRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(updateRecord.getLastname(), returnedRecord.getLastname());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsDeleteByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            List<Pkfieldsblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            PkfieldsblobsKey key = new PkfieldsblobsKey();
            key.setId1(5);
            key.setId2(6);
            int rows = mapper.deleteByPrimaryKey(key);
            Assertions.assertEquals(1, rows);
            example = new PkfieldsblobsExample();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsDeleteByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            List<Pkfieldsblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            example = new PkfieldsblobsExample();
            example.createCriteria().andId1NotEqualTo(3);
            int rows = mapper.deleteByExample(example);
            Assertions.assertEquals(1, rows);
            example = new PkfieldsblobsExample();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsSelectByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            List<Pkfieldsblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            PkfieldsblobsKey key = new PkfieldsblobsKey();
            key.setId1(5);
            key.setId2(6);
            Pkfieldsblobs newRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertEquals(record.getId1(), newRecord.getId1());
            Assertions.assertEquals(record.getId2(), newRecord.getId2());
            Assertions.assertEquals(record.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), newRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsSelectByExampleWithoutBlobs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId2EqualTo(6);
            List<Pkfieldsblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            Pkfieldsblobs newRecord = answer.get(0);
            Assertions.assertEquals(record.getId1(), newRecord.getId1());
            Assertions.assertEquals(record.getId2(), newRecord.getId2());
            Assertions.assertEquals(record.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), newRecord.getLastname());
            Assertions.assertNull(newRecord.getBlob1());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsSelectByExampleWithBlobs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId2EqualTo(6);
            example.setOrderByClause("ID1");// test for Issue 174

            List<Pkfieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            Pkfieldsblobs newRecord = answer.get(0);
            Assertions.assertEquals(record.getId1(), newRecord.getId1());
            Assertions.assertEquals(record.getId2(), newRecord.getId2());
            Assertions.assertEquals(record.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), newRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsSelectByExampleWithBlobsNoCriteria() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria();
            List<Pkfieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(2, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsCountByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId1NotEqualTo(3);
            long rows = mapper.countByExample(example);
            Assertions.assertEquals(1, rows);
            example.clear();
            rows = mapper.countByExample(example);
            Assertions.assertEquals(2, rows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs();
            record.setFirstname("Bob");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameEqualTo("Bob");
            List<FieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            FieldsblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), returnedRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsDeleteByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            FieldsblobsExample example = new FieldsblobsExample();
            List<Fieldsblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            int rows = mapper.deleteByExample(example);
            Assertions.assertEquals(1, rows);
            example = new FieldsblobsExample();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsSelectByExampleWithoutBlobs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            List<Fieldsblobs> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            Fieldsblobs newRecord = answer.get(0);
            Assertions.assertFalse((newRecord instanceof FieldsblobsWithBLOBs));
            Assertions.assertEquals(record.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), newRecord.getLastname());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsSelectByExampleWithBlobs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            List<FieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            FieldsblobsWithBLOBs newRecord = answer.get(0);
            Assertions.assertEquals(record.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), newRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), newRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), newRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsSelectByExampleWithBlobsNoCriteria() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria();
            List<FieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(2, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsCountByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            long rows = mapper.countByExample(example);
            Assertions.assertEquals(1, rows);
            example.clear();
            rows = mapper.countByExample(example);
            Assertions.assertEquals(2, rows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            int rows = mapper.insert(record);
            Assertions.assertEquals(1, rows);
            Integer generatedCustomerId = record.getCustomerId();
            Assertions.assertEquals(57, generatedCustomerId.intValue());
            AwfulTable returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId);
            Assertions.assertEquals(generatedCustomerId, returnedRecord.getCustomerId());
            Assertions.assertEquals(record.geteMail(), returnedRecord.geteMail());
            Assertions.assertEquals(record.getEmailaddress(), returnedRecord.getEmailaddress());
            Assertions.assertEquals(record.getFirstFirstName(), returnedRecord.getFirstFirstName());
            Assertions.assertEquals(record.getFrom(), returnedRecord.getFrom());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getId5(), returnedRecord.getId5());
            Assertions.assertEquals(record.getId6(), returnedRecord.getId6());
            Assertions.assertEquals(record.getId7(), returnedRecord.getId7());
            Assertions.assertEquals(record.getSecondFirstName(), returnedRecord.getSecondFirstName());
            Assertions.assertEquals(record.getThirdFirstName(), returnedRecord.getThirdFirstName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableInsertSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            int rows = mapper.insertSelective(record);
            Assertions.assertEquals(1, rows);
            Integer generatedCustomerId = record.getCustomerId();
            Assertions.assertEquals(57, generatedCustomerId.intValue());
            AwfulTable returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId);
            Assertions.assertEquals(generatedCustomerId, returnedRecord.getCustomerId());
            Assertions.assertEquals(record.geteMail(), returnedRecord.geteMail());
            Assertions.assertEquals(record.getEmailaddress(), returnedRecord.getEmailaddress());
            Assertions.assertEquals("Mabel", returnedRecord.getFirstFirstName());
            Assertions.assertEquals(record.getFrom(), returnedRecord.getFrom());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getId5(), returnedRecord.getId5());
            Assertions.assertEquals(record.getId6(), returnedRecord.getId6());
            Assertions.assertEquals(record.getId7(), returnedRecord.getId7());
            Assertions.assertEquals(record.getSecondFirstName(), returnedRecord.getSecondFirstName());
            Assertions.assertEquals(record.getThirdFirstName(), returnedRecord.getThirdFirstName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableUpdateByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            int rows = mapper.insert(record);
            Assertions.assertEquals(1, rows);
            Integer generatedCustomerId = record.getCustomerId();
            Assertions.assertEquals(57, generatedCustomerId.intValue());
            record.setId1(11);
            record.setId2(22);
            rows = mapper.updateByPrimaryKey(record);
            Assertions.assertEquals(1, rows);
            AwfulTable returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId);
            Assertions.assertEquals(generatedCustomerId, returnedRecord.getCustomerId());
            Assertions.assertEquals(record.geteMail(), returnedRecord.geteMail());
            Assertions.assertEquals(record.getEmailaddress(), returnedRecord.getEmailaddress());
            Assertions.assertEquals(record.getFirstFirstName(), returnedRecord.getFirstFirstName());
            Assertions.assertEquals(record.getFrom(), returnedRecord.getFrom());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getId5(), returnedRecord.getId5());
            Assertions.assertEquals(record.getId6(), returnedRecord.getId6());
            Assertions.assertEquals(record.getId7(), returnedRecord.getId7());
            Assertions.assertEquals(record.getSecondFirstName(), returnedRecord.getSecondFirstName());
            Assertions.assertEquals(record.getThirdFirstName(), returnedRecord.getThirdFirstName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableUpdateByPrimaryKeySelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            int rows = mapper.insert(record);
            Assertions.assertEquals(1, rows);
            Integer generatedCustomerId = record.getCustomerId();
            Assertions.assertEquals(57, generatedCustomerId.intValue());
            AwfulTable newRecord = new AwfulTable();
            newRecord.setCustomerId(generatedCustomerId);
            newRecord.setId1(11);
            newRecord.setId2(22);
            rows = mapper.updateByPrimaryKeySelective(newRecord);
            Assertions.assertEquals(1, rows);
            AwfulTable returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId);
            Assertions.assertEquals(generatedCustomerId, returnedRecord.getCustomerId());
            Assertions.assertEquals(record.geteMail(), returnedRecord.geteMail());
            Assertions.assertEquals(record.getEmailaddress(), returnedRecord.getEmailaddress());
            Assertions.assertEquals(record.getFirstFirstName(), returnedRecord.getFirstFirstName());
            Assertions.assertEquals(record.getFrom(), returnedRecord.getFrom());
            Assertions.assertEquals(newRecord.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(newRecord.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getId5(), returnedRecord.getId5());
            Assertions.assertEquals(record.getId6(), returnedRecord.getId6());
            Assertions.assertEquals(record.getId7(), returnedRecord.getId7());
            Assertions.assertEquals(record.getSecondFirstName(), returnedRecord.getSecondFirstName());
            Assertions.assertEquals(record.getThirdFirstName(), returnedRecord.getThirdFirstName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableDeleteByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            int rows = mapper.insert(record);
            Assertions.assertEquals(1, rows);
            Integer generatedCustomerId = record.getCustomerId();
            Assertions.assertEquals(57, generatedCustomerId.intValue());
            rows = mapper.deleteByPrimaryKey(generatedCustomerId);
            Assertions.assertEquals(1, rows);
            AwfulTableExample example = new AwfulTableExample();
            List<AwfulTable> answer = mapper.selectByExample(example);
            Assertions.assertEquals(0, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableDeleteByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("fred2@fred.com");
            record.setEmailaddress("alsofred2@fred.com");
            record.setFirstFirstName("fred11");
            record.setFrom("from from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("fred22");
            record.setThirdFirstName("fred33");
            mapper.insert(record);
            AwfulTableExample example = new AwfulTableExample();
            List<AwfulTable> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            example = new AwfulTableExample();
            example.createCriteria().andEMailLike("fred@%");
            int rows = mapper.deleteByExample(example);
            Assertions.assertEquals(1, rows);
            example.clear();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableSelectByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("fred2@fred.com");
            record.setEmailaddress("alsofred2@fred.com");
            record.setFirstFirstName("fred11");
            record.setFrom("from from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("fred22");
            record.setThirdFirstName("fred33");
            int rows = mapper.insert(record);
            Assertions.assertEquals(1, rows);
            Integer generatedKey = record.getCustomerId();
            Assertions.assertEquals(58, generatedKey.intValue());
            AwfulTable returnedRecord = mapper.selectByPrimaryKey(generatedKey);
            Assertions.assertNotNull(returnedRecord);
            Assertions.assertEquals(record.getCustomerId(), returnedRecord.getCustomerId());
            Assertions.assertEquals(record.geteMail(), returnedRecord.geteMail());
            Assertions.assertEquals(record.getEmailaddress(), returnedRecord.getEmailaddress());
            Assertions.assertEquals(record.getFirstFirstName(), returnedRecord.getFirstFirstName());
            Assertions.assertEquals(record.getFrom(), returnedRecord.getFrom());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getId5(), returnedRecord.getId5());
            Assertions.assertEquals(record.getId6(), returnedRecord.getId6());
            Assertions.assertEquals(record.getId7(), returnedRecord.getId7());
            Assertions.assertEquals(record.getSecondFirstName(), returnedRecord.getSecondFirstName());
            Assertions.assertEquals(record.getThirdFirstName(), returnedRecord.getThirdFirstName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableSelectByExampleLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);
            AwfulTableExample example = new AwfulTableExample();
            example.createCriteria().andFirstFirstNameLike("b%");
            example.setOrderByClause("\"A_CuStOmEr iD\"");
            List<AwfulTable> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
            AwfulTable returnedRecord = answer.get(0);
            Assertions.assertEquals(1111, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2222, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(11111, returnedRecord.getId1().intValue());
            Assertions.assertEquals(22222, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(111111, returnedRecord.getId1().intValue());
            Assertions.assertEquals(222222, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableSelectByExampleNotLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);
            AwfulTableExample example = new AwfulTableExample();
            example.createCriteria().andFirstFirstNameNotLike("b%");
            example.setOrderByClause("\"A_CuStOmEr iD\"");
            List<AwfulTable> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
            AwfulTable returnedRecord = answer.get(0);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(11, returnedRecord.getId1().intValue());
            Assertions.assertEquals(22, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(111, returnedRecord.getId1().intValue());
            Assertions.assertEquals(222, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableSelectByExampleComplexLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);
            AwfulTableExample example = new AwfulTableExample();
            example.or().andFirstFirstNameLike("b%").andId2EqualTo(222222);
            example.or().andFirstFirstNameLike("wi%");
            example.setOrderByClause("\"A_CuStOmEr iD\"");
            List<AwfulTable> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            AwfulTable returnedRecord = answer.get(0);
            Assertions.assertEquals(11, returnedRecord.getId1().intValue());
            Assertions.assertEquals(22, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(111111, returnedRecord.getId1().intValue());
            Assertions.assertEquals(222222, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableSelectByExampleIn() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);
            List<Integer> ids = new ArrayList<>();
            ids.add(1);
            ids.add(11);
            AwfulTableExample example = new AwfulTableExample();
            example.createCriteria().andId1In(ids);
            example.setOrderByClause("\"A_CuStOmEr iD\"");
            List<AwfulTable> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            AwfulTable returnedRecord = answer.get(0);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(11, returnedRecord.getId1().intValue());
            Assertions.assertEquals(22, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableSelectByExampleBetween() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);
            AwfulTableExample example = new AwfulTableExample();
            example.createCriteria().andId1Between(1, 1000);
            List<AwfulTable> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableSelectByExampleNoCriteria() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);
            AwfulTableExample example = new AwfulTableExample();
            example.createCriteria();
            example.setOrderByClause("\"A_CuStOmEr iD\" desc");
            List<AwfulTable> answer = mapper.selectByExample(example);
            Assertions.assertEquals(6, answer.size());
            AwfulTable returnedRecord = answer.get(0);
            Assertions.assertEquals(111111, returnedRecord.getId1().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(11111, returnedRecord.getId1().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(1111, returnedRecord.getId1().intValue());
            returnedRecord = answer.get(3);
            Assertions.assertEquals(111, returnedRecord.getId1().intValue());
            returnedRecord = answer.get(4);
            Assertions.assertEquals(11, returnedRecord.getId1().intValue());
            returnedRecord = answer.get(5);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableCountByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("fred2@fred.com");
            record.setEmailaddress("alsofred2@fred.com");
            record.setFirstFirstName("fred11");
            record.setFrom("from from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("fred22");
            record.setThirdFirstName("fred33");
            mapper.insert(record);
            AwfulTableExample example = new AwfulTableExample();
            example.createCriteria().andEMailLike("fred@%");
            long rows = mapper.countByExample(example);
            Assertions.assertEquals(1, rows);
            example.clear();
            rows = mapper.countByExample(example);
            Assertions.assertEquals(2, rows);
        } finally {
            sqlSession.close();
        }
    }
}
