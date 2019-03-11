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
import com.owncloud.android.test.ui.models.ElementMenuOptions;
import com.owncloud.android.test.ui.models.FileListView;
import com.owncloud.android.test.ui.models.MoveView;
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
public class MoveFileTestSuite {
    AndroidDriver driver;

    Common common;

    private String FOLDER_WHERE_MOVE = "folderWhereMove";

    private String FILE_NAME = Config.fileToTestName;

    @Rule
    public TestName name = new TestName();

    @Test
    @Category({ NoIgnoreTestCategory.class, SmokeTestCategory.class })
    public void testMoveFile() throws Exception {
        WaitAMomentPopUp waitAMomentPopUp;
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        // Common.waitTillElementIsNotPresentWithoutTimeout(
        // fileListView.getProgressCircular(), 1000);
        // check if the folder already exists and if true, delete them
        Actions.deleteElement(FOLDER_WHERE_MOVE, fileListView, driver);
        Actions.deleteElement(FILE_NAME, fileListView, driver);
        // Create the folder where the other is gone to be moved
        waitAMomentPopUp = Actions.createFolder(FOLDER_WHERE_MOVE, fileListView);
        Common.waitTillElementIsNotPresentWithoutTimeout(waitAMomentPopUp.getWaitAMomentTextElement(), 100);
        fileListView.scrollTillFindElement(FOLDER_WHERE_MOVE);
        Assert.assertTrue(fileListView.getFileElement().isDisplayed());
        FileListView fileListViewAfterUploadFile = Actions.uploadFile(FILE_NAME, fileListView);
        fileListViewAfterUploadFile.scrollTillFindElement(FILE_NAME);
        Assert.assertTrue(fileListViewAfterUploadFile.getFileElement().isDisplayed());
        // select to move the file
        ElementMenuOptions menuOptions = fileListView.longPressOnElement(FILE_NAME);
        MoveView moveView = menuOptions.clickOnMove();
        // to move to a folder
        moveView.scrollTillFindElement(FOLDER_WHERE_MOVE).tap(1, 1);
        waitAMomentPopUp = moveView.clickOnChoose();
        Common.waitTillElementIsNotPresentWithoutTimeout(waitAMomentPopUp.getWaitAMomentTextElement(), 100);
        // check that the folder moved is inside the other
        fileListView.scrollTillFindElement(FOLDER_WHERE_MOVE).tap(1, 1);
        Common.waitTillElementIsNotPresentWithoutTimeout(fileListView.getProgressCircular(), 1000);
        Thread.sleep(1000);
        fileListView.scrollTillFindElement(FILE_NAME);
        Assert.assertEquals(FILE_NAME, fileListView.getFileElement().getText());
    }
}

