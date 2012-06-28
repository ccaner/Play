package remotemock;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import play.baseline.dao.PetDao;
import play.baseline.dao.PetDaoImpl;
import play.baseline.model.Pet;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Use actual db.
 */
public class UnitTestPetDao {

    private static PetDao petDao; // class under test

    @BeforeClass
    public static void setup() {
        ApplicationContext context = new ClassPathXmlApplicationContext("dao-context.xml");
        petDao = (PetDao) context.getBean("petDao");
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
