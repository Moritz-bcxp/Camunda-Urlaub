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
}
