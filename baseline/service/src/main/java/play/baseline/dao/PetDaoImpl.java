package play.baseline.dao;

import play.baseline.model.Pet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PetDaoImpl implements PetDao {

    DataSource dataSource;

    public PetDaoImpl(DataSource dataSource) {
            this.dataSource = dataSource;
    }

    @Override
    public List<Pet> loadPets(String name, int age) {
        List<Pet> pets = new ArrayList<Pet>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "select * from pets where name = ? and age = ?";

            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, age);
            rs = ps.executeQuery();

            while (rs.next()) {
                Pet p = new Pet();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setAge(rs.getInt("age"));
                p.setOwner(rs.getString("owner_firstname") + " " + rs.getString("owner_lastname"));
                pets.add(p);
            }
        } catch (SQLException e) {
            // log
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                // log
            }
        }
        return pets;
    }

    @Override
    public List<Pet> loadPets(String ownerFirstName) {
        List<Pet> pets = new ArrayList<Pet>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "select * from pets where owner_firstname = ?";

            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, ownerFirstName);
            rs = ps.executeQuery();

            while (rs.next()) {
                Pet p = new Pet();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setAge(rs.getInt("age"));
                p.setOwner(rs.getString("owner_firstname") + " " + rs.getString("owner_lastname"));
                pets.add(p);
            }
        } catch (SQLException e) {
            // log
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                // log
            }
        }
        return pets;
    }


}
