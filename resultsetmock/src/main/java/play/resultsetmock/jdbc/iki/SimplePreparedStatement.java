package play.resultsetmock.jdbc.iki;

import java.sql.PreparedStatement;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 6/26/12
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SimplePreparedStatement extends SimpleStatement implements PreparedStatement {
    
    String sql;

}
