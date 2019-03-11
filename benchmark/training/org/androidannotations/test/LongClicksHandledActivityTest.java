/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test;


import R.id.button1;
import R.id.button2;
import R.id.buttonWithButtonArgument;
import R.id.buttonWithViewArgument;
import R.id.configurationOverConventionButton;
import R.id.conventionButton;
import R.id.extendedConventionButton;
import R.id.snake_case_button;
import R.id.stackOverflowProofButton;
import R.id.unboundButton;
import android.widget.Button;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class LongClicksHandledActivityTest {
    private LongClicksHandledActivity_ activity;

    @Test
    public void avoidStackOverflow() throws InterruptedException {
        assertThat(activity.avoidStackOverflowEventHandled).isFalse();
        activity.findViewById(stackOverflowProofButton).performLongClick();
        assertThat(activity.avoidStackOverflowEventHandled).isTrue();
    }

    @Test
    public void handlingWithConvention() throws InterruptedException {
        assertThat(activity.conventionButtonEventHandled).isFalse();
        activity.findViewById(conventionButton).performLongClick();
        assertThat(activity.conventionButtonEventHandled).isTrue();
    }

    @Test
    public void handlingWithSnakeCase() {
        assertThat(activity.snakeCaseButtonEventHandled).isFalse();
        activity.findViewById(snake_case_button).performLongClick();
        assertThat(activity.snakeCaseButtonEventHandled).isTrue();
    }

    @Test
    public void handlingWithExtendedConvention() {
        assertThat(activity.extendedConventionButtonEventHandled).isFalse();
        activity.findViewById(extendedConventionButton).performLongClick();
        assertThat(activity.extendedConventionButtonEventHandled).isTrue();
    }

    @Test
    public void handlingWithConfigurationOverConvention() {
        assertThat(activity.overridenConventionButtonEventHandled).isFalse();
        activity.findViewById(configurationOverConventionButton).performLongClick();
        assertThat(activity.overridenConventionButtonEventHandled).isTrue();
    }

    @Test
    public void unannotatedButtonIsNotHandled() {
        activity.findViewById(unboundButton).performLongClick();
        assertThat(activity.unboundButtonEventHandled).isFalse();
    }

    @Test
    public void viewArgumentIsGiven() {
        assertThat(activity.viewArgument).isNull();
        activity.findViewById(buttonWithViewArgument).performLongClick();
        assertThat(activity.viewArgument).hasId(buttonWithViewArgument);
    }

    @Test
    public void buttonArgumentIsGiven() {
        assertThat(activity.viewArgument).isNull();
        Button button = ((Button) (activity.findViewById(buttonWithButtonArgument)));
        button.performLongClick();
        assertThat(activity.viewArgument).isSameAs(button);
    }

    @Test
    public void multipleButtonsClicked() {
        assertThat(activity.multipleButtonsEventHandled).isFalse();
        activity.findViewById(button1).performLongClick();
        assertThat(activity.multipleButtonsEventHandled).isTrue();
        assertThat(activity.viewArgument).hasId(button1);
        activity.multipleButtonsEventHandled = false;
        activity.findViewById(button2).performLongClick();
        assertThat(activity.multipleButtonsEventHandled).isTrue();
        assertThat(activity.viewArgument).hasId(button2);
    }
}

