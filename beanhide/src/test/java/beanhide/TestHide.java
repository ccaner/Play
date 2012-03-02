package beanhide;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-hide.xml",
        "classpath:applicationContext-main.xml"})
public class TestHide {

    @Autowired
    ApplicationContext context;

    @Test
    public void replace() {
        Foo foo = (Foo) context.getBean("foo");
        Assert.assertEquals("Bean replace failed", "StubFoo", foo.signature());
    }

}
