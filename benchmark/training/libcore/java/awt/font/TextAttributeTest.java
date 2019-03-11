/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.awt.font;


import java.awt.font.TextAttribute;
import junit.framework.TestCase;


public class TextAttributeTest extends TestCase {
    public void testAttributeNames() {
        TestCase.assertEquals("java.awt.font.TextAttribute(kerning)", TextAttribute.KERNING.toString());
        TestCase.assertEquals("java.awt.font.TextAttribute(ligatures)", TextAttribute.LIGATURES.toString());
        TestCase.assertEquals("java.awt.font.TextAttribute(tracking)", TextAttribute.TRACKING.toString());
    }

    public void testAttributeValues() {
        TestCase.assertEquals(new Integer(1), TextAttribute.KERNING_ON);
        TestCase.assertEquals(new Integer(1), TextAttribute.LIGATURES_ON);
        TestCase.assertEquals(new Float(0.04F), TextAttribute.TRACKING_LOOSE);
        TestCase.assertEquals(new Float((-0.04F)), TextAttribute.TRACKING_TIGHT);
    }
}

