package org.jabref.model.entry;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static BibtexEntryTypes.ARTICLE;


public class CanonicalBibEntryTest {
    @Test
    public void simpleCanonicalRepresentation() {
        BibEntry e = new BibEntry(ARTICLE);
        e.setCiteKey("key");
        e.setField("author", "abc");
        e.setField("title", "def");
        e.setField("journal", "hij");
        String canonicalRepresentation = CanonicalBibtexEntry.getCanonicalRepresentation(e);
        Assertions.assertEquals("@article{key,\n  author = {abc},\n  journal = {hij},\n  title = {def}\n}", canonicalRepresentation);
    }

    @Test
    public void canonicalRepresentationWithNewlines() {
        BibEntry e = new BibEntry(ARTICLE);
        e.setCiteKey("key");
        e.setField("abstract", "line 1\nline 2");
        String canonicalRepresentation = CanonicalBibtexEntry.getCanonicalRepresentation(e);
        Assertions.assertEquals("@article{key,\n  abstract = {line 1\nline 2}\n}", canonicalRepresentation);
    }
}

