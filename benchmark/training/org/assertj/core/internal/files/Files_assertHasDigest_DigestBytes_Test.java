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
package org.assertj.core.internal.files;


import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeFile;
import org.assertj.core.error.ShouldBeReadable;
import org.assertj.core.error.ShouldExist;
import org.assertj.core.error.ShouldHaveDigest;
import org.assertj.core.internal.DigestDiff;
import org.assertj.core.internal.FilesBaseTest;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Files#assertHasDigest(AssertionInfo, File, MessageDigest, byte[])}</code>
 *
 * @author Valeriy Vyrva
 */
public class Files_assertHasDigest_DigestBytes_Test extends FilesBaseTest {
    private final MessageDigest digest = Mockito.mock(MessageDigest.class);

    private final byte[] expected = new byte[0];

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertHasDigest(FilesBaseTest.INFO, null, digest, expected)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_with_should_exist_error_if_actual_does_not_exist() {
        // GIVEN
        BDDMockito.given(actual.exists()).willReturn(false);
        // WHEN
        Assertions.catchThrowable(() -> files.assertHasDigest(FilesBaseTest.INFO, actual, digest, expected));
        // THEN
        Mockito.verify(failures).failure(FilesBaseTest.INFO, ShouldExist.shouldExist(actual));
    }

    @Test
    public void should_fail_if_actual_exists_but_is_not_file() {
        // GIVEN
        BDDMockito.given(actual.exists()).willReturn(true);
        BDDMockito.given(actual.isFile()).willReturn(false);
        // WHEN
        Assertions.catchThrowable(() -> files.assertHasDigest(FilesBaseTest.INFO, actual, digest, expected));
        // THEN
        Mockito.verify(failures).failure(FilesBaseTest.INFO, ShouldBeFile.shouldBeFile(actual));
    }

    @Test
    public void should_fail_if_actual_exists_but_is_not_readable() {
        // GIVEN
        BDDMockito.given(actual.exists()).willReturn(true);
        BDDMockito.given(actual.isFile()).willReturn(true);
        BDDMockito.given(actual.canRead()).willReturn(false);
        // WHEN
        Assertions.catchThrowable(() -> files.assertHasDigest(FilesBaseTest.INFO, actual, digest, expected));
        // THEN
        Mockito.verify(failures).failure(FilesBaseTest.INFO, ShouldBeReadable.shouldBeReadable(actual));
    }

    @Test
    public void should_throw_error_if_digest_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> files.assertHasDigest(FilesBaseTest.INFO, null, ((MessageDigest) (null)), expected)).withMessage("The message digest algorithm should not be null");
    }

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> files.assertHasDigest(FilesBaseTest.INFO, null, digest, ((byte[]) (null)))).withMessage("The binary representation of digest to compare to should not be null");
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        // GIVEN
        IOException cause = new IOException();
        BDDMockito.given(actual.exists()).willReturn(true);
        BDDMockito.given(actual.isFile()).willReturn(true);
        BDDMockito.given(actual.canRead()).willReturn(true);
        BDDMockito.given(nioFilesWrapper.newInputStream(ArgumentMatchers.any())).willThrow(cause);
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> files.assertHasDigest(FilesBaseTest.INFO, actual, digest, expected));
        // THEN
        Assertions.assertThat(error).isInstanceOf(UncheckedIOException.class).hasCause(cause);
    }

    @Test
    public void should_throw_error_wrapping_catched_NoSuchAlgorithmException() {
        // GIVEN
        String unknownDigestAlgorithm = "UnknownDigestAlgorithm";
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> files.assertHasDigest(FilesBaseTest.INFO, actual, unknownDigestAlgorithm, expected));
        // THEN
        Assertions.assertThat(error).isInstanceOf(IllegalStateException.class).hasMessage("Unable to find digest implementation for: <UnknownDigestAlgorithm>");
    }

    @Test
    public void should_fail_if_actual_does_not_have_expected_digest() throws IOException {
        // GIVEN
        InputStream stream = getClass().getResourceAsStream("/red.png");
        BDDMockito.given(actual.exists()).willReturn(true);
        BDDMockito.given(actual.isFile()).willReturn(true);
        BDDMockito.given(actual.canRead()).willReturn(true);
        BDDMockito.given(nioFilesWrapper.newInputStream(ArgumentMatchers.any())).willReturn(stream);
        BDDMockito.given(digest.digest()).willReturn(new byte[]{ 0, 1 });
        // WHEN
        Assertions.catchThrowable(() -> files.assertHasDigest(FilesBaseTest.INFO, actual, digest, expected));
        // THEN
        Mockito.verify(failures).failure(FilesBaseTest.INFO, ShouldHaveDigest.shouldHaveDigest(actual, new DigestDiff("0001", "", digest)));
        FilesBaseTest.failIfStreamIsOpen(stream);
    }

    @Test
    public void should_pass_if_actual_has_expected_digest() throws IOException {
        // GIVEN
        InputStream stream = getClass().getResourceAsStream("/red.png");
        BDDMockito.given(actual.exists()).willReturn(true);
        BDDMockito.given(actual.isFile()).willReturn(true);
        BDDMockito.given(actual.canRead()).willReturn(true);
        BDDMockito.given(nioFilesWrapper.newInputStream(ArgumentMatchers.any())).willReturn(stream);
        BDDMockito.given(digest.digest()).willReturn(expected);
        // WHEN
        files.assertHasDigest(FilesBaseTest.INFO, actual, digest, expected);
        // THEN
        FilesBaseTest.failIfStreamIsOpen(stream);
    }
}
