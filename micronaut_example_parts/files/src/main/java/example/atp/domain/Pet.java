package example.atp.domain;

import javax.annotation.Nullable;
import java.util.UUID;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.GeneratedValue;


@MappedEntity
public class Pet {

    public enum PetType {DOG, CAT}

    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    private Long id;
    private String name;
    @Relation(Relation.Kind.MANY_TO_ONE)
    private Owner owner;
    private PetType type = PetType.DOG;

    @Creator
    public Pet(String name, @Nullable Owner owner) {
        this.name = name;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        id = value;
    }

    public String getName() {
        return name;
    }

    public Owner getOwner() {
        return owner;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType value) {
        type = value;
    }
}
