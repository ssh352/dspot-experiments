package org.jabref.model.entry;


import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class AuthorListTest {
    @Test
    public void testFixAuthorNatbib() {
        Assertions.assertEquals("", AuthorList.fixAuthorNatbib(""));
        Assertions.assertEquals("Smith", AuthorList.fixAuthorNatbib("John Smith"));
        Assertions.assertEquals("Smith and Black Brown", AuthorList.fixAuthorNatbib("John Smith and Black Brown, Peter"));
        Assertions.assertEquals("von Neumann et al.", AuthorList.fixAuthorNatbib("John von Neumann and John Smith and Black Brown, Peter"));
        // Is not cached!
        Assertions.assertTrue(AuthorList.fixAuthorNatbib("John von Neumann and John Smith and Black Brown, Peter").equals(AuthorList.fixAuthorNatbib("John von Neumann and John Smith and Black Brown, Peter")));
    }

    @Test
    public void testGetAuthorList() {
        // Test caching in authorCache.
        AuthorList al = AuthorList.parse("John Smith");
        Assertions.assertEquals(al, AuthorList.parse("John Smith"));
        Assertions.assertFalse(al.equals(AuthorList.parse("Smith")));
    }

    @Test
    public void testFixAuthorFirstNameFirstCommas() {
        // No Commas
        Assertions.assertEquals("", AuthorList.fixAuthorFirstNameFirstCommas("", true, false));
        Assertions.assertEquals("", AuthorList.fixAuthorFirstNameFirstCommas("", false, false));
        Assertions.assertEquals("John Smith", AuthorList.fixAuthorFirstNameFirstCommas("John Smith", false, false));
        Assertions.assertEquals("J. Smith", AuthorList.fixAuthorFirstNameFirstCommas("John Smith", true, false));
        // Check caching
        Assertions.assertTrue(AuthorList.fixAuthorFirstNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, false).equals(AuthorList.fixAuthorFirstNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, false)));
        Assertions.assertEquals("John Smith and Peter Black Brown", AuthorList.fixAuthorFirstNameFirstCommas("John Smith and Black Brown, Peter", false, false));
        Assertions.assertEquals("J. Smith and P. Black Brown", AuthorList.fixAuthorFirstNameFirstCommas("John Smith and Black Brown, Peter", true, false));
        // Method description is different than code -> additional comma
        // there
        Assertions.assertEquals("John von Neumann, John Smith and Peter Black Brown", AuthorList.fixAuthorFirstNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", false, false));
        Assertions.assertEquals("J. von Neumann, J. Smith and P. Black Brown", AuthorList.fixAuthorFirstNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, false));
        Assertions.assertEquals("J. P. von Neumann", AuthorList.fixAuthorFirstNameFirstCommas("John Peter von Neumann", true, false));
        // Oxford Commas
        Assertions.assertEquals("", AuthorList.fixAuthorFirstNameFirstCommas("", true, true));
        Assertions.assertEquals("", AuthorList.fixAuthorFirstNameFirstCommas("", false, true));
        Assertions.assertEquals("John Smith", AuthorList.fixAuthorFirstNameFirstCommas("John Smith", false, true));
        Assertions.assertEquals("J. Smith", AuthorList.fixAuthorFirstNameFirstCommas("John Smith", true, true));
        // Check caching
        Assertions.assertTrue(AuthorList.fixAuthorFirstNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, true).equals(AuthorList.fixAuthorFirstNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, true)));
        Assertions.assertEquals("John Smith and Peter Black Brown", AuthorList.fixAuthorFirstNameFirstCommas("John Smith and Black Brown, Peter", false, true));
        Assertions.assertEquals("J. Smith and P. Black Brown", AuthorList.fixAuthorFirstNameFirstCommas("John Smith and Black Brown, Peter", true, true));
        // Method description is different than code -> additional comma
        // there
        Assertions.assertEquals("John von Neumann, John Smith, and Peter Black Brown", AuthorList.fixAuthorFirstNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", false, true));
        Assertions.assertEquals("J. von Neumann, J. Smith, and P. Black Brown", AuthorList.fixAuthorFirstNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, true));
        Assertions.assertEquals("J. P. von Neumann", AuthorList.fixAuthorFirstNameFirstCommas("John Peter von Neumann", true, true));
    }

    @Test
    public void testFixAuthorFirstNameFirst() {
        Assertions.assertEquals("John Smith", AuthorList.fixAuthorFirstNameFirst("John Smith"));
        Assertions.assertEquals("John Smith and Peter Black Brown", AuthorList.fixAuthorFirstNameFirst("John Smith and Black Brown, Peter"));
        Assertions.assertEquals("John von Neumann and John Smith and Peter Black Brown", AuthorList.fixAuthorFirstNameFirst("John von Neumann and John Smith and Black Brown, Peter"));
        Assertions.assertEquals("First von Last, Jr. III", AuthorList.fixAuthorFirstNameFirst("von Last, Jr. III, First"));
        // Check caching
        Assertions.assertTrue(AuthorList.fixAuthorFirstNameFirst("John von Neumann and John Smith and Black Brown, Peter").equals(AuthorList.fixAuthorFirstNameFirst("John von Neumann and John Smith and Black Brown, Peter")));
    }

    @Test
    public void testFixAuthorLastNameFirstCommasNoComma() {
        // No commas before and
        Assertions.assertEquals("", AuthorList.fixAuthorLastNameFirstCommas("", true, false));
        Assertions.assertEquals("", AuthorList.fixAuthorLastNameFirstCommas("", false, false));
        Assertions.assertEquals("Smith, John", AuthorList.fixAuthorLastNameFirstCommas("John Smith", false, false));
        Assertions.assertEquals("Smith, J.", AuthorList.fixAuthorLastNameFirstCommas("John Smith", true, false));
        String a = AuthorList.fixAuthorLastNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, false);
        String b = AuthorList.fixAuthorLastNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, false);
        // Check caching
        Assertions.assertEquals(a, b);
        Assertions.assertTrue(a.equals(b));
        Assertions.assertEquals("Smith, John and Black Brown, Peter", AuthorList.fixAuthorLastNameFirstCommas("John Smith and Black Brown, Peter", false, false));
        Assertions.assertEquals("Smith, J. and Black Brown, P.", AuthorList.fixAuthorLastNameFirstCommas("John Smith and Black Brown, Peter", true, false));
        Assertions.assertEquals("von Neumann, John, Smith, John and Black Brown, Peter", AuthorList.fixAuthorLastNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", false, false));
        Assertions.assertEquals("von Neumann, J., Smith, J. and Black Brown, P.", AuthorList.fixAuthorLastNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, false));
        Assertions.assertEquals("von Neumann, J. P.", AuthorList.fixAuthorLastNameFirstCommas("John Peter von Neumann", true, false));
    }

    @Test
    public void testFixAuthorLastNameFirstCommasOxfordComma() {
        // Oxford Commas
        Assertions.assertEquals("", AuthorList.fixAuthorLastNameFirstCommas("", true, true));
        Assertions.assertEquals("", AuthorList.fixAuthorLastNameFirstCommas("", false, true));
        Assertions.assertEquals("Smith, John", AuthorList.fixAuthorLastNameFirstCommas("John Smith", false, true));
        Assertions.assertEquals("Smith, J.", AuthorList.fixAuthorLastNameFirstCommas("John Smith", true, true));
        String a = AuthorList.fixAuthorLastNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, true);
        String b = AuthorList.fixAuthorLastNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, true);
        // Check caching
        Assertions.assertEquals(a, b);
        Assertions.assertTrue(a.equals(b));
        Assertions.assertEquals("Smith, John and Black Brown, Peter", AuthorList.fixAuthorLastNameFirstCommas("John Smith and Black Brown, Peter", false, true));
        Assertions.assertEquals("Smith, J. and Black Brown, P.", AuthorList.fixAuthorLastNameFirstCommas("John Smith and Black Brown, Peter", true, true));
        Assertions.assertEquals("von Neumann, John, Smith, John, and Black Brown, Peter", AuthorList.fixAuthorLastNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", false, true));
        Assertions.assertEquals("von Neumann, J., Smith, J., and Black Brown, P.", AuthorList.fixAuthorLastNameFirstCommas("John von Neumann and John Smith and Black Brown, Peter", true, true));
        Assertions.assertEquals("von Neumann, J. P.", AuthorList.fixAuthorLastNameFirstCommas("John Peter von Neumann", true, true));
    }

    @Test
    public void testFixAuthorLastNameFirst() {
        // Test helper method
        Assertions.assertEquals("Smith, John", AuthorList.fixAuthorLastNameFirst("John Smith"));
        Assertions.assertEquals("Smith, John and Black Brown, Peter", AuthorList.fixAuthorLastNameFirst("John Smith and Black Brown, Peter"));
        Assertions.assertEquals("von Neumann, John and Smith, John and Black Brown, Peter", AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter"));
        Assertions.assertEquals("von Last, Jr, First", AuthorList.fixAuthorLastNameFirst("von Last, Jr ,First"));
        Assertions.assertTrue(AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter").equals(AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter")));
        // Test Abbreviation == false
        Assertions.assertEquals("Smith, John", AuthorList.fixAuthorLastNameFirst("John Smith", false));
        Assertions.assertEquals("Smith, John and Black Brown, Peter", AuthorList.fixAuthorLastNameFirst("John Smith and Black Brown, Peter", false));
        Assertions.assertEquals("von Neumann, John and Smith, John and Black Brown, Peter", AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter", false));
        Assertions.assertEquals("von Last, Jr, First", AuthorList.fixAuthorLastNameFirst("von Last, Jr ,First", false));
        Assertions.assertTrue(AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter", false).equals(AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter", false)));
        // Test Abbreviate == true
        Assertions.assertEquals("Smith, J.", AuthorList.fixAuthorLastNameFirst("John Smith", true));
        Assertions.assertEquals("Smith, J. and Black Brown, P.", AuthorList.fixAuthorLastNameFirst("John Smith and Black Brown, Peter", true));
        Assertions.assertEquals("von Neumann, J. and Smith, J. and Black Brown, P.", AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter", true));
        Assertions.assertEquals("von Last, Jr, F.", AuthorList.fixAuthorLastNameFirst("von Last, Jr ,First", true));
        Assertions.assertTrue(AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter", true).equals(AuthorList.fixAuthorLastNameFirst("John von Neumann and John Smith and Black Brown, Peter", true)));
    }

    @Test
    public void testFixAuthorLastNameOnlyCommas() {
        // No comma before and
        Assertions.assertEquals("", AuthorList.fixAuthorLastNameOnlyCommas("", false));
        Assertions.assertEquals("Smith", AuthorList.fixAuthorLastNameOnlyCommas("John Smith", false));
        Assertions.assertEquals("Smith", AuthorList.fixAuthorLastNameOnlyCommas("Smith, Jr, John", false));
        Assertions.assertTrue(AuthorList.fixAuthorLastNameOnlyCommas("John von Neumann and John Smith and Black Brown, Peter", false).equals(AuthorList.fixAuthorLastNameOnlyCommas("John von Neumann and John Smith and Black Brown, Peter", false)));
        Assertions.assertEquals("von Neumann, Smith and Black Brown", AuthorList.fixAuthorLastNameOnlyCommas("John von Neumann and John Smith and Black Brown, Peter", false));
        // Oxford Comma
        Assertions.assertEquals("", AuthorList.fixAuthorLastNameOnlyCommas("", true));
        Assertions.assertEquals("Smith", AuthorList.fixAuthorLastNameOnlyCommas("John Smith", true));
        Assertions.assertEquals("Smith", AuthorList.fixAuthorLastNameOnlyCommas("Smith, Jr, John", true));
        Assertions.assertTrue(AuthorList.fixAuthorLastNameOnlyCommas("John von Neumann and John Smith and Black Brown, Peter", true).equals(AuthorList.fixAuthorLastNameOnlyCommas("John von Neumann and John Smith and Black Brown, Peter", true)));
        Assertions.assertEquals("von Neumann, Smith, and Black Brown", AuthorList.fixAuthorLastNameOnlyCommas("John von Neumann and John Smith and Black Brown, Peter", true));
    }

    @Test
    public void testFixAuthorForAlphabetization() {
        Assertions.assertEquals("Smith, J.", AuthorList.fixAuthorForAlphabetization("John Smith"));
        Assertions.assertEquals("Neumann, J.", AuthorList.fixAuthorForAlphabetization("John von Neumann"));
        Assertions.assertEquals("Neumann, J.", AuthorList.fixAuthorForAlphabetization("J. von Neumann"));
        Assertions.assertEquals("Neumann, J. and Smith, J. and Black Brown, Jr., P.", AuthorList.fixAuthorForAlphabetization("John von Neumann and John Smith and de Black Brown, Jr., Peter"));
    }

    @Test
    public void testSize() {
        Assertions.assertEquals(0, AuthorListTest.size(""));
        Assertions.assertEquals(1, AuthorListTest.size("Bar"));
        Assertions.assertEquals(1, AuthorListTest.size("Foo Bar"));
        Assertions.assertEquals(1, AuthorListTest.size("Foo von Bar"));
        Assertions.assertEquals(1, AuthorListTest.size("von Bar, Foo"));
        Assertions.assertEquals(1, AuthorListTest.size("Bar, Foo"));
        Assertions.assertEquals(1, AuthorListTest.size("Bar, Jr., Foo"));
        Assertions.assertEquals(1, AuthorListTest.size("Bar, Foo"));
        Assertions.assertEquals(2, AuthorListTest.size("John Neumann and Foo Bar"));
        Assertions.assertEquals(2, AuthorListTest.size("John von Neumann and Bar, Jr, Foo"));
        Assertions.assertEquals(3, AuthorListTest.size("John von Neumann and John Smith and Black Brown, Peter"));
        StringBuilder s = new StringBuilder("John von Neumann");
        for (int i = 0; i < 25; i++) {
            Assertions.assertEquals((i + 1), AuthorListTest.size(s.toString()));
            s.append(" and Albert Einstein");
        }
    }

    @Test
    public void testIsEmpty() {
        Assertions.assertTrue(AuthorList.parse("").isEmpty());
        Assertions.assertFalse(AuthorList.parse("Bar").isEmpty());
    }

    @Test
    public void testGetEmptyAuthor() {
        Assertions.assertThrows(Exception.class, () -> AuthorList.parse("").getAuthor(0));
    }

    @Test
    public void testGetAuthor() {
        Author author = AuthorList.parse("John Smith and von Neumann, Jr, John").getAuthor(0);
        Assertions.assertEquals(Optional.of("John"), author.getFirst());
        Assertions.assertEquals(Optional.of("J."), author.getFirstAbbr());
        Assertions.assertEquals("John Smith", author.getFirstLast(false));
        Assertions.assertEquals("J. Smith", author.getFirstLast(true));
        Assertions.assertEquals(Optional.empty(), author.getJr());
        Assertions.assertEquals(Optional.of("Smith"), author.getLast());
        Assertions.assertEquals("Smith, John", author.getLastFirst(false));
        Assertions.assertEquals("Smith, J.", author.getLastFirst(true));
        Assertions.assertEquals("Smith", author.getLastOnly());
        Assertions.assertEquals("Smith, J.", author.getNameForAlphabetization());
        Assertions.assertEquals(Optional.empty(), author.getVon());
        author = AuthorList.parse("Peter Black Brown").getAuthor(0);
        Assertions.assertEquals(Optional.of("Peter Black"), author.getFirst());
        Assertions.assertEquals(Optional.of("P. B."), author.getFirstAbbr());
        Assertions.assertEquals("Peter Black Brown", author.getFirstLast(false));
        Assertions.assertEquals("P. B. Brown", author.getFirstLast(true));
        Assertions.assertEquals(Optional.empty(), author.getJr());
        Assertions.assertEquals(Optional.empty(), author.getVon());
        author = AuthorList.parse("John Smith and von Neumann, Jr, John").getAuthor(1);
        Assertions.assertEquals(Optional.of("John"), author.getFirst());
        Assertions.assertEquals(Optional.of("J."), author.getFirstAbbr());
        Assertions.assertEquals("John von Neumann, Jr", author.getFirstLast(false));
        Assertions.assertEquals("J. von Neumann, Jr", author.getFirstLast(true));
        Assertions.assertEquals(Optional.of("Jr"), author.getJr());
        Assertions.assertEquals(Optional.of("Neumann"), author.getLast());
        Assertions.assertEquals("von Neumann, Jr, John", author.getLastFirst(false));
        Assertions.assertEquals("von Neumann, Jr, J.", author.getLastFirst(true));
        Assertions.assertEquals("von Neumann", author.getLastOnly());
        Assertions.assertEquals("Neumann, Jr, J.", author.getNameForAlphabetization());
        Assertions.assertEquals(Optional.of("von"), author.getVon());
    }

    @Test
    public void testCompanyAuthor() {
        Author author = AuthorList.parse("{JabRef Developers}").getAuthor(0);
        Author expected = new Author(null, null, null, "JabRef Developers", null);
        Assertions.assertEquals(expected, author);
    }

    @Test
    public void testCompanyAuthorAndPerson() {
        Author company = new Author(null, null, null, "JabRef Developers", null);
        Author person = new Author("Stefan", "S.", null, "Kolb", null);
        Assertions.assertEquals(Arrays.asList(company, person), AuthorList.parse("{JabRef Developers} and Stefan Kolb").getAuthors());
    }

    @Test
    public void testCompanyAuthorWithLowerCaseWord() {
        Author author = AuthorList.parse("{JabRef Developers on Fire}").getAuthor(0);
        Author expected = new Author(null, null, null, "JabRef Developers on Fire", null);
        Assertions.assertEquals(expected, author);
    }

    @Test
    public void testAbbreviationWithRelax() {
        Author author = AuthorList.parse("{\\relax Ch}ristoph Cholera").getAuthor(0);
        Author expected = new Author("{\\relax Ch}ristoph", "{\\relax Ch}.", null, "Cholera", null);
        Assertions.assertEquals(expected, author);
    }

    @Test
    public void testGetAuthorsNatbib() {
        Assertions.assertEquals("", AuthorList.parse("").getAsNatbib());
        Assertions.assertEquals("Smith", AuthorList.parse("John Smith").getAsNatbib());
        Assertions.assertEquals("Smith and Black Brown", AuthorList.parse("John Smith and Black Brown, Peter").getAsNatbib());
        Assertions.assertEquals("von Neumann et al.", AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter").getAsNatbib());
        /* [ 1465610 ] (Double-)Names containing hyphen (-) not handled correctly */
        Assertions.assertEquals("Last-Name et al.", AuthorList.parse(("First Second Last-Name" + " and John Smith and Black Brown, Peter")).getAsNatbib());
        // Test caching
        AuthorList al = AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter");
        Assertions.assertTrue(al.getAsNatbib().equals(al.getAsNatbib()));
    }

    @Test
    public void testGetAuthorsLastOnly() {
        // No comma before and
        Assertions.assertEquals("", AuthorList.parse("").getAsLastNames(false));
        Assertions.assertEquals("Smith", AuthorList.parse("John Smith").getAsLastNames(false));
        Assertions.assertEquals("Smith", AuthorList.parse("Smith, Jr, John").getAsLastNames(false));
        Assertions.assertEquals("von Neumann, Smith and Black Brown", AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter").getAsLastNames(false));
        // Oxford comma
        Assertions.assertEquals("", AuthorList.parse("").getAsLastNames(true));
        Assertions.assertEquals("Smith", AuthorList.parse("John Smith").getAsLastNames(true));
        Assertions.assertEquals("Smith", AuthorList.parse("Smith, Jr, John").getAsLastNames(true));
        Assertions.assertEquals("von Neumann, Smith, and Black Brown", AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter").getAsLastNames(true));
        Assertions.assertEquals("von Neumann and Smith", AuthorList.parse("John von Neumann and John Smith").getAsLastNames(false));
    }

    @Test
    public void testGetAuthorsLastFirstNoComma() {
        // No commas before and
        AuthorList al;
        al = AuthorList.parse("");
        Assertions.assertEquals("", al.getAsLastFirstNames(true, false));
        Assertions.assertEquals("", al.getAsLastFirstNames(false, false));
        al = AuthorList.parse("John Smith");
        Assertions.assertEquals("Smith, John", al.getAsLastFirstNames(false, false));
        Assertions.assertEquals("Smith, J.", al.getAsLastFirstNames(true, false));
        al = AuthorList.parse("John Smith and Black Brown, Peter");
        Assertions.assertEquals("Smith, John and Black Brown, Peter", al.getAsLastFirstNames(false, false));
        Assertions.assertEquals("Smith, J. and Black Brown, P.", al.getAsLastFirstNames(true, false));
        al = AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter");
        // Method description is different than code -> additional comma
        // there
        Assertions.assertEquals("von Neumann, John, Smith, John and Black Brown, Peter", al.getAsLastFirstNames(false, false));
        Assertions.assertEquals("von Neumann, J., Smith, J. and Black Brown, P.", al.getAsLastFirstNames(true, false));
        al = AuthorList.parse("John Peter von Neumann");
        Assertions.assertEquals("von Neumann, J. P.", al.getAsLastFirstNames(true, false));
    }

    @Test
    public void testGetAuthorsLastFirstOxfordComma() {
        // Oxford comma
        AuthorList al;
        al = AuthorList.parse("");
        Assertions.assertEquals("", al.getAsLastFirstNames(true, true));
        Assertions.assertEquals("", al.getAsLastFirstNames(false, true));
        al = AuthorList.parse("John Smith");
        Assertions.assertEquals("Smith, John", al.getAsLastFirstNames(false, true));
        Assertions.assertEquals("Smith, J.", al.getAsLastFirstNames(true, true));
        al = AuthorList.parse("John Smith and Black Brown, Peter");
        Assertions.assertEquals("Smith, John and Black Brown, Peter", al.getAsLastFirstNames(false, true));
        Assertions.assertEquals("Smith, J. and Black Brown, P.", al.getAsLastFirstNames(true, true));
        al = AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter");
        Assertions.assertEquals("von Neumann, John, Smith, John, and Black Brown, Peter", al.getAsLastFirstNames(false, true));
        Assertions.assertEquals("von Neumann, J., Smith, J., and Black Brown, P.", al.getAsLastFirstNames(true, true));
        al = AuthorList.parse("John Peter von Neumann");
        Assertions.assertEquals("von Neumann, J. P.", al.getAsLastFirstNames(true, true));
    }

    @Test
    public void testGetAuthorsLastFirstAnds() {
        Assertions.assertEquals("Smith, John", AuthorList.parse("John Smith").getAsLastFirstNamesWithAnd(false));
        Assertions.assertEquals("Smith, John and Black Brown, Peter", AuthorList.parse("John Smith and Black Brown, Peter").getAsLastFirstNamesWithAnd(false));
        Assertions.assertEquals("von Neumann, John and Smith, John and Black Brown, Peter", AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter").getAsLastFirstNamesWithAnd(false));
        Assertions.assertEquals("von Last, Jr, First", AuthorList.parse("von Last, Jr ,First").getAsLastFirstNamesWithAnd(false));
        Assertions.assertEquals("Smith, J.", AuthorList.parse("John Smith").getAsLastFirstNamesWithAnd(true));
        Assertions.assertEquals("Smith, J. and Black Brown, P.", AuthorList.parse("John Smith and Black Brown, Peter").getAsLastFirstNamesWithAnd(true));
        Assertions.assertEquals("von Neumann, J. and Smith, J. and Black Brown, P.", AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter").getAsLastFirstNamesWithAnd(true));
        Assertions.assertEquals("von Last, Jr, F.", AuthorList.parse("von Last, Jr ,First").getAsLastFirstNamesWithAnd(true));
    }

    @Test
    public void testGetAuthorsLastFirstAndsCaching() {
        // getAsLastFirstNamesWithAnd caches its results, therefore we call the method twice using the same arguments
        Assertions.assertEquals("Smith, John", AuthorList.parse("John Smith").getAsLastFirstNamesWithAnd(false));
        Assertions.assertEquals("Smith, John", AuthorList.parse("John Smith").getAsLastFirstNamesWithAnd(false));
        Assertions.assertEquals("Smith, J.", AuthorList.parse("John Smith").getAsLastFirstNamesWithAnd(true));
        Assertions.assertEquals("Smith, J.", AuthorList.parse("John Smith").getAsLastFirstNamesWithAnd(true));
    }

    @Test
    public void testGetAuthorsFirstFirst() {
        AuthorList al;
        al = AuthorList.parse("");
        Assertions.assertEquals("", al.getAsFirstLastNames(true, false));
        Assertions.assertEquals("", al.getAsFirstLastNames(false, false));
        Assertions.assertEquals("", al.getAsFirstLastNames(true, true));
        Assertions.assertEquals("", al.getAsFirstLastNames(false, true));
        al = AuthorList.parse("John Smith");
        Assertions.assertEquals("John Smith", al.getAsFirstLastNames(false, false));
        Assertions.assertEquals("J. Smith", al.getAsFirstLastNames(true, false));
        Assertions.assertEquals("John Smith", al.getAsFirstLastNames(false, true));
        Assertions.assertEquals("J. Smith", al.getAsFirstLastNames(true, true));
        al = AuthorList.parse("John Smith and Black Brown, Peter");
        Assertions.assertEquals("John Smith and Peter Black Brown", al.getAsFirstLastNames(false, false));
        Assertions.assertEquals("J. Smith and P. Black Brown", al.getAsFirstLastNames(true, false));
        Assertions.assertEquals("John Smith and Peter Black Brown", al.getAsFirstLastNames(false, true));
        Assertions.assertEquals("J. Smith and P. Black Brown", al.getAsFirstLastNames(true, true));
        al = AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter");
        Assertions.assertEquals("John von Neumann, John Smith and Peter Black Brown", al.getAsFirstLastNames(false, false));
        Assertions.assertEquals("J. von Neumann, J. Smith and P. Black Brown", al.getAsFirstLastNames(true, false));
        Assertions.assertEquals("John von Neumann, John Smith, and Peter Black Brown", al.getAsFirstLastNames(false, true));
        Assertions.assertEquals("J. von Neumann, J. Smith, and P. Black Brown", al.getAsFirstLastNames(true, true));
        al = AuthorList.parse("John Peter von Neumann");
        Assertions.assertEquals("John Peter von Neumann", al.getAsFirstLastNames(false, false));
        Assertions.assertEquals("John Peter von Neumann", al.getAsFirstLastNames(false, true));
        Assertions.assertEquals("J. P. von Neumann", al.getAsFirstLastNames(true, false));
        Assertions.assertEquals("J. P. von Neumann", al.getAsFirstLastNames(true, true));
    }

    @Test
    public void testGetAuthorsFirstFirstAnds() {
        Assertions.assertEquals("John Smith", AuthorList.parse("John Smith").getAsFirstLastNamesWithAnd());
        Assertions.assertEquals("John Smith and Peter Black Brown", AuthorList.parse("John Smith and Black Brown, Peter").getAsFirstLastNamesWithAnd());
        Assertions.assertEquals("John von Neumann and John Smith and Peter Black Brown", AuthorList.parse("John von Neumann and John Smith and Black Brown, Peter").getAsFirstLastNamesWithAnd());
        Assertions.assertEquals("First von Last, Jr. III", AuthorList.parse("von Last, Jr. III, First").getAsFirstLastNamesWithAnd());
    }

    @Test
    public void testGetAuthorsForAlphabetization() {
        Assertions.assertEquals("Smith, J.", AuthorList.parse("John Smith").getForAlphabetization());
        Assertions.assertEquals("Neumann, J.", AuthorList.parse("John von Neumann").getForAlphabetization());
        Assertions.assertEquals("Neumann, J.", AuthorList.parse("J. von Neumann").getForAlphabetization());
        Assertions.assertEquals("Neumann, J. and Smith, J. and Black Brown, Jr., P.", AuthorList.parse("John von Neumann and John Smith and de Black Brown, Jr., Peter").getForAlphabetization());
    }

    @Test
    public void testRemoveStartAndEndBraces() {
        Assertions.assertEquals("{A}bbb{c}", AuthorList.parse("{A}bbb{c}").getAsLastNames(false));
        Assertions.assertEquals("Vall{\\\'e}e Poussin", AuthorList.parse("{Vall{\\\'e}e Poussin}").getAsLastNames(false));
        Assertions.assertEquals("Poussin", AuthorList.parse("{Vall{\\\'e}e} {Poussin}").getAsLastNames(false));
        Assertions.assertEquals("Poussin", AuthorList.parse("Vall{\\\'e}e Poussin").getAsLastNames(false));
        Assertions.assertEquals("Lastname", AuthorList.parse("Firstname {Lastname}").getAsLastNames(false));
        Assertions.assertEquals("Firstname Lastname", AuthorList.parse("{Firstname Lastname}").getAsLastNames(false));
    }

    @Test
    public void createCorrectInitials() {
        Assertions.assertEquals(Optional.of("J. G."), AuthorList.parse("Hornberg, Johann Gottfried").getAuthor(0).getFirstAbbr());
    }

    @Test
    public void parseNameWithBracesAroundFirstName() throws Exception {
        // TODO: Be more intelligent and abbreviate the first name correctly
        Author expected = new Author("Tse-tung", "{Tse-tung}.", null, "Mao", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("{Tse-tung} Mao"));
    }

    @Test
    public void parseNameWithBracesAroundLastName() throws Exception {
        Author expected = new Author("Hans", "H.", null, "van den Bergen", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("{van den Bergen}, Hans"));
    }

    @Test
    public void parseNameWithHyphenInFirstName() throws Exception {
        Author expected = new Author("Tse-tung", "T.-t.", null, "Mao", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("Tse-tung Mao"));
    }

    @Test
    public void parseNameWithHyphenInLastName() throws Exception {
        Author expected = new Author("Firstname", "F.", null, "Bailey-Jones", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("Firstname Bailey-Jones"));
    }

    @Test
    public void parseNameWithHyphenInLastNameWithInitials() throws Exception {
        Author expected = new Author("E. S.", "E. S.", null, "El-{M}allah", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("E. S. El-{M}allah"));
    }

    @Test
    public void parseNameWithHyphenInLastNameWithEscaped() throws Exception {
        Author expected = new Author("E. S.", "E. S.", null, "{K}ent-{B}oswell", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("E. S. {K}ent-{B}oswell"));
    }

    @Test
    public void parseNameWithHyphenInLastNameWhenLastNameGivenFirst() throws Exception {
        // TODO: Fix abbreviation to be "A."
        Author expected = new Author("?Abdall?h", "?.", null, "al-??li?", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("al-??li?, ?Abdall?h"));
    }

    @Test
    public void parseNameWithBraces() throws Exception {
        Author expected = new Author("H{e}lene", "H.", null, "Fiaux", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("H{e}lene Fiaux"));
    }

    /**
     * This tests the issue described at https://github.com/JabRef/jabref/pull/2669#issuecomment-288519458
     */
    @Test
    public void correctNamesWithOneComma() throws Exception {
        Author expected = new Author("Alexander der Gro?e", "A. d. G.", null, "Canon der Barbar", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("Canon der Barbar, Alexander der Gro?e"));
        expected = new Author("Alexander H. G.", "A. H. G.", null, "Rinnooy Kan", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("Rinnooy Kan, Alexander H. G."));
        expected = new Author("Alexander Hendrik George", "A. H. G.", null, "Rinnooy Kan", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("Rinnooy Kan, Alexander Hendrik George"));
        expected = new Author("Jos? Mar?a", "J. M.", null, "Rodriguez Fernandez", null);
        Assertions.assertEquals(new AuthorList(expected), AuthorList.parse("Rodriguez Fernandez, Jos? Mar?a"));
    }
}

