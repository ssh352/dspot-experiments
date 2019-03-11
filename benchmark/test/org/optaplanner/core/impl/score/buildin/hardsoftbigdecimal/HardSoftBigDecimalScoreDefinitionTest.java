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
package org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal;


import org.junit.Assert;
import org.junit.Test;


// Optimistic and pessimistic bounds are currently not supported for this score definition
public class HardSoftBigDecimalScoreDefinitionTest {
    @Test
    public void getLevelsSize() {
        Assert.assertEquals(2, new HardSoftBigDecimalScoreDefinition().getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        Assert.assertArrayEquals(new String[]{ "hard score", "soft score" }, new HardSoftBigDecimalScoreDefinition().getLevelLabels());
    }

    @Test
    public void getFeasibleLevelsSize() {
        Assert.assertEquals(1, new HardSoftBigDecimalScoreDefinition().getFeasibleLevelsSize());
    }
}

