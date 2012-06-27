package play.resultsetmock.jdbc.iki;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SimpleStatement implements Statement {

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
