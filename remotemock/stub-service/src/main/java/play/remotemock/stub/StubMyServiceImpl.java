package play.remotemock.stub;

import org.springframework.web.bind.annotation.RequestMapping;
import play.remotemock.MyService;

import static java.lang.System.out;

public class StubMyServiceImpl implements MyService {

    @Override
    public String returnSomething() {
        return "Stub: do something";
    }

    @Override
    public String returnSomethingElse() {
        return "Stub: do something else";
    }

}
