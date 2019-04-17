package org.baeldung.properties.parentchild;


import org.baeldung.properties.parentchild.config.ChildConfig;
import org.baeldung.properties.parentchild.config.ParentConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({ @ContextConfiguration(classes = ParentConfig.class), @ContextConfiguration(classes = ChildConfig.class) })
public class ParentChildPropertySourcePropertiesIntegrationTest {
    @Autowired
    private WebApplicationContext wac;

    @Test
    public void givenPropertySource_whenGetPropertyUsingEnv_thenCorrect() {
        final Environment childEnv = wac.getEnvironment();
        final Environment parentEnv = wac.getParent().getEnvironment();
        Assert.assertEquals(parentEnv.getProperty("parent.name"), "parent");
        Assert.assertNull(parentEnv.getProperty("child.name"));
        Assert.assertEquals(childEnv.getProperty("parent.name"), "parent");
        Assert.assertEquals(childEnv.getProperty("child.name"), "child");
    }

    @Test
    public void givenPropertySource_whenGetPropertyUsingValueAnnotation_thenCorrect() {
        final ChildValueHolder childValueHolder = wac.getBean(ChildValueHolder.class);
        final ParentValueHolder parentValueHolder = wac.getParent().getBean(ParentValueHolder.class);
        Assert.assertEquals(parentValueHolder.getParentName(), "parent");
        Assert.assertEquals(parentValueHolder.getChildName(), "-");
        Assert.assertEquals(childValueHolder.getParentName(), "parent");
        Assert.assertEquals(childValueHolder.getChildName(), "child");
    }
}
