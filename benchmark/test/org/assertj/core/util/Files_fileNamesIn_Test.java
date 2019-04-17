/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util;


import java.io.File;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Files#fileNamesIn(String, boolean)}</code>.
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public class Files_fileNamesIn_Test extends Files_TestCase {
    @Test
    public void should_throw_error_if_directory_does_not_exist() {
        String path = Strings.concat("root", File.separator, "not_existing_dir");
        assertThatIllegalArgumentException().isThrownBy(() -> Files.fileNamesIn(path, false));
    }

    @Test
    public void should_throw_error_if_path_does_not_belong_to_a_directory() throws Exception {
        String fileName = "file_1";
        root.addFiles(fileName);
        String path = Strings.concat("root", File.separator, fileName);
        assertThatIllegalArgumentException().isThrownBy(() -> Files.fileNamesIn(path, false));
    }

    @Test
    public void should_return_names_of_files_in_given_directory_but_not_subdirectories() {
        String path = Strings.concat("root", File.separator, "dir_1");
        assertThatContainsFiles(Lists.newArrayList("file_1_1", "file_1_2"), Files.fileNamesIn(path, false));
    }

    @Test
    public void should_return_names_of_files_in_given_directory_and_its_subdirectories() {
        String path = Strings.concat("root", File.separator, "dir_1");
        assertThatContainsFiles(Lists.newArrayList("file_1_1", "file_1_2", "file_1_1_1"), Files.fileNamesIn(path, true));
    }
}
