package app.messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MessageRepository {

    private DataSource dataSource;

    public MessageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final static Log logger = LogFactory.getLog(MessageRepository.class);

    public Message saveMessage(Message message) {

        Connection c = DataSourceUtils.getConnection(dataSource);

        try {
            String insertSql = "INSERT INTO messages (text, created_date) VALUES (?, ?)";
            PreparedStatement ps = c.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, message.getText());
            ps.setTimestamp(2, new Timestamp(message.getCreatedDate().getTime()));
            int rowsAffected = ps.executeUpdate();

            if(rowsAffected > 0){
                ResultSet result = ps.getGeneratedKeys();
                if (result.next()) {
                    int id = result.getInt(1);
                    return new Message(id, message.getText(), message.getCreatedDate());
                } else {
                    logger.error("Failed to retrieve id. No row in result set");
                    return null;
                }
            } else {
                //Insert did not succeed
                return null;
            }
        } catch (SQLException ex) {
            logger.error("Failed to save message", ex);
            try {
                c.close();
            } catch (SQLException e) {
                logger.error("Failed to close connection", e);
            }
        } finally {
            DataSourceUtils.releaseConnection(c, dataSource);
        }
        //If we reach here then the insert did not succeed
        return null;
    }
}
