package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


/**
 * Created by kimmking on 02/07/2017.
 */
public class Issue1303 extends TestCase {
    public void test_for_issue() {
        String jsonString = "[{\"author\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\"a876c49c18\"},\"createdAt\":\"2017-07-02 20:06:13\",\"imgurl\":\"https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=11075891,34401011&fm=117&gp=0.jpg\",\"name\":\"\u8863\u67b6\",\"objectId\":\"029d5493cd\",\"prices\":\"1\",\"updatedAt\":\"2017-07-02 20:06:13\"}]";
        JSONArray jsonArray = JSON.parseArray(jsonString);
        // jsonArray = new JSONArray(jsonArray);//????????????
        double total = 0;
        for (int i = 0; i < (jsonArray.size()); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            TestCase.assertEquals("??", object.getString("name"));
            total = total + (Double.valueOf(object.getString("prices")));
        }
        TestCase.assertEquals(1.0, total);
    }
}

