/**
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liulishuo.okdownload.core.listener;


import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class DownloadListener1Test {
    private DownloadListener1 listener1;

    @Mock
    private DownloadTask task;

    @Mock
    private BreakpointInfo info;

    @Mock
    private Listener1Assist assist;

    private Map<String, List<String>> tmpFields;

    @Test
    public void taskStart() {
        listener1.taskStart(task);
        verify(listener1.assist).taskStart(eq(task));
    }

    @Test
    public void downloadFromBeginning() {
        final ResumeFailedCause cause = Mockito.mock(ResumeFailedCause.class);
        listener1.downloadFromBeginning(task, info, cause);
        verify(listener1.assist).downloadFromBeginning(eq(task), eq(info), eq(cause));
    }

    @Test
    public void downloadFromBreakpoint() {
        listener1.downloadFromBreakpoint(task, info);
        verify(listener1.assist).downloadFromBreakpoint(eq(task), eq(info));
    }

    @Test
    public void connectEnd() {
        listener1.connectEnd(task, 1, 200, tmpFields);
        verify(listener1.assist).connectEnd(eq(task));
    }

    @Test
    public void fetchProgress() {
        listener1.fetchProgress(task, 1, 2);
        verify(listener1.assist).fetchProgress(eq(task), org.mockito.ArgumentMatchers.eq(2L));
    }

    @Test
    public void isAlwaysRecoverAssistModel() {
        Mockito.when(assist.isAlwaysRecoverAssistModel()).thenReturn(true);
        assertThat(listener1.isAlwaysRecoverAssistModel()).isTrue();
        Mockito.when(assist.isAlwaysRecoverAssistModel()).thenReturn(false);
        assertThat(listener1.isAlwaysRecoverAssistModel()).isFalse();
    }

    @Test
    public void setAlwaysRecoverAssistModel() {
        listener1.setAlwaysRecoverAssistModel(true);
        verify(assist).setAlwaysRecoverAssistModel(eq(true));
        listener1.setAlwaysRecoverAssistModel(false);
        verify(assist).setAlwaysRecoverAssistModel(eq(false));
    }

    @Test
    public void setAlwaysRecoverAssistModelIfNotSet() {
        listener1.setAlwaysRecoverAssistModelIfNotSet(true);
        verify(assist).setAlwaysRecoverAssistModelIfNotSet(eq(true));
        listener1.setAlwaysRecoverAssistModelIfNotSet(false);
        verify(assist).setAlwaysRecoverAssistModelIfNotSet(eq(false));
    }
}

