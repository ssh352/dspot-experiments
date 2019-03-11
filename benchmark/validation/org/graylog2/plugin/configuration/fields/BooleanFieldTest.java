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
package org.graylog2.plugin.configuration.fields;


import BooleanField.FIELD_TYPE;
import ConfigurationField.Optional.OPTIONAL;
import org.junit.Assert;
import org.junit.Test;


public class BooleanFieldTest {
    @Test
    public void testGetFieldType() throws Exception {
        BooleanField f = new BooleanField("name", "Name", false, "description");
        Assert.assertEquals(FIELD_TYPE, f.getFieldType());
    }

    @Test
    public void testGetName() throws Exception {
        BooleanField f = new BooleanField("name", "Name", false, "description");
        Assert.assertEquals("name", f.getName());
    }

    @Test
    public void testGetHumanName() throws Exception {
        BooleanField f = new BooleanField("name", "Name", false, "description");
        Assert.assertEquals("Name", f.getHumanName());
    }

    @Test
    public void testGetDescription() throws Exception {
        BooleanField f = new BooleanField("name", "Name", false, "description");
        Assert.assertEquals("description", f.getDescription());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        BooleanField f = new BooleanField("name", "Name", true, "description");
        Assert.assertEquals(true, f.getDefaultValue());
    }

    @Test
    public void testIsOptional() throws Exception {
        BooleanField f = new BooleanField("name", "Name", true, "description");
        Assert.assertEquals(OPTIONAL, f.isOptional());
    }

    @Test
    public void testGetAttributes() throws Exception {
        // Boolean field has no attributes.
        BooleanField f = new BooleanField("name", "Name", true, "description");
        Assert.assertNotNull(f.getAttributes());
    }
}

