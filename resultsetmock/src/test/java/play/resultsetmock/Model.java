package play.resultsetmock;

import play.baseline.model.Pet;
import play.resultsetmock.annotations.Param;
import play.resultsetmock.annotations.Query;

import java.util.List;

public class Model implements IModel {

    IModel model; // so we can verify call

    public Model(IModel model) {
        this.model = model;
    }

    @Override
    @Query("select * from pets")
    public List<Pet> loadPets(
            @Param("name") String name,
            @Param("age") int age) {

        return model.loadPets(name, age);
    }

}
