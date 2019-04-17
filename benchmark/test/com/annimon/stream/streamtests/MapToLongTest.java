package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import com.annimon.stream.function.ToLongFunction;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapToLongTest {
    @Test
    public void testMapToLong() {
        final ToLongFunction<String> stringToSquareLong = new ToLongFunction<String>() {
            @Override
            public long applyAsLong(String t) {
                final String str = t.substring(1, ((t.length()) - 1));
                final long value = Long.parseLong(str);
                return value * value;
            }
        };
        Stream.of("[2]", "[3]", "[4]", "[8]", "[25]").mapToLong(stringToSquareLong).custom(assertElements(Matchers.arrayContaining(4L, 9L, 16L, 64L, 625L)));
    }
}
