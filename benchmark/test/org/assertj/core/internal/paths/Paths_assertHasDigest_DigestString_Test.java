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
package org.assertj.core.internal.paths;


import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeReadable;
import org.assertj.core.error.ShouldBeRegularFile;
import org.assertj.core.error.ShouldExist;
import org.assertj.core.error.ShouldHaveDigest;
import org.assertj.core.internal.DigestDiff;
import org.assertj.core.internal.PathsBaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Paths#assertHasDigest(AssertionInfo, Path, MessageDigest, String)}</code>
 *
 * @author Valeriy Vyrva
 */
public class Paths_assertHasDigest_DigestString_Test extends MockPathsBaseTest {
    private final MessageDigest digest = Mockito.mock(MessageDigest.class);

    private final String expected = "";

    @Test
    public void should_fail_with_should_exist_error_if_actual_does_not_exist() {
        // GIVEN
        BDDMockito.given(nioFilesWrapper.exists(actual)).willReturn(false);
        // WHEN
        Assertions.catchThrowable(() -> paths.assertHasDigest(MockPathsBaseTest.INFO, actual, digest, expected));
        // THEN
        Mockito.verify(failures).failure(MockPathsBaseTest.INFO, ShouldExist.shouldExist(actual));
    }

    @Test
    public void should_fail_if_actual_exists_but_is_not_file() {
        // GIVEN
        BDDMockito.given(nioFilesWrapper.exists(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isRegularFile(actual)).willReturn(false);
        // WHEN
        Assertions.catchThrowable(() -> paths.assertHasDigest(MockPathsBaseTest.INFO, actual, digest, expected));
        // THEN
        Mockito.verify(failures).failure(MockPathsBaseTest.INFO, ShouldBeRegularFile.shouldBeRegularFile(actual));
    }

    @Test
    public void should_fail_if_actual_exists_but_is_not_readable() {
        // GIVEN
        BDDMockito.given(nioFilesWrapper.exists(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isRegularFile(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isReadable(actual)).willReturn(false);
        // WHEN
        Assertions.catchThrowable(() -> paths.assertHasDigest(MockPathsBaseTest.INFO, actual, digest, expected));
        // THEN
        Mockito.verify(failures).failure(MockPathsBaseTest.INFO, ShouldBeReadable.shouldBeReadable(actual));
    }

    @Test
    public void should_throw_error_if_digest_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertHasDigest(MockPathsBaseTest.INFO, null, ((MessageDigest) (null)), expected)).withMessage("The message digest algorithm should not be null");
    }

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertHasDigest(MockPathsBaseTest.INFO, null, digest, ((byte[]) (null)))).withMessage("The binary representation of digest to compare to should not be null");
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        // GIVEN
        IOException cause = new IOException();
        BDDMockito.given(nioFilesWrapper.exists(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isRegularFile(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isReadable(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.newInputStream(actual)).willThrow(cause);
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> paths.assertHasDigest(MockPathsBaseTest.INFO, actual, digest, expected));
        // THEN
        Assertions.assertThat(error).isInstanceOf(UncheckedIOException.class).hasCause(cause);
    }

    @Test
    public void should_throw_error_wrapping_catched_NoSuchAlgorithmException() {
        // GIVEN
        String unknownDigestAlgorithm = "UnknownDigestAlgorithm";
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> paths.assertHasDigest(MockPathsBaseTest.INFO, actual, unknownDigestAlgorithm, expected));
        // THEN
        Assertions.assertThat(error).isInstanceOf(IllegalStateException.class).hasMessage("Unable to find digest implementation for: <UnknownDigestAlgorithm>");
    }

    @Test
    public void should_fail_if_actual_does_not_have_expected_digest() throws IOException {
        // GIVEN
        InputStream stream = getClass().getResourceAsStream("/red.png");
        BDDMockito.given(nioFilesWrapper.exists(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isRegularFile(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isReadable(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.newInputStream(actual)).willReturn(stream);
        BDDMockito.given(digest.digest()).willReturn(new byte[]{ 0, 1 });
        // WHEN
        Assertions.catchThrowable(() -> paths.assertHasDigest(MockPathsBaseTest.INFO, actual, digest, expected));
        // THEN
        Mockito.verify(failures).failure(MockPathsBaseTest.INFO, ShouldHaveDigest.shouldHaveDigest(actual, new DigestDiff("0001", "", digest)));
        MockPathsBaseTest.failIfStreamIsOpen(stream);
    }

    @Test
    public void should_pass_if_actual_has_expected_digest() throws IOException {
        // GIVEN
        InputStream stream = getClass().getResourceAsStream("/red.png");
        BDDMockito.given(nioFilesWrapper.exists(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isRegularFile(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.isReadable(actual)).willReturn(true);
        BDDMockito.given(nioFilesWrapper.newInputStream(actual)).willReturn(stream);
        BDDMockito.given(digest.digest()).willReturn(expected.getBytes());
        // WHEN
        paths.assertHasDigest(MockPathsBaseTest.INFO, actual, digest, expected);
        // THEN
        MockPathsBaseTest.failIfStreamIsOpen(stream);
    }
}

