/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.solver.termination;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.definition.FeasibilityScoreDefinition;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;


public class BestScoreFeasibleTerminationTest {
    @Test
    public void solveTermination() {
        FeasibilityScoreDefinition scoreDefinition = Mockito.mock(FeasibilityScoreDefinition.class);
        Mockito.when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreFeasibleTermination(scoreDefinition, new double[]{  });
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        Mockito.when(solverScope.getScoreDefinition()).thenReturn(new HardSoftScoreDefinition());
        Mockito.when(solverScope.getStartingInitializedScore()).thenReturn(HardSoftScore.of((-100), (-100)));
        Mockito.when(solverScope.isBestSolutionInitialized()).thenReturn(true);
        Mockito.when(solverScope.getBestScore()).thenReturn(HardSoftScore.of((-100), (-100)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(HardSoftScore.of((-80), (-100)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.2, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(HardSoftScore.of((-60), (-100)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.4, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(HardSoftScore.of((-40), (-100)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.6, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(HardSoftScore.of((-20), (-100)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.8, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(HardSoftScore.of(0, (-100)));
        Assert.assertEquals(true, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(1.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
    }

    @Test
    public void phaseTermination() {
        FeasibilityScoreDefinition scoreDefinition = Mockito.mock(FeasibilityScoreDefinition.class);
        Mockito.when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreFeasibleTermination(scoreDefinition, new double[]{  });
        AbstractPhaseScope phaseScope = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScope.getScoreDefinition()).thenReturn(new HardSoftScoreDefinition());
        Mockito.when(phaseScope.getStartingScore()).thenReturn(HardSoftScore.of((-100), (-100)));
        Mockito.when(phaseScope.isBestSolutionInitialized()).thenReturn(true);
        Mockito.when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of((-100), (-100)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of((-80), (-100)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.2, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of((-60), (-100)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.4, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of((-40), (-100)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.6, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of((-20), (-100)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.8, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(HardSoftScore.of(0, (-100)));
        Assert.assertEquals(true, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
    }

    @Test
    public void calculateTimeGradientBendableScoreHHSSS() {
        FeasibilityScoreDefinition scoreDefinition = Mockito.mock(FeasibilityScoreDefinition.class);
        Mockito.when(scoreDefinition.getFeasibleLevelsSize()).thenReturn(2);
        BestScoreFeasibleTermination termination = new BestScoreFeasibleTermination(scoreDefinition, new double[]{ 0.75 });
        // Normal cases
        // Smack in the middle
        Assert.assertEquals(0.6, termination.calculateFeasibilityTimeGradient(BendableScore.of(new int[]{ -10, -100 }, new int[]{ -50, -60, -70 }), BendableScore.of(new int[]{ -4, -40 }, new int[]{ -50, -60, -70 })), 0.0);
    }
}

