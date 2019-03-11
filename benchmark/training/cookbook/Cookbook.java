package cookbook;


import org.junit.Test;
import water.TestUtil;


// @Test
// public void testWillFail2() {
// throw new RuntimeException("3 test fails");
// }
public class Cookbook extends TestUtil {
    // @Test
    // public void testWillFail() {
    // throw new RuntimeException("first test fails");
    // }
    // ---
    // Test flow-coding a filter & group-by computing e.g. mean
    @Test
    public void testBasic() {
        Key k = Key.make("cars.hex");
        Frame fr = TestUtil.parseFrame(k, "../smalldata/cars.csv");
        // Frame fr = parseFrame(k, "../datasets/UCI/UCI-large/covtype/covtype.data");
        // Call into another class so we do not need to weave anything in this class
        // when run as a JUnit
        Cookbook2.basicStatic(k, fr);
    }
}

