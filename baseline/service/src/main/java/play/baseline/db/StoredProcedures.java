package play.baseline.db;

import java.sql.*;

public class StoredProcedures {

    public static void load_pets_grouped(String name, int age, ResultSet[] byName,
                                  ResultSet[] byAge) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        PreparedStatement psName = conn.prepareStatement("select * from pets where name = ?");
        psName.setString(1, name);
        byName[0] = psName.executeQuery();

        PreparedStatement psAge = conn.prepareStatement("select * from pets where age = ?");
        psAge.setInt(1, age);
        byAge[0] = psAge.executeQuery();

        conn.close();
    }

}
