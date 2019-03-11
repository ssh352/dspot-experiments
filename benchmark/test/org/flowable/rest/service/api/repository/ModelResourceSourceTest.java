/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.rest.service.api.repository;


import RestUrls.URL_MODEL_SOURCE;
import RestUrls.URL_MODEL_SOURCE_EXTRA;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.flowable.engine.repository.Model;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.HttpMultipartHelper;
import org.flowable.rest.service.api.RestUrls;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Frederik Heremans
 */
public class ModelResourceSourceTest extends BaseSpringRestTestCase {
    @Test
    public void testGetModelEditorSource() throws Exception {
        Model model = null;
        try {
            model = repositoryService.newModel();
            model.setName("Model name");
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), "This is the editor source".getBytes());
            HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE, model.getId()))));
            CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);
            // Check "OK" status
            Assert.assertEquals("application/octet-stream", response.getEntity().getContentType().getValue());
            Assert.assertEquals("This is the editor source", IOUtils.toString(response.getEntity().getContent()));
            closeResponse(response);
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    public void testGetModelEditorSourceNoSource() throws Exception {
        Model model = null;
        try {
            model = repositoryService.newModel();
            model.setName("Model name");
            repositoryService.saveModel(model);
            HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE, model.getId()))));
            closeResponse(executeRequest(httpGet, HttpStatus.SC_NOT_FOUND));
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    public void testGetModelEditorSourceExtra() throws Exception {
        Model model = null;
        try {
            model = repositoryService.newModel();
            model.setName("Model name");
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSourceExtra(model.getId(), "This is the extra editor source".getBytes());
            HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE_EXTRA, model.getId()))));
            CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);
            // Check "OK" status
            Assert.assertEquals("application/octet-stream", response.getEntity().getContentType().getValue());
            Assert.assertEquals("This is the extra editor source", IOUtils.toString(response.getEntity().getContent()));
            closeResponse(response);
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    public void testGetModelEditorSourceExtraNoSource() throws Exception {
        Model model = null;
        try {
            model = repositoryService.newModel();
            model.setName("Model name");
            repositoryService.saveModel(model);
            HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE_EXTRA, model.getId()))));
            closeResponse(executeRequest(httpGet, HttpStatus.SC_NOT_FOUND));
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    public void testGetModelSourceUnexistingModel() throws Exception {
        HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE, "unexisting"))));
        closeResponse(executeRequest(httpGet, HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void testGetModelSourceExtraUnexistingModel() throws Exception {
        HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE_EXTRA, "unexisting"))));
        closeResponse(executeRequest(httpGet, HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void testSetModelEditorSource() throws Exception {
        Model model = null;
        try {
            model = repositoryService.newModel();
            model.setName("Model name");
            repositoryService.saveModel(model);
            HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE, model.getId()))));
            httpPut.setEntity(HttpMultipartHelper.getMultiPartEntity("sourcefile", "application/octet-stream", new ByteArrayInputStream("This is the new editor source".getBytes()), null));
            closeResponse(executeBinaryRequest(httpPut, HttpStatus.SC_NO_CONTENT));
            Assert.assertEquals("This is the new editor source", new String(repositoryService.getModelEditorSource(model.getId())));
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    public void testSetModelEditorSourceExtra() throws Exception {
        Model model = null;
        try {
            model = repositoryService.newModel();
            model.setName("Model name");
            repositoryService.saveModel(model);
            HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE_EXTRA, model.getId()))));
            httpPut.setEntity(HttpMultipartHelper.getMultiPartEntity("sourcefile", "application/octet-stream", new ByteArrayInputStream("This is the new extra editor source".getBytes()), null));
            closeResponse(executeBinaryRequest(httpPut, HttpStatus.SC_NO_CONTENT));
            Assert.assertEquals("This is the new extra editor source", new String(repositoryService.getModelEditorSourceExtra(model.getId())));
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    public void testSetModelSourceUnexistingModel() throws Exception {
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE, "unexisting"))));
        httpPut.setEntity(MultipartEntityBuilder.create().build());
        closeResponse(executeBinaryRequest(httpPut, HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void testSetModelSourceExtraUnexistingModel() throws Exception {
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE_EXTRA, "unexisting"))));
        httpPut.setEntity(MultipartEntityBuilder.create().build());
        closeResponse(executeBinaryRequest(httpPut, HttpStatus.SC_NOT_FOUND));
    }
}

