package remotemock.data;

import play.baseline.stub.data.PetDbRow;

import java.util.ArrayList;
import java.util.List;

public class Pets {

    public static List<PetDbRow> createTestData() {
        List<PetDbRow> pets = new ArrayList<PetDbRow>();

        PetDbRow row = new PetDbRow();
        row.setId(1);
        row.setName("Cango");
        row.setAge(3);
        row.setOwnerFirstName("Osman");
        row.setOwnerLastName("Bosman");
        pets.add(row);
        
        row = new PetDbRow();
        row.setId(2);
        row.setName("Koko");
        row.setAge(3);
        row.setOwnerFirstName("Hakan");
        row.setOwnerLastName("Tarkan");
        pets.add(row);

        row = new PetDbRow();
        row.setId(3);
        row.setName("Popo");
        row.setAge(3);
        row.setOwnerFirstName("Osman");
        row.setOwnerLastName("Bosman");
        pets.add(row);

        return pets;
    }

}
