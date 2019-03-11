/**
 * ownCloud Android client application
 *
 * @author purigarcia
Copyright (C) 2016 ownCloud GmbH.
<p>
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 2,
as published by the Free Software Foundation.
<p>
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
<p>
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.test.ui.testSuites;


import com.owncloud.android.test.ui.actions.Actions;
import com.owncloud.android.test.ui.groups.NoIgnoreTestCategory;
import com.owncloud.android.test.ui.groups.SmokeTestCategory;
import com.owncloud.android.test.ui.models.FileListView;
import com.owncloud.android.test.ui.models.WaitAMomentPopUp;
import io.appium.java_client.android.AndroidDriver;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateFolderTestSuite {
    AndroidDriver driver;

    Common common;

    private Boolean folderHasBeenCreated = false;

    private final String FOLDER_NAME = "testCreateFolder";

    private String CurrentCreatedFolder = "";

    @Rule
    public TestName name = new TestName();

    @Test
    @Category({ NoIgnoreTestCategory.class, SmokeTestCategory.class })
    public void testCreateNewFolder() throws Exception {
        String NEW_FOLDER_NAME = "testCreateFolder";
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        // check if the folder already exists and if true, delete them
        Actions.deleteElement(NEW_FOLDER_NAME, fileListView, driver);
        WaitAMomentPopUp waitAMomentPopUp = Actions.createFolder(NEW_FOLDER_NAME, fileListView);
        Common.waitTillElementIsNotPresentWithoutTimeout(waitAMomentPopUp.getWaitAMomentTextElement(), 100);
        fileListView.scrollTillFindElement(FOLDER_NAME);
        Assert.assertNotNull(fileListView.getFileElement());
        Assert.assertTrue((folderHasBeenCreated = fileListView.getFileElement().isDisplayed()));
        CurrentCreatedFolder = FOLDER_NAME;
        Assert.assertEquals(FOLDER_NAME, fileListView.getFileElement().getText());
    }
}

