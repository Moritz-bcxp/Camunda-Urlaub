package com.camunda.urlaub.worker;

import com.camunda.urlaub.service.MitarbeiterService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MitarbeiterDatenWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MitarbeiterDatenWorker.class);

    @Autowired
    private MitarbeiterService mitarbeiterService;

    @JobWorker(type = "get-mitarbeiter-daten")
    public void getMitarbeiterDaten(final JobClient client, final ActivatedJob job) {
        LOGGER.info("Processing job: {}", job.getKey());

        try {
            // Get variables from the process
            Map<String, Object> variables = job.getVariablesAsMap();
            String antragsteller = (String) variables.get("urlaub_antragsteller");
            Integer beantrageUrlaubstage = (Integer) variables.get("urlaub_tageAnzahl");

            LOGGER.info("Getting data for employee: {} requesting {} days", antragsteller, beantrageUrlaubstage);

            // Get complete employee data from database
            var mitarbeiterOpt = mitarbeiterService.getMitarbeiterDaten(antragsteller);

            if (mitarbeiterOpt.isEmpty()) {
                // Throw business error if employee not found
                LOGGER.warn("Employee not found in database: {}", antragsteller);
                client.newThrowErrorCommand(job.getKey())
                        .errorCode("MITARBEITER_NOT_FOUND")
                        .errorMessage("Employee data not found for: " + antragsteller)
                        .send()
                        .join();
                return;
            }

            var mitarbeiter = mitarbeiterOpt.get();
            Integer verfuegbareUrlaubstage = mitarbeiter.getVerfuegbareUrlaubstage();

            // Check if enough vacation days are available
            boolean genugUrlaubstage = mitarbeiterService.hatGenugUrlaubstage(antragsteller, beantrageUrlaubstage);

            // Complete the job with the result including additional employee data
            client.newCompleteCommand(job.getKey())
                    .variables(Map.of(
                            "verfuegbareUrlaubstage", verfuegbareUrlaubstage,
                            "genugUrlaubstage", genugUrlaubstage,
                            "mitarbeiterEmail", mitarbeiter.getEmail() != null ? mitarbeiter.getEmail() : "",
                            "mitarbeiterAbteilung",
                            mitarbeiter.getAbteilung() != null ? mitarbeiter.getAbteilung() : "",
                            "mitarbeiterPersonalnummer",
                            mitarbeiter.getPersonalnummer() != null ? mitarbeiter.getPersonalnummer() : ""))
                    .send()
                    .join();

            LOGGER.info("Job completed successfully. Employee: {}, Available days: {}, Enough days: {}",
                    antragsteller, verfuegbareUrlaubstage, genugUrlaubstage);

        } catch (Exception e) {
            LOGGER.error("Error processing job: {}", e.getMessage(), e);
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Failed to get employee data: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
