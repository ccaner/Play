package play.remotemockexample.stub;

import play.remotemockexample.MyService;
import play.remotemockexample.annotation.Remotable;

@Remotable(MyService.class)
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
