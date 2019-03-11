/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.database.validators;


import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import org.graylog2.plugin.database.validators.Validator;
import org.junit.Assert;
import org.junit.Test;


public class MapValidatorTest {
    @Test
    public void testValidate() throws Exception {
        Validator v = new MapValidator();
        Assert.assertFalse(v.validate(null).passed());
        Assert.assertFalse(v.validate(Collections.emptyList()).passed());
        Assert.assertFalse(v.validate(9001).passed());
        Assert.assertFalse(v.validate("foo").passed());
        Map<String, String> actuallyFilledMap = ImmutableMap.of("foo", "bar", "lol", "wut");
        Assert.assertTrue(v.validate(actuallyFilledMap).passed());
        Assert.assertTrue(v.validate(Collections.emptyMap()).passed());
    }
}

