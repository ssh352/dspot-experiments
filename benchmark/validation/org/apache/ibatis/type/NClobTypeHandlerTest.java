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


import java.io.Reader;
import java.sql.Clob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


class NClobTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<String> TYPE_HANDLER = new NClobTypeHandler();

    @Mock
    protected Clob clob;

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        NClobTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, "Hello", null);
        Mockito.verify(ps).setCharacterStream(Mockito.eq(1), Mockito.any(Reader.class), Mockito.eq(5));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getClob("column")).thenReturn(clob);
        Mockito.when(clob.length()).thenReturn(3L);
        Mockito.when(clob.getSubString(1, 3)).thenReturn("Hello");
        Assertions.assertEquals("Hello", NClobTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getClob("column")).thenReturn(null);
        Assertions.assertNull(NClobTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getClob(1)).thenReturn(clob);
        Mockito.when(clob.length()).thenReturn(3L);
        Mockito.when(clob.getSubString(1, 3)).thenReturn("Hello");
        Assertions.assertEquals("Hello", NClobTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getClob(1)).thenReturn(null);
        Assertions.assertNull(NClobTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getClob(1)).thenReturn(clob);
        Mockito.when(clob.length()).thenReturn(3L);
        Mockito.when(clob.getSubString(1, 3)).thenReturn("Hello");
        Assertions.assertEquals("Hello", NClobTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getClob(1)).thenReturn(null);
        Assertions.assertNull(NClobTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

