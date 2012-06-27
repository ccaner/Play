package play.baseline.dao;

import play.baseline.model.Pet;

import java.util.List;

public interface PetDao {

    List<Pet> loadPets(String name, int age);

    List<Pet> loadPets(String ownerFirstName);

}
