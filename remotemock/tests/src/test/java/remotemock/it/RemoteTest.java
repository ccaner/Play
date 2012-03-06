package remotemock.it;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import play.remotemock.MyService;
import play.remotemock.mock.Remotable;
import static remotemock.it.util.RemoteTestUtil.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationConfig.xml")
public class RemoteTest {

    @Autowired
    MyService myService;

    @Test
    public void testStubCall() throws Exception {
        String response = getResponse("/doSomething");
        Assert.assertEquals("Stub message expected", "Stub: do something", response);
    }

    @Test
    public void testMock() throws Exception {
        switchRemoteModeOn(myService);

        when(myService.returnSomething()).thenReturn("Mocked response");

        String response = getResponse("/doSomething");
        Assert.assertEquals("Mocked message expected", "Mocked response", response);

        switchRemoteModeOff(myService);
    }

    private String getResponse(String path) throws IOException {
        URL url = new URL("http://localhost:8118/MyService" + path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.readLine();
    }


}
