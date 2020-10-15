package example.atp.domain;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.GeneratedValue;

@MappedEntity
public class Owner {

    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private int id;
    private String name;
    private int age;

    @Creator
    public Owner(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        id = value;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int value) {
        age = value;
    }
}
