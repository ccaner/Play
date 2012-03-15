package play.remotemock;

import org.springframework.web.bind.annotation.RequestMapping;

public interface MyService {

    @RequestMapping("/doSomething")
    String returnSomething();

    @RequestMapping("/doSomethingElse")
    String returnSomethingElse();

}
