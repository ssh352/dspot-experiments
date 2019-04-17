/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.heuristic.selector.value.decorator;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.nullable.TestdataNullableEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class InitializedValueSelectorTest {
    @Test
    public void originalSelectionNullable() {
        EntityDescriptor entityDescriptor = TestdataNullableEntity.buildEntityDescriptor();
        TestdataNullableEntity e1 = new TestdataNullableEntity("e1");
        // This variable is unable to have entities as values, but it's an interesting nullable test anyway
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("value");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = null;
        TestdataValue v3 = new TestdataValue("v3");
        ValueSelector childValueSelector = SelectorTestUtils.mockValueSelector(variableDescriptor, v1, v2, v3);
        ValueSelector valueSelector = new InitializedValueSelector(childValueSelector);
        Mockito.verify(childValueSelector, Mockito.times(1)).isNeverEnding();
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        valueSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfValueSelectorForEntity(valueSelector, e1, PlannerAssert.DO_NOT_ASSERT_SIZE, "v1", null, "v3");
        e1.setValue(v1);
        valueSelector.stepEnded(stepScopeA1);
        valueSelector.phaseEnded(phaseScopeA);
        valueSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(childValueSelector, 1, 1, 1);
        Mockito.verify(childValueSelector, Mockito.times(1)).iterator(ArgumentMatchers.any());
    }

    @Test
    public void originalSelectionChained() {
        EntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("chainedObject");
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1");
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2");
        ValueSelector childValueSelector = SelectorTestUtils.mockValueSelector(variableDescriptor, a0, a1, a2);
        ValueSelector valueSelector = new InitializedValueSelector(childValueSelector);
        Mockito.verify(childValueSelector, Mockito.times(1)).isNeverEnding();
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        valueSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA1);
        PlannerAssert.assertAllCodesOfValueSelectorForEntity(valueSelector, a1, PlannerAssert.DO_NOT_ASSERT_SIZE, "a0");
        a1.setChainedObject(a0);
        valueSelector.stepEnded(stepScopeA1);
        AbstractStepScope stepScopeA2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA2);
        PlannerAssert.assertAllCodesOfValueSelectorForEntity(valueSelector, a2, PlannerAssert.DO_NOT_ASSERT_SIZE, "a0", "a1");
        a2.setChainedObject(a1);
        valueSelector.stepEnded(stepScopeA2);
        valueSelector.phaseEnded(phaseScopeA);
        AbstractPhaseScope phaseScopeB = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeB);
        AbstractStepScope stepScopeB1 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB1);
        PlannerAssert.assertAllCodesOfValueSelectorForEntity(valueSelector, a1, PlannerAssert.DO_NOT_ASSERT_SIZE, "a0", "a1", "a2");
        valueSelector.stepEnded(stepScopeB1);
        AbstractStepScope stepScopeB2 = Mockito.mock(AbstractStepScope.class);
        Mockito.when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB2);
        PlannerAssert.assertAllCodesOfValueSelectorForEntity(valueSelector, a2, PlannerAssert.DO_NOT_ASSERT_SIZE, "a0", "a1", "a2");
        valueSelector.stepEnded(stepScopeB2);
        valueSelector.phaseEnded(phaseScopeB);
        valueSelector.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(childValueSelector, 1, 2, 4);
        Mockito.verify(childValueSelector, Mockito.times(4)).iterator(ArgumentMatchers.any());
    }
}
