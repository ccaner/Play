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
import play.baseline.model.Pet;
import play.baseline.stub.MockDatabase;
import play.baseline.stub.data.PetDbRow;
import play.remotemock.util.RemotableMockFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test everything up to Jdbc interfaces on a running server.
 * This excludes actual SQLs (only possible to test if an actual DB exists)
 */
public class MockTest_PreparedStatement extends BaseServerTest {

    @Autowired
    RemotableMockFactory mockFactory;

    private static final String PATH_QUERY_BY_NAME_AGE = "/pets/list/name={name}&age={age}";

    @Test
    public void testFindPetByNameAndAge() throws Exception {
        MockDatabase mockDatabase = mockFactory.mockAndAttach(MockDatabase.class, "MockDatabase");

        PetDbRow dbPet = new PetDbRow();
        dbPet.setId(19);
        dbPet.setAge(2);
        dbPet.setName("Ponpon");
        dbPet.setOwnerFirstName("Kenan");
        dbPet.setOwnerLastName("Kantar");
        doReturn(Lists.newArrayList(dbPet)).when(mockDatabase).queryPetsTable(eq("Ponpon"), eq(2));

        String query = PATH_QUERY_BY_NAME_AGE.replace("{name}", "Ponpon").replace("{age}", "2");
        String response = getResponse(query);

        ObjectMapper mapper = new ObjectMapper();
        List<Pet> pets = mapper.readValue(response, new TypeReference<List<Pet>>() {});

        Pet p = new Pet();
        p.setId(19);
        p.setName("Ponpon");
        p.setAge(2);
        p.setOwner("Kenan Kantar");
        Assert.assertEquals("Invalid return value", Lists.newArrayList(p), pets);

        verify(mockDatabase, times(1)).queryPetsTable(eq("Ponpon"), eq(2));
    }

    private String getResponse(String path) throws IOException {
        URL url = new URL("http://localhost:8118" + path.replace(" ", "%20"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.readLine();
    }

}
