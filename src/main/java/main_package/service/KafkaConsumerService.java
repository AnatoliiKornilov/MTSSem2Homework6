package main_package.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main_package.entity.UserAction;
import main_package.statement.UserActionStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

  private final ObjectMapper objectMapper;

  @Autowired
  private CqlSession session;

  @Autowired
  UserActionStatement userActionStatement;

  @Autowired
  public KafkaConsumerService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "action", groupId = "action")
  public void listenActionMessages(String userActionJson) throws JsonProcessingException {
    if (userActionJson == null) {
      throw new IllegalArgumentException("Неверные данные");
    }
    UserAction userAction = objectMapper.readValue(userActionJson, UserAction.class);
    insertUserAction(userAction);
  }

  private void insertUserAction(UserAction userAction) {
    PreparedStatement insertStatement = userActionStatement.getInsertStatement();
    BoundStatement boundStatement = insertStatement.bind(
        userAction.getId(),
        userAction.getEventTime(),
        userAction.getEventType()
    );
    session.execute(boundStatement);
  }
}
