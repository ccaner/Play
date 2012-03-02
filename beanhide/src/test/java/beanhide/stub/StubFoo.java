package beanhide.stub;

import beanhide.Foo;

public class StubFoo implements Foo {

    Integer integerDependency;

    public void setIntegerDependency(Integer integerDependency) {
        this.integerDependency = integerDependency;
    }

    @Override
    public String signature() {
        return "StubFoo";
    }

}
