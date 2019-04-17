/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.Database.PostgreSQL;


import com.google.security.zynamics.binnavi.Database.CDatabase;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Converts a single module from a raw module to a BinNavi module. Is used as integration test.
 */
@RunWith(JUnit4.class)
public class PostgreSQLConvertNotepadTest {
    private CDatabase database;

    @Test
    public void testConvert() throws CouldntSaveDataException, IllegalStateException {
        database.getContent().getModules().get(0).initialize();
    }
}
