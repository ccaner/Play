package play.baseline.stub.data;

import play.resultsetmock.annotations.RsColumn;

import java.io.Serializable;

public class PetDbRow implements Serializable {

    private int id;

    private String name;

    private int age;

    private String ownerFirstName;

    private String ownerLastName;

    public PetDbRow() {
    }

    @RsColumn(name = "ID", index = 1)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @RsColumn(name = "NAME", index = 2)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @RsColumn(name = "AGE", index = 3)
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @RsColumn(name = "OWNER_FIRSTNAME", index = 4)
    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    @RsColumn(name = "OWNER_LASTNAME", index = 5)
    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }
}
