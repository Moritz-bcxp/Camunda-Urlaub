package com.camunda.urlaub.worker;

import com.camunda.urlaub.service.MitarbeiterService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UrlaubGenehmigungWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlaubGenehmigungWorker.class);

    @Autowired
    private MitarbeiterService mitarbeiterService;

    @Autowired(required = false)
    private ZeebeClient camundaClient;

    @EventListener(ApplicationReadyEvent.class)
    public void startWorkers() {
        if (camundaClient != null) {
            LOGGER.info("🚀 Starting Urlaub Genehmigung Workers...");

            // Worker for vacation approval
            camundaClient.newWorker()
                    .jobType("urlaub-genehmigen")
                    .handler(this::urlaubGenehmigen)
                    .open();

            // Worker for vacation rejection
            camundaClient.newWorker()
                    .jobType("urlaub-ablehnen")
                    .handler(this::urlaubAblehnen)
                    .open();

            LOGGER.info("✅ Urlaub Genehmigung Workers started successfully!");
        } else {
            LOGGER.warn("⚠️  Camunda client not available - Urlaub Genehmigung Workers not started");
        }
    }

    public void urlaubGenehmigen(final JobClient client, final ActivatedJob job) {
        LOGGER.info("Processing vacation approval job: {}", job.getKey());

        try {
            Map<String, Object> variables = job.getVariablesAsMap();
            String antragsteller = (String) variables.get("urlaub_antragsteller");
            Integer tageAnzahl = (Integer) variables.get("urlaub_tageAnzahl");

            LOGGER.info("Approving vacation for {} with {} days", antragsteller, tageAnzahl);

            // Reduce available vacation days
            mitarbeiterService.reduziereUrlaubstage(antragsteller, tageAnzahl);

            // Complete the job with approval status
            client.newCompleteCommand(job.getKey())
                    .variables(Map.of(
                            "approved", true,
                            "status", "GENEHMIGT",
                            "message", "Urlaubsantrag wurde genehmigt"))
                    .send()
                    .join();

            LOGGER.info("Vacation approved successfully for {}", antragsteller);

        } catch (Exception e) {
            LOGGER.error("Error approving vacation: {}", e.getMessage(), e);
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Failed to approve vacation: " + e.getMessage())
                    .send()
                    .join();
        }
    }

    public void urlaubAblehnen(final JobClient client, final ActivatedJob job) {
        LOGGER.info("Processing vacation rejection job: {}", job.getKey());

        try {
            Map<String, Object> variables = job.getVariablesAsMap();
            String antragsteller = (String) variables.get("urlaub_antragsteller");

            LOGGER.info("Rejecting vacation for {}", antragsteller);

            // Complete the job with rejection status
            client.newCompleteCommand(job.getKey())
                    .variables(Map.of(
                            "approved", false,
                            "status", "ABGELEHNT",
                            "message", "Urlaubsantrag wurde abgelehnt: Nicht genügend Urlaubstage verfügbar"))
                    .send()
                    .join();

            LOGGER.info("Vacation rejected for {}", antragsteller);

        } catch (Exception e) {
            LOGGER.error("Error rejecting vacation: {}", e.getMessage(), e);
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Failed to reject vacation: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
