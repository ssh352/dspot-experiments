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
package uk.gov.gchq.gaffer.data.element.function;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.koryphe.function.FunctionTest;


public class ExtractPropertyTest extends FunctionTest {
    @Test
    public void shouldReturnNullForNullElement() {
        // Given
        final ExtractProperty extractor = new ExtractProperty();
        // When
        final Object result = extractor.apply(null);
        // Then
        Assert.assertNull(result);
    }

    @Test
    public void shouldReturnNullWithNoNameProvided() {
        // Given
        final Element element = Mockito.mock(Element.class);
        final ExtractProperty extractor = new ExtractProperty();
        // When
        final Object result = extractor.apply(element);
        // Then
        Assert.assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenNameNotFoundInElementProperties() {
        // Given
        final Element element = Mockito.mock(Element.class);
        final String propName = "absentProperty";
        final ExtractProperty extractor = new ExtractProperty(propName);
        // When
        final Object result = extractor.apply(element);
        // Then
        Assert.assertNull(result);
    }

    @Test
    public void shouldReturnValueOfProperty() {
        // Given
        final Element element = Mockito.mock(Element.class);
        final String propName = "presentProperty";
        final int propValue = 3;
        final ExtractProperty extractor = new ExtractProperty(propName);
        BDDMockito.given(element.getProperty(propName)).willReturn(propValue);
        // When
        final Object result = extractor.apply(element);
        // Then
        Assert.assertEquals(propValue, result);
    }
}

