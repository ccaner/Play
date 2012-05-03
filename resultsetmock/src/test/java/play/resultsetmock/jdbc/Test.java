package play.resultsetmock.jdbc;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.mockito.Mockito;
import play.resultsetmock.IModel;
import play.resultsetmock.Model;
import play.resultsetmock.Pet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Test {
    
    Model proxy;
    IModel inner;
    
    
    @Before
    public void setup() {
        inner = Mockito.mock(IModel.class);
        proxy = new Model(inner);
        when(inner.loadPets(anyString(), anyInt())).thenReturn(
                Lists.<Pet>newArrayList(
                        new Pet("pet", 1, "Caner"),
                        new Pet("pet2", 2, "Caner2")
        ));
    }

    @org.junit.Test
    public void testRsOnly() throws SQLException {

        ResultSet rs = MockJdbcFactory.createResultSet(proxy.loadPets(null, 0));

        assertResultSet(rs);
    }

    @org.junit.Test
    public void testFromDs() throws SQLException {
        DataSource ds = MockJdbcFactory.createDataSource(proxy);

        Connection conn = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "select * from pets";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "argName");
            ps.setInt(2, 44);
            rs = ps.executeQuery();

            assertResultSet(rs);
        } finally {
            try {
                rs.close();
                ps.close();
                conn.close();
            } catch (Exception e) {
            }
        }

        verify(inner).loadPets(eq("argName"), eq(44));
    }

    private void assertResultSet(ResultSet rs) throws SQLException {
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
    
}
