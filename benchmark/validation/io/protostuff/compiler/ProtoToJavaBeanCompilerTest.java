package io.protostuff.compiler;


import STCodeGenerator.errorCount;
import junit.framework.TestCase;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;


/**
 *
 *
 * @author Ryan Rawson
 */
public class ProtoToJavaBeanCompilerTest extends TestCase {
    public void testSimpleLoad() {
        StringTemplateGroup group = STCodeGenerator.getSTG("java_bean_primitives");
        StringTemplate messageBlock = group.getInstanceOf("message_block");
        TestCase.assertEquals(0, errorCount);
    }
}

