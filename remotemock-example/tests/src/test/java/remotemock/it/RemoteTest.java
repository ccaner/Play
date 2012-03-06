package remotemock.it;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import play.remotemock.MyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static org.mockito.Mockito.*;
import static remotemock.it.util.RemoteTestUtil.switchRemoteModeOff;
import static remotemock.it.util.RemoteTestUtil.switchRemoteModeOn;

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
        doReturn("Mocked response").when(myService).returnSomething();

        String response = getResponse("/doSomething");
        Assert.assertEquals("Mocked message expected", "Mocked response", response);

        verify(myService, times(1)).returnSomething();

        response = getResponse("/doSomethingElse");
        Assert.assertEquals("Stub message expected", "Stub: do something else", response);

        verify(myService, times(1)).returnSomethingElse();
    }

    private String getResponse(String path) throws IOException {
        URL url = new URL("http://localhost:8118/MyService" + path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.readLine();
    }


}
