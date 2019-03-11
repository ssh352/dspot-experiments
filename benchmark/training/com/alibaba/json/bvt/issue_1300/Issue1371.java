package com.alibaba.json.bvt.issue_1300;


import SerializerFeature.WriteNonStringKeyAsString;
import com.alibaba.fastjson.JSON;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by wenshao on 05/08/2017.
 */
// public void testParsed(){
// 
// String oldStyleJson = "{1:'abc', 2:'cde'}";
// 
// Gson gson = new Gson();
// 
// Map fromJson = gson.fromJson(oldStyleJson, Map.class);
// 
// Assert.assertNull(fromJson.get(1));
// 
// Assert.assertEquals(fromJson.get("1"), "abc" );
// 
// Map parsed = JSON.parseObject(oldStyleJson, Map.class, Feature.IgnoreAutoType, Feature.DisableFieldSmartMatch);
// 
// 
// Assert.assertNull(parsed.get(1));
// 
// Assert.assertEquals(parsed.get("1"), "abc" );
// 
// }
// 
// public void testParsed_jackson() throws Exception {
// 
// String oldStyleJson = "{1:\"abc\", 2:\"cde\"}";
// 
// ObjectMapper objectMapper = new ObjectMapper();
// Map fromJson = objectMapper.readValue(oldStyleJson, Map.class);
// Assert.assertNull(fromJson.get(1));
// }
public class Issue1371 extends TestCase {
    private enum Rooms {

        A,
        B,
        C,
        D,
        E;}

    public void testFastjsonEnum() {
        Map<Issue1371.Rooms, Issue1371.Rooms> enumMap = new TreeMap<Issue1371.Rooms, Issue1371.Rooms>();
        enumMap.put(Issue1371.Rooms.C, Issue1371.Rooms.D);
        enumMap.put(Issue1371.Rooms.E, Issue1371.Rooms.A);
        Assert.assertEquals(JSON.toJSONString(enumMap, WriteNonStringKeyAsString), "{\"C\":\"D\",\"E\":\"A\"}");
    }
}

