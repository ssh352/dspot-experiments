package com.baeldung.lombok.intro;


import org.junit.jupiter.api.Test;


public class BuilderMethodUnitTest {
    @Test
    public void givenBuilderMethod_ClientIsBuilt() {
        ImmutableClient testImmutableClient = ClientBuilder.builder().name("foo").id(1).build();
        assertThat(testImmutableClient.getName()).isEqualTo("foo");
        assertThat(testImmutableClient.getId()).isEqualTo(1);
    }
}

