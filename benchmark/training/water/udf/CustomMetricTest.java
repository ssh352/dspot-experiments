package water.udf;


import org.junit.Test;
import water.TestUtil;


public class CustomMetricTest extends TestUtil {
    @Test
    public void testNullModelCustomMetric() throws Exception {
        CustomMetricTest.testNullModelRegression(maeCustomMetric());
    }
}
