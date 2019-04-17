package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static TableRowScrolledBottom.part1;
import static TableRowScrolledBottom.part2;


/**
 * Tests that adding long labels to a Table and scrolling to the bottom works as
 * intended.
 *
 * @author Vaadin Ltd
 */
public class TableRowScrolledBottomTest extends MultiBrowserTest {
    @Test
    public void testScrolling() throws IOException, InterruptedException {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).first();
        TableElement table = $(TableElement.class).first();
        // initialise contents
        button.click();
        sleep(500);
        List<WebElement> rows = table.findElement(By.className("v-table-body")).findElements(By.tagName("tr"));
        // check that the final row is the one intended
        WebElement finalRow = rows.get(((rows.size()) - 1));
        WebElement label = finalRow.findElement(By.className("v-label"));
        Assert.assertEquals((((part1) + 100) + (part2)), label.getText());
        // add more rows
        button.click();
        sleep(500);
        rows = table.findElement(By.className("v-table-body")).findElements(By.tagName("tr"));
        // check that the final row is the one intended
        finalRow = rows.get(((rows.size()) - 1));
        label = finalRow.findElement(By.className("v-label"));
        Assert.assertEquals((((part1) + 200) + (part2)), label.getText());
    }
}
