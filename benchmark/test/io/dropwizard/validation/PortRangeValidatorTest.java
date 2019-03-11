package io.dropwizard.validation;


import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;


public class PortRangeValidatorTest {
    @SuppressWarnings("PublicField")
    public static class Example {
        @PortRange
        public int port = 8080;

        @PortRange(min = 10000, max = 15000)
        public int otherPort = 10001;

        @Valid
        List<@PortRange
        Integer> ports = Collections.emptyList();
    }

    private final Validator validator = BaseValidator.newValidator();

    private final PortRangeValidatorTest.Example example = new PortRangeValidatorTest.Example();

    @Test
    public void acceptsNonPrivilegedPorts() throws Exception {
        example.port = 2048;
        assertThat(validator.validate(example)).isEmpty();
    }

    @Test
    public void acceptsDynamicPorts() throws Exception {
        example.port = 0;
        assertThat(validator.validate(example)).isEmpty();
    }

    @Test
    public void rejectsNegativePorts() throws Exception {
        example.port = -1;
        assertThat(ConstraintViolations.format(validator.validate(example))).containsOnly("port must be between 1 and 65535");
    }

    @Test
    public void allowsForCustomMinimumPorts() throws Exception {
        example.otherPort = 8080;
        assertThat(ConstraintViolations.format(validator.validate(example))).containsOnly("otherPort must be between 10000 and 15000");
    }

    @Test
    public void allowsForCustomMaximumPorts() throws Exception {
        example.otherPort = 16000;
        assertThat(ConstraintViolations.format(validator.validate(example))).containsOnly("otherPort must be between 10000 and 15000");
    }

    @Test
    public void rejectsInvalidPortsInList() {
        example.ports = Collections.singletonList((-1));
        assertThat(ConstraintViolations.format(validator.validate(example))).containsOnly("ports[0].<list element> must be between 1 and 65535");
    }
}

