package play.baseline;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import play.baseline.model.Pet;

import java.util.List;
import java.util.Map;

public interface BaselineService {

    @RequestMapping("/pets/list/name={name}&age={age}")
    List<Pet> listPets(
            @PathVariable("name") String name,
            @PathVariable("age") int age
    );

    @RequestMapping("/pets/group/name={name}&age={age}")
    Map<String, List<Pet>> listGrouped(
            @PathVariable("name") String name,
            @PathVariable("age") int age
    );

    @RequestMapping("/pets/list/ownerFirstName={ownerFirstName}")
    List<Pet> listPets(
            @PathVariable("ownerFirstName") String ownerFirstName
    );

    @RequestMapping("/pets/count/age={age}")
    int countPets(
            @PathVariable("age") int age
    );

    @RequestMapping("/doSomething")
    String returnSomething();

    @RequestMapping("/doSomethingElse")
    String returnSomethingElse();

}
