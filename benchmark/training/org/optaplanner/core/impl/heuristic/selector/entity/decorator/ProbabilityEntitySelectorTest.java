/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.heuristic.selector.entity.decorator;


import java.util.Iterator;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class ProbabilityEntitySelectorTest {
    @Test
    public void randomSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class, new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"), new TestdataEntity("e4"));
        SelectionProbabilityWeightFactory<TestdataSolution, TestdataEntity> probabilityWeightFactory = ( scoreDirector, entity) -> {
            switch (entity.getCode()) {
                case "e1" :
                    return 1000.0;
                case "e2" :
                    return 200.0;
                case "e3" :
                    return 30.0;
                case "e4" :
                    return 4.0;
                default :
                    throw new IllegalStateException((("Unknown entity (" + entity) + ")."));
            }
        };
        EntitySelector entitySelector = new ProbabilityEntitySelector(childEntitySelector, SelectionCacheType.STEP, probabilityWeightFactory);
        Random workingRandom = Mockito.mock(Random.class);
        Mockito.when(workingRandom.nextDouble()).thenReturn((1222.0 / 1234.0), (111.0 / 1234.0), 0.0, (1230.0 / 1234.0), (1199.0 / 1234.0));
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        Mockito.when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        entitySelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        Mockito.when(phaseScopeA.getWorkingRandom()).thenReturn(workingRandom);
        entitySelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        Mockito.when(stepScopeA1.getWorkingRandom()).thenReturn(workingRandom);
        entitySelector.stepStarted(stepScopeA1);
        Assert.assertEquals(true, entitySelector.isCountable());
        Assert.assertEquals(true, entitySelector.isNeverEnding());
        Assert.assertEquals(4L, entitySelector.getSize());
        Iterator<Object> iterator = entitySelector.iterator();
        Assert.assertTrue(iterator.hasNext());
        PlannerAssert.assertCode("e3", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        PlannerAssert.assertCode("e1", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        PlannerAssert.assertCode("e1", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        PlannerAssert.assertCode("e4", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        PlannerAssert.assertCode("e2", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        entitySelector.stepEnded(stepScopeA1);
        entitySelector.phaseEnded(phaseScopeA);
        entitySelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(childEntitySelector, 1, 1, 1);
        Mockito.verify(childEntitySelector, Mockito.times(1)).iterator();
    }

    @Test
    public void isCountable() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        EntitySelector entitySelector = new ProbabilityEntitySelector(childEntitySelector, SelectionCacheType.STEP, null);
        Assert.assertTrue(entitySelector.isCountable());
    }

    @Test
    public void isNeverEnding() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        EntitySelector entitySelector = new ProbabilityEntitySelector(childEntitySelector, SelectionCacheType.STEP, null);
        Assert.assertTrue(entitySelector.isNeverEnding());
    }

    @Test
    public void getSize() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class, new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"), new TestdataEntity("e4"));
        SelectionProbabilityWeightFactory<TestdataSolution, TestdataEntity> probabilityWeightFactory = ( scoreDirector, entity) -> {
            switch (entity.getCode()) {
                case "e1" :
                    return 1000.0;
                case "e2" :
                    return 200.0;
                case "e3" :
                    return 30.0;
                case "e4" :
                    return 4.0;
                default :
                    throw new IllegalStateException((("Unknown entity (" + entity) + ")."));
            }
        };
        ProbabilityEntitySelector entitySelector = new ProbabilityEntitySelector(childEntitySelector, SelectionCacheType.STEP, probabilityWeightFactory);
        entitySelector.constructCache(Mockito.mock(DefaultSolverScope.class));
        Assert.assertEquals(4, entitySelector.getSize());
    }

    @Test(expected = IllegalStateException.class)
    public void withNeverEndingSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        Mockito.when(childEntitySelector.isNeverEnding()).thenReturn(true);
        SelectionProbabilityWeightFactory prob = Mockito.mock(SelectionProbabilityWeightFactory.class);
        ProbabilityEntitySelector entitySelector = new ProbabilityEntitySelector(childEntitySelector, SelectionCacheType.STEP, prob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withoutCachedSelectionType() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        SelectionProbabilityWeightFactory prob = Mockito.mock(SelectionProbabilityWeightFactory.class);
        ProbabilityEntitySelector entitySelector = new ProbabilityEntitySelector(childEntitySelector, SelectionCacheType.JUST_IN_TIME, prob);
    }
}
