/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.statement;


import java.sql.Types;
import org.assertj.core.data.Offset;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestCallable {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    private Handle h;

    @Test
    public void testStatement() {
        OutParameters ret = h.createCall("? = CALL TO_DEGREES(?)").registerOutParameter(0, Types.DOUBLE).bind(1, 100.0).invoke();
        Double expected = Math.toDegrees(100.0);
        assertThat(ret.getDouble(0)).isEqualTo(expected, Offset.offset(0.001));
        assertThat(ret.getLong(0).longValue()).isEqualTo(expected.longValue());
        assertThat(ret.getShort(0).shortValue()).isEqualTo(expected.shortValue());
        assertThat(ret.getInt(0).intValue()).isEqualTo(expected.intValue());
        assertThat(ret.getFloat(0).floatValue()).isEqualTo(expected.floatValue(), Offset.offset(0.001F));
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> ret.getDate(1));
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> ret.getDate(2));
    }

    @Test
    public void testStatementWithNamedParam() {
        OutParameters ret = h.createCall(":x = CALL TO_DEGREES(:y)").registerOutParameter("x", Types.DOUBLE).bind("y", 100.0).invoke();
        Double expected = Math.toDegrees(100.0);
        assertThat(ret.getDouble("x")).isEqualTo(expected, Offset.offset(0.001));
        assertThat(ret.getLong("x").longValue()).isEqualTo(expected.longValue());
        assertThat(ret.getShort("x").shortValue()).isEqualTo(expected.shortValue());
        assertThat(ret.getInt("x").intValue()).isEqualTo(expected.intValue());
        assertThat(ret.getFloat("x")).isEqualTo(expected.floatValue());
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> ret.getDate("x"));
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> ret.getDate("y"));
    }
}

