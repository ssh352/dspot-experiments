/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import androidx.annotation.Nullable;
import com.facebook.litho.testing.assertj.LithoAssertions;
import com.facebook.litho.testing.logging.TestComponentsLogger;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;


@RunWith(ComponentsTestRunner.class)
public class LogTreePopulatorTest {
    private ComponentContext mContext;

    @Test
    public void testCustomTreePropLogger() {
        final BaseComponentsLogger logger = new TestComponentsLogger() {
            @Nullable
            @Override
            public Map<String, String> getExtraAnnotations(TreeProps treeProps) {
                final Object o = treeProps.get(LogTreePopulatorTest.MyKey.class);
                final Map<String, String> map = new HashMap<>(1);
                map.put("my_key", String.valueOf(((int) (o))));
                return map;
            }
        };
        final PerfEvent event = Mockito.mock(PerfEvent.class);
        final TreeProps treeProps = new TreeProps();
        treeProps.put(LogTreePopulatorTest.MyKey.class, 1337);
        mContext.setTreeProps(treeProps);
        LogTreePopulator.populatePerfEventFromLogger(mContext, logger, event);
        Mockito.verify(event).markerAnnotate("my_key", "1337");
    }

    @Test
    public void testSkipOnEmptyTag() {
        final TestComponentsLogger logger = new TestComponentsLogger() {
            @Nullable
            @Override
            public Map<String, String> getExtraAnnotations(TreeProps treeProps) {
                final Object o = treeProps.get(LogTreePopulatorTest.MyKey.class);
                final Map<String, String> map = new HashMap<>(1);
                map.put("my_key", String.valueOf(((int) (o))));
                return map;
            }
        };
        final PerfEvent event = Mockito.mock(PerfEvent.class);
        final TreeProps treeProps = new TreeProps();
        treeProps.put(LogTreePopulatorTest.MyKey.class, 1337);
        mContext.setTreeProps(treeProps);
        final ComponentContext noLogTagContext = new ComponentContext(RuntimeEnvironment.application);
        final PerfEvent perfEvent = LogTreePopulator.populatePerfEventFromLogger(noLogTagContext, logger, event);
        LithoAssertions.assertThat(perfEvent).isNull();
        LithoAssertions.assertThat(logger.getCanceledPerfEvents()).containsExactly(event);
        Mockito.verifyNoMoreInteractions(event);
    }

    @Test
    public void testNullTreePropLogger() {
        final BaseComponentsLogger logger = new TestComponentsLogger() {
            @Nullable
            @Override
            public Map<String, String> getExtraAnnotations(TreeProps treeProps) {
                return null;
            }
        };
        final PerfEvent event = Mockito.mock(PerfEvent.class);
        final TreeProps treeProps = new TreeProps();
        treeProps.put(LogTreePopulatorTest.MyKey.class, 1337);
        mContext.setTreeProps(treeProps);
        LogTreePopulator.populatePerfEventFromLogger(mContext, logger, event);
        Mockito.verify(event).markerAnnotate("log_tag", "test");
        Mockito.verifyNoMoreInteractions(event);
    }

    @Test
    public void testGetAnnotationBundleFromLogger() {
        final BaseComponentsLogger logger = new TestComponentsLogger() {
            @Nullable
            @Override
            public Map<String, String> getExtraAnnotations(TreeProps treeProps) {
                final Object o = treeProps.get(LogTreePopulatorTest.MyKey.class);
                final Map<String, String> map = new LinkedHashMap<>(2);
                map.put("my_key", String.valueOf(((int) (o))));
                map.put("other_key", "value");
                return map;
            }
        };
        final TreeProps treeProps = new TreeProps();
        final Component component = Mockito.mock(Component.class);
        Mockito.when(component.getScopedContext()).thenReturn(mContext);
        treeProps.put(LogTreePopulatorTest.MyKey.class, 1337);
        mContext.setTreeProps(treeProps);
        final String res = LogTreePopulator.getAnnotationBundleFromLogger(component, logger);
        LithoAssertions.assertThat(res).isEqualTo("my_key:1337:other_key:value:");
    }

    private static class MyKey {}
}
