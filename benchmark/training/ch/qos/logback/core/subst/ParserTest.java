/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.subst;


import Node.Type;
import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created with IntelliJ IDEA.
 * User: ceki
 * Date: 05.08.12
 * Time: 00:15
 * To change this template use File | Settings | File Templates.
 */
public class ParserTest {
    @Test
    public void literal() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("abc");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.LITERAL, "abc");
        Assert.assertEquals(witness, node);
    }

    @Test
    public void literalWithAccolade0() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("{}");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.LITERAL, "{");
        witness.next = new Node(Type.LITERAL, "}");
        Assert.assertEquals(witness, node);
    }

    @Test
    public void literalWithAccolade1() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("%x{a}");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.LITERAL, "%x");
        Node t = witness.next = new Node(Type.LITERAL, "{");
        t.next = new Node(Type.LITERAL, "a");
        t = t.next;
        t.next = new Node(Type.LITERAL, "}");
        Assert.assertEquals(witness, node);
    }

    @Test
    public void literalWithTwoAccolades() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("%x{y} %a{b} c");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.LITERAL, "%x");
        Node t = witness.next = new Node(Type.LITERAL, "{");
        t.next = new Node(Type.LITERAL, "y");
        t = t.next;
        t.next = new Node(Type.LITERAL, "}");
        t = t.next;
        t.next = new Node(Type.LITERAL, " %a");
        t = t.next;
        t.next = new Node(Type.LITERAL, "{");
        t = t.next;
        t.next = new Node(Type.LITERAL, "b");
        t = t.next;
        t.next = new Node(Type.LITERAL, "}");
        t = t.next;
        t.next = new Node(Type.LITERAL, " c");
        node.dump();
        System.out.println("");
        Assert.assertEquals(witness, node);
    }

    @Test
    public void variable() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("${abc}");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.VARIABLE, new Node(Type.LITERAL, "abc"));
        Assert.assertEquals(witness, node);
    }

    @Test
    public void literalVariableLiteral() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("a${b}c");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.LITERAL, "a");
        witness.next = new Node(Type.VARIABLE, new Node(Type.LITERAL, "b"));
        witness.next.next = new Node(Type.LITERAL, "c");
        Assert.assertEquals(witness, node);
    }

    // /LOGBACK-744
    @Test
    public void withColon() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("a:${b}");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.LITERAL, "a");
        Node t = witness.next = new Node(Type.LITERAL, ":");
        t.next = new Node(Type.VARIABLE, new Node(Type.LITERAL, "b"));
        Assert.assertEquals(witness, node);
    }

    @Test
    public void nested() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("a${b${c}}d");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.LITERAL, "a");
        Node bLiteralNode = new Node(Type.LITERAL, "b");
        Node cLiteralNode = new Node(Type.LITERAL, "c");
        Node bVariableNode = new Node(Type.VARIABLE, bLiteralNode);
        Node cVariableNode = new Node(Type.VARIABLE, cLiteralNode);
        bLiteralNode.next = cVariableNode;
        witness.next = bVariableNode;
        witness.next.next = new Node(Type.LITERAL, "d");
        Assert.assertEquals(witness, node);
    }

    @Test
    public void withDefault() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("${b:-c}");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        Node witness = new Node(Type.VARIABLE, new Node(Type.LITERAL, "b"));
        witness.defaultPart = new Node(Type.LITERAL, "c");
        Assert.assertEquals(witness, node);
    }

    @Test
    public void defaultSeparatorOutsideOfAVariable() throws ScanException {
        Tokenizer tokenizer = new Tokenizer("{a:-b}");
        Parser parser = new Parser(tokenizer.tokenize());
        Node node = parser.parse();
        dump(node);
        Node witness = new Node(Type.LITERAL, "{");
        Node t = witness.next = new Node(Type.LITERAL, "a");
        t.next = new Node(Type.LITERAL, ":-");
        t = t.next;
        t.next = new Node(Type.LITERAL, "b");
        t = t.next;
        t.next = new Node(Type.LITERAL, "}");
        Assert.assertEquals(witness, node);
    }

    @Test
    public void emptyTokenListDoesNotThrowNullPointerException() throws ScanException {
        // An empty token list would be returned from Tokenizer.tokenize()
        // if it were constructed with an empty string. The parser should
        // be able to handle this.
        Parser parser = new Parser(new ArrayList<Token>());
        parser.parse();
    }
}

