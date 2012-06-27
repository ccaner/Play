package play.baseline;

import play.baseline.dao.PetDao;
import play.baseline.model.Pet;

import java.util.List;

public class BaselineServiceImpl implements BaselineService {
    
    private PetDao petDao;

    @Override
    public List<Pet> listPets(String name, int age) {
        return petDao.loadPets(name, age);
    }

    @Override
    public List<Pet> listPets(String ownerFirstName) {
        return petDao.loadPets(ownerFirstName);
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
