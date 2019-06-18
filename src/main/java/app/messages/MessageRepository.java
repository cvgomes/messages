package app.messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MessageRepository {

    private static final Log logger = LogFactory.getLog(MessageRepository.class);

    private NamedParameterJdbcTemplate jdbcTemplate;

    public MessageRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Message saveMessage(Message message) {

        String sql = "INSERT INTO messages (text, created_date) VALUES (:text, :createdDate)";
        KeyHolder holder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("text", message.getText());
        params.addValue("createdDate", message.getCreatedDate());

        try {
            this.jdbcTemplate.update(sql, params, holder);
        } catch (DataAccessException e) {
            logger.error("Failed to save message", e);
            return null;
        }
        return new Message((Integer)holder.getKeys().get("id"), message.getText(), message.getCreatedDate());
    }
}
