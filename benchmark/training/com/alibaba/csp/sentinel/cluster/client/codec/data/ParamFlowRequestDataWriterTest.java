package com.alibaba.csp.sentinel.cluster.client.codec.data;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Eric Zhao
 */
public class ParamFlowRequestDataWriterTest {
    @Test
    public void testCalculateParamTransportSize() {
        ParamFlowRequestDataWriter writer = new ParamFlowRequestDataWriter();
        // POJO (non-primitive type) should not be regarded as a valid parameter.
        Assert.assertEquals(0, writer.calculateParamTransportSize(new ParamFlowRequestDataWriterTest.SomePojo().setParam1("abc")));
        Assert.assertEquals((4 + 1), writer.calculateParamTransportSize(1));
        Assert.assertEquals((1 + 1), writer.calculateParamTransportSize(((byte) (1))));
        Assert.assertEquals((1 + 1), writer.calculateParamTransportSize(false));
        Assert.assertEquals((8 + 1), writer.calculateParamTransportSize(2L));
        Assert.assertEquals((8 + 1), writer.calculateParamTransportSize(4.0));
        final String paramStr = "Sentinel";
        Assert.assertEquals(((1 + 4) + (paramStr.getBytes().length)), writer.calculateParamTransportSize(paramStr));
    }

    @Test
    public void testCalculateParamAmountExceedsMaxSize() {
        final int maxSize = 10;
        ParamFlowRequestDataWriter writer = new ParamFlowRequestDataWriter(maxSize);
        Assert.assertEquals(1, writer.calculateParamAmount(new ArrayList<Object>() {
            {
                add(1);
            }
        }));
        Assert.assertEquals(2, writer.calculateParamAmount(new ArrayList<Object>() {
            {
                add(1);
                add(64);
            }
        }));
        Assert.assertEquals(2, writer.calculateParamAmount(new ArrayList<Object>() {
            {
                add(1);
                add(64);
                add(3);
            }
        }));
    }

    @Test
    public void testCalculateParamAmount() {
        ParamFlowRequestDataWriter writer = new ParamFlowRequestDataWriter();
        Assert.assertEquals(6, writer.calculateParamAmount(new ArrayList<Object>() {
            {
                add(1);
                add(1.0);
                add(1.0F);
                add(((byte) (1)));
                add("123");
                add(true);
            }
        }));
        // POJO (non-primitive type) should not be regarded as a valid parameter.
        Assert.assertEquals(0, writer.calculateParamAmount(new ArrayList<Object>() {
            {
                add(new ParamFlowRequestDataWriterTest.SomePojo());
            }
        }));
        Assert.assertEquals(1, writer.calculateParamAmount(new ArrayList<Object>() {
            {
                add(new Object());
                add(1);
            }
        }));
    }

    private static class SomePojo {
        private String param1;

        public String getParam1() {
            return param1;
        }

        public ParamFlowRequestDataWriterTest.SomePojo setParam1(String param1) {
            this.param1 = param1;
            return this;
        }

        @Override
        public String toString() {
            return ((("SomePojo{" + "param1='") + (param1)) + '\'') + '}';
        }
    }
}

