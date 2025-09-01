package com.camunda.urlaub.controller;

import com.camunda.urlaub.model.Mitarbeiter;
import com.camunda.urlaub.service.MitarbeiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/mitarbeiter")
public class MitarbeiterController {

    @Autowired
    private MitarbeiterService mitarbeiterService;

    @GetMapping
    public List<Mitarbeiter> getAllMitarbeiter() {
        return mitarbeiterService.getAllMitarbeiter();
    }

    @GetMapping("/{personalnummer}")
    public ResponseEntity<Mitarbeiter> getMitarbeiterByPersonalnummer(@PathVariable String personalnummer) {
        return mitarbeiterService.getMitarbeiterDatenByPersonalnummer(personalnummer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{personalnummer}/urlaub")
    public ResponseEntity<String> beantragteUrlaub(
            @PathVariable String personalnummer,
            @RequestParam Integer tage) {

        try {
            if (mitarbeiterService.hatGenugUrlaubstageByPersonalnummer(personalnummer, tage)) {
                mitarbeiterService.reduziereUrlaubstageByPersonalnummer(personalnummer, tage);

                // Hole den Namen für die Antwort
                var mitarbeiter = mitarbeiterService.findByPersonalnummer(personalnummer);
                String name = mitarbeiter != null ? mitarbeiter.getName() : personalnummer;

                return ResponseEntity.ok("Urlaub wurde beantragt und genehmigt für " + name + " (ID: " + personalnummer
                        + ", " + tage + " Tage)");
            } else {
                // Hole den Namen für die Fehlermeldung
                var mitarbeiter = mitarbeiterService.findByPersonalnummer(personalnummer);
                String name = mitarbeiter != null ? mitarbeiter.getName() : personalnummer;

                return ResponseEntity.badRequest()
                        .body("Nicht genügend Urlaubstage verfügbar für " + name + " (ID: " + personalnummer + ")");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Fehler: " + e.getMessage());
        }
    }

    @GetMapping("/{personalnummer}/urlaubstage")
    public ResponseEntity<Integer> getVerfuegbareUrlaubstage(@PathVariable String personalnummer) {
        try {
            Integer tage = mitarbeiterService.getVerbleibendeUrlaubstageByPersonalnummer(personalnummer);
            if (tage != null) {
                return ResponseEntity.ok(tage);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
