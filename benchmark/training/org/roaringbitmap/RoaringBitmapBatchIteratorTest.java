package org.roaringbitmap;


import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class RoaringBitmapBatchIteratorTest {
    private final RoaringBitmap bitmap;

    public RoaringBitmapBatchIteratorTest(RoaringBitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Test
    public void testBatchIterator256() {
        test(256);
    }

    @Test
    public void testBatchIterator1024() {
        test(1024);
    }

    @Test
    public void testBatchIterator65536() {
        test(65536);
    }

    @Test
    public void testBatchIterator8192() {
        test(8192);
    }

    @Test
    public void testBatchIteratorRandom() {
        IntStream.range(0, 10).map(( i) -> ThreadLocalRandom.current().nextInt(0, (1 << 16))).forEach(this::test);
    }

    @Test
    public void testBatchIteratorAsIntIterator() {
        IntIterator it = bitmap.getBatchIterator().asIntIterator(new int[128]);
        RoaringBitmapWriter<RoaringBitmap> w = RoaringBitmapWriter.writer().constantMemory().initialCapacity(bitmap.highLowContainer.size).get();
        while (it.hasNext()) {
            w.add(it.next());
        } 
        RoaringBitmap copy = w.get();
        Assert.assertEquals(bitmap, copy);
    }
}

