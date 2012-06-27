package remotemock;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.util.AntPathMatcher;
import play.baseline.BaselineService;

import java.util.Map;

public class TestMock {

    @Mock
    BaselineService mockService;

    @Test
    public void replace() {
    }

    
    @Test
    public void tt() {
        String ptr = "/spring-web/name={name}&age={age}";
        AntPathMatcher matcher = new AntPathMatcher();
        Map<String,String> stringStringMap = matcher.extractUriTemplateVariables(ptr, "/spring-web/name=caner&age=32");
        System.out.println("stringStringMap = " + stringStringMap);
    }
}
