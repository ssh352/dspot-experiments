/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.operators.hash;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.functions.AbstractRichFunction;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.TypePairComparator;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.operators.testutils.DummyInvokable;
import org.apache.flink.runtime.operators.testutils.TestData;
import org.apache.flink.runtime.operators.testutils.UniformIntPairGenerator;
import org.apache.flink.runtime.operators.testutils.types.IntPair;
import org.apache.flink.types.NullKeyFieldException;
import org.apache.flink.util.Collector;
import org.apache.flink.util.MutableObjectIterator;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.KeyMode.RANDOM;
import static org.apache.flink.runtime.operators.testutils.TestData.TupleGenerator.ValueMode.RANDOM_LENGTH;


@SuppressWarnings({ "serial" })
public class ReusingHashJoinIteratorITCase extends TestLogger {
    private static final int MEMORY_SIZE = 16000000;// total memory


    private static final int INPUT_1_SIZE = 20000;

    private static final int INPUT_2_SIZE = 1000;

    private static final long SEED1 = 561349061987311L;

    private static final long SEED2 = 231434613412342L;

    private final AbstractInvokable parentTask = new DummyInvokable();

    private IOManager ioManager;

    private MemoryManager memoryManager;

    private TypeSerializer<Tuple2<Integer, String>> recordSerializer;

    private TypeComparator<Tuple2<Integer, String>> record1Comparator;

    private TypeComparator<Tuple2<Integer, String>> record2Comparator;

    private TypePairComparator<Tuple2<Integer, String>, Tuple2<Integer, String>> recordPairComparator;

    private TypeSerializer<IntPair> pairSerializer;

    private TypeComparator<IntPair> pairComparator;

    private TypePairComparator<IntPair, Tuple2<Integer, String>> pairRecordPairComparator;

    private TypePairComparator<Tuple2<Integer, String>, IntPair> recordPairPairComparator;

