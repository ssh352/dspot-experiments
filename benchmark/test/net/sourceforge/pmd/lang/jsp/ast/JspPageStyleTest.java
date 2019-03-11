/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.ast;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class JspPageStyleTest extends AbstractJspNodesTst {
    /**
     * Test parsing of a JSP comment.
     */
    @Test
    public void testComment() {
        Set<ASTJspComment> comments = getNodes(ASTJspComment.class, JspPageStyleTest.JSP_COMMENT);
        Assert.assertEquals("One comment expected!", 1, comments.size());
        ASTJspComment comment = comments.iterator().next();
        Assert.assertEquals("Correct comment content expected!", "some comment", comment.getImage());
    }

    /**
     * Test parsing a JSP directive.
     */
    @Test
    public void testDirective() {
        Set<JspNode> nodes = getNodes(null, JspPageStyleTest.JSP_DIRECTIVE);
        Set<ASTJspDirective> directives = getNodesOfType(ASTJspDirective.class, nodes);
        Assert.assertEquals("One directive expected!", 1, directives.size());
        ASTJspDirective directive = directives.iterator().next();
        Assert.assertEquals("Correct directive name expected!", "page", directive.getName());
        Set<ASTJspDirectiveAttribute> directiveAttrs = getNodesOfType(ASTJspDirectiveAttribute.class, nodes);
        Assert.assertEquals("Two directive attributes expected!", 2, directiveAttrs.size());
        List<ASTJspDirectiveAttribute> attrsList = new java.util.ArrayList(directiveAttrs);
        Collections.sort(attrsList, new Comparator<ASTJspDirectiveAttribute>() {
            public int compare(ASTJspDirectiveAttribute arg0, ASTJspDirectiveAttribute arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
        });
        ASTJspDirectiveAttribute attr = attrsList.get(0);
        Assert.assertEquals("Correct directive attribute name expected!", "language", attr.getName());
        Assert.assertEquals("Correct directive attribute value expected!", "java", attr.getValue());
        attr = attrsList.get(1);
        Assert.assertEquals("Correct directive attribute name expected!", "session", attr.getName());
        Assert.assertEquals("Correct directive attribute value expected!", "true", attr.getValue());
    }

    /**
     * Test parsing of a JSP declaration.
     */
    @Test
    public void testDeclaration() {
        Set<ASTJspDeclaration> declarations = getNodes(ASTJspDeclaration.class, JspPageStyleTest.JSP_DECLARATION);
        Assert.assertEquals("One declaration expected!", 1, declarations.size());
        ASTJspDeclaration declaration = declarations.iterator().next();
        Assert.assertEquals("Correct declaration content expected!", "String someString = \"s\";", declaration.getImage());
    }

    /**
     * Test parsing of a JSP scriptlet.
     */
    @Test
    public void testScriptlet() {
        Set<ASTJspScriptlet> scriptlets = getNodes(ASTJspScriptlet.class, JspPageStyleTest.JSP_SCRIPTLET);
        Assert.assertEquals("One scriptlet expected!", 1, scriptlets.size());
        ASTJspScriptlet scriptlet = scriptlets.iterator().next();
        Assert.assertEquals("Correct scriptlet content expected!", "someString = someString + \"suffix\";", scriptlet.getImage());
    }

    /**
     * Test parsing of a JSP expression.
     */
    @Test
    public void testExpression() {
        Set<ASTJspExpression> expressions = getNodes(ASTJspExpression.class, JspPageStyleTest.JSP_EXPRESSION);
        Assert.assertEquals("One expression expected!", 1, expressions.size());
        ASTJspExpression expression = expressions.iterator().next();
        Assert.assertEquals("Correct expression content expected!", "someString", expression.getImage());
    }

    /**
     * Test parsing of a JSP expression in an attribute.
     */
    @Test
    public void testExpressionInAttribute() {
        Set<ASTJspExpressionInAttribute> expressions = getNodes(ASTJspExpressionInAttribute.class, JspPageStyleTest.JSP_EXPRESSION_IN_ATTRIBUTE);
        Assert.assertEquals("One expression expected!", 1, expressions.size());
        ASTJspExpressionInAttribute expression = expressions.iterator().next();
        Assert.assertEquals("Correct expression content expected!", "style.getClass()", expression.getImage());
    }

    /**
     * Test parsing of a EL expression.
     */
    @Test
    public void testElExpression() {
        Set<ASTElExpression> expressions = getNodes(ASTElExpression.class, JspPageStyleTest.JSP_EL_EXPRESSION);
        Assert.assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = expressions.iterator().next();
        Assert.assertEquals("Correct expression content expected!", "myBean.get(\"${ World }\")", expression.getImage());
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    public void testElExpressionInAttribute() {
        Set<ASTElExpression> expressions = getNodes(ASTElExpression.class, JspPageStyleTest.JSP_EL_EXPRESSION_IN_ATTRIBUTE);
        Assert.assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = expressions.iterator().next();
        Assert.assertEquals("Correct expression content expected!", "myValidator.find(\"\'jsp\'\")", expression.getImage());
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    public void testJsfValueBinding() {
        Set<ASTValueBinding> valueBindings = getNodes(ASTValueBinding.class, JspPageStyleTest.JSF_VALUE_BINDING);
        Assert.assertEquals("One value binding expected!", 1, valueBindings.size());
        ASTValueBinding valueBinding = valueBindings.iterator().next();
        Assert.assertEquals("Correct expression content expected!", "myValidator.find(\"\'jsf\'\")", valueBinding.getImage());
    }

    private static final String JSP_COMMENT = "<html> <%-- some comment --%> </html>";

    private static final String JSP_DIRECTIVE = "<html> <%@ page language=\"java\" session=\'true\'%> </html>";

    private static final String JSP_DECLARATION = "<html><%! String someString = \"s\"; %></html>";

    private static final String JSP_SCRIPTLET = "<html> <% someString = someString + \"suffix\"; %> </html>";

    private static final String JSP_EXPRESSION = "<html><head><title> <%= someString %> </title></head></html>";

    private static final String JSP_EXPRESSION_IN_ATTRIBUTE = "<html> <body> <p class='<%= style.getClass() %>'> Hello </p> </body> </html>";

    private static final String JSP_EL_EXPRESSION = "<html><title>Hello ${myBean.get(\"${ World }\") } .jsp</title></html>";

    private static final String JSP_EL_EXPRESSION_IN_ATTRIBUTE = "<html> <f:validator type=\"get(\'type\').${myValidator.find(\"\'jsp\'\")}\" /> </html>";

    private static final String JSF_VALUE_BINDING = "<html> <body> <p class=\'#{myValidator.find(\"\'jsf\'\")}\'> Hello </p> </body> </html>";
}

