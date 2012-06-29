package play.baseline.dao;

import play.baseline.model.Pet;

import java.util.List;
import java.util.Map;

public interface PetDao {

    List<Pet> loadPets(String name, int age);

    List<Pet> loadPets(String ownerFirstName);
    
    /* Stupid operation to test stored proc */
    Map<String, List<Pet>> loadPetsGrouped(String name, int age);


}
