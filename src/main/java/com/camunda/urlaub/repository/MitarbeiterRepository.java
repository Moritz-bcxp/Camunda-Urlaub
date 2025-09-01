package com.camunda.urlaub.repository;

import com.camunda.urlaub.model.Mitarbeiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MitarbeiterRepository extends JpaRepository<Mitarbeiter, Long> {

    /**
     * Findet einen Mitarbeiter anhand des Namens
     */
    Optional<Mitarbeiter> findByName(String name);

    /**
     * Findet einen Mitarbeiter anhand der Personalnummer
     */
    Optional<Mitarbeiter> findByPersonalnummer(String personalnummer);

    /**
     * Prüft ob ein Mitarbeiter existiert anhand des Namens
     */
    boolean existsByName(String name);

    /**
     * Prüft ob ein Mitarbeiter existiert anhand der Personalnummer
     */
    boolean existsByPersonalnummer(String personalnummer);

    /**
     * Custom Query um Mitarbeiter mit genügend Urlaubstagen zu finden (nach Name)
     */
    @Query("SELECT m FROM Mitarbeiter m WHERE m.name = :name AND m.verfuegbareUrlaubstage >= :benoetigteUrlaubstage")
    Optional<Mitarbeiter> findMitarbeiterMitGenugUrlaubstagen(@Param("name") String name,
            @Param("benoetigteUrlaubstage") Integer benoetigteUrlaubstage);

    /**
     * Custom Query um Mitarbeiter mit genügend Urlaubstagen zu finden (nach
     * Personalnummer)
     */
    @Query("SELECT m FROM Mitarbeiter m WHERE m.personalnummer = :personalnummer AND m.verfuegbareUrlaubstage >= :benoetigteUrlaubstage")
    Optional<Mitarbeiter> findMitarbeiterMitGenugUrlaubstagenByPersonalnummer(
            @Param("personalnummer") String personalnummer,
            @Param("benoetigteUrlaubstage") Integer benoetigteUrlaubstage);
}
