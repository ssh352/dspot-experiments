package com.github.javafaker.integration;


import com.github.javafaker.Faker;
import com.github.javafaker.matchers.MatchesRegularExpression;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class Issue194SlashFormatRegexIT {
    @Test
    public void enGBZipCodeReturnsProperRegexifiedValue() {
        final Locale uk = new Locale("en-GB");
        final String postalCode = new Faker(uk).address().zipCode();
        Assert.assertThat(postalCode, MatchesRegularExpression.matchesRegularExpression("[A-PR-UWYZ]([A-HK-Y][0-9][ABEHMNPRVWXY0-9]?|[0-9][ABCDEFGHJKPSTUW0-9]?) [0-9][ABD-HJLNP-UW-Z]{2}"));
    }

    @Test
    public void enCAZipCodeReturnsProperRegexifiedValue() {
        final Locale uk = new Locale("en-CA");
        final String postalCode = new Faker(uk).address().zipCode();
        Assert.assertThat(postalCode, MatchesRegularExpression.matchesRegularExpression("[A-CEJ-NPR-TVXY][0-9][A-CEJ-NPR-TV-Z] ?[0-9][A-CEJ-NPR-TV-Z][0-9]"));
    }

    @Test
    public void viZipCodeReturnsProperRegexifiedValue() {
        final Locale uk = new Locale("vi");
        final String postalCode = new Faker(uk).address().zipCode();
        Assert.assertThat(postalCode, MatchesRegularExpression.matchesRegularExpression("[A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}"));
    }
}
