/**
 * Copyright 2009-2019 the original author or authors.
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
package org.apache.ibatis.type;


import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class BigIntegerTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<BigInteger> TYPE_HANDLER = new BigIntegerTypeHandler();

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        BigIntegerTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, new BigInteger("707070656505050302797979792923232303"), null);
        Mockito.verify(ps).setBigDecimal(1, new BigDecimal("707070656505050302797979792923232303"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getBigDecimal("column")).thenReturn(new BigDecimal("707070656505050302797979792923232303"));
        Assertions.assertEquals(new BigInteger("707070656505050302797979792923232303"), BigIntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getBigDecimal("column")).thenReturn(null);
        Assertions.assertNull(BigIntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getBigDecimal(1)).thenReturn(new BigDecimal("707070656505050302797979792923232303"));
        Assertions.assertEquals(new BigInteger("707070656505050302797979792923232303"), BigIntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getBigDecimal(1)).thenReturn(null);
        Assertions.assertNull(BigIntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getBigDecimal(1)).thenReturn(new BigDecimal("707070656505050302797979792923232303"));
        Assertions.assertEquals(new BigInteger("707070656505050302797979792923232303"), BigIntegerTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getBigDecimal(1)).thenReturn(null);
        Assertions.assertNull(BigIntegerTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }
}

