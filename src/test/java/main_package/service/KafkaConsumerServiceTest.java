/*package main_package.service;

import static org.junit.jupiter.api.Assertions.*;

import main_package.config.JacksonConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
@Import(JacksonConfig.class)
class KafkaConsumerServiceTest {

  @Container
  public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

  @Autowired
  private KafkaConsumerService kafkaConsumerService;

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
  }
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
  }

  @Test
  public void consumeAction() {
    String action = "{\"id\":\"1\",\"eventTime\":\"2025-05-24T14:30:00Z\",\"eventType\":\"CREATE\"}";
    kafkaConsumerService.listenAuditMessages(action);
  }

  @Test
  public void consumeNullAction() {
    String nullAction = null;
    assertThrows(Exception.class, () -> {
      kafkaConsumerService.listenAuditMessages(nullAction);
    });
  }
}
*/