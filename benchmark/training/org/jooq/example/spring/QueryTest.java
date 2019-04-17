package org.jooq.example.spring;


import BOOK.ID;
import java.util.Arrays;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.example.db.h2.tables.Author;
import org.jooq.example.db.h2.tables.Book;
import org.jooq.example.db.h2.tables.BookStore;
import org.jooq.example.db.h2.tables.BookToBookStore;
import org.jooq.example.db.h2.tables.records.BookRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Lukas Eder
 * @author Thomas Darimont
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class QueryTest {
    @Autowired
    DSLContext create;

    @Test
    public void testJoin() throws Exception {
        // All of these tables were generated by jOOQ's Maven plugin
        Book b = BOOK.as("b");
        Author a = AUTHOR.as("a");
        BookStore s = BOOK_STORE.as("s");
        BookToBookStore t = BOOK_TO_BOOK_STORE.as("t");
        Result<Record3<String, String, Integer>> result = create.select(a.FIRST_NAME, a.LAST_NAME, countDistinct(s.NAME)).from(a).join(b).on(b.AUTHOR_ID.equal(a.ID)).join(t).on(t.BOOK_ID.equal(b.ID)).join(s).on(t.BOOK_STORE_NAME.equal(s.NAME)).groupBy(a.FIRST_NAME, a.LAST_NAME).orderBy(countDistinct(s.NAME).desc()).fetch();
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("Paulo", result.getValue(0, a.FIRST_NAME));
        Assert.assertEquals("George", result.getValue(1, a.FIRST_NAME));
        Assert.assertEquals("Coelho", result.getValue(0, a.LAST_NAME));
        Assert.assertEquals("Orwell", result.getValue(1, a.LAST_NAME));
        Assert.assertEquals(Integer.valueOf(3), result.getValue(0, countDistinct(s.NAME)));
        Assert.assertEquals(Integer.valueOf(2), result.getValue(1, countDistinct(s.NAME)));
    }

    @Test
    public void testActiveRecords() throws Exception {
        Result<BookRecord> result = create.selectFrom(BOOK).orderBy(ID).fetch();
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), result.getValues(0));
    }
}
