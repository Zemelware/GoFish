import java.io.Serializable;

public class Person implements Serializable {

    private String name;
    private Gender gender;
    private byte age;

    // Getters
    public String getName() {
        return this.name;
    }

    public Gender getGender() {
        return this.gender;
    }

    public byte getAge() {
        return this.age;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setAge(byte age) {
        if (age >= 0) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Age must be equal to or above 0");
        }
    }

    // Constructors
    public Person(String name, Gender gender, byte age) {
        setName(name);
        setGender(gender);
        setAge(age);
    }

    // Other
    public enum Gender {
        MALE("Male"),
        FEMALE("Female");

        public final String string;

        Gender(String string) {
            this.string = string;
        }
    }

}
