/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 4/27/12
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pet {
    
    String name;
    
    Integer age;

    String owner;

    public Pet(String name, Integer age, String owner) {
        this.name = name;
        this.age = age;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
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
}
