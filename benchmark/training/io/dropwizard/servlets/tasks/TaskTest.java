package io.dropwizard.servlets.tasks;


import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;


public class TaskTest {
    private final Task task = new Task("test") {
        @Override
        public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
        }
    };

    @Test
    public void hasAName() throws Exception {
        assertThat(task.getName()).isEqualTo("test");
    }
}

