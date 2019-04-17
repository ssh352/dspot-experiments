/**
 * Copyright 2018 The Data Transfer Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datatransferproject.datatransfer.google.contacts;


import com.google.api.services.people.v1.model.Address;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.FieldMetadata;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.google.api.services.people.v1.model.Source;
import com.google.gdata.util.common.base.Pair;
import ezvcard.VCard;
import ezvcard.property.Email;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;


public class GoogleContactsExportConversionTest {
    private static final String DEFAULT_SOURCE_TYPE = "CONTACT";

    private static final Source DEFAULT_SOURCE = new Source().setType(GoogleContactsExportConversionTest.DEFAULT_SOURCE_TYPE);

    private static final FieldMetadata PRIMARY_FIELD_METADATA = new FieldMetadata().setSource(GoogleContactsExportConversionTest.DEFAULT_SOURCE).setPrimary(true);

    private static final FieldMetadata SECONDARY_FIELD_METADATA = new FieldMetadata().setSource(GoogleContactsExportConversionTest.DEFAULT_SOURCE).setPrimary(false);

    private static final Name DEFAULT_NAME = new Name().setFamilyName("Church").setGivenName("Alonzo").setMetadata(GoogleContactsExportConversionTest.PRIMARY_FIELD_METADATA);

    private static final Person DEFAULT_PERSON = new Person().setNames(Collections.singletonList(GoogleContactsExportConversionTest.DEFAULT_NAME));

    @Test
    public void testConversionToVCardNames() {
        // Set up Person with a primary name and two secondary names
        String primaryGivenName = "Mark";
        String primaryFamilyName = "Twain";
        Name primaryName = new Name().setGivenName(primaryGivenName).setFamilyName(primaryFamilyName).setMetadata(GoogleContactsExportConversionTest.PRIMARY_FIELD_METADATA);
        String alternateGivenName1 = "Samuel";
        String alternateFamilyName1 = "Clemens";
        String alternateSourceType1 = "PROFILE";
        Name alternateName1 = new Name().setGivenName(alternateGivenName1).setFamilyName(alternateFamilyName1).setMetadata(new FieldMetadata().setPrimary(false).setSource(new Source().setType(alternateSourceType1)));
        String alternateGivenName2 = "Louis";
        String alternateFamilyName2 = "de Conte";
        String alternateSourceType2 = "PEN_NAME";
        Name alternateName2 = new Name().setGivenName(alternateGivenName2).setFamilyName(alternateFamilyName2).setMetadata(new FieldMetadata().setPrimary(false).setSource(new Source().setType(alternateSourceType2)));
        // Order shouldn't matter
        Person person = new Person().setNames(Arrays.asList(alternateName2, alternateName1, primaryName));
        // Run test
        VCard vCard = GoogleContactsExporter.convert(person);
        // Check name conversion correctness
        List<StructuredName> structuredNames = vCard.getStructuredNames();
        assertThat(structuredNames.size()).isEqualTo(3);
        // Check primary (non-alternate) names
        List<StructuredName> actualPrimaryNames = structuredNames.stream().filter(( n) -> (n.getAltId()) == null).collect(Collectors.toList());
        List<Pair<String, String>> actualPrimaryNamesValues = actualPrimaryNames.stream().map(GoogleContactsExportConversionTest::getGivenAndFamilyNames).collect(Collectors.toList());
        assertThat(actualPrimaryNamesValues).containsExactly(Pair.of(primaryGivenName, primaryFamilyName));
        List<String> actualPrimarySourceValues = actualPrimaryNames.stream().map(( a) -> a.getParameter(SOURCE_PARAM_NAME_TYPE)).collect(Collectors.toList());
        assertThat(actualPrimarySourceValues).containsExactly(GoogleContactsExportConversionTest.DEFAULT_SOURCE_TYPE);
        // Check alternate names
        List<StructuredName> actualAlternateNames = structuredNames.stream().filter(( n) -> (n.getAltId()) != null).collect(Collectors.toList());
        List<Pair<String, String>> actualAlternateNamesValues = actualAlternateNames.stream().map(GoogleContactsExportConversionTest::getGivenAndFamilyNames).collect(Collectors.toList());
        assertThat(actualAlternateNamesValues).containsExactly(Pair.of(alternateGivenName1, alternateFamilyName1), Pair.of(alternateGivenName2, alternateFamilyName2));
        List<String> actualAlternateSourceValues = actualAlternateNames.stream().map(( a) -> a.getParameter(SOURCE_PARAM_NAME_TYPE)).collect(Collectors.toList());
        assertThat(actualAlternateSourceValues).containsExactly(alternateSourceType1, alternateSourceType2);
    }

    @Test
    public void testConversionToVCardAddress() {
        // Set up test: person with a primary address and a secondary address
        String primaryStreet = "221B Baker St";
        String primaryCity = "London";
        String primaryPostcode = "NW1";
        String primaryCountry = "United Kingdom";
        Address primaryAddress = new Address().setStreetAddress(primaryStreet).setCity(primaryCity).setPostalCode(primaryPostcode).setCountry(primaryCountry).setMetadata(GoogleContactsExportConversionTest.PRIMARY_FIELD_METADATA);
        String altStreet = "42 Wallaby Way";
        String altCity = "Sydney";
        String altRegion = "New South Wales";
        String altCountry = "Australia";
        Address altAddress = new Address().setStreetAddress(altStreet).setCity(altCity).setRegion(altRegion).setCountry(altCountry).setMetadata(GoogleContactsExportConversionTest.SECONDARY_FIELD_METADATA);
        Person person = GoogleContactsExportConversionTest.DEFAULT_PERSON.setAddresses(Arrays.asList(altAddress, primaryAddress));
        // Run test
        VCard vCard = GoogleContactsExporter.convert(person);
        // Check results for correct values and preferences
        List<ezvcard.property.Address> actualPrimaryAddressList = GoogleContactsExportConversionTest.getPropertiesWithPreference(vCard, ezvcard.property.Address.class, VCARD_PRIMARY_PREF);
        assertThat(actualPrimaryAddressList.stream().map(Address::getStreetAddress).collect(Collectors.toList())).containsExactly(primaryStreet);
        List<ezvcard.property.Address> actualAltAddressList = GoogleContactsExportConversionTest.getPropertiesWithPreference(vCard, ezvcard.property.Address.class, ((VCARD_PRIMARY_PREF) + 1));
        assertThat(actualAltAddressList.stream().map(Address::getRegion).collect(Collectors.toList())).containsExactly(altRegion);
    }

    @Test
    public void testConversionToVCardTelephone() {
        // Set up test: person with 2 primary phone numbers and 1 secondary phone number
        String primaryValue1 = "334-844-4244";
        String primaryValue2 = "411";
        String secondaryValue = "(555) 867-5309";
        PhoneNumber primaryPhone1 = new PhoneNumber().setValue(primaryValue1).setMetadata(GoogleContactsExportConversionTest.PRIMARY_FIELD_METADATA);
        PhoneNumber primaryPhone2 = new PhoneNumber().setValue(primaryValue2).setMetadata(GoogleContactsExportConversionTest.PRIMARY_FIELD_METADATA);
        PhoneNumber secondaryPhone = new PhoneNumber().setValue(secondaryValue).setMetadata(GoogleContactsExportConversionTest.SECONDARY_FIELD_METADATA);
        Person person = GoogleContactsExportConversionTest.DEFAULT_PERSON.setPhoneNumbers(Arrays.asList(secondaryPhone, primaryPhone1, primaryPhone2));
        // Run test
        VCard vCard = GoogleContactsExporter.convert(person);
        // Check results for correct values and preferences
        List<Telephone> resultPrimaryPhoneList = GoogleContactsExportConversionTest.getPropertiesWithPreference(vCard, Telephone.class, VCARD_PRIMARY_PREF);
        assertThat(GoogleContactsExportConversionTest.getValuesFromProperties(resultPrimaryPhoneList, Telephone::getText)).containsExactly(primaryValue1, primaryValue2);
        List<Telephone> resultSecondaryPhoneList = GoogleContactsExportConversionTest.getPropertiesWithPreference(vCard, Telephone.class, ((VCARD_PRIMARY_PREF) + 1));
        assertThat(GoogleContactsExportConversionTest.getValuesFromProperties(resultSecondaryPhoneList, Telephone::getText)).containsExactly(secondaryValue);
    }

    @Test
    public void testConversionToVCardEmail() {
        // Set up test: person with 1 primary email and 2 secondary emails
        String primaryString = "primary@email.com";
        String secondaryString1 = "secondary1@email.com";
        String secondaryString2 = "secondary2@email.com";
        EmailAddress primaryEmail = new EmailAddress().setValue(primaryString).setMetadata(GoogleContactsExportConversionTest.PRIMARY_FIELD_METADATA);
        EmailAddress secondaryEmail1 = new EmailAddress().setValue(secondaryString1).setMetadata(GoogleContactsExportConversionTest.SECONDARY_FIELD_METADATA);
        EmailAddress secondaryEmail2 = new EmailAddress().setValue(secondaryString2).setMetadata(GoogleContactsExportConversionTest.SECONDARY_FIELD_METADATA);
        Person person = GoogleContactsExportConversionTest.DEFAULT_PERSON.setEmailAddresses(Arrays.asList(secondaryEmail1, primaryEmail, secondaryEmail2));// Making sure order isn't a factor

        // Run test - NB, this Person only has emails
        VCard vCard = GoogleContactsExporter.convert(person);
        // Check results for correct values and preferences
        List<Email> resultPrimaryEmailList = GoogleContactsExportConversionTest.getPropertiesWithPreference(vCard, Email.class, VCARD_PRIMARY_PREF);
        assertThat(GoogleContactsExportConversionTest.getValuesFromTextProperties(resultPrimaryEmailList)).containsExactly(primaryString);
        List<Email> resultSecondaryEmailList = GoogleContactsExportConversionTest.getPropertiesWithPreference(vCard, Email.class, ((VCARD_PRIMARY_PREF) + 1));
        assertThat(GoogleContactsExportConversionTest.getValuesFromTextProperties(resultSecondaryEmailList)).containsExactly(secondaryString1, secondaryString2);
    }
}
