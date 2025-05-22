package main_package.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import main_package.entity.UserAction;
import main_package.statement.UserActionStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserActionService {

  @Autowired
  private CqlSession session;

  @Autowired
  private UserActionStatement userStatement;

  public List<Row> getById(UUID id) {
    BoundStatement boundStatement = userStatement.getSelectStatement().bind(id);
    ResultSet resultSet = session.execute(boundStatement);
    List<Row> result = new ArrayList<>();
    for (Row row : resultSet) {
      result.add(row);
    }
    return result;
  }

  public void insertAction(UserAction userAction) {

    BoundStatement boundStatement = userStatement.getInsertStatement().bind(
        userAction.getId(),
        userAction.getEventTime(),
        userAction.getEventType()
    );
    session.execute(boundStatement);
  }
}
