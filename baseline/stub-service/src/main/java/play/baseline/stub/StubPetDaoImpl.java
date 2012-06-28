package play.baseline.stub;

import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.PathVariable;
import play.baseline.BaselineService;
import play.baseline.dao.PetDao;
import play.baseline.model.Pet;
import play.remotemock.annotation.Remotable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Remotable(PetDao.class)
public class StubPetDaoImpl implements PetDao {

    @Override
    public List<Pet> loadPets(String name, int age) {
        Pet p1 = new Pet();
        p1.setId(10);
        p1.setName(name);
        p1.setAge(age);
        p1.setOwner("Mock Osman");
        return Lists.newArrayList(p1);
    }

    @Override
    public List<Pet> loadPets(String ownerFirstName) {
        Pet p1 = new Pet();
        p1.setId(10);
        p1.setName("MockPet");
        p1.setAge(11);
        p1.setOwner(ownerFirstName + " Bosman");
        return Lists.newArrayList(p1);
    }
}
