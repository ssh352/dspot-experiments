package com.baeldung.eclipsecollections;


import Lists.mutable;
import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.list.MutableList;
import org.junit.Test;


public class LazyIterationUnitTest {
    @Test
    public void whenLazyIteration_thenCorrect() {
        Student student1 = new Student("John", "Hopkins");
        Student student2 = new Student("George", "Adams");
        Student student3 = new Student("Jennifer", "Rodriguez");
        MutableList<Student> students = mutable.with(student1, student2, student3);
        LazyIterable<Student> lazyStudents = students.asLazy();
        LazyIterable<String> lastNames = lazyStudents.collect(Student::getLastName);
        Assertions.assertThat(lastNames).containsAll(mutable.with("Hopkins", "Adams", "Rodriguez"));
    }
}

