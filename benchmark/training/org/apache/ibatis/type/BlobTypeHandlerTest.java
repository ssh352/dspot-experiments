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


import java.io.InputStream;
import java.sql.Blob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


class BlobTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<byte[]> TYPE_HANDLER = new BlobTypeHandler();

    @Mock
    protected Blob blob;

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        BlobTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, new byte[]{ 1, 2, 3 }, null);
        Mockito.verify(ps).setBinaryStream(Mockito.eq(1), Mockito.any(InputStream.class), Mockito.eq(3));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getBlob("column")).thenReturn(blob);
        Mockito.when(blob.length()).thenReturn(3L);
        Mockito.when(blob.getBytes(1, 3)).thenReturn(new byte[]{ 1, 2, 3 });
        Assertions.assertArrayEquals(new byte[]{ 1, 2, 3 }, BlobTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getBlob("column")).thenReturn(null);
        Assertions.assertNull(BlobTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getBlob(1)).thenReturn(blob);
        Mockito.when(blob.length()).thenReturn(3L);
        Mockito.when(blob.getBytes(1, 3)).thenReturn(new byte[]{ 1, 2, 3 });
        Assertions.assertArrayEquals(new byte[]{ 1, 2, 3 }, BlobTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getBlob(1)).thenReturn(null);
        Assertions.assertNull(BlobTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getBlob(1)).thenReturn(blob);
        Mockito.when(blob.length()).thenReturn(3L);
        Mockito.when(blob.getBytes(1, 3)).thenReturn(new byte[]{ 1, 2, 3 });
        Assertions.assertArrayEquals(new byte[]{ 1, 2, 3 }, BlobTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getBlob(1)).thenReturn(null);
        Assertions.assertNull(BlobTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

