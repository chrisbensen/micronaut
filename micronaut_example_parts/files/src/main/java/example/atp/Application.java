package example.atp;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;

import example.atp.domain.Owner;
import example.atp.repositories.OwnerRepository;
import example.atp.repositories.PetRepository;


@Singleton
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    Application(OwnerRepository ownerRepository, PetRepository petRepository) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }

    @EventListener
    @Transactional
    void init(StartupEvent event) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Populating data");
        }

        petRepository.deleteAll();
        ownerRepository.deleteAll();
        Owner fred = new Owner("Fred");
        fred.setAge(45);
        Owner barney = new Owner("Barney");
        barney.setAge(40);
        ownerRepository.saveAll(Arrays.asList(fred, barney));
    }
}
