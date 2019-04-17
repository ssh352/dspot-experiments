/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.dataformat;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.impl.MySerialBean;
import org.junit.Assert;
import org.junit.Test;


public class DataFormatEndpointSerializationTest extends ContextTestSupport {
    @Test
    public void testSerialization() throws Exception {
        MySerialBean bean = new MySerialBean();
        bean.setId(123);
        bean.setName("Donald");
        Object data = template.requestBody("direct:marshal", bean);
        Assert.assertNotNull(data);
        Object out = template.requestBody("direct:unmarshal", data);
        Assert.assertNotNull(out);
        MySerialBean outBean = context.getTypeConverter().convertTo(MySerialBean.class, out);
        Assert.assertNotNull(outBean);
        Assert.assertEquals(123, outBean.getId());
        Assert.assertEquals("Donald", outBean.getName());
    }
}
