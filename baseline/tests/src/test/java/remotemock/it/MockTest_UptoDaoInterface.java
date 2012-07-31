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
import play.baseline.BaselineService;
import play.baseline.Main;
import play.baseline.dao.PetDao;
import play.baseline.model.Pet;
import play.remotemock.util.RemotableMockFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test everything up to Dao interface on a running server.
 * This excludes dao impl (possible resultset mappers etc) and actual SQLs (only possible to test if an actual DB exists)
 */
public class MockTest_UptoDaoInterface extends BaseServerTest {

    @Autowired
    RemotableMockFactory mockFactory;

    private static final String PATH_QUERY_BY_OWNER = "/pets/list/ownerFirstName={ownerFirstName}";

    @Test
    public void testFindPetByOwnerName() throws Exception {
        PetDao mockPetDao = mockFactory.mockAndAttach(PetDao.class, "PetDao");
        Pet p = new Pet();
        p.setId(55);
        p.setName("MockPet");
        p.setAge(43);
        p.setOwner("Osman Bosman");
        doReturn(Lists.newArrayList(p)).when(mockPetDao).loadPets(eq("Osman Bosman"));

        String query = PATH_QUERY_BY_OWNER.replace("{ownerFirstName}", "Osman Bosman");
        String response = getResponse(query);

        ObjectMapper mapper = new ObjectMapper();
        List<Pet> pets = mapper.readValue(response, new TypeReference<List<Pet>>() {});

        Assert.assertEquals("Invalid return value", Lists.newArrayList(p), pets);

        verify(mockPetDao, times(1)).loadPets(eq("Osman Bosman"));
    }

    private String getResponse(String path) throws IOException {
        URL url = new URL("http://localhost:8118" + path.replace(" ", "%20"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.readLine();
    }

}
