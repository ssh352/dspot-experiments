package org.baeldung.gson.deserialization.test;


import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import org.baeldung.gson.deserialization.Foo;
import org.baeldung.gson.deserialization.FooDeserializerFromJsonWithDifferentFields;
import org.baeldung.gson.deserialization.FooInstanceCreator;
import org.baeldung.gson.deserialization.FooWithInner;
import org.baeldung.gson.deserialization.GenericFoo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GsonDeserializationUnitTest {
    // tests - single element
    @Test
    public final void whenDeserializingToSimpleObject_thenCorrect() {
        final String json = "{\"intValue\":1,\"stringValue\":\"one\"}";
        final Foo targetObject = new Gson().fromJson(json, Foo.class);
        Assert.assertEquals(targetObject.intValue, 1);
        Assert.assertEquals(targetObject.stringValue, "one");
    }

    @Test
    public final void givenJsonHasExtraValues_whenDeserializing_thenCorrect() {
        final String json = "{\"intValue\":1,\"stringValue\":\"one\",\"extraString\":\"two\",\"extraFloat\":2.2}";
        final Foo targetObject = new Gson().fromJson(json, Foo.class);
        Assert.assertEquals(targetObject.intValue, 1);
        Assert.assertEquals(targetObject.stringValue, "one");
    }

    @Test
    public final void givenJsonHasNonMatchingFields_whenDeserializingWithCustomDeserializer_thenCorrect() {
        final String json = "{\"valueInt\":7,\"valueString\":\"seven\"}";
        final GsonBuilder gsonBldr = new GsonBuilder();
        gsonBldr.registerTypeAdapter(Foo.class, new FooDeserializerFromJsonWithDifferentFields());
        final Foo targetObject = gsonBldr.create().fromJson(json, Foo.class);
        Assert.assertEquals(targetObject.intValue, 7);
        Assert.assertEquals(targetObject.stringValue, "seven");
    }

    @Test
    public final void whenDeserializingToGenericObject_thenCorrect() {
        final Type typeToken = new TypeToken<GenericFoo<Integer>>() {}.getType();
        final String json = "{\"theValue\":1}";
        final GenericFoo<Integer> targetObject = new Gson().fromJson(json, typeToken);
        Assert.assertEquals(targetObject.theValue, new Integer(1));
    }

    // tests - multiple elements
    @Test
    public final void givenJsonArrayOfFoos_whenDeserializingToArray_thenCorrect() {
        final String json = "[{\"intValue\":1,\"stringValue\":\"one\"}," + "{\"intValue\":2,\"stringValue\":\"two\"}]";
        final Foo[] targetArray = new GsonBuilder().create().fromJson(json, Foo[].class);
        Assert.assertThat(Lists.newArrayList(targetArray), Matchers.hasItem(new Foo(1, "one")));
        Assert.assertThat(Lists.newArrayList(targetArray), Matchers.hasItem(new Foo(2, "two")));
        Assert.assertThat(Lists.newArrayList(targetArray), Matchers.not(Matchers.hasItem(new Foo(1, "two"))));
    }

    @Test
    public final void givenJsonArrayOfFoos_whenDeserializingCollection_thenCorrect() {
        final String json = "[{\"intValue\":1,\"stringValue\":\"one\"},{\"intValue\":2,\"stringValue\":\"two\"}]";
        final Type targetClassType = new TypeToken<ArrayList<Foo>>() {}.getType();
        final Collection<Foo> targetCollection = new Gson().fromJson(json, targetClassType);
        Assert.assertThat(targetCollection, Matchers.instanceOf(ArrayList.class));
    }

    // 
    @Test
    public void whenDeserializingJsonIntoElements_thenCorrect() {
        final String jsonSourceObject = "{\"valueInt\":7,\"valueString\":\"seven\"}";
        final JsonParser jParser = new JsonParser();
        final JsonElement jElement = jParser.parse(jsonSourceObject);
        final JsonObject jObject = jElement.getAsJsonObject();
        final int intValue = jObject.get("valueInt").getAsInt();
        final String stringValue = jObject.get("valueString").getAsString();
        final Foo targetObject = new Foo(intValue, stringValue);
        Assert.assertEquals(targetObject.intValue, 7);
        Assert.assertEquals(targetObject.stringValue, "seven");
    }

    // new examples
    @Test
    public void whenDeserializingToNestedObjects_thenCorrect() {
        final String json = "{\"intValue\":1,\"stringValue\":\"one\",\"innerFoo\":{\"name\":\"inner\"}}";
        final FooWithInner targetObject = new Gson().fromJson(json, FooWithInner.class);
        Assert.assertEquals(targetObject.intValue, 1);
        Assert.assertEquals(targetObject.stringValue, "one");
        Assert.assertEquals(targetObject.innerFoo.name, "inner");
    }

    @Test
    public void whenDeserializingUsingInstanceCreator_thenCorrect() {
        final String json = "{\"intValue\":1}";
        final GsonBuilder gsonBldr = new GsonBuilder();
        gsonBldr.registerTypeAdapter(Foo.class, new FooInstanceCreator());
        final Foo targetObject = gsonBldr.create().fromJson(json, Foo.class);
        Assert.assertEquals(targetObject.intValue, 1);
        Assert.assertEquals(targetObject.stringValue, "sample");
    }
}

