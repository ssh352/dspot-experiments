package com.baeldung;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class Example1IntegrationTest {
    @Test
    public void test1a() {
        Example1IntegrationTest.block(3000);
    }

    @Test
    public void test1b() {
        Example1IntegrationTest.block(3000);
    }
}

