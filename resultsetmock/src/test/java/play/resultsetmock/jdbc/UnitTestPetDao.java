package play.resultsetmock.jdbc;

import org.junit.*;
import play.baseline.dao.PetDao;
import play.baseline.dao.PetDaoImpl;
import play.baseline.model.Pet;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Assume db contains;
 * 
 * <table>
 *     <tr><th>name</th><th>age</th><th>owner_firstname</th><th>owner_lastname</th></tr>
 *     <tr><td>Cango</td><td>3</td><td>Osman</td><td>Bosman</td></tr>
 *     <tr><td>Koko</td><td>8</td><td>Hakan</td><td>Tarkan</td></tr>
 *     <tr><td>Toto</td><td>9</td><td>Osman</td><td>Bosman</td></tr>
 * </table>
 *
 * The aim is to make this test pass using different DataSources
 */
public class UnitTestPetDao {

    DataSource dataSource; // injected

    private PetDao petDao; // class under test
    
    public void init() {
        petDao = new PetDaoImpl(dataSource);
    }
    
    @org.junit.Test
    public void testFilterByNameAndAge() {
        List<Pet> pets = petDao.loadPets("Cango", 3);
        
        Assert.assertTrue("1 pet expected", 1 == pets.size());
        Pet pet = pets.get(0);
        Assert.assertEquals(pet.getName(), "Cango");
        Assert.assertEquals(pet.getAge(), 3);
        Assert.assertEquals(pet.getOwner(), "Osman Bosman");
    }

    @org.junit.Test
    public void testFilterByOwnersFirstName() {
        List<Pet> pets = petDao.loadPets("Osman");
        
        Assert.assertTrue("2 pets expected", 2 == pets.size());
        Collections.sort(pets, new Comparator<Pet>() {
            @Override
            public int compare(Pet o1, Pet o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Pet pet = pets.get(0);
        Assert.assertEquals(pet.getName(), "Cango");
        Assert.assertEquals(pet.getAge(), 3);
        Assert.assertEquals(pet.getOwner(), "Osman Bosman");

        pet = pets.get(1);
        Assert.assertEquals(pet.getName(), "Toto");
        Assert.assertEquals(pet.getAge(), 9);
        Assert.assertEquals(pet.getOwner(), "Osman Bosman");
    }
    
}
