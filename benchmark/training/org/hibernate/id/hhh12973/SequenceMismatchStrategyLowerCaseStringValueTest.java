/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id.hhh12973;


import SequenceMismatchStrategy.EXCEPTION;
import SequenceMismatchStrategy.FIX;
import SequenceMismatchStrategy.LOG;
import org.hibernate.id.SequenceMismatchStrategy;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12973")
public class SequenceMismatchStrategyLowerCaseStringValueTest extends BaseUnitTestCase {
    @Test
    public void test() {
        Assert.assertEquals(EXCEPTION, SequenceMismatchStrategy.interpret("exception"));
        Assert.assertEquals(LOG, SequenceMismatchStrategy.interpret("log"));
        Assert.assertEquals(FIX, SequenceMismatchStrategy.interpret("fix"));
    }
}

