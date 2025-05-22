package main_package;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {

  @Bean
  public CqlSession cqlSession(CqlSessionBuilder sessionBuilder) {
    InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 9042);
    sessionBuilder = sessionBuilder.addContactPoint(address).withLocalDatacenter("datacenter1").withConfigLoader(
        DriverConfigLoader.programmaticBuilder()
            .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10))
            .build()
    ); ;
    sessionBuilder.withKeyspace((CqlIdentifier) null);

    CqlSession session = sessionBuilder.build();

    SimpleStatement statement = SchemaBuilder.createKeyspace("my_keyspace")
        .ifNotExists()
        .withNetworkTopologyStrategy(Map.of("datacenter1", 1))
        .build();
    session.execute(statement);

    session.execute("""
            CREATE TABLE IF NOT EXISTS my_keyspace.user_audit (
                id UUID,
                event_time TIMESTAMP,
                event_type TEXT,
                PRIMARY KEY ((id), event_time)
            ) WITH CLUSTERING ORDER BY (event_time DESC)
               AND default_time_to_live = 31536000;
            """);

    return sessionBuilder
        .withKeyspace("my_keyspace")
        .build();
  }
}
