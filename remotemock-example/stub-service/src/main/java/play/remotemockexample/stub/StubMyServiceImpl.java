package play.remotemockexample.stub;

import play.remotemock.annotation.Remotable;
import play.remotemockexample.MyService;

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
