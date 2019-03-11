package tech.tablesaw.table;


import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.numbers.DoubleColumnType;


public class RollingColumnTest {
    @Test
    public void testRollingMean() {
        double[] data = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        double missing = DoubleColumnType.missingValueIndicator();
        double[] sma5 = new double[]{ missing, missing, missing, missing, 3, 4, 5, 6, 7, 8 };
        DoubleColumn result = DoubleColumn.create("data", data).rolling(5).mean();
        Assertions.assertArrayEquals(sma5, result.asDoubleArray(), 1.0E-6);
        Assertions.assertEquals("dataMean5", result.name());
    }

    @Test
    public void testRollingMaxDate() {
        LocalDateTime[] data = new LocalDateTime[]{ LocalDate.of(2011, 1, 1).atStartOfDay(), LocalDate.of(2011, 1, 3).atStartOfDay(), LocalDate.of(2011, 1, 5).atStartOfDay(), LocalDate.of(2011, 1, 7).atStartOfDay(), LocalDate.of(2011, 1, 9).atStartOfDay() };
        LocalDateTime[] sma5 = new LocalDateTime[]{ null, LocalDate.of(2011, 1, 3).atStartOfDay(), LocalDate.of(2011, 1, 5).atStartOfDay(), LocalDate.of(2011, 1, 7).atStartOfDay(), LocalDate.of(2011, 1, 9).atStartOfDay() };
        DateTimeColumn result = ((DateTimeColumn) (DateTimeColumn.create("data", data).rolling(2).calc(AggregateFunctions.latestDateTime)));
        Assertions.assertArrayEquals(sma5, result.asObjectArray());
    }

    @Test
    public void testRollingCountTrue() {
        Boolean[] data = new Boolean[]{ true, false, false, true, true };
        BooleanColumn booleanColumn = BooleanColumn.create("data", data);
        DoubleColumn result = ((DoubleColumn) (booleanColumn.rolling(2).calc(AggregateFunctions.countTrue)));
        Assertions.assertEquals(Double.NaN, result.getDouble(0), 0.0);
        Assertions.assertEquals(1, result.getDouble(1), 0.0);
        Assertions.assertEquals(0, result.getDouble(2), 0.0);
        Assertions.assertEquals(1, result.getDouble(3), 0.0);
        Assertions.assertEquals(2, result.getDouble(4), 0.0);
    }
}

