package play.baseline.dao;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.baseline.model.Pet;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class PetDaoImpl implements PetDao {
    
    public static Logger logger = LoggerFactory.getLogger(PetDaoImpl.class);

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
            logger.error("DAO Error: ", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("DAO Error!!!: ", e);
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
            logger.error("DAO Error: ", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("DAO Error!!!: ", e);
            }
        }
        return pets;
    }

    @Override
    public Map<String, List<Pet>> loadPetsGrouped(String name, int age) {
        List<Pet> petsByName = new ArrayList<Pet>();
        List<Pet> petsByAge = new ArrayList<Pet>();

        Map<String, List<Pet>> pets = new LinkedHashMap<String, List<Pet>>();
        pets.put("byName", petsByName);
        pets.put("byAge", petsByAge);

        Connection conn = null;
        CallableStatement cs = null;

        try {
            String sql = "{ call load_pets_grouped(?, ?) }";

            conn = dataSource.getConnection();
            cs = conn.prepareCall(sql);
            cs.setString(1, name);
            cs.setInt(2, age);

            boolean hasResult = cs.execute();

            List<Pet> petList = petsByName;
            while (hasResult) {
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    Pet p = new Pet();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setAge(rs.getInt("age"));
                    p.setOwner(rs.getString("owner_firstname") + " " + rs.getString("owner_lastname"));
                    petList.add(p);
                }
                petList = petsByAge;
                rs.close();
                hasResult = cs.getMoreResults();
            }
        } catch (SQLException e) {
            logger.error("DAO Error: ", e);
        } finally {
            try {
                if (cs != null) cs.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("DAO Error!!!: ", e);
            }
        }
        return pets;
    }


}
