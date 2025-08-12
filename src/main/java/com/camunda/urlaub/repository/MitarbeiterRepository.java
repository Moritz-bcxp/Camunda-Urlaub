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
     * Custom Query um Mitarbeiter mit genügend Urlaubstagen zu finden
     */
    @Query("SELECT m FROM Mitarbeiter m WHERE m.name = :name AND m.verfuegbareUrlaubstage >= :benoetigteUrlaubstage")
    Optional<Mitarbeiter> findMitarbeiterMitGenugUrlaubstagen(@Param("name") String name,
            @Param("benoetigteUrlaubstage") Integer benoetigteUrlaubstage);
}
