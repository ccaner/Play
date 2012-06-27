package play.baseline;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import play.baseline.model.Pet;

import java.util.List;

public interface BaselineService {

    @RequestMapping("/pets/list/name={name}&age={age}")
    List<Pet> listPets(
            @PathVariable("name") String name,
            @PathVariable("age") int age
    );

    @RequestMapping("/pets/list/ownerFirstName={ownerFirstName}")
    List<Pet> listPets(
            @PathVariable("ownerFirstName") String ownerFirstName
    );

    @RequestMapping("/doSomething")
    String returnSomething();

    @RequestMapping("/doSomethingElse")
    String returnSomethingElse();

}
