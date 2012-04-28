import com.google.common.collect.Lists;
import play.resultsetmock.jdbc.MockJdbcFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 4/28/12
 * Time: 1:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    @org.junit.Test
    public void testRsOnly() throws SQLException {
        List<Pet> pets = Lists.<Pet>newArrayList(
                new Pet("pet", 1, "Caner"),
                new Pet("pet2", 2, "Caner2"));

        ResultSet rs = MockJdbcFactory.createResultSet(pets);

        int i = 0;
        while(rs.next()) {
            if (i == 0) {
                assertEquals("pet", rs.getString("name"));
                assertEquals(1, rs.getInt("age"));
                assertEquals("Caner", rs.getString("owner_name"));
            } else if (i == 1) {
                assertEquals("pet2", rs.getString("name"));
                assertEquals(2, rs.getInt("age"));
                assertEquals("Caner2", rs.getString("owner_name"));
            } else {
                fail("Only 2 rows are expected");
            }
            i++;
        }
    }
    
    @org.junit.Test
    public void testFromDs() throws SQLException {
        Model model = new Model();
        DataSource ds = MockJdbcFactory.createDataSource(model);

        Connection conn = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String SQL = "select * from pets";
            ps = conn.prepareStatement(SQL);
            ps.setString(1, "argName");
            ps.setInt(2, 44);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                if (i == 0) {
                    assertEquals("pet", rs.getString("name"));
                    assertEquals(1, rs.getInt("age"));
                    assertEquals("Caner", rs.getString("owner_name"));
                } else if (i == 1) {
                    assertEquals("pet2", rs.getString("name"));
                    assertEquals(2, rs.getInt("age"));
                    assertEquals("Caner2", rs.getString("owner_name"));
                } else {
                    fail("Only 2 rows are expected");
                }
                i++;
            }
        } finally {
            try {
                rs.close();
                ps.close();
                conn.close();
            } catch (Exception e) {
            }
        }

    }
    
    
}
