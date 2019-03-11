package com.baeldung.dao.repositories;


import com.baeldung.domain.user.User;
import java.time.LocalDate;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("tc")
@ContextConfiguration(initializers = { UserRepositoryTCIntegrationTest.Initializer.class })
public class UserRepositoryTCIntegrationTest extends UserRepositoryCommon {
    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1").withDatabaseName("integration-tests-db").withUsername("sa").withPassword("sa");

    @Test
    @Transactional
    public void givenUsersInDB_WhenUpdateStatusForNameModifyingQueryAnnotationNative_ThenModifyMatchingUsers() {
        userRepository.save(new User("SAMPLE", LocalDate.now(), USER_EMAIL, ACTIVE_STATUS));
        userRepository.save(new User("SAMPLE1", LocalDate.now(), USER_EMAIL2, ACTIVE_STATUS));
        userRepository.save(new User("SAMPLE", LocalDate.now(), USER_EMAIL3, ACTIVE_STATUS));
        userRepository.save(new User("SAMPLE3", LocalDate.now(), USER_EMAIL4, ACTIVE_STATUS));
        userRepository.flush();
        int updatedUsersSize = userRepository.updateUserSetStatusForNameNativePostgres(INACTIVE_STATUS, "SAMPLE");
        assertThat(updatedUsersSize).isEqualTo(2);
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(("spring.datasource.url=" + (UserRepositoryTCIntegrationTest.postgreSQLContainer.getJdbcUrl())), ("spring.datasource.username=" + (UserRepositoryTCIntegrationTest.postgreSQLContainer.getUsername())), ("spring.datasource.password=" + (UserRepositoryTCIntegrationTest.postgreSQLContainer.getPassword()))).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}

