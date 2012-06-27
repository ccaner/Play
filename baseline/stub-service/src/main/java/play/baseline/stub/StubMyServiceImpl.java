package play.baseline.stub;

import org.springframework.web.bind.annotation.PathVariable;
import play.baseline.BaselineService;
import play.baseline.model.Pet;
import play.remotemock.annotation.Remotable;

import java.util.ArrayList;
import java.util.List;

@Remotable(BaselineService.class)
public class StubMyServiceImpl implements BaselineService {

    @Override
    public List<Pet> listPets(String name, int age) {
        return new ArrayList<Pet>();
    }

    @Override
    public List<Pet> listPets(String ownerFirstName) {
        return new ArrayList<Pet>();
    }

    @Override
    public String returnSomething() {
        return "Stub: do something";
    }

    @Override
    public String returnSomethingElse() {
        return "Stub: do something else";
    }

}
