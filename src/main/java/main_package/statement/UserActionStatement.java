package main_package.statement;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UserActionStatement {

    private final PreparedStatement selectStatement;
    private final PreparedStatement insertStatement;

    public UserActionStatement(CqlSession session) {
        this.selectStatement = session.prepare(
            "SELECT * FROM my_keyspace.user WHERE user_id = ?"
        );
        this.insertStatement = session.prepare(
            "INSERT INTO my_keyspace.user_audit (user_id, event_time, event_type) " +
                "VALUES (?, ?, ?)"
        );
    }
}
