/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tablesaw.api;


import Table.Pairs;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.io.csv.CsvReadOptions;


public class TableTest {
    private static final String LINE_END = System.lineSeparator();

    private static final int ROWS_BOUNDARY = 1000;

    private static final Random RANDOM = new Random();

    private Table table;

    private DoubleColumn f1 = DoubleColumn.create("f1");

    private DoubleColumn numberColumn = DoubleColumn.create("d1");

    @Test
    public void testSummarize() throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        Table result = table.summarize("Injuries", AggregateFunctions.mean, AggregateFunctions.stdDev).by("State");
        Assertions.assertEquals(49, result.rowCount());
        Assertions.assertEquals(3, result.columnCount());
        Assertions.assertEquals("4.580805569368455", result.column(1).getString(0));
    }

    @Test
    public void testColumn() {
        Column<?> column1 = table.column(0);
        Assertions.assertNotNull(column1);
    }

    @Test
    public void testRowWiseAddition() {
        double[] a = new double[]{ 3, 4, 5 };
        double[] b = new double[]{ 3, 4, 5 };
        double[] c = new double[]{ 3, 4, 5 };
        Table t = Table.create("test", DoubleColumn.create("a", a), DoubleColumn.create("b", b), DoubleColumn.create("c", c));
        DoubleColumn n = t.doubleColumn(0).add(t.doubleColumn(1)).add(t.doubleColumn(2));
        Assertions.assertEquals(n.get(0), 9, 0);
        Assertions.assertEquals(n.get(1), 12, 0);
        Assertions.assertEquals(n.get(2), 15, 0);
    }

    @Test
    public void testRowWiseAddition2() {
        double[] a = new double[]{ 3, 4, 5 };
        double[] b = new double[]{ 3, 4, 5 };
        double[] c = new double[]{ 3, 4, 5 };
        Table t = Table.create("test", DoubleColumn.create("a", a), DoubleColumn.create("b", b), DoubleColumn.create("c", c));
        DoubleColumn n = sum(t.doubleColumn("a"), t.doubleColumn("b"), t.doubleColumn("c"));
        Assertions.assertEquals(n.get(0), 9, 0);
        Assertions.assertEquals(n.get(1), 12, 0);
        Assertions.assertEquals(n.get(2), 15, 0);
    }

    @Test
    public void testRemoveColumns() {
        StringColumn sc = StringColumn.create("0");
        StringColumn sc1 = StringColumn.create("1");
        StringColumn sc2 = StringColumn.create("2");
        StringColumn sc3 = StringColumn.create("3");
        Table t = Table.create("t", sc, sc1, sc2, sc3);
        t.removeColumns(1, 3);
        Assertions.assertTrue(t.containsColumn(sc));
        Assertions.assertTrue(t.containsColumn(sc2));
        Assertions.assertFalse(t.containsColumn(sc1));
        Assertions.assertFalse(t.containsColumn(sc3));
    }

    @Test
    public void printEmptyTable() {
        Table t = Table.create("Test");
        Assertions.assertEquals((("Test" + (TableTest.LINE_END)) + (TableTest.LINE_END)), t.print());
        StringColumn c1 = StringColumn.create("SC");
        t.addColumns(c1);
        Assertions.assertEquals(((((" Test " + (TableTest.LINE_END)) + " SC  |") + (TableTest.LINE_END)) + "------"), t.print());
    }

    @Test
    public void testDropDuplicateRows() throws Exception {
        Table t1 = Table.read().csv("../data/bush.csv");
        int rowCount = t1.rowCount();
        Table t2 = Table.read().csv("../data/bush.csv");
        Table t3 = Table.read().csv("../data/bush.csv");
        t1.append(t2).append(t3);
        Assertions.assertEquals((3 * rowCount), t1.rowCount());
        t1 = t1.dropDuplicateRows();
        Assertions.assertEquals(rowCount, t1.rowCount());
    }

    @Test
    public void testMissingValueCounts() {
        StringColumn c1 = StringColumn.create("SC");
        DoubleColumn c2 = DoubleColumn.create("NC");
        DateColumn c3 = DateColumn.create("DC");
        Table t = Table.create("Test", c1, c2, c3);
        Assertions.assertEquals(0, t.missingValueCounts().doubleColumn(1).get(0), 1.0E-5);
    }

    @Test
    public void testFullCopy() {
        numberColumn.append(2.23424);
        Table t = Table.create("test");
        t.addColumns(numberColumn);
        Table c = t.copy();
        DoubleColumn doubles = c.doubleColumn(0);
        Assertions.assertNotNull(doubles);
        Assertions.assertEquals(1, doubles.size());
    }

    @Test
    public void testColumnCount() {
        Assertions.assertEquals(0, Table.create("t").columnCount());
        Assertions.assertEquals(1, table.columnCount());
    }

    @Test
    public void testLast() throws IOException {
        Table t = Table.read().csv("../data/bush.csv");
        t = t.sortOn("date");
        Table t1 = t.last(3);
        Assertions.assertEquals(3, t1.rowCount());
        Assertions.assertEquals(LocalDate.of(2004, 2, 5), t1.dateColumn(0).get(2));
    }

    @Test
    public void testSelect1() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table t1 = t.select(t.column(1), t.column(2));
        Assertions.assertEquals(2, t1.columnCount());
    }

    @Test
    public void testSelect2() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table t1 = t.select(t.column(0), t.column(1), t.column(2), t.dateColumn(0).year());
        Assertions.assertEquals(4, t1.columnCount());
        Assertions.assertEquals("date year", t1.column(3).name());
    }

    @Test
    public void testSampleSplit() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table[] results = t.sampleSplit(0.75);
        Assertions.assertEquals(t.rowCount(), ((results[0].rowCount()) + (results[1].rowCount())));
    }

    @Test
    public void testDoWithEachRow() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true)).first(10);
        Short[] ratingsArray = new Short[]{ 53, 58 };
        List<Short> ratings = Lists.asList(((short) (52)), ratingsArray);
        Consumer<Row> doable = ( row) -> {
            if ((row.getRowNumber()) < 5) {
                Assertions.assertTrue(ratings.contains(row.getShort("approval")));
            }
        };
        t.doWithRows(doable);
    }

    @Test
    public void testDoWithEachRow2() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true));
        int dateTarget = PackedLocalDate.pack(LocalDate.of(2002, 1, 1));
        double ratingTarget = 75;
        AtomicInteger count = new AtomicInteger(0);
        Consumer<Row> doable = ( row) -> {
            if (((row.getPackedDate("date")) > dateTarget) && ((row.getShort("approval")) > ratingTarget)) {
                count.getAndIncrement();
            }
        };
        t.doWithRows(doable);
        Assertions.assertTrue(((count.get()) > 0));
    }

    @Test
    public void testDetect() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true));
        int dateTarget = PackedLocalDate.pack(LocalDate.of(2002, 1, 1));
        double ratingTarget = 75;
        Predicate<Row> doable = ( row) -> ((row.getPackedDate("date")) > dateTarget) && ((row.getShort("approval")) > ratingTarget);
        Assertions.assertTrue(t.detect(doable));
    }

    @Test
    public void testRowToString() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Row row = new Row(t);
        row.at(0);
        Assertions.assertEquals((((((("             bush.csv              " + (TableTest.LINE_END)) + "    date     |  approval  |  who  |") + (TableTest.LINE_END)) + "-----------------------------------") + (TableTest.LINE_END)) + " 2004-02-04  |        53  |  fox  |"), row.toString());
    }

    @Test
    public void testPairs() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true));
        TableTest.PairChild pairs = new TableTest.PairChild();
        t.doWithRows(pairs);
    }

    @Test
    public void testPairs2() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true));
        Table.Pairs runningAvg = new Table.Pairs() {
            private List<Double> values = new ArrayList<>();

            @Override
            public void doWithPair(Row row1, Row row2) {
                short r1 = row1.getShort("approval");
                short r2 = row2.getShort("approval");
                values.add(((r1 + r2) / 2.0));
            }

            @Override
            public List<Double> getResult() {
                return values;
            }
        };
        t.doWithRows(runningAvg);
    }

    @Test
    public void stepWithRows() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true)).first(6);
        final int sum1 = ((int) (t.shortColumn("approval").sum()));
        TableTest.RowConsumer rowConsumer = new TableTest.RowConsumer();
        t.stepWithRows(rowConsumer, 3);
        Assertions.assertEquals(sum1, rowConsumer.getSum());
    }

    private static class RowConsumer implements Consumer<Row[]> {
        private int sum = 0;

        public int getSum() {
            return sum;
        }

        @Override
        public void accept(Row[] rows) {
            for (int i = 0; i < 3; i++) {
                sum += rows[i].getShort("approval");
            }
        }
    }

    @Test
    public void testRollWithNrows2() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true)).first(4);
        ShortColumn approval = t.shortColumn("approval");
        List<Integer> sums = new ArrayList<>();
        Consumer<Row[]> rowConsumer = ( rows) -> {
            int sum = 0;
            for (Row row : rows) {
                sum += row.getShort("approval");
            }
            sums.add(sum);
        };
        t.rollWithRows(rowConsumer, 2);
        Assertions.assertTrue(sums.contains((((int) (approval.getDouble(0))) + ((int) (approval.getDouble(1))))));
        Assertions.assertTrue(sums.contains((((int) (approval.getDouble(1))) + ((int) (approval.getDouble(2))))));
        Assertions.assertTrue(sums.contains((((int) (approval.getDouble(2))) + ((int) (approval.getDouble(3))))));
    }

    private class PairChild implements Table.Pairs {
        private List<Double> runningAverage = new ArrayList<>();

        @Override
        public void doWithPair(Row row1, Row row2) {
            double r1 = row1.getShort("approval");
            double r2 = row2.getShort("approval");
            runningAverage.add(((r1 + r2) / 2.0));
        }
    }

    @Test
    public void testRowCount() {
        Assertions.assertEquals(0, table.rowCount());
        DoubleColumn floatColumn = this.f1;
        floatColumn.append(2.0F);
        Assertions.assertEquals(1, table.rowCount());
        floatColumn.append(2.2342F);
        Assertions.assertEquals(2, table.rowCount());
    }

    @Test
    public void testAppend() {
        int appendedRows = appendRandomlyGeneratedColumn(table);
        assertTableColumnSize(table, f1, appendedRows);
    }

    @Test
    public void testAppendEmptyTable() {
        appendEmptyColumn(table);
        Assertions.assertTrue(table.isEmpty());
    }

    @Test
    public void testAppendToNonEmptyTable() {
        populateColumn(f1);
        Assertions.assertFalse(table.isEmpty());
        int initialSize = table.rowCount();
        int appendedRows = appendRandomlyGeneratedColumn(table);
        assertTableColumnSize(table, f1, (initialSize + appendedRows));
    }

    @Test
    public void testAppendEmptyTableToNonEmptyTable() {
        populateColumn(f1);
        Assertions.assertFalse(table.isEmpty());
        int initialSize = table.rowCount();
        appendEmptyColumn(table);
        assertTableColumnSize(table, f1, initialSize);
    }

    @Test
    public void testAppendMultipleColumns() {
        DoubleColumn column = DoubleColumn.create("e1");
        table.addColumns(column);
        DoubleColumn first = f1.emptyCopy();
        DoubleColumn second = column.emptyCopy();
        int rowCount = TableTest.RANDOM.nextInt(TableTest.ROWS_BOUNDARY);
        int firstColumnSize = populateColumn(first, rowCount);
        int secondColumnSize = populateColumn(second, rowCount);
        Table tableToAppend = Table.create("populated", first, second);
        table.append(tableToAppend);
        assertTableColumnSize(table, f1, firstColumnSize);
        assertTableColumnSize(table, column, secondColumnSize);
    }

    @Test
    public void testReplaceColumn() {
        DoubleColumn first = DoubleColumn.create("c1", new double[]{ 1, 2, 3, 4, 5 });
        DoubleColumn second = DoubleColumn.create("c2", new double[]{ 6, 7, 8, 9, 10 });
        DoubleColumn replacement = DoubleColumn.create("c2", new double[]{ 10, 20, 30, 40, 50 });
        Table t = Table.create("populated", first, second);
        int colIndex = t.columnIndex(second);
        Assertions.assertSame(t.column("c2"), second);
        t.replaceColumn("c2", replacement);
        Assertions.assertSame(t.column("c1"), first);
        Assertions.assertSame(t.column("c2"), replacement);
        Assertions.assertEquals(t.columnIndex(replacement), colIndex);
    }

    @Test
    public void testAsMatrix() {
        DoubleColumn first = DoubleColumn.create("c1", new double[]{ 1L, 2L, 3L, 4L, 5L });
        DoubleColumn second = DoubleColumn.create("c2", new double[]{ 6.0F, 7.0F, 8.0F, 9.0F, 10.0F });
        DoubleColumn third = DoubleColumn.create("c3", new double[]{ 10.0, 20.0, 30.0, 40.0, 50.0 });
        Table t = Table.create("table", first, second, third);
        double[][] matrix = t.as().doubleMatrix();
        Assertions.assertEquals(5, matrix.length);
        Assertions.assertArrayEquals(new double[]{ 1.0, 6.0, 10.0 }, matrix[0], 1.0E-7);
        Assertions.assertArrayEquals(new double[]{ 2.0, 7.0, 20.0 }, matrix[1], 1.0E-7);
        Assertions.assertArrayEquals(new double[]{ 3.0, 8.0, 30.0 }, matrix[2], 1.0E-7);
        Assertions.assertArrayEquals(new double[]{ 4.0, 9.0, 40.0 }, matrix[3], 1.0E-7);
        Assertions.assertArrayEquals(new double[]{ 5.0, 10.0, 50.0 }, matrix[4], 1.0E-7);
    }

    @Test
    public void testRowSort() throws Exception {
        Table bush = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true));
        Comparator<Row> rowComparator = Comparator.comparingDouble(( o) -> o.getShort("approval"));
        Table sorted = bush.sortOn(rowComparator);
        ShortColumn approval = sorted.shortColumn("approval");
        for (int i = 0; i < ((bush.rowCount()) - 2); i++) {
            Assertions.assertTrue(((approval.get(i)) <= (approval.get((i + 1)))));
        }
    }

    @Test
    public void testIterable() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        int rowNumber = 0;
        for (Row row : bush.first(10)) {
            Assertions.assertEquals(row.getRowNumber(), (rowNumber++));
        }
    }
}
