package play.remotemock;

import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.System.out;

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
