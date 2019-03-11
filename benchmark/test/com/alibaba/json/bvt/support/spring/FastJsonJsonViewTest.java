package com.alibaba.json.bvt.support.spring;


import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class FastJsonJsonViewTest extends TestCase {
    public void test_1() throws Exception {
        FastJsonJsonView view = new FastJsonJsonView();
        Assert.assertNotNull(view.getFastJsonConfig());
        view.setFastJsonConfig(new FastJsonConfig());
        Map<String, Object> model = new HashMap<String, Object>();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        view.render(model, request, response);
        view.setRenderedAttributes(null);
        view.render(model, request, response);
        view.setUpdateContentLength(true);
        view.render(model, request, response);
        view.setExtractValueFromSingleKeyModel(true);
        Assert.assertEquals(true, view.isExtractValueFromSingleKeyModel());
        view.setDisableCaching(true);
        view.render(Collections.singletonMap("abc", "cde"), request, response);
    }

    @Test
    public void test_jsonp() throws Exception {
        FastJsonJsonView view = new FastJsonJsonView();
        Assert.assertNotNull(view.getFastJsonConfig());
        view.setFastJsonConfig(new FastJsonConfig());
        view.setExtractValueFromSingleKeyModel(true);
        view.setDisableCaching(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("callback", "queryName");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Assert.assertEquals(true, view.isExtractValueFromSingleKeyModel());
        view.render(Collections.singletonMap("abc", "cde??"), request, response);
        String contentAsString = response.getContentAsString();
        int contentLength = response.getContentLength();
        Assert.assertEquals(contentLength, contentAsString.getBytes(view.getFastJsonConfig().getCharset().name()).length);
    }

    @Test
    public void test_jsonp_invalidParam() throws Exception {
        FastJsonJsonView view = new FastJsonJsonView();
        Assert.assertNotNull(view.getFastJsonConfig());
        view.setFastJsonConfig(new FastJsonConfig());
        view.setExtractValueFromSingleKeyModel(true);
        view.setDisableCaching(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("callback", "-methodName");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Assert.assertEquals(true, view.isExtractValueFromSingleKeyModel());
        view.render(Collections.singletonMap("doesn't matter", Collections.singletonMap("abc", "cde??")), request, response);
        String contentAsString = response.getContentAsString();
        Assert.assertTrue(contentAsString.startsWith("{\"abc\":\"cde\u4e2d\u6587\"}"));
    }

    private SerializeFilter serializeFilter = new ValueFilter() {
        public Object process(Object object, String name, Object value) {
            if (value == null) {
                return "";
            }
            if (value instanceof Number) {
                return String.valueOf(value);
            }
            return value;
        }
    };
}

