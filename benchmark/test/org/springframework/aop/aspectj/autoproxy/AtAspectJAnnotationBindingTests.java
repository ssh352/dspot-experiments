/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.aop.aspectj.autoproxy;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author Adrian Colyer
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class AtAspectJAnnotationBindingTests {
    private AnnotatedTestBean testBean;

    private ClassPathXmlApplicationContext ctx;

    @Test
    public void testAnnotationBindingInAroundAdvice() {
        Assert.assertEquals("this value doThis", testBean.doThis());
        Assert.assertEquals("that value doThat", testBean.doThat());
        Assert.assertEquals(2, testBean.doArray().length);
    }

    @Test
    public void testNoMatchingWithoutAnnotationPresent() {
        Assert.assertEquals("doTheOther", testBean.doTheOther());
    }

    @Test
    public void testPointcutEvaluatedAgainstArray() {
        ctx.getBean("arrayFactoryBean");
    }
}
