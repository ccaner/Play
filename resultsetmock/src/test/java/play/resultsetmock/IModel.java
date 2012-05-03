package play.resultsetmock;

import play.resultsetmock.annotations.Param;
import play.resultsetmock.annotations.Query;

import java.util.List;

public interface IModel {

    List<Pet> loadPets(String name, int age);
}
