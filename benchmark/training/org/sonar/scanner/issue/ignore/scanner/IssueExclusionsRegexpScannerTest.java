/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.issue.ignore.scanner;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.junit.Test;
import org.mockito.Mock;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.scanner.issue.ignore.pattern.IssueExclusionPatternInitializer;
import org.sonar.scanner.issue.ignore.scanner.IssueExclusionsLoader.DoubleRegexpMatcher;


public class IssueExclusionsRegexpScannerTest {
    private DefaultInputFile javaFile;

    @Mock
    private IssueExclusionPatternInitializer patternsInitializer;

    private List<Pattern> allFilePatterns;

    private List<DoubleRegexpMatcher> blockPatterns;

    private IssueExclusionsRegexpScanner regexpScanner;

    private FileMetadata fileMetadata = new FileMetadata();

    @Test
    public void shouldDetectPatternLastLine() throws IOException, URISyntaxException {
        Path filePath = getResource("file-with-single-regexp-last-line.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(javaFile.isIgnoreAllIssues()).isTrue();
    }

    @Test
    public void shouldDoNothing() throws Exception {
        Path filePath = getResource("file-with-no-regexp.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(javaFile.isIgnoreAllIssues()).isFalse();
    }

    @Test
    public void shouldExcludeAllIssues() throws Exception {
        Path filePath = getResource("file-with-single-regexp.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(javaFile.isIgnoreAllIssues()).isTrue();
    }

    @Test
    public void shouldExcludeAllIssuesEvenIfAlsoDoubleRegexps() throws Exception {
        Path filePath = getResource("file-with-single-regexp-and-double-regexp.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(javaFile.isIgnoreAllIssues()).isTrue();
    }

    @Test
    public void shouldExcludeLines() throws Exception {
        Path filePath = getResource("file-with-double-regexp.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(javaFile.isIgnoreAllIssues()).isFalse();
        assertThat(IntStream.rangeClosed(1, 20).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(21, 25).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(26, 34).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    }

    @Test
    public void shouldAddPatternToExcludeLinesTillTheEnd() throws Exception {
        Path filePath = getResource("file-with-double-regexp-unfinished.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(javaFile.isIgnoreAllIssues()).isFalse();
        assertThat(IntStream.rangeClosed(1, 20).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(21, 34).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    }

    @Test
    public void shouldAddPatternToExcludeSeveralLineRanges() throws Exception {
        Path filePath = getResource("file-with-double-regexp-twice.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(javaFile.isIgnoreAllIssues()).isFalse();
        assertThat(IntStream.rangeClosed(1, 20).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(21, 25).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(26, 28).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(29, 33).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    }

    @Test
    public void shouldAddPatternToExcludeLinesWithWrongOrder() throws Exception {
        Path filePath = getResource("file-with-double-regexp-wrong-order.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(IntStream.rangeClosed(1, 24).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(25, 35).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    }

    @Test
    public void shouldAddPatternToExcludeLinesWithMess() throws Exception {
        Path filePath = getResource("file-with-double-regexp-mess.txt");
        fileMetadata.readMetadata(Files.newInputStream(filePath), StandardCharsets.UTF_8, filePath.toString(), regexpScanner);
        assertThat(IntStream.rangeClosed(1, 20).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(21, 29).allMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
        assertThat(IntStream.rangeClosed(30, 37).noneMatch(javaFile::isIgnoreAllIssuesOnLine)).isTrue();
    }
}

