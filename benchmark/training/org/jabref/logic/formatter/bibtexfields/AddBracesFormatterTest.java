package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
class AddBracesFormatterTest {
    private AddBracesFormatter formatter;

    @Test
    public void formatAddsSingleEnclosingBraces() {
        Assertions.assertEquals("{test}", formatter.format("test"));
    }

    @Test
    public void formatKeepsUnmatchedBracesAtBeginning() {
        Assertions.assertEquals("{test", formatter.format("{test"));
    }

    @Test
    public void formatKeepsUnmatchedBracesAtEnd() {
        Assertions.assertEquals("test}", formatter.format("test}"));
    }

    @Test
    public void formatKeepsShortString() {
        Assertions.assertEquals("t", formatter.format("t"));
    }

    @Test
    public void formatKeepsEmptyString() {
        Assertions.assertEquals("", formatter.format(""));
    }

    @Test
    public void formatKeepsDoubleEnclosingBraces() {
        Assertions.assertEquals("{{test}}", formatter.format("{{test}}"));
    }

    @Test
    public void formatKeepsTripleEnclosingBraces() {
        Assertions.assertEquals("{{{test}}}", formatter.format("{{{test}}}"));
    }

    @Test
    public void formatKeepsNonMatchingBraces() {
        Assertions.assertEquals("{A} and {B}", formatter.format("{A} and {B}"));
    }

    @Test
    public void formatKeepsOnlyMatchingBraces() {
        Assertions.assertEquals("{{A} and {B}}", formatter.format("{{A} and {B}}"));
    }

    @Test
    public void formatDoesNotRemoveBracesInBrokenString() {
        // We opt here for a conservative approach although one could argue that "A} and {B}" is also a valid return
        Assertions.assertEquals("{A} and {B}}", formatter.format("{A} and {B}}"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("{In CDMA}", formatter.format(formatter.getExampleInput()));
    }
}

