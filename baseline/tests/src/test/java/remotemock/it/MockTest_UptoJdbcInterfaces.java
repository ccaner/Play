package remotemock.it;

import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import play.baseline.Main;
import play.baseline.dao.PetDao;
import play.baseline.model.Pet;
import play.baseline.stub.MockDatabase;
import play.baseline.stub.QueryResult;
import play.remotemock.util.RemotableMockFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test everything up to Jdbc interfaces on a running server.
 * This excludes actual SQLs (only possible to test if an actual DB exists)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationConfig.xml")
public class MockTest_UptoJdbcInterfaces {

    @Autowired
    RemotableMockFactory mockFactory;

    private static final String PATH_QUERY_BY_NAME = "/pets/list/name={name}&age={age}";

    private static Process server;

    @BeforeClass
    public static void startServer() throws IOException, InterruptedException {
        String classpath = System.getProperty("java.class.path");
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String className = Main.class.getCanonicalName();
        ProcessBuilder pb = new ProcessBuilder(javaBin, "-cp", classpath, className);
        pb.inheritIO();
        server = pb.start();
        Thread.sleep(5000);
    }


    @Test
    public void testFindPetByNameAndAge() throws Exception {
        MockDatabase mockDatabase = mockFactory.mockAndAttach(MockDatabase.class, "MockDatabase");

        doReturn(new QueryResult() {}).when(mockDatabase).queryPetsTable(eq("Cango"), 33);


        String query = PATH_QUERY_BY_NAME.replace("{name}", "Cango").replace("age", "33");
        String response = getResponse(query);

        ObjectMapper mapper = new ObjectMapper();
        List<Pet> pets = mapper.readValue(response, new TypeReference<List<Pet>>() {});

        Pet p = new Pet();
        p.setId(55);
        p.setName("Cango");
        p.setAge(33);
        p.setOwner("Osman Bosman");
        Assert.assertEquals("Invalid return value", Lists.newArrayList(p), pets);

        verify(mockDatabase, times(1)).queryPetsTable(eq("Cango"), 33);
    }

    private String getResponse(String path) throws IOException {
        URL url = new URL("http://localhost:8118" + path.replace(" ", "%20"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.readLine();
    }

    @AfterClass
    public static void stopServer() throws IOException, InterruptedException {
        server.destroy();
    }

}