    @Test
    public void testBuildFirst() {
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 500, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingHashJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.joinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildFirstHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildFirstHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, false, false, true);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildFirstWithHighNumberOfCommonKeys() {
        // the size of the left and right inputs
        final int INPUT_1_SIZE = 200;
        final int INPUT_2_SIZE = 100;
        final int INPUT_1_DUPLICATES = 10;
        final int INPUT_2_DUPLICATES = 2000;
        final int DUPLICATE_KEY = 13;
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 500, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator gen1Iter = new TestData.TupleGeneratorIterator(generator1, INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator gen2Iter = new TestData.TupleGeneratorIterator(generator2, INPUT_2_SIZE);
            final TestData.TupleConstantValueIterator const1Iter = new TestData.TupleConstantValueIterator(DUPLICATE_KEY, "LEFT String for Duplicate Keys", INPUT_1_DUPLICATES);
            final TestData.TupleConstantValueIterator const2Iter = new TestData.TupleConstantValueIterator(DUPLICATE_KEY, "RIGHT String for Duplicate Keys", INPUT_2_DUPLICATES);
            final List<MutableObjectIterator<Tuple2<Integer, String>>> inList1 = new ArrayList<>();
            inList1.add(gen1Iter);
            inList1.add(const1Iter);
            final List<MutableObjectIterator<Tuple2<Integer, String>>> inList2 = new ArrayList<>();
            inList2.add(gen2Iter);
            inList2.add(const2Iter);
            MutableObjectIterator<Tuple2<Integer, String>> input1 = new org.apache.flink.runtime.operators.testutils.UnionIterator(inList1);
            MutableObjectIterator<Tuple2<Integer, String>> input2 = new org.apache.flink.runtime.operators.testutils.UnionIterator(inList2);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.joinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            // re-create the whole thing for actual processing
            // reset the generators and iterators
            generator1.reset();
            generator2.reset();
            const1Iter.reset();
            const2Iter.reset();
            gen1Iter.reset();
            gen2Iter.reset();
            inList1.clear();
            inList1.add(gen1Iter);
            inList1.add(const1Iter);
            inList2.clear();
            inList2.add(gen2Iter);
            inList2.add(const2Iter);
            input1 = new org.apache.flink.runtime.operators.testutils.UnionIterator(inList1);
            input2 = new org.apache.flink.runtime.operators.testutils.UnionIterator(inList2);
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            ReusingBuildFirstHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildFirstHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, false, false, true);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildSecond() {
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 500, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingHashJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.joinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildSecondHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildSecondHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, false, false, true);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildSecondWithHighNumberOfCommonKeys() {
        // the size of the left and right inputs
        final int INPUT_1_SIZE = 200;
        final int INPUT_2_SIZE = 100;
        final int INPUT_1_DUPLICATES = 10;
        final int INPUT_2_DUPLICATES = 2000;
        final int DUPLICATE_KEY = 13;
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 500, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator gen1Iter = new TestData.TupleGeneratorIterator(generator1, INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator gen2Iter = new TestData.TupleGeneratorIterator(generator2, INPUT_2_SIZE);
            final TestData.TupleConstantValueIterator const1Iter = new TestData.TupleConstantValueIterator(DUPLICATE_KEY, "LEFT String for Duplicate Keys", INPUT_1_DUPLICATES);
            final TestData.TupleConstantValueIterator const2Iter = new TestData.TupleConstantValueIterator(DUPLICATE_KEY, "RIGHT String for Duplicate Keys", INPUT_2_DUPLICATES);
            final List<MutableObjectIterator<Tuple2<Integer, String>>> inList1 = new ArrayList<>();
            inList1.add(gen1Iter);
            inList1.add(const1Iter);
            final List<MutableObjectIterator<Tuple2<Integer, String>>> inList2 = new ArrayList<>();
            inList2.add(gen2Iter);
            inList2.add(const2Iter);
            MutableObjectIterator<Tuple2<Integer, String>> input1 = new org.apache.flink.runtime.operators.testutils.UnionIterator(inList1);
            MutableObjectIterator<Tuple2<Integer, String>> input2 = new org.apache.flink.runtime.operators.testutils.UnionIterator(inList2);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.joinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            // re-create the whole thing for actual processing
            // reset the generators and iterators
            generator1.reset();
            generator2.reset();
            const1Iter.reset();
            const2Iter.reset();
            gen1Iter.reset();
            gen2Iter.reset();
            inList1.clear();
            inList1.add(gen1Iter);
            inList1.add(const1Iter);
            inList2.clear();
            inList2.add(gen2Iter);
            inList2.add(const2Iter);
            input1 = new org.apache.flink.runtime.operators.testutils.UnionIterator(inList1);
            input2 = new org.apache.flink.runtime.operators.testutils.UnionIterator(inList2);
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            ReusingBuildSecondHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildSecondHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, false, false, true);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildFirstWithMixedDataTypes() {
        try {
            MutableObjectIterator<IntPair> input1 = new UniformIntPairGenerator(500, 40, false);
            final TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleIntPairMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.joinIntPairs(NonReusingHashJoinIteratorITCase.collectIntPairData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction<IntPair, Tuple2<Integer, String>, Tuple2<Integer, String>> matcher = new ReusingHashJoinIteratorITCase.TupleIntPairMatchRemovingMatcher(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            input1 = new UniformIntPairGenerator(500, 40, false);
            generator2.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildSecondHashJoinIterator<IntPair, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildSecondHashJoinIterator(input1, input2, this.pairSerializer, this.pairComparator, this.recordSerializer, this.record2Comparator, this.pairRecordPairComparator, this.memoryManager, this.ioManager, this.parentTask, 1.0, false, false, true);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleIntPairMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildSecondWithMixedDataTypes() {
        try {
            MutableObjectIterator<IntPair> input1 = new UniformIntPairGenerator(500, 40, false);
            final TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleIntPairMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.joinIntPairs(NonReusingHashJoinIteratorITCase.collectIntPairData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction<IntPair, Tuple2<Integer, String>, Tuple2<Integer, String>> matcher = new ReusingHashJoinIteratorITCase.TupleIntPairMatchRemovingMatcher(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            input1 = new UniformIntPairGenerator(500, 40, false);
            generator2.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildFirstHashJoinIterator<IntPair, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildFirstHashJoinIterator(input1, input2, this.pairSerializer, this.pairComparator, this.recordSerializer, this.record2Comparator, this.recordPairPairComparator, this.memoryManager, this.ioManager, this.parentTask, 1.0, false, false, true);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleIntPairMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildFirstAndProbeSideOuterJoin() {
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 500, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 1000, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingHashJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.rightOuterJoinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildFirstHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildFirstHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, true, false, false);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildFirstAndBuildSideOuterJoin() {
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 500, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 1000, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingHashJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.leftOuterJoinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildFirstHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildFirstHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, false, true, false);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildFirstAndFullOuterJoin() {
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 500, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 1000, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingHashJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.fullOuterJoinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildFirstHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildFirstHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, true, true, false);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildSecondAndProbeSideOuterJoin() {
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 1000, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingHashJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.leftOuterJoinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildSecondHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildSecondHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, true, false, false);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildSecondAndBuildSideOuterJoin() {
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 1000, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingHashJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.rightOuterJoinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildSecondHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildSecondHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, false, true, false);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBuildSecondAndFullOuterJoin() {
        try {
            TestData.TupleGenerator generator1 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED1, 1000, 4096, RANDOM, RANDOM_LENGTH);
            TestData.TupleGenerator generator2 = new TestData.TupleGenerator(ReusingHashJoinIteratorITCase.SEED2, 500, 2048, RANDOM, RANDOM_LENGTH);
            final TestData.TupleGeneratorIterator input1 = new TestData.TupleGeneratorIterator(generator1, ReusingHashJoinIteratorITCase.INPUT_1_SIZE);
            final TestData.TupleGeneratorIterator input2 = new TestData.TupleGeneratorIterator(generator2, ReusingHashJoinIteratorITCase.INPUT_2_SIZE);
            // collect expected data
            final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> expectedMatchesMap = NonReusingHashJoinIteratorITCase.fullOuterJoinTuples(NonReusingHashJoinIteratorITCase.collectTupleData(input1), NonReusingHashJoinIteratorITCase.collectTupleData(input2));
            final FlatJoinFunction matcher = new ReusingHashJoinIteratorITCase.TupleMatchRemovingJoin(expectedMatchesMap);
            final Collector<Tuple2<Integer, String>> collector = new org.apache.flink.runtime.operators.testutils.DiscardingOutputCollector();
            // reset the generators
            generator1.reset();
            generator2.reset();
            input1.reset();
            input2.reset();
            // compare with iterator values
            ReusingBuildSecondHashJoinIterator<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> iterator = new ReusingBuildSecondHashJoinIterator(input1, input2, this.recordSerializer, this.record1Comparator, this.recordSerializer, this.record2Comparator, this.recordPairComparator, this.memoryManager, ioManager, this.parentTask, 1.0, true, true, false);
            iterator.open();
            while (iterator.callWithNextKey(matcher, collector));
            iterator.close();
            // assert that each expected match was seen
            for (Map.Entry<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> entry : expectedMatchesMap.entrySet()) {
                if (!(entry.getValue().isEmpty())) {
                    Assert.fail((("Collection for key " + (entry.getKey())) + " is not empty"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("An exception occurred during the test: " + (e.getMessage())));
        }
    }

    // --------------------------------------------------------------------------------------------
    // Utilities
    // --------------------------------------------------------------------------------------------
    static final class TupleMatchRemovingJoin implements FlatJoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<Integer, String>> {
        private final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> toRemoveFrom;

        protected TupleMatchRemovingJoin(Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleMatch>> map) {
            this.toRemoveFrom = map;
        }

        @Override
        public void join(Tuple2<Integer, String> rec1, Tuple2<Integer, String> rec2, Collector<Tuple2<Integer, String>> out) throws Exception {
            Integer key = (rec1 != null) ? rec1.f0 : rec2.f0;
            String value1 = (rec1 != null) ? rec1.f1 : null;
            String value2 = (rec2 != null) ? rec2.f1 : null;
            // System.err.println("rec1 key = "+key+"  rec2 key= "+rec2.getField(0, TestData.Key.class));
            Collection<NonReusingHashJoinIteratorITCase.TupleMatch> matches = this.toRemoveFrom.get(key);
            if (matches == null) {
                Assert.fail((((((("Match " + key) + " - ") + value1) + ":") + value2) + " is unexpected."));
            }
            Assert.assertTrue(((((("Produced match was not contained: " + key) + " - ") + value1) + ":") + value2), matches.remove(new NonReusingHashJoinIteratorITCase.TupleMatch(value1, value2)));
            if (matches.isEmpty()) {
                this.toRemoveFrom.remove(key);
            }
        }
    }

    static final class TupleIntPairMatchRemovingMatcher extends AbstractRichFunction implements FlatJoinFunction<IntPair, Tuple2<Integer, String>, Tuple2<Integer, String>> {
        private final Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleIntPairMatch>> toRemoveFrom;

        protected TupleIntPairMatchRemovingMatcher(Map<Integer, Collection<NonReusingHashJoinIteratorITCase.TupleIntPairMatch>> map) {
            this.toRemoveFrom = map;
        }

        @Override
        public void join(IntPair rec1, Tuple2<Integer, String> rec2, Collector<Tuple2<Integer, String>> out) throws Exception {
            final int k = rec1.getKey();
            final int v = rec1.getValue();
            final Integer key = rec2.f0;
            final String value = rec2.f1;
            Assert.assertTrue("Key does not match for matching IntPair Tuple combination.", (k == key));
            Collection<NonReusingHashJoinIteratorITCase.TupleIntPairMatch> matches = this.toRemoveFrom.get(key);
            if (matches == null) {
                Assert.fail((((((("Match " + key) + " - ") + v) + ":") + value) + " is unexpected."));
            }
            Assert.assertTrue(((((("Produced match was not contained: " + key) + " - ") + v) + ":") + value), matches.remove(new NonReusingHashJoinIteratorITCase.TupleIntPairMatch(v, value)));
            if (matches.isEmpty()) {
                this.toRemoveFrom.remove(key);
            }
        }
    }

    static final class IntPairTuplePairComparator extends TypePairComparator<IntPair, Tuple2<Integer, String>> {
        private int reference;

        @Override
        public void setReference(IntPair reference) {
            this.reference = reference.getKey();
        }

        @Override
        public boolean equalToReference(Tuple2<Integer, String> candidate) {
            try {
                return (candidate.f0) == (this.reference);
            } catch (NullPointerException npex) {
                throw new NullKeyFieldException();
            }
        }

        @Override
        public int compareToReference(Tuple2<Integer, String> candidate) {
            try {
                return (candidate.f0) - (this.reference);
            } catch (NullPointerException npex) {
                throw new NullKeyFieldException();
            }
        }
    }

    static final class TupleIntPairPairComparator extends TypePairComparator<Tuple2<Integer, String>, IntPair> {
        private int reference;

        @Override
        public void setReference(Tuple2<Integer, String> reference) {
            this.reference = reference.f0;
        }

        @Override
        public boolean equalToReference(IntPair candidate) {
            return (this.reference) == (candidate.getKey());
        }

        @Override
        public int compareToReference(IntPair candidate) {
            return (candidate.getKey()) - (this.reference);
        }
    }
}

