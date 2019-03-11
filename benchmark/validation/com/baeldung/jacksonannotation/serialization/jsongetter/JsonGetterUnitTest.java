package com.baeldung.jacksonannotation.serialization.jsongetter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.junit.Test;


/**
 * Source code github.com/eugenp/tutorials
 *
 * @author Alex Theedom www.baeldung.com
 * @version 1.0
 */
public class JsonGetterUnitTest {
    @Test
    public void whenSerializingUsingJsonGetter_andNoPropertyNameSet_thenCorrect() throws JsonProcessingException {
        // arrange
        Author1 author = new Author1("Alex", "Theedom");
        // act
        String result = new ObjectMapper().writeValueAsString(author);
        // assert
        assertThat(JsonPath.from(result).getList("items")).isNotNull();
        /* {
        "firstName": "Alex",
        "lastName": "Theedom",
        "items": []
        }
         */
    }

    @Test
    public void whenSerializingUsingJsonGetter_andPropertyNameSet_thenCorrect() throws JsonProcessingException {
        // arrange
        Author2 author = new Author2("Alex", "Theedom");
        // act
        String result = new ObjectMapper().writeValueAsString(author);
        // assert
        assertThat(JsonPath.from(result).getList("publications")).isNotNull();
        assertThat(JsonPath.from(result).getList("items")).isNull();
        /* {
        "firstName": "Alex",
        "lastName": "Theedom",
        "publications": []
        }
         */
    }
}

