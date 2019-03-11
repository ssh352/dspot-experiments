package io.searchbox.indices.aliases;


import com.google.gson.Gson;
import java.util.Map;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author cihat keser
 */
public class RemoveAliasMappingTest {
    public static final Map<String, Object> USER_FILTER_JSON = new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("term", org.elasticsearch.common.collect.MapBuilder.newMapBuilder().put("user", "kimchy").immutableMap()).immutableMap();

    @Test
    public void testBasicGetDataForJson() throws JSONException {
        RemoveAliasMapping addAliasMapping = new RemoveAliasMapping.Builder("tIndex", "tAlias").build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"remove\":{\"index\":\"tIndex\",\"alias\":\"tAlias\"}}]";
        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void testGetDataForJsonWithFilter() throws JSONException {
        RemoveAliasMapping addAliasMapping = new RemoveAliasMapping.Builder("tIndex", "tAlias").setFilter(RemoveAliasMappingTest.USER_FILTER_JSON).build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"remove\":{\"index\":\"tIndex\",\"alias\":\"tAlias\",\"filter\":{\"term\":{\"user\":\"kimchy\"}}}}]";
        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void testGetDataForJsonWithFilterAndRouting() throws JSONException {
        RemoveAliasMapping addAliasMapping = new RemoveAliasMapping.Builder("tIndex", "tAlias").setFilter(RemoveAliasMappingTest.USER_FILTER_JSON).addRouting("1").build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"remove\":{\"index\":\"tIndex\",\"alias\":\"tAlias\",\"filter\":{\"term\":{\"user\":\"kimchy\"}},\"search_routing\":\"1\",\"index_routing\":\"1\"}}]";
        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }
}

