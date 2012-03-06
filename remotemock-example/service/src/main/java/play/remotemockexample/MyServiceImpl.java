package play.remotemockexample;

public class MyServiceImpl implements MyService {

    @Override
    public String returnSomething() {
        return "Impl: do something";
    }

    @Override
    public String returnSomethingElse() {
        return "Impl: do something else";
    }
}
