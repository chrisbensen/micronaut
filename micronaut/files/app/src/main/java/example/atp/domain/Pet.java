package example.atp.domain;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.GeneratedValue;

import javax.annotation.Nullable;
import java.util.UUID;

@MappedEntity
public class Pet {

    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private int id;
    private String name;
    @Relation(Relation.Kind.MANY_TO_ONE)
    private Owner owner;
    private PetType type = PetType.DOG;

    @Creator
    public Pet(String name, @Nullable Owner owner) {
        this.name = name;
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public enum PetType {
        DOG,
        CAT
    }
}
