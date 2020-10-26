package example.atp.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

import example.atp.domain.NameDTO;
import example.atp.domain.Pet;


@JdbcRepository(dialect = Dialect.ORACLE)
public interface PetRepository extends PageableRepository<Pet, UUID> {

    List<NameDTO> list();

    @Join("owner")
    Optional<Pet> findByName(String name);
}
