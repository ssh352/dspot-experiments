package com.vaadin.tests.fieldgroup;


import Keys.BACK_SPACE;
import Keys.ENTER;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class BooleanTextFieldTest extends BasicPersonFormTest {
    @Test
    public void testSetBooleanTextField() throws Exception {
        openTestURL();
        getLastNameArea().clear();
        getLastNameArea().click();
        getLastNameArea().sendKeys("Dover");
        assertBeanValuesUnchanged();
        getDeceasedField().click();
        while (!(getDeceasedField().getValue().isEmpty())) {
            getDeceasedField().sendKeys(BACK_SPACE);
        } 
        getDeceasedField().sendKeys("false", ENTER);
        /* error indicator */
        Assert.assertEquals("Incorrect amount of error indicators on page.", 1, findElements(By.className("v-errorindicator")).size());
        Assert.assertEquals("false", getDeceasedField().getAttribute("value"));
        assertBeanValuesUnchanged();
        /* error message in tooltip */
        getDeceasedField().showTooltip();
        Assert.assertEquals("Could not convert value to Boolean", getTooltipErrorElement().getText());
        getDeceasedField().click();
        while (!(getDeceasedField().getValue().isEmpty())) {
            getDeceasedField().sendKeys(BACK_SPACE);
        } 
        getDeceasedField().sendKeys("YAY!", ENTER);
        /* no error indicator */
        Assert.assertFalse(isElementPresent(By.className("v-errorindicator")));
        assertCommitSuccessful();
        /* commit last name and new deceased status */
        showBeanValues();
        Assert.assertEquals("4. Person [firstName=John, lastName=Dover, email=john@doe.com, age=64, sex=Male, address=Address [streetAddress=John street, postalCode=11223, city=John's town, country=USA], deceased=true, salary=null, salaryDouble=null, rent=null]", getLogRow(0));
    }
}
