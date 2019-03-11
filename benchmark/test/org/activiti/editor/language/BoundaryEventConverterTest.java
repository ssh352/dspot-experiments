package org.activiti.editor.language;


import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;


public class BoundaryEventConverterTest extends AbstractConverterTest {
    @Test
    public void convertJsonToModel() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        validateModel(bpmnModel);
    }

    @Test
    public void doubleConversionValidation() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        bpmnModel = convertToJsonAndBack(bpmnModel);
        validateModel(bpmnModel);
    }
}

