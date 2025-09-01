package com.camunda.urlaub.controller;

import com.camunda.urlaub.model.UrlaubsAntrag;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/urlaub")
public class UrlaubController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlaubController.class);

    @Autowired
    private ZeebeClient camundaClient;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startUrlaubProcess(@RequestBody UrlaubsAntrag urlaubsAntrag) {
        try {
            LOGGER.info("Starting vacation process for: {}", urlaubsAntrag.getAntragsteller());

            Map<String, Object> variables = new HashMap<>();
            variables.put("urlaub_antragsteller", urlaubsAntrag.getAntragsteller());
            variables.put("urlaub_tageAnzahl", urlaubsAntrag.getTageAnzahl());
            variables.put("urlaub_vonDatum", urlaubsAntrag.getVonDatum());
            variables.put("urlaub_bisDatum", urlaubsAntrag.getBisDatum());
            variables.put("urlaub_grund", urlaubsAntrag.getGrund());

            ProcessInstanceEvent processInstance = camundaClient.newCreateInstanceCommand()
                    .bpmnProcessId("Process_0k5mdnz")
                    .latestVersion()
                    .variables(variables)
                    .send()
                    .join();

            Map<String, Object> response = new HashMap<>();
            response.put("processInstanceKey", processInstance.getProcessInstanceKey());
            response.put("bpmnProcessId", processInstance.getBpmnProcessId());
            response.put("version", processInstance.getVersion());
            response.put("status", "STARTED");

            LOGGER.info("Process instance started with key: {}", processInstance.getProcessInstanceKey());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOGGER.error("Error starting process: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "ERROR");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Urlaub Process Application");
        return ResponseEntity.ok(response);
    }
}
