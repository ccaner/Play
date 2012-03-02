package beanhide.bean;

import beanhide.Foo;

public class FooImpl implements Foo {

    String stringDependency;

    public void setStringDependency(String stringDependency) {
        this.stringDependency = stringDependency;
    }

    @Override
    public String signature() {
        return "FooImpl";
    }

}
