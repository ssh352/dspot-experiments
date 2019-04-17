/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.collection.map;


import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.test.collection.map.other.ImportedType;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for implementation of {@code Map} mapping methods.
 *
 * @author Gunnar Morling
 */
@WithClasses({ SourceTargetMapper.class, CustomNumberMapper.class, Source.class, Target.class, ImportedType.class })
@IssueKey("44")
@RunWith(AnnotationProcessorTestRunner.class)
public class MapMappingTest {
    @Test
    public void shouldCreateMapMethodImplementation() {
        Map<Long, Date> values = new HashMap<Long, Date>();
        values.put(42L, new GregorianCalendar(1980, 0, 1).getTime());
        values.put(121L, new GregorianCalendar(2013, 6, 20).getTime());
        Map<String, String> target = SourceTargetMapper.INSTANCE.longDateMapToStringStringMap(values);
        assertThat(target).isNotNull();
        assertThat(target).hasSize(2);
        assertThat(target).contains(entry("42", "01.01.1980"), entry("121", "20.07.2013"));
    }

    @Test
    public void shouldCreateReverseMapMethodImplementation() {
        Map<String, String> values = createStringStringMap();
        Map<Long, Date> target = SourceTargetMapper.INSTANCE.stringStringMapToLongDateMap(values);
        assertResult(target);
    }

    @Test
    @IssueKey("19")
    public void shouldCreateMapMethodImplementationWithTargetParameter() {
        Map<String, String> values = createStringStringMap();
        Map<Long, Date> target = new HashMap<Long, Date>();
        target.put(66L, new GregorianCalendar(2013, 7, 16).getTime());
        SourceTargetMapper.INSTANCE.stringStringMapToLongDateMapUsingTargetParameter(target, values);
        assertResult(target);
    }

    @Test
    @IssueKey("19")
    public void shouldCreateMapMethodImplementationWithReturnedTargetParameter() {
        Map<String, String> values = createStringStringMap();
        Map<Long, Date> target = new HashMap<Long, Date>();
        target.put(66L, new GregorianCalendar(2013, 7, 16).getTime());
        Map<Long, Date> returnedTarget = SourceTargetMapper.INSTANCE.stringStringMapToLongDateMapUsingTargetParameterAndReturn(values, target);
        assertThat(target).isSameAs(returnedTarget);
        assertResult(target);
    }

    @Test
    public void shouldInvokeMapMethodImplementationForMapTypedProperty() {
        Map<Long, Date> values = new HashMap<Long, Date>();
        values.put(42L, new GregorianCalendar(1980, 0, 1).getTime());
        values.put(121L, new GregorianCalendar(2013, 6, 20).getTime());
        Source source = new Source();
        source.setValues(values);
        source.setPublicValues(new HashMap<Long, Date>(values));
        Target target = SourceTargetMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getValues()).isNotNull();
        assertThat(target.getValues()).hasSize(2);
        assertThat(target.getValues()).contains(entry("42", "01.01.1980"), entry("121", "20.07.2013"));
        assertThat(target.publicValues).isNotNull().hasSize(2).contains(entry("42", "01.01.1980"), entry("121", "20.07.2013"));
    }

    @Test
    public void shouldInvokeReverseMapMethodImplementationForMapTypedProperty() {
        Map<String, String> values = createStringStringMap();
        Target target = new Target();
        target.setValues(values);
        target.publicValues = new HashMap<String, String>(values);
        Source source = SourceTargetMapper.INSTANCE.targetToSource(target);
        assertThat(source).isNotNull();
        assertThat(source.getValues()).isNotNull();
        assertThat(source.getValues()).hasSize(2);
        assertThat(source.getValues()).contains(entry(42L, new GregorianCalendar(1980, 0, 1).getTime()), entry(121L, new GregorianCalendar(2013, 6, 20).getTime()));
        assertThat(source.getPublicValues()).isNotNull().hasSize(2).contains(entry(42L, new GregorianCalendar(1980, 0, 1).getTime()), entry(121L, new GregorianCalendar(2013, 6, 20).getTime()));
    }

    @Test
    @IssueKey("87")
    public void shouldCreateMapMethodImplementationWithoutConversionOrElementMappingMethod() {
        Map<Integer, Integer> values = createIntIntMap();
        Map<Number, Number> target = SourceTargetMapper.INSTANCE.intIntToNumberNumberMap(values);
        assertThat(target).isNotNull();
        assertThat(target).hasSize(2);
        assertThat(target).isEqualTo(values);
    }
}
