package play.baseline;

import org.springframework.web.bind.annotation.PathVariable;
import play.baseline.dao.PetDao;
import play.baseline.model.Pet;

import java.util.List;
import java.util.Map;

public class BaselineServiceImpl implements BaselineService {
    
    private PetDao petDao;

    @Override
    public List<Pet> listPets(String name, int age) {
        return petDao.loadPets(name, age);
    }

    @Override
    public Map<String, List<Pet>> listGrouped(String name, int age) {
        return petDao.loadPetsGrouped(name, age);
    }

    @Override
    public List<Pet> listPets(String ownerFirstName) {
        return petDao.loadPets(ownerFirstName);
    }

    @Override
    public int countPets(int age) {
        return petDao.countPetsByAge(age);
    }

    @Override
    public String returnSomething() {
        return "Impl: do something";
    }

    @Override
    public String returnSomethingElse() {
        return "Impl: do something else";
    }

    public void setPetDao(PetDao petDao) {
        this.petDao = petDao;
    }
}
