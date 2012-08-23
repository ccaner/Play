package baseline;

import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.util.AntPathMatcher;
import play.baseline.BaselineService;
import play.baseline.Main;
import play.baseline.model.Pet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;

/**
 * Run server. (with database)
 * Make a call & check result
 */
public class IntegrationTest {

    private static String PATH_QUERY_BY_NAME = "/pets/list/name={name}&age={age}";
    private static String PATH_COUNT_BY_AGE = "/pets/count/age={age}";

    private static Process server;

    @BeforeClass
    public static void startServer() throws IOException, InterruptedException {
        String classpath = System.getProperty("java.class.path");
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String className = Main.class.getCanonicalName();
        ProcessBuilder pb = new ProcessBuilder(javaBin, "-cp", classpath, className, "jetty");
        pb.inheritIO();
        server = pb.start();
        Thread.sleep(5000);
    }

    @org.junit.Test
    public void testListByNameAndAge() throws InterruptedException, IOException {
        String query = PATH_QUERY_BY_NAME.replace("{name}", "Cango").replace("{age}", "3");
        String response = getResponse(query);

        ObjectMapper mapper = new ObjectMapper();
        List<Pet> pets = mapper.readValue(response, new TypeReference<List<Pet>>() {
        });

        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Cango");
        pet.setAge(3);
        pet.setOwner("Osman Bosman");
        List<Pet> exptected = Lists.newArrayList(pet);

        Assert.assertEquals("Invalid return value", exptected, pets);
    }

    @org.junit.Test
    public void testCountByAge() throws InterruptedException, IOException {
        String query = PATH_COUNT_BY_AGE.replace("{age}", "9");
        String response = getResponse(query);

        Assert.assertEquals("Invalid return value", "2", response);
    }

    @AfterClass
    public static void stopServer() throws IOException {
        server.destroy();
    }

    private String getResponse(String path) throws IOException {
        URL url = new URL("http://localhost:8118" + path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.readLine();
    }

}
