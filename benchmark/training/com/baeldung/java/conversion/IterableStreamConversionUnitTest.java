package com.baeldung.java.conversion;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class IterableStreamConversionUnitTest {
    @Test
    public void givenIterable_whenConvertedToStream_thenNotNull() {
        Iterable<String> iterable = Arrays.asList("Testing", "Iterable", "conversion", "to", "Stream");
        Assert.assertNotNull(StreamSupport.stream(iterable.spliterator(), false));
    }

    @Test
    public void whenConvertedToList_thenCorrect() {
        Iterable<String> iterable = Arrays.asList("Testing", "Iterable", "conversion", "to", "Stream");
        List<String> result = StreamSupport.stream(iterable.spliterator(), false).map(String::toUpperCase).collect(Collectors.toList());
        MatcherAssert.assertThat(result, contains("TESTING", "ITERABLE", "CONVERSION", "TO", "STREAM"));
    }
}

