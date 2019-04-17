package org.mockserver.serialization.serializers.condition;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.serialization.ObjectMapperFactory;


/**
 *
 *
 * @author jamesdbloom
 */
public class VerificationTimesDTOSerializerTest {
    @Test
    public void shouldSerializeBetween() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.VerificationTimesDTO(between(1, 2))), Is.is((((((("{" + (NEW_LINE)) + "  \"atLeast\" : 1,") + (NEW_LINE)) + "  \"atMost\" : 2") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeOnce() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.VerificationTimesDTO(once())), Is.is((((((("{" + (NEW_LINE)) + "  \"atLeast\" : 1,") + (NEW_LINE)) + "  \"atMost\" : 1") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeExact() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.VerificationTimesDTO(exactly(2))), Is.is((((((("{" + (NEW_LINE)) + "  \"atLeast\" : 2,") + (NEW_LINE)) + "  \"atMost\" : 2") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeAtLeast() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.VerificationTimesDTO(atLeast(2))), Is.is((((("{" + (NEW_LINE)) + "  \"atLeast\" : 2") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeAtMost() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.VerificationTimesDTO(atMost(2))), Is.is((((("{" + (NEW_LINE)) + "  \"atMost\" : 2") + (NEW_LINE)) + "}")));
    }
}
