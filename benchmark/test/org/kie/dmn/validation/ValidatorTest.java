/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation;


import DMNMessageType.FAILED_XML_VALIDATION;
import DMNMessageType.MISSING_EXPRESSION;
import DMNValidator.Validation.VALIDATE_SCHEMA;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.DMNInputRuntimeTest;
import org.kie.dmn.core.DMNRuntimeTest;
import org.kie.dmn.core.decisionservices.DMNDecisionServicesTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.Definitions;


public class ValidatorTest extends AbstractValidatorTest {
    @Test
    public void testDryRun() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string.dmn", DMNInputRuntimeTest.class);
        DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Definitions definitions = dmnModel.getDefinitions();
        Assert.assertThat(definitions, CoreMatchers.notNullValue());
        DMNValidatorFactory.newValidator().validate(definitions);
    }

    @Test
    public void testMACDInputDefinitions() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("MACD-enhanced_iteration.dmn", DMNInputRuntimeTest.class);
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6cfe7d88-6741-45d1-968c-b61a597d0964", "MACD-enhanced iteration");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Definitions definitions = dmnModel.getDefinitions();
        Assert.assertThat(definitions, CoreMatchers.notNullValue());
        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(messages.toString(), messages.size(), CoreMatchers.is(0));
    }

    @Test
    public void testMACDInputReader() throws IOException {
        try (final Reader reader = new InputStreamReader(getClass().getResourceAsStream("/org/kie/dmn/core/MACD-enhanced_iteration.dmn"))) {
            List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(reader, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
            Assert.assertThat(messages.toString(), messages.size(), CoreMatchers.is(0));
        }
    }

    @Test
    public void testInvalidXml() throws URISyntaxException {
        List<DMNMessage> validateXML = AbstractValidatorTest.validator.validate(new File(this.getClass().getResource("invalidXml.dmn").toURI()), VALIDATE_SCHEMA);
        Assert.assertThat(ValidatorUtil.formatMessages(validateXML), validateXML.size(), CoreMatchers.is(1));
        Assert.assertThat(validateXML.get(0).toString(), validateXML.get(0).getMessageType(), CoreMatchers.is(FAILED_XML_VALIDATION));
    }

    @Test
    public void testINVOCATION_MISSING_EXPR() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("INVOCATION_MISSING_EXPR.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertThat(validate.get(0).toString(), validate.get(0).getMessageType(), CoreMatchers.is(MISSING_EXPRESSION));
    }

    @Test
    public void testNAME_IS_VALID() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("NAME_IS_VALID.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testNAME_INVALID_empty_name() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("DROOLS-1447.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(4));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> (p.getMessageType().equals(DMNMessageType.INVALID_NAME)) && (p.getSourceId().equals("_5e43b55c-888e-443c-b1b9-80e4aa6746bd"))));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> (p.getMessageType().equals(DMNMessageType.INVALID_NAME)) && (p.getSourceId().equals("_b1e4588e-9ce1-4474-8e4e-48dbcdb7524b"))));
    }

    @Test
    public void testDRGELEM_NOT_UNIQUE() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("DRGELEM_NOT_UNIQUE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME)));
    }

    @Test
    public void testFORMAL_PARAM_DUPLICATED() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("FORMAL_PARAM_DUPLICATED.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(3));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.DUPLICATED_PARAM)));
    }

    @Test
    public void testINVOCATION_INCONSISTENT_PARAM_NAMES() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("INVOCATION_INCONSISTENT_PARAM_NAMES.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH)));
    }

    @Test
    public void testINVOCATION_WRONG_PARAM_COUNT() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("INVOCATION_WRONG_PARAM_COUNT.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(3));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH)));
    }

    @Test
    public void testITEMCOMP_DUPLICATED() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("ITEMCOMP_DUPLICATED.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.DUPLICATED_ITEM_DEF)));
    }

    @Test
    public void testITEMDEF_NOT_UNIQUE() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("ITEMDEF_NOT_UNIQUE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.DUPLICATED_ITEM_DEF)));
    }

    @Test
    public void testITEMDEF_NOT_UNIQUE_DROOLS_1450() {
        // DROOLS-1450
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("ITEMDEF_NOT_UNIQUE_DROOLS-1450.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testRELATION_DUP_COLUMN() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("RELATION_DUP_COLUMN.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.DUPLICATED_RELATION_COLUMN)));
    }

    @Test
    public void testRELATION_ROW_CELL_NOTLITERAL() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("RELATION_ROW_CELL_NOTLITERAL.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.RELATION_CELL_NOT_LITERAL)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testRELATION_ROW_CELLCOUNTMISMATCH() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("RELATION_ROW_CELLCOUNTMISMATCH.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.RELATION_CELL_COUNT_MISMATCH)));
    }

    @Test
    public void testMortgageRecommender() {
        // This file has a gazillion errors. The goal of this test is simply check that the validator itself is not blowing up
        // and raising an exception. The errors in the file itself are irrelevant.
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("MortgageRecommender.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void testREQAUTH_NOT_KNOWLEDGESOURCEbis() {
        // DROOLS-1435
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("REQAUTH_NOT_KNOWLEDGESOURCEbis.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
    }

    @Test
    public void testVARIABLE_LEADING_TRAILING_SPACES() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("VARIABLE_LEADING_TRAILING_SPACES.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.INVALID_NAME)));
        Assert.assertThat(validate.get(0).getSourceId(), CoreMatchers.is("_dd662d27-7896-42cb-9d14-bd74203bdbec"));
    }

    @Test
    public void testUNKNOWN_VARIABLE() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("UNKNOWN_VARIABLE.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL)));
    }

    @Test
    public void testVALIDATION() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("validation.dmn"), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(5));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.INVALID_NAME)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL)));
        // on node DTI the `Loan Payment` is of type `tLoanPayment` hence the property is `monthlyAmount`, NOT `amount` as reported in the model FEEL expression: (Loan Payment.amount+...
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL)));
    }

    @Test
    public void testUsingSemanticNamespacePrefix() {
        // DROOLS-2419
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("UsingSemanticNS.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testUsingSemanticNamespacePrefixAndExtensions() {
        // DROOLS-2447
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("Hello_World_semantic_namespace_with_extensions.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testNoPrefixAndExtensions() {
        // DROOLS-2447
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("Hello_World_no_prefix_with_extensions.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testRelationwithemptycell() {
        // DROOLS-2439
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("relation_with_empty_cell.dmn", DMNRuntimeTest.class);
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_99a00903-2943-47df-bab1-a32f276617ea", "Relation with empty cell");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Definitions definitions = dmnModel.getDefinitions();
        Assert.assertThat(definitions, CoreMatchers.notNullValue());
        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(messages.toString(), messages.size(), CoreMatchers.is(0));
    }

    @Test
    public void testRelationwithemptycellJustValidator() {
        // DROOLS-2439
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("relation_with_empty_cell.dmn", DMNRuntimeTest.class), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testBoxedInvocationMissingExpression() {
        // DROOLS-2813 DMN boxed invocation missing expression NPE and Validator issue
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("DROOLS-2813-NPE-BoxedInvocationMissingExpression.dmn", DMNRuntimeTest.class), Validation.VALIDATE_MODEL);
        Assert.assertTrue(validate.stream().anyMatch(( p) -> (p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)) && (p.getSourceId().equals("_a111c4df-c5b5-4d84-81e7-3ec735b50d06"))));
    }

    @Test
    public void testDMNv1_2_ch11Modified() {
        // DROOLS-2832
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("v1_2/ch11MODIFIED.dmn", DMNRuntimeTest.class), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testDMNv1_2_ch11() {
        // DROOLS-2832
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("DMNv12_ch11.dmn"), Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL);
        // DMN v1.2 CH11 example for Adjudication does not define decision logic nor typeRef:
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(2));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION)));
    }

    @Test
    public void testDecisionServiceCompiler20180830() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("DecisionServiceABC.dmn", DMNDecisionServicesTest.class), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
    }

    @Test
    public void testDecisionServiceCompiler20180830DMNV12() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("DecisionServiceABC_DMN12.dmn", DMNDecisionServicesTest.class), Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION);
        // DMNMessage{ severity=WARN, type=MISSING_TYPE_REF, message='Variable named 'Decision Service ABC' is missing its type reference on node 'Decision Service ABC'', sourceId='_63d05cff-8e3b-4dad-a355-fd88f8bcd613', exception='', feelEvent=''}
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(1));
        Assert.assertTrue(validate.stream().anyMatch(( p) -> (p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)) && (p.getSourceId().equals("_63d05cff-8e3b-4dad-a355-fd88f8bcd613"))));
    }

    @Test
    public void testDecisionService20181008() {
        // DROOLS-3087 DMN Validation of DecisionService referencing a missing import
        List<DMNMessage> validate = AbstractValidatorTest.validator.validateUsing(Validation.VALIDATE_MODEL, Validation.VALIDATE_COMPILATION).theseModels(getReader("DSWithImport20181008-ModelA.dmn"), getReader("DSWithImport20181008-ModelB.dmn"));
        Assert.assertThat(ValidatorUtil.formatMessages(validate), validate.size(), CoreMatchers.is(0));
        List<DMNMessage> missingDMNImport = AbstractValidatorTest.validator.validateUsing(Validation.VALIDATE_MODEL).theseModels(getReader("DSWithImport20181008-ModelA.dmn"), getReader("DSWithImport20181008-ModelB-missingDMNImport.dmn"));
        Assert.assertThat(missingDMNImport.stream().filter(( p) -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)).count(), CoreMatchers.is(2L));// on Decision and Decision Service missing to locate the dependency given Import is omitted.

    }
}
