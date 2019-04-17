/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.persistence.jackson.api.score.buildin.bendablebigdecimal;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonJsonSerializerAndDeserializerTest;


public class BendableBigDecimalScoreJacksonJsonSerializerAndDeserializerTest extends AbstractScoreJacksonJsonSerializerAndDeserializerTest {
    @Test
    public void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new BendableBigDecimalScoreJacksonJsonSerializerAndDeserializerTest.TestBendableBigDecimalScoreWrapper(null));
        BendableBigDecimalScore score = BendableBigDecimalScore.of(new BigDecimal[]{ new BigDecimal("1000.0001"), new BigDecimal("200.0020") }, new BigDecimal[]{ new BigDecimal("34.4300") });
        assertSerializeAndDeserialize(score, new BendableBigDecimalScoreJacksonJsonSerializerAndDeserializerTest.TestBendableBigDecimalScoreWrapper(score));
        score = BendableBigDecimalScore.ofUninitialized((-7), new BigDecimal[]{ new BigDecimal("1000.0001"), new BigDecimal("200.0020") }, new BigDecimal[]{ new BigDecimal("34.4300") });
        assertSerializeAndDeserialize(score, new BendableBigDecimalScoreJacksonJsonSerializerAndDeserializerTest.TestBendableBigDecimalScoreWrapper(score));
    }

    public static class TestBendableBigDecimalScoreWrapper extends AbstractScoreJacksonJsonSerializerAndDeserializerTest.TestScoreWrapper<BendableBigDecimalScore> {
        @JsonSerialize(using = BendableBigDecimalScoreJacksonJsonSerializer.class)
        @JsonDeserialize(using = BendableBigDecimalScoreJacksonJsonDeserializer.class)
        private BendableBigDecimalScore score;

        @SuppressWarnings("unused")
        private TestBendableBigDecimalScoreWrapper() {
        }

        public TestBendableBigDecimalScoreWrapper(BendableBigDecimalScore score) {
            this.score = score;
        }

        @Override
        public BendableBigDecimalScore getScore() {
            return score;
        }
    }
}
