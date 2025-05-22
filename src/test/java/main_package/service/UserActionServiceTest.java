package main_package.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.datastax.oss.driver.api.core.cql.Row;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import main_package.Application;
import main_package.entity.UserAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = Application.class)
@Testcontainers
class UserActionServiceTest {

  @Autowired
  public UserActionService userActionService;

  @Container
  private static final CassandraContainer<?> cassandraContainer =
      new CassandraContainer<>("cassandra:4.1.3")
          .withExposedPorts(9042)
          .withReuse(true)
          .waitingFor(Wait.forLogMessage(".*Created default superuser role 'cassandra'.*", 1));

  @DynamicPropertySource
  static void cassandraProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.cassandra.contact-points",
        () -> cassandraContainer.getHost() + ":" + cassandraContainer.getMappedPort(9042));
    registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
    registry.add("spring.cassandra.keyspace-name", () -> "my_keyspace");
  }

  @BeforeAll
  static void checkContainer() {
    if (!cassandraContainer.isRunning()) {
      cassandraContainer.start();
    }
    System.out.println("Cassandra port: " + cassandraContainer.getMappedPort(9042));
  }


  @Test
  public void testCreateUser() {
    UserAction action = new UserAction(UUID.randomUUID(), Instant.now(), "CREATE");
    userActionService.insertAction(action);
    List<Row> userAudit = userActionService.getById(action.getId());
    Assertions.assertNotNull(action.getId());
    assertEquals(userAudit.size(), 1);
  }

  @Test
  public void testGetUserById() {
    UserAction nullUser = null;
    assertThrows(RuntimeException.class, () -> {
      userActionService.insertAction(nullUser);
    });
  }
}
