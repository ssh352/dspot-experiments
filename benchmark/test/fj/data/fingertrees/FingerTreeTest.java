package fj.data.fingertrees;


import fj.Function;
import fj.Monoid;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 10/10/2015.
 */
public class FingerTreeTest {
    public static final int SIZE = 10;

    @Test
    public void size() {
        validateOperations(List.list((-92), 68, 54, (-77), (-18), 67));
        validateOperations(List.list((-92), 68, 54, (-77), (-18), 67, (-60), 23, (-70), 99, 66, (-79), (-5)));
    }

    @Test
    public void testHeadOption() {
        Assert.assertThat(Empty.emptyIntAddition().headOption(), Is.is(Option.none()));
        FingerTree<Integer, Integer> ft = new MakeTree<Integer, Integer>(FingerTree.measured(Monoid.intAdditionMonoid, Function.constant(1))).single(1);
        Assert.assertThat(ft.headOption(), Is.is(Option.some(1)));
    }

    @Test
    public void testUncons() {
        Assert.assertThat(Empty.emptyIntAddition().uncons(0, ( h, t) -> h), Is.is(0));
        FingerTree<Integer, Integer> ft = new MakeTree<Integer, Integer>(FingerTree.measured(Monoid.intAdditionMonoid, Function.constant(1))).single(1);
        Assert.assertThat(ft.uncons(0, ( h, t) -> h), Is.is(1));
    }

    @Test
    public void testSeqString() {
        String actual = midSeq().toString();
        String expected = "Deep(9 -> One(1 -> 1), Deep(6 -> One(3 -> Node3(3 -> V3(2,3,4))), Empty(), One(3 -> Node3(3 -> V3(5,6,7)))), Two(2 -> V2(8,9)))";
        Assert.assertThat(actual, CoreMatchers.equalTo(expected));
    }

    @Test
    public void testQueueString() {
        String actual = midPriorityQueue().toString();
        String expected = "Deep(16 -> One(1 -> (1,1)), Deep(12 -> One(8 -> Node3(8 -> V3((4,4),(3,3),(8,8)))), Empty(), One(12 -> Node3(12 -> V3((5,5),(12,12),(7,7))))), Two(16 -> V2((16,16),(9,9))))";
        Assert.assertThat(actual, CoreMatchers.equalTo(expected));
    }

    @Test
    public void stream() {
        FingerTree<Integer, Integer> ft = midSeq();
        Assert.assertThat(ft.toStream().toList(), CoreMatchers.equalTo(List.range(1, FingerTreeTest.SIZE)));
    }

    @Test
    public void split() {
        int splitPoint = 3;
        FingerTree<Integer, Integer> ft = FingerTree.emptyIntAddition();
        FingerTree<Integer, Integer> ft3 = List.range(1, FingerTreeTest.SIZE).foldLeft(( ft2) -> ( i) -> ft2.snoc(i), ft);
        P2<FingerTree<Integer, Integer>, FingerTree<Integer, Integer>> p = ft3.split(( v) -> v >= splitPoint);
        Assert.assertThat(p._1().toStream().toList(), CoreMatchers.equalTo(List.range(1, splitPoint)));
        Assert.assertThat(p._2().toStream().toList(), CoreMatchers.equalTo(List.range(splitPoint, FingerTreeTest.SIZE)));
    }
}

