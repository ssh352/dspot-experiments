/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class BasicPlumbingTerminationTest {
    @Test
    public void addProblemFactChangeWithoutDaemon() {
        AtomicInteger count = new AtomicInteger(0);
        BasicPlumbingTermination basicPlumbingTermination = new BasicPlumbingTermination(false);
        Assert.assertEquals(false, basicPlumbingTermination.waitForRestartSolverDecision());
        basicPlumbingTermination.addProblemFactChange(( scoreDirector) -> count.getAndIncrement());
        Assert.assertEquals(true, basicPlumbingTermination.waitForRestartSolverDecision());
        Assert.assertEquals(0, count.get());
        basicPlumbingTermination.startProblemFactChangesProcessing().removeIf(( problemFactChange) -> {
            problemFactChange.doChange(null);
            return true;
        });
        Assert.assertEquals(false, basicPlumbingTermination.waitForRestartSolverDecision());
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void addProblemFactChangesWithoutDaemon() {
        AtomicInteger count = new AtomicInteger(0);
        BasicPlumbingTermination basicPlumbingTermination = new BasicPlumbingTermination(false);
        Assert.assertEquals(false, basicPlumbingTermination.waitForRestartSolverDecision());
        basicPlumbingTermination.addProblemFactChanges(Arrays.asList(( scoreDirector) -> count.getAndIncrement(), ( scoreDirector) -> count.getAndAdd(20)));
        Assert.assertEquals(true, basicPlumbingTermination.waitForRestartSolverDecision());
        Assert.assertEquals(0, count.get());
        basicPlumbingTermination.startProblemFactChangesProcessing().removeIf(( problemFactChange) -> {
            problemFactChange.doChange(null);
            return true;
        });
        Assert.assertEquals(false, basicPlumbingTermination.waitForRestartSolverDecision());
        Assert.assertEquals(21, count.get());
    }
}

