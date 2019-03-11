package com.jayway.jsonpath.old;


import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.spi.json.JsonProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import junit.framework.Assert;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;


public class FilterTest extends BaseTest {
    public static final String DOCUMENT = "{ \"store\": {\n" + ((((((((((((((((((((((((((((((("    \"book\": [ \n" + "      { \"category\": \"reference\",\n") + "        \"author\": \"Nigel Rees\",\n") + "        \"title\": \"Sayings of the Century\",\n") + "        \"price\": 8.95\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"Evelyn Waugh\",\n") + "        \"title\": \"Sword of Honour\",\n") + "        \"price\": 12.99\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"Herman Melville\",\n") + "        \"title\": \"Moby Dick\",\n") + "        \"isbn\": \"0-553-21311-3\",\n") + "        \"price\": 8.99\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"J. R. R. Tolkien\",\n") + "        \"title\": \"The Lord of the Rings\",\n") + "        \"isbn\": \"0-395-19395-8\",\n") + "        \"price\": 22.99\n") + "      }\n") + "    ],\n") + "    \"bicycle\": {\n") + "      \"color\": \"red\",\n") + "      \"price\": 19.95,\n") + "      \"foo:bar\": \"fooBar\",\n") + "      \"dot.notation\": \"new\"\n") + "    }\n") + "  }\n") + "}");

    private static final Configuration conf = Configuration.defaultConfiguration();

    private static final JsonProvider jp = FilterTest.conf.jsonProvider();

    @Test
    public void ne_filters_evaluates() throws Exception {
        final Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");
        check.put("bar", null);
        Assert.assertTrue(Filter.filter(Criteria.where("foo").ne(null)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("foo").ne("not foo")).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").ne("foo")).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("bar").ne(null)).apply(createPredicateContext(check)));
    }

    @Test
    public void gt_filters_evaluates() throws Exception {
        final Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5);
        check.put("foo_null", null);
        Assert.assertTrue(Filter.filter(Criteria.where("foo").gt(12.0)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").gt(null)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").gt(20.0)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo_null").gt(20.0)).apply(createPredicateContext(check)));
    }

    @Test
    public void gte_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5);
        check.put("foo_null", null);
        Assert.assertTrue(Filter.filter(Criteria.where("foo").gte(12.0)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("foo").gte(12.5)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").gte(null)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").gte(20.0)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo_null").gte(20.0)).apply(createPredicateContext(check)));
    }

    @Test
    public void lt_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 10.5);
        check.put("foo_null", null);
        // assertTrue(filter(where("foo").lt(12D)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").lt(null)).apply(createPredicateContext(check)));
        // assertFalse(filter(where("foo").lt(5D)).apply(createPredicateContext(check)));
        // assertFalse(filter(where("foo_null").lt(5D)).apply(createPredicateContext(check)));
    }

    @Test
    public void lte_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", 12.5);
        check.put("foo_null", null);
        Assert.assertTrue(Filter.filter(Criteria.where("foo").lte(13.0)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").lte(null)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").lte(5.0)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo_null").lte(5.0)).apply(createPredicateContext(check)));
    }

    @Test
    public void in_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("item", 3);
        check.put("null_item", null);
        Assert.assertTrue(Filter.filter(Criteria.where("item").in(1, 2, 3)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("item").in(Arrays.asList(1, 2, 3))).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("item").in(4, 5, 6)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("item").in(Arrays.asList(4, 5, 6))).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("item").in(Arrays.asList('A'))).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("item").in(Arrays.asList(((Object) (null))))).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("null_item").in(((Object) (null)))).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("null_item").in(1, 2, 3)).apply(createPredicateContext(check)));
    }

    @Test
    public void nin_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("item", 3);
        check.put("null_item", null);
        Assert.assertTrue(Filter.filter(Criteria.where("item").nin(4, 5)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("item").nin(Arrays.asList(4, 5))).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("item").nin(Arrays.asList('A'))).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("null_item").nin(1, 2, 3)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("item").nin(Arrays.asList(((Object) (null))))).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("item").nin(3)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("item").nin(Arrays.asList(3))).apply(createPredicateContext(check)));
    }

    @Test
    public void all_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("items", Arrays.asList(1, 2, 3));
        Assert.assertTrue(Filter.filter(Criteria.where("items").all(1, 2, 3)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("items").all(1, 2, 3, 4)).apply(createPredicateContext(check)));
    }

    @Test
    public void size_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("items", Arrays.asList(1, 2, 3));
        check.put("items_empty", Collections.emptyList());
        Assert.assertTrue(Filter.filter(Criteria.where("items").size(3)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("items_empty").size(0)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("items").size(2)).apply(createPredicateContext(check)));
    }

    @Test
    public void exists_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("foo", "foo");
        check.put("foo_null", null);
        Assert.assertTrue(Filter.filter(Criteria.where("foo").exists(true)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo").exists(false)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("foo_null").exists(true)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("foo_null").exists(false)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("bar").exists(false)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("bar").exists(true)).apply(createPredicateContext(check)));
    }

    @Test
    public void type_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 1);
        check.put("long", 1L);
        check.put("double", 1.12);
        check.put("boolean", true);
        Assert.assertFalse(Filter.filter(Criteria.where("string_null").type(String.class)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("string").type(String.class)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("string").type(Number.class)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("int").type(Number.class)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("int").type(String.class)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("long").type(Number.class)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("long").type(String.class)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("double").type(Number.class)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("double").type(String.class)).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("boolean").type(Boolean.class)).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("boolean").type(String.class)).apply(createPredicateContext(check)));
    }

    @Test
    public void pattern_filters_evaluates() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("name", "kalle");
        check.put("name_null", null);
        Assert.assertFalse(Filter.filter(Criteria.where("name_null").regex(Pattern.compile(".alle"))).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("name").regex(Pattern.compile(".alle"))).apply(createPredicateContext(check)));
        Assert.assertFalse(Filter.filter(Criteria.where("name").regex(Pattern.compile("KALLE"))).apply(createPredicateContext(check)));
        Assert.assertTrue(Filter.filter(Criteria.where("name").regex(Pattern.compile("KALLE", Pattern.CASE_INSENSITIVE))).apply(createPredicateContext(check)));
    }

    @Test
    public void combine_filter_deep_criteria() {
        String json = "[\n" + ((((((((((((((("   {\n" + "      \"first-name\" : \"John\",\n") + "      \"last-name\" : \"Irving\",\n") + "      \"address\" : {\"state\" : \"Texas\"}\n") + "   },\n") + "   {\n") + "      \"first-name\" : \"Jock\",\n") + "      \"last-name\" : \"Ewing\",\n") + "      \"address\" : {\"state\" : \"Texas\"}\n") + "   },\n") + "   {\n") + "      \"first-name\" : \"Jock\",\n") + "      \"last-name\" : \"Barnes\",\n") + "      \"address\" : {\"state\" : \"Nevada\"}\n") + "   } \n") + "]");
        Filter filter = Filter.filter(Criteria.where("first-name").is("Jock").and("address.state").is("Texas"));
        List<Map<String, Object>> jocksInTexas1 = JsonPath.read(json, "$[?]", filter);
        List<Map<String, Object>> jocksInTexas2 = JsonPath.read(json, "$[?(@.first-name == 'Jock' && @.address.state == 'Texas')]");
        JsonPath.parse(json).json();
        assertThat(((String) (JsonPath.read(jocksInTexas1, "$[0].address.state"))), CoreMatchers.is("Texas"));
        assertThat(((String) (JsonPath.read(jocksInTexas1, "$[0].first-name"))), CoreMatchers.is("Jock"));
        assertThat(((String) (JsonPath.read(jocksInTexas1, "$[0].last-name"))), CoreMatchers.is("Ewing"));
    }

    // -------------------------------------------------
    // 
    // Single filter tests
    // 
    // -------------------------------------------------
    @Test
    public void filters_can_be_combined() throws Exception {
        Map<String, Object> check = new HashMap<String, Object>();
        check.put("string", "foo");
        check.put("string_null", null);
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12);
        Filter shouldMarch = Filter.filter(Criteria.where("string").is("foo").and("int").lt(11));
        Filter shouldNotMarch = Filter.filter(Criteria.where("string").is("foo").and("int").gt(11));
        Assert.assertTrue(shouldMarch.apply(createPredicateContext(check)));
        Assert.assertFalse(shouldNotMarch.apply(createPredicateContext(check)));
    }

    @Test
    public void arrays_of_maps_can_be_filtered() throws Exception {
        Map<String, Object> rootGrandChild_A = new HashMap<String, Object>();
        rootGrandChild_A.put("name", "rootGrandChild_A");
        Map<String, Object> rootGrandChild_B = new HashMap<String, Object>();
        rootGrandChild_B.put("name", "rootGrandChild_B");
        Map<String, Object> rootGrandChild_C = new HashMap<String, Object>();
        rootGrandChild_C.put("name", "rootGrandChild_C");
        Map<String, Object> rootChild_A = new HashMap<String, Object>();
        rootChild_A.put("name", "rootChild_A");
        rootChild_A.put("children", Arrays.asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));
        Map<String, Object> rootChild_B = new HashMap<String, Object>();
        rootChild_B.put("name", "rootChild_B");
        rootChild_B.put("children", Arrays.asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));
        Map<String, Object> rootChild_C = new HashMap<String, Object>();
        rootChild_C.put("name", "rootChild_C");
        rootChild_C.put("children", Arrays.asList(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("children", Arrays.asList(rootChild_A, rootChild_B, rootChild_C));
        Predicate customFilter = new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                if (ctx.configuration().jsonProvider().getMapValue(ctx.item(), "name").equals("rootGrandChild_A")) {
                    return true;
                }
                return false;
            }
        };
        Filter rootChildFilter = Filter.filter(Criteria.where("name").regex(Pattern.compile("rootChild_[A|B]")));
        Filter rootGrandChildFilter = Filter.filter(Criteria.where("name").regex(Pattern.compile("rootGrandChild_[A|B]")));
        List read = JsonPath.read(root, "children[?].children[?, ?]", rootChildFilter, rootGrandChildFilter, customFilter);
    }

    @Test
    public void arrays_of_objects_can_be_filtered() throws Exception {
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("items", Arrays.asList(1, 2, 3));
        Predicate customFilter = new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                return 1 == ((Integer) (ctx.item()));
            }
        };
        List<Integer> res = JsonPath.read(doc, "$.items[?]", customFilter);
        Assert.assertEquals(1, res.get(0).intValue());
    }

    @Test
    public void filters_can_contain_json_path_expressions() throws Exception {
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(FilterTest.DOCUMENT);
        Assert.assertFalse(Filter.filter(Criteria.where("$.store.bicycle.color").ne("red")).apply(createPredicateContext(doc)));
    }

    @Test
    public void not_empty_filter_evaluates() {
        String json = "{\n" + (((((((((((((((((("    \"fields\": [\n" + "        {\n") + "            \"errors\": [], \n") + "            \"name\": \"\", \n") + "            \"empty\": true \n") + "        }, \n") + "        {\n") + "            \"errors\": [], \n") + "            \"name\": \"foo\"\n") + "        }, \n") + "        {\n") + "            \"errors\": [\n") + "                \"first\", \n") + "                \"second\"\n") + "            ], \n") + "            \"name\": \"invalid\"\n") + "        }\n") + "    ]\n") + "}\n");
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(json);
        List<Map<String, Object>> result = JsonPath.read(doc, "$.fields[?]", Filter.filter(Criteria.where("errors").notEmpty()));
        Assert.assertEquals(1, result.size());
        List<Map<String, Object>> result2 = JsonPath.read(doc, "$.fields[?]", Filter.filter(Criteria.where("name").notEmpty()));
        Assert.assertEquals(2, result2.size());
    }

    @Test
    public void contains_filter_evaluates_on_array() {
        String json = "{\n" + (((((((((((((((((((((((((((((("\"store\": {\n" + "    \"book\": [\n") + "        {\n") + "            \"category\": \"reference\",\n") + "            \"authors\" : [\n") + "                 {\n") + "                     \"firstName\" : \"Nigel\",\n") + "                     \"lastName\" :  \"Rees\"\n") + "                  }\n") + "            ],\n") + "            \"title\": \"Sayings of the Century\",\n") + "            \"price\": 8.95\n") + "        },\n") + "        {\n") + "            \"category\": \"fiction\",\n") + "            \"authors\": [\n") + "                 {\n") + "                     \"firstName\" : \"Evelyn\",\n") + "                     \"lastName\" :  \"Waugh\"\n") + "                  },\n") + "                 {\n") + "                     \"firstName\" : \"Another\",\n") + "                     \"lastName\" :  \"Author\"\n") + "                  }\n") + "            ],\n") + "            \"title\": \"Sword of Honour\",\n") + "            \"price\": 12.99\n") + "        }\n") + "    ]\n") + "  }\n") + "}");
        Filter filter = Filter.filter(Criteria.where("authors[*].lastName").contains("Waugh"));
        List<String> result = JsonPath.parse(json).read("$.store.book[?].title", filter);
        Assertions.assertThat(result).containsExactly("Sword of Honour");
    }

    @Test
    public void contains_filter_evaluates_on_string() {
        String json = "{\n" + (((((((((((((("\"store\": {\n" + "    \"book\": [\n") + "        {\n") + "            \"category\": \"reference\",\n") + "            \"title\": \"Sayings of the Century\",\n") + "            \"price\": 8.95\n") + "        },\n") + "        {\n") + "            \"category\": \"fiction\",\n") + "            \"title\": \"Sword of Honour\",\n") + "            \"price\": 12.99\n") + "        }\n") + "    ]\n") + "  }\n") + "}");
        Filter filter = Filter.filter(Criteria.where("category").contains("fic"));
        List<String> result = JsonPath.parse(json).read("$.store.book[?].title", filter);
        Assertions.assertThat(result).containsExactly("Sword of Honour");
    }
}

