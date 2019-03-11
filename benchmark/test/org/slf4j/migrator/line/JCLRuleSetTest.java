/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.slf4j.migrator.line;


import org.junit.Assert;
import org.junit.Test;


public class JCLRuleSetTest {
    LineConverter jclConverter = new LineConverter(new JCLRuleSet());

    @Test
    public void testImportReplacement() {
        // LogFactory import replacement
        Assert.assertEquals("import org.slf4j.LoggerFactory;", jclConverter.getOneLineReplacement("import org.apache.commons.logging.LogFactory;"));
        // Log import replacement
        Assert.assertEquals("import org.slf4j.Logger;", jclConverter.getOneLineReplacement("import org.apache.commons.logging.Log;"));
    }

    @Test
    public void testLogFactoryGetLogReplacement() {
        // Logger declaration and instanciation without modifier
        Assert.assertEquals("  Logger   l = LoggerFactory.getLogger(MyClass.class);", jclConverter.getOneLineReplacement("  Log   l = LogFactory.getLog(MyClass.class);"));
        // Logger declaration and instanciation with one modifier
        Assert.assertEquals("public Logger mylog=LoggerFactory.getLogger(MyClass.class);", jclConverter.getOneLineReplacement("public Log mylog=LogFactory.getLog(MyClass.class);"));
        // Logger declaration and instanciation with two modifier
        Assert.assertEquals("public static Logger mylog1 = LoggerFactory.getLogger(MyClass.class);", jclConverter.getOneLineReplacement("public static Log mylog1 = LogFactory.getLog(MyClass.class);"));
        // Logger declaration and instanciation with two modifier and comment at the
        // end of line
        Assert.assertEquals("public static Logger mylog1 = LoggerFactory.getLogger(MyClass.class); //logger instanciation and declaration", jclConverter.getOneLineReplacement("public static Log mylog1 = LogFactory.getLog(MyClass.class); //logger instanciation and declaration"));
        // Logger instanciation without declaration and comment at the end of line
        Assert.assertEquals(" myLog = LoggerFactory.getLogger(MyClass.class);//logger instanciation", jclConverter.getOneLineReplacement(" myLog = LogFactory.getLog(MyClass.class);//logger instanciation"));
        // commented Logger declaration and instanciation with two modifier
        Assert.assertEquals("//public static Logger mylog1 = LoggerFactory.getLogger(MyClass.class);", jclConverter.getOneLineReplacement("//public static Log mylog1 = LogFactory.getLog(MyClass.class);"));
        // commented Logger instanciation without declaration
        Assert.assertEquals("// myLog = LoggerFactory.getLogger(MyClass.class);//logger instanciation", jclConverter.getOneLineReplacement("// myLog = LogFactory.getLog(MyClass.class);//logger instanciation"));
    }

    @Test
    public void testLogFactoryGetFactoryReplacement() {
        // Logger declaration and instanciation without modifier
        Assert.assertEquals("Logger l = LoggerFactory.getLogger(MyClass.class);", jclConverter.getOneLineReplacement("Log l = LogFactory.getFactory().getInstance(MyClass.class);"));
        // Logger declaration and instanciation with one modifier
        Assert.assertEquals("public Logger mylog=LoggerFactory.getLogger(MyClass.class);", jclConverter.getOneLineReplacement("public Log mylog=LogFactory.getFactory().getInstance(MyClass.class);"));
        // Logger declaration and instanciation with modifiers
        Assert.assertEquals("public static Logger mylog1 = LoggerFactory.getLogger(MyClass.class);", jclConverter.getOneLineReplacement("public static Log mylog1 = LogFactory.getFactory().getInstance(MyClass.class);"));
        // Logger declaration and instanciation with two modifier and comment at the
        // end of line
        Assert.assertEquals("public static Logger mylog1 = LoggerFactory.getLogger(MyClass.class); //logger instanciation and declaration", jclConverter.getOneLineReplacement("public static Log mylog1 = LogFactory.getFactory().getInstance(MyClass.class); //logger instanciation and declaration"));
        // Logger instanciation without declaration and comment at the end of line
        Assert.assertEquals(" myLog = LoggerFactory.getLogger(MyClass.class);//logger instanciation", jclConverter.getOneLineReplacement(" myLog = LogFactory.getFactory().getInstance(MyClass.class);//logger instanciation"));
        // commented Logger declaration and instanciation with two modifier
        Assert.assertEquals("//public static Logger mylog1 = LoggerFactory.getLogger(MyClass.class);", jclConverter.getOneLineReplacement("//public static Log mylog1 = LogFactory.getFactory().getInstance(MyClass.class);"));
        // commented Logger instanciation without declaration
        Assert.assertEquals("// myLog = LoggerFactory.getLogger(MyClass.class);//logger instanciation", jclConverter.getOneLineReplacement("// myLog = LogFactory.getFactory().getInstance(MyClass.class);//logger instanciation"));
    }

    @Test
    public void testLogDeclarationReplacement() {
        // simple Logger declaration
        Assert.assertEquals("Logger mylog;", jclConverter.getOneLineReplacement("Log mylog;"));
        // Logger declaration with a modifier
        Assert.assertEquals("private Logger mylog;", jclConverter.getOneLineReplacement("private Log mylog;"));
        // Logger declaration with modifiers
        Assert.assertEquals("public static final Logger myLog;", jclConverter.getOneLineReplacement("public static final Log myLog;"));
        // Logger declaration with modifiers and comment at the end of line
        Assert.assertEquals("public Logger myLog;//logger declaration", jclConverter.getOneLineReplacement("public Log myLog;//logger declaration"));
        // commented Logger declaration
        Assert.assertEquals("//private Logger myLog;", jclConverter.getOneLineReplacement("//private Log myLog;"));
    }

    @Test
    public void testMultiLineReplacement() {
        // Logger declaration on a line
        Assert.assertEquals("protected Logger log =", jclConverter.getOneLineReplacement("protected Log log ="));
        // Logger instanciation on the next line
        Assert.assertEquals(" LoggerFactory.getLogger(MyComponent.class);", jclConverter.getOneLineReplacement(" LogFactory.getLog(MyComponent.class);"));
        // Logger declaration on a line
        Assert.assertEquals("protected Logger log ", jclConverter.getOneLineReplacement("protected Log log "));
        // Logger instanciation on the next line
        Assert.assertEquals(" = LoggerFactory.getLogger(MyComponent.class);", jclConverter.getOneLineReplacement(" = LogFactory.getFactory().getInstance(MyComponent.class);"));
    }
}

