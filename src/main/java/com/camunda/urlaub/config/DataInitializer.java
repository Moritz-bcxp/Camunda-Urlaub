package com.camunda.urlaub.config;

import com.camunda.urlaub.model.Mitarbeiter;
import com.camunda.urlaub.repository.MitarbeiterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private MitarbeiterRepository mitarbeiterRepository;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Initializing database with test data...");

        // Prüfen ob bereits Daten vorhanden sind
        if (mitarbeiterRepository.count() == 0) {
            // Testdaten erstellen
            Mitarbeiter maxMustermann = new Mitarbeiter(
                    "Max Mustermann",
                    25,
                    "max.mustermann@company.com",
                    "IT",
                    "123456");

            Mitarbeiter maxiMusterfrau = new Mitarbeiter(
                    "Maxi Musterfrau",
                    30,
                    "maxi.musterfrau@company.com",
                    "HR",
                    "654321");

            Mitarbeiter johnDoe = new Mitarbeiter(
                    "John Doe",
                    20,
                    "john.doe@company.com",
                    "Marketing",
                    "111111");

            Mitarbeiter janeSmith = new Mitarbeiter(
                    "Jane Smith",
                    28,
                    "jane.smith@company.com",
                    "Finance",
                    "222222");

            // Mitarbeiter in Datenbank speichern
            mitarbeiterRepository.save(maxMustermann);
            mitarbeiterRepository.save(maxiMusterfrau);
            mitarbeiterRepository.save(johnDoe);
            mitarbeiterRepository.save(janeSmith);

            LOGGER.info("Created {} employees in database", mitarbeiterRepository.count());
        } else {
            LOGGER.info("Database already contains {} employees", mitarbeiterRepository.count());
        }
    }
}
