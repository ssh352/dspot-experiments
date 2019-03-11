package org.jabref.logic.layout.format;


import org.jabref.logic.layout.ParamLayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AuthorsTest {
    @Test
    public void testStandardUsage() {
        ParamLayoutFormatter a = new Authors();
        Assertions.assertEquals("B. C. Bruce, C. Manson and J. Jumper", a.format("Bob Croydon Bruce and Charles Manson and Jolly Jumper"));
    }

    @Test
    public void testStandardUsageOne() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("fullname, LastFirst, Comma, Comma");
        Assertions.assertEquals("Bruce, Bob Croydon, Jumper, Jolly", a.format("Bob Croydon Bruce and Jolly Jumper"));
    }

    @Test
    public void testStandardUsageTwo() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("initials");
        Assertions.assertEquals("B. C. Bruce and J. Jumper", a.format("Bob Croydon Bruce and Jolly Jumper"));
    }

    @Test
    public void testStandardUsageThree() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("fullname, LastFirst, Comma");
        Assertions.assertEquals("Bruce, Bob Croydon, Manson, Charles and Jumper, Jolly", a.format("Bob Croydon Bruce and Charles Manson and Jolly Jumper"));
    }

    @Test
    public void testStandardUsageFour() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("fullname, LastFirst, Comma, 2");
        Assertions.assertEquals("Bruce, Bob Croydon et al.", a.format("Bob Croydon Bruce and Charles Manson and Jolly Jumper"));
    }

    @Test
    public void testStandardUsageFive() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("fullname, LastFirst, Comma, 3");
        Assertions.assertEquals("Bruce, Bob Croydon et al.", a.format("Bob Croydon Bruce and Charles Manson and Jolly Jumper and Chuck Chuckles"));
    }

    @Test
    public void testStandardUsageSix() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("fullname, LastFirst, Comma, 3, 2");
        Assertions.assertEquals("Bruce, Bob Croydon, Manson, Charles et al.", a.format("Bob Croydon Bruce and Charles Manson and Jolly Jumper and Chuck Chuckles"));
    }

    @Test
    public void testSpecialEtAl() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("fullname, LastFirst, Comma, 3, etal= and a few more");
        Assertions.assertEquals("Bruce, Bob Croydon and a few more", a.format("Bob Croydon Bruce and Charles Manson and Jolly Jumper and Chuck Chuckles"));
    }

    @Test
    public void testStandardUsageNull() {
        ParamLayoutFormatter a = new Authors();
        Assertions.assertEquals("", a.format(null));
    }

    @Test
    public void testStandardOxford() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("Oxford");
        Assertions.assertEquals("B. C. Bruce, C. Manson, and J. Jumper", a.format("Bob Croydon Bruce and Charles Manson and Jolly Jumper"));
    }

    @Test
    public void testStandardOxfordFullName() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("FullName,Oxford");
        Assertions.assertEquals("Bob Croydon Bruce, Charles Manson, and Jolly Jumper", a.format("Bruce, Bob Croydon and Charles Manson and Jolly Jumper"));
    }

    @Test
    public void testStandardCommaFullName() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("FullName,Comma,Comma");
        Assertions.assertEquals("Bob Croydon Bruce, Charles Manson, Jolly Jumper", a.format("Bruce, Bob Croydon and Charles Manson and Jolly Jumper"));
    }

    @Test
    public void testStandardAmpFullName() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("FullName,Amp");
        Assertions.assertEquals("Bob Croydon Bruce, Charles Manson & Jolly Jumper", a.format("Bruce, Bob Croydon and Charles Manson and Jolly Jumper"));
    }

    @Test
    public void testLastName() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("LastName");
        Assertions.assertEquals("Bruce, von Manson and Jumper", a.format("Bruce, Bob Croydon and Charles von Manson and Jolly Jumper"));
    }

    @Test
    public void testMiddleInitial() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("MiddleInitial");
        Assertions.assertEquals("Bob C. Bruce, Charles K. von Manson and Jolly Jumper", a.format("Bruce, Bob Croydon and Charles Kermit von Manson and Jumper, Jolly"));
    }

    @Test
    public void testNoPeriod() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("NoPeriod");
        Assertions.assertEquals("B C Bruce, C K von Manson and J Jumper", a.format("Bruce, Bob Croydon and Charles Kermit von Manson and Jumper, Jolly"));
    }

    @Test
    public void testEtAl() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("2,1");
        Assertions.assertEquals("B. C. Bruce et al.", a.format("Bruce, Bob Croydon and Charles Kermit von Manson and Jumper, Jolly"));
    }

    @Test
    public void testEtAlNotEnoughAuthors() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("2,1");
        Assertions.assertEquals("B. C. Bruce and C. K. von Manson", a.format("Bruce, Bob Croydon and Charles Kermit von Manson"));
    }

    @Test
    public void testEmptyEtAl() {
        ParamLayoutFormatter a = new Authors();
        a.setArgument("fullname, LastFirst, Comma, 3, etal=");
        Assertions.assertEquals("Bruce, Bob Croydon", a.format("Bob Croydon Bruce and Charles Manson and Jolly Jumper and Chuck Chuckles"));
    }
}

