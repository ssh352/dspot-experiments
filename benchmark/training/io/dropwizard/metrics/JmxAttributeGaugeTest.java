/**
 * Copyright 2010-2013 Coda Hale and Yammer, Inc., 2014-2017 Dropwizard Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dropwizard.metrics;


import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Test;


public class JmxAttributeGaugeTest {
    private static final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    private static final List<ObjectName> registeredMBeans = new ArrayList<>();

    public interface JmxTestMBean {
        Long getValue();
    }

    private static class JmxTest implements JmxAttributeGaugeTest.JmxTestMBean {
        @Override
        public Long getValue() {
            return Long.MAX_VALUE;
        }
    }

    @Test
    public void returnsJmxAttribute() throws Exception {
        ObjectName objectName = new ObjectName("java.lang:type=ClassLoading");
        JmxAttributeGauge gauge = new JmxAttributeGauge(JmxAttributeGaugeTest.mBeanServer, objectName, "LoadedClassCount");
        assertThat(gauge.getValue()).isInstanceOf(Integer.class);
        assertThat(((Integer) (gauge.getValue()))).isGreaterThan(0);
    }

    @Test
    public void returnsNullIfAttributeDoesNotExist() throws Exception {
        ObjectName objectName = new ObjectName("java.lang:type=ClassLoading");
        JmxAttributeGauge gauge = new JmxAttributeGauge(JmxAttributeGaugeTest.mBeanServer, objectName, "DoesNotExist");
        assertThat(gauge.getValue()).isNull();
    }

    @Test
    public void returnsNullIfMBeanNotFound() throws Exception {
        ObjectName objectName = new ObjectName("foo.bar:type=NoSuchMBean");
        JmxAttributeGauge gauge = new JmxAttributeGauge(JmxAttributeGaugeTest.mBeanServer, objectName, "LoadedClassCount");
        assertThat(gauge.getValue()).isNull();
    }

    @Test
    public void returnsAttributeForObjectNamePattern() throws Exception {
        ObjectName objectName = new ObjectName("JmxAttributeGaugeTest:name=test1,*");
        JmxAttributeGauge gauge = new JmxAttributeGauge(JmxAttributeGaugeTest.mBeanServer, objectName, "Value");
        assertThat(gauge.getValue()).isInstanceOf(Long.class);
        assertThat(((Long) (gauge.getValue()))).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void returnsNullIfObjectNamePatternAmbiguous() throws Exception {
        ObjectName objectName = new ObjectName("JmxAttributeGaugeTest:type=test,*");
        JmxAttributeGauge gauge = new JmxAttributeGauge(JmxAttributeGaugeTest.mBeanServer, objectName, "Value");
        assertThat(gauge.getValue()).isNull();
    }
}

