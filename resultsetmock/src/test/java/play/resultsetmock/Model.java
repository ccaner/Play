package play.resultsetmock;

import com.google.common.collect.Lists;
import play.resultsetmock.annotations.Param;
import play.resultsetmock.annotations.Query;

import java.util.List;

public class Model {

    @Query("select * from pets")
    public List<Pet> loadPets(
            @Param("name") String name,
            @Param("age") int age) {

        return Lists.<Pet>newArrayList(
                new Pet("pet", 1, "Caner"),
                new Pet("pet2", 2, "Caner2")
        );

    }

}
