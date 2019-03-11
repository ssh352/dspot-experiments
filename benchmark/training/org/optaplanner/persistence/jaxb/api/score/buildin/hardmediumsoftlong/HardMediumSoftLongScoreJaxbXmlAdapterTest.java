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
package org.optaplanner.persistence.jaxb.api.score.buildin.hardmediumsoftlong;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbXmlAdapterTest;


public class HardMediumSoftLongScoreJaxbXmlAdapterTest extends AbstractScoreJaxbXmlAdapterTest {
    @Test
    public void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new HardMediumSoftLongScoreJaxbXmlAdapterTest.TestHardMediumSoftLongScoreWrapper(null));
        HardMediumSoftLongScore score = HardMediumSoftLongScore.of(1200L, 30L, 4L);
        assertSerializeAndDeserialize(score, new HardMediumSoftLongScoreJaxbXmlAdapterTest.TestHardMediumSoftLongScoreWrapper(score));
        score = HardMediumSoftLongScore.ofUninitialized((-7), 1200L, 30L, 4L);
        assertSerializeAndDeserialize(score, new HardMediumSoftLongScoreJaxbXmlAdapterTest.TestHardMediumSoftLongScoreWrapper(score));
    }

    @XmlRootElement
    public static class TestHardMediumSoftLongScoreWrapper extends AbstractScoreJaxbXmlAdapterTest.TestScoreWrapper<HardMediumSoftLongScore> {
        @XmlJavaTypeAdapter(HardMediumSoftLongScoreJaxbXmlAdapter.class)
        private HardMediumSoftLongScore score;

        @SuppressWarnings("unused")
        private TestHardMediumSoftLongScoreWrapper() {
        }

        public TestHardMediumSoftLongScoreWrapper(HardMediumSoftLongScore score) {
            this.score = score;
        }

        @Override
        public HardMediumSoftLongScore getScore() {
            return score;
        }
    }
}

