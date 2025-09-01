package com.camunda.urlaub.service;

import com.camunda.urlaub.model.Mitarbeiter;
import com.camunda.urlaub.repository.MitarbeiterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MitarbeiterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MitarbeiterService.class);

    @Autowired
    private MitarbeiterRepository mitarbeiterRepository;

    /**
     * Holt die verfügbaren Urlaubstage für einen Mitarbeiter aus der Datenbank
     */
    public Integer getVerfuegbareUrlaubstage(String mitarbeiterName) {
        LOGGER.debug("Getting vacation days for employee: {}", mitarbeiterName);

        Optional<Mitarbeiter> mitarbeiter = mitarbeiterRepository.findByName(mitarbeiterName);
        if (mitarbeiter.isPresent()) {
            Integer urlaubstage = mitarbeiter.get().getVerfuegbareUrlaubstage();
            LOGGER.debug("Found {} vacation days for employee {}", urlaubstage, mitarbeiterName);
            return urlaubstage;
        } else {
            LOGGER.warn("Employee not found: {}", mitarbeiterName);
            return null; // Mitarbeiter nicht gefunden
        }
    }

    /**
     * Prüft ob ein Mitarbeiter genügend Urlaubstage hat
     */
    public boolean hatGenugUrlaubstage(String mitarbeiterName, Integer beantrageUrlaubstage) {
        LOGGER.debug("Checking if employee {} has enough vacation days for {} days", mitarbeiterName,
                beantrageUrlaubstage);

        Optional<Mitarbeiter> mitarbeiter = mitarbeiterRepository.findMitarbeiterMitGenugUrlaubstagen(mitarbeiterName,
                beantrageUrlaubstage);
        boolean hatGenug = mitarbeiter.isPresent();

        LOGGER.debug("Employee {} has enough vacation days: {}", mitarbeiterName, hatGenug);
        return hatGenug;
    }

    /**
     * Reduziert die verfügbaren Urlaubstage eines Mitarbeiters
     */
    public void reduziereUrlaubstage(String mitarbeiterName, Integer tage) {
        LOGGER.debug("Reducing vacation days for employee {} by {} days", mitarbeiterName, tage);

        Optional<Mitarbeiter> mitarbeiterOpt = mitarbeiterRepository.findByName(mitarbeiterName);
        if (mitarbeiterOpt.isPresent()) {
            Mitarbeiter mitarbeiter = mitarbeiterOpt.get();
            Integer aktuelleUrlaubstage = mitarbeiter.getVerfuegbareUrlaubstage();

            if (aktuelleUrlaubstage >= tage) {
                mitarbeiter.setVerfuegbareUrlaubstage(aktuelleUrlaubstage - tage);
                mitarbeiterRepository.save(mitarbeiter);
                LOGGER.info("Reduced vacation days for employee {} from {} to {}",
                        mitarbeiterName, aktuelleUrlaubstage, mitarbeiter.getVerfuegbareUrlaubstage());
            } else {
                LOGGER.warn("Cannot reduce vacation days for employee {} - not enough days available", mitarbeiterName);
                throw new IllegalStateException("Nicht genügend Urlaubstage verfügbar");
            }
        } else {
            LOGGER.error("Cannot reduce vacation days - employee not found: {}", mitarbeiterName);
            throw new IllegalArgumentException("Mitarbeiter nicht gefunden: " + mitarbeiterName);
        }
    }

    /**
     * Holt alle Mitarbeiterdaten für einen Mitarbeiter
     */
    public Optional<Mitarbeiter> getMitarbeiterDaten(String mitarbeiterName) {
        LOGGER.debug("Getting complete employee data for: {}", mitarbeiterName);
        return mitarbeiterRepository.findByName(mitarbeiterName);
    }

    /**
     * Prüft ob ein Mitarbeiter existiert
     */
    public boolean mitarbeiterExistiert(String mitarbeiterName) {
        return mitarbeiterRepository.existsByName(mitarbeiterName);
    }

    /**
     * Holt alle Mitarbeiter aus der Datenbank
     */
    public java.util.List<Mitarbeiter> getAllMitarbeiter() {
        LOGGER.debug("Getting all employees");
        return mitarbeiterRepository.findAll();
    }

    /**
     * Holt die verbleibenden Urlaubstage für einen Mitarbeiter
     */
    public Integer getVerbleibendeUrlaubstage(String mitarbeiterName) {
        LOGGER.debug("Getting remaining vacation days for employee: {}", mitarbeiterName);
        return getVerfuegbareUrlaubstage(mitarbeiterName);
    }

    /**
     * Sucht einen Mitarbeiter nach Namen
     */
    public Mitarbeiter findByName(String name) {
        return mitarbeiterRepository.findByName(name).orElse(null);
    }

    // --- Methoden für Personalnummer ---

    /**
     * Holt die verfügbaren Urlaubstage für einen Mitarbeiter anhand der
     * Personalnummer
     */
    public Integer getVerfuegbareUrlaubstageByPersonalnummer(String personalnummer) {
        LOGGER.debug("Getting vacation days for employee with ID: {}", personalnummer);

        Optional<Mitarbeiter> mitarbeiter = mitarbeiterRepository.findByPersonalnummer(personalnummer);
        if (mitarbeiter.isPresent()) {
            Integer urlaubstage = mitarbeiter.get().getVerfuegbareUrlaubstage();
            LOGGER.debug("Found {} vacation days for employee ID {}", urlaubstage, personalnummer);
            return urlaubstage;
        } else {
            LOGGER.warn("Employee not found with ID: {}", personalnummer);
            return null;
        }
    }

    /**
     * Prüft ob ein Mitarbeiter genügend Urlaubstage hat anhand der Personalnummer
     */
    public boolean hatGenugUrlaubstageByPersonalnummer(String personalnummer, Integer beantrageUrlaubstage) {
        LOGGER.debug("Checking if employee with ID {} has enough vacation days for {} days", personalnummer,
                beantrageUrlaubstage);

        Optional<Mitarbeiter> mitarbeiter = mitarbeiterRepository.findMitarbeiterMitGenugUrlaubstagenByPersonalnummer(
                personalnummer,
                beantrageUrlaubstage);
        boolean hatGenug = mitarbeiter.isPresent();

        LOGGER.debug("Employee with ID {} has enough vacation days: {}", personalnummer, hatGenug);
        return hatGenug;
    }

    /**
     * Reduziert die verfügbaren Urlaubstage eines Mitarbeiters anhand der
     * Personalnummer
     */
    public void reduziereUrlaubstageByPersonalnummer(String personalnummer, Integer tage) {
        LOGGER.debug("Reducing vacation days for employee with ID {} by {} days", personalnummer, tage);

        Optional<Mitarbeiter> mitarbeiterOpt = mitarbeiterRepository.findByPersonalnummer(personalnummer);
        if (mitarbeiterOpt.isPresent()) {
            Mitarbeiter mitarbeiter = mitarbeiterOpt.get();
            Integer aktuelleUrlaubstage = mitarbeiter.getVerfuegbareUrlaubstage();

            if (aktuelleUrlaubstage >= tage) {
                mitarbeiter.setVerfuegbareUrlaubstage(aktuelleUrlaubstage - tage);
                mitarbeiterRepository.save(mitarbeiter);
                LOGGER.info("Reduced vacation days for employee ID {} from {} to {}",
                        personalnummer, aktuelleUrlaubstage, mitarbeiter.getVerfuegbareUrlaubstage());
            } else {
                LOGGER.warn("Cannot reduce vacation days for employee ID {} - not enough days available",
                        personalnummer);
                throw new IllegalStateException("Nicht genügend Urlaubstage verfügbar");
            }
        } else {
            LOGGER.error("Cannot reduce vacation days - employee not found with ID: {}", personalnummer);
            throw new IllegalArgumentException("Mitarbeiter nicht gefunden mit Personalnummer: " + personalnummer);
        }
    }

    /**
     * Holt alle Mitarbeiterdaten für einen Mitarbeiter anhand der Personalnummer
     */
    public Optional<Mitarbeiter> getMitarbeiterDatenByPersonalnummer(String personalnummer) {
        LOGGER.debug("Getting complete employee data for ID: {}", personalnummer);
        return mitarbeiterRepository.findByPersonalnummer(personalnummer);
    }

    /**
     * Prüft ob ein Mitarbeiter existiert anhand der Personalnummer
     */
    public boolean mitarbeiterExistiertByPersonalnummer(String personalnummer) {
        return mitarbeiterRepository.existsByPersonalnummer(personalnummer);
    }

    /**
     * Holt die verbleibenden Urlaubstage für einen Mitarbeiter anhand der
     * Personalnummer
     */
    public Integer getVerbleibendeUrlaubstageByPersonalnummer(String personalnummer) {
        LOGGER.debug("Getting remaining vacation days for employee ID: {}", personalnummer);
        return getVerfuegbareUrlaubstageByPersonalnummer(personalnummer);
    }

    /**
     * Sucht einen Mitarbeiter nach Personalnummer
     */
    public Mitarbeiter findByPersonalnummer(String personalnummer) {
        return mitarbeiterRepository.findByPersonalnummer(personalnummer).orElse(null);
    }
}
