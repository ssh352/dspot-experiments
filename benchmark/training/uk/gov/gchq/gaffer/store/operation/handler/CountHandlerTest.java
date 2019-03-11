/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store.operation.handler;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.Count;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;


public class CountHandlerTest {
    @Test
    public void shouldReturnCount() throws OperationException {
        // Given
        final CountHandler handler = new CountHandler();
        final Store store = Mockito.mock(Store.class);
        final Count count = Mockito.mock(Count.class);
        final CloseableIterable<Element> elements = CountGroupsHandlerTest.getElements();
        final Context context = new Context();
        BDDMockito.given(count.getInput()).willReturn(elements);
        // When
        final Long result = handler.doOperation(count, context, store);
        // Then
        Assert.assertEquals(8L, ((long) (result)));
    }
}

