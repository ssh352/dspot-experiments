/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DateUtil#millisecondOf(Date)}</code>.
 *
 * @author Joel Costigliola
 */
public class DateUtil_millisecondOf_Test {
    @Test
    public void should_return_millisecond_of_date() throws ParseException {
        String dateAsString = "26/08/1994T22:35:17:29";
        Date date = new SimpleDateFormat("dd/MM/yyyy'T'hh:mm:ss:SS").parse(dateAsString);
        Assertions.assertThat(DateUtil.millisecondOf(date)).isEqualTo(29);
    }

    @Test
    public void should_throws_NullPointerException_if_date_parameter_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> millisecondOf(null));
    }
}
