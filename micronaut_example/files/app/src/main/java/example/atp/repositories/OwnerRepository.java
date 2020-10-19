package example.atp.repositories;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import example.atp.domain.Owner;


@JdbcRepository(dialect = Dialect.ORACLE)
public interface OwnerRepository extends CrudRepository<Owner, Long> {

    @Override
    List<Owner> findAll();

    Optional<Owner> findByName(String name);
}
