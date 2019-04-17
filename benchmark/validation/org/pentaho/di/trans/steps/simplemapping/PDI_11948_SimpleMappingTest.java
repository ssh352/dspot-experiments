/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.simplemapping;


import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.steps.PDI_11948_StepsTestsParent;


/**
 * The PDI_11948_SimpleMappingTest class tests Simple Mapping step of PDI-11948 bug. It's check if HttpServletResponse
 * object is null and call or not setServletReponse( HttpServletResponse response ) method of appropriate Trans object.
 *
 * @author Yury Bakhmutski
 * @see org.pentaho.di.trans.steps.simplemapping.SimpleMapping
 */
public class PDI_11948_SimpleMappingTest extends PDI_11948_StepsTestsParent<SimpleMapping, SimpleMappingData> {
    @Test
    public void testSimpleMappingStep() throws KettleException {
        Mockito.when(stepMock.getData()).thenReturn(stepDataMock);
        Mockito.when(stepDataMock.getMappingTrans()).thenReturn(transMock);
        // stubbing methods for null-checking
        Mockito.when(stepMock.getTrans()).thenReturn(transMock);
        Mockito.when(transMock.getServletResponse()).thenReturn(null);
        Mockito.doThrow(new RuntimeException("The getServletResponse() mustn't be executed!")).when(transMock).setServletReponse(ArgumentMatchers.any(HttpServletResponse.class));
        Mockito.doCallRealMethod().when(stepMock).initServletConfig();
        stepMock.initServletConfig();
    }
}
