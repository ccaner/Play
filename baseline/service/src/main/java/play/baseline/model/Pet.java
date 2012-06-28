package play.baseline.model;

import java.io.Serializable;

public class Pet implements Serializable {
    
    private int id;
    
    private String name;
    
    private int age;

    private String owner;

    public Pet() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pet pet = (Pet) o;

        if (age != pet.age) return false;
        if (id != pet.id) return false;
        if (name != null ? !name.equals(pet.name) : pet.name != null) return false;
        if (owner != null ? !owner.equals(pet.owner) : pet.owner != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }
}
