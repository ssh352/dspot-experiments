/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Scannable.Attr.DELAY_ERROR;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;


public class DelegateProcessorTest {
    @Test
    public void scanReturnsDownStreamForParentElseDelegates() {
        Publisher<?> downstream = Mockito.mock(FluxOperator.class);
        IllegalStateException boom = new IllegalStateException("boom");
        InnerConsumer<?> upstream = Mockito.mock(InnerConsumer.class);
        Mockito.when(upstream.scanUnsafe(ERROR)).thenReturn(boom);
        Mockito.when(upstream.scanUnsafe(DELAY_ERROR)).thenReturn(true);
        DelegateProcessor<?, ?> processor = new DelegateProcessor(downstream, upstream);
        assertThat(processor.scan(PARENT)).isSameAs(downstream);
        assertThat(processor.scan(ERROR)).isSameAs(boom);
        assertThat(processor.scan(DELAY_ERROR)).isTrue();
    }
}
