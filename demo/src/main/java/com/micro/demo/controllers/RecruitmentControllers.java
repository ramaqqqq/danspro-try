package com.micro.demo.controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.micro.demo.helpers.Helpers;
import com.micro.demo.models.entity.PositionEntity;

@RestController
@RequestMapping("/job")
public class RecruitmentControllers {

    private String removesTag(String html) {
        String cleanedText = html.replaceAll("<[^>]+>", "");
        return cleanedText;
    }

    @GetMapping
    public ResponseEntity<?> getJobs() {
        try {
            String apiUrl = "http://dev3.dansmultipro.co.id/api/recruitment/positions.json";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<PositionEntity[]> responseEntity = restTemplate.getForEntity(apiUrl, PositionEntity[].class);
            PositionEntity[] positionEntities = responseEntity.getBody();
            return ResponseEntity.ok(Arrays.asList(positionEntities));
        } catch (Exception e) {
            Helpers.log("error", "In Server: " + e.getMessage());
            Helpers.MsgErr(404, "Bad request", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobsDetail(@PathVariable String id) {
        String apiUrl = "http://dev3.dansmultipro.co.id/api/recruitment/positions/{id}";
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<PositionEntity[]> responseEntity = restTemplate.getForEntity(apiUrl, PositionEntity[].class, id);
            PositionEntity[] positionEntities = responseEntity.getBody();
            return ResponseEntity.ok(Arrays.asList(positionEntities));
        } catch (HttpClientErrorException.NotFound notFoundEx) {
            Helpers.log("error", "In Server: Position not found in external API");
            Map<String, Object> resp = Helpers.MsgErr(404, "In Server: Position not found in external API", notFoundEx.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        } catch (Exception e) {
            Helpers.log("error", "In Server: " + e.getMessage());
            Map<String, Object> resp = Helpers.MsgErr(500, "In Server: Internal Server Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    @GetMapping("/csv")
    public ResponseEntity<String> downloadJobsAsCsv() {
        try {
            String apiUrl = "http://dev3.dansmultipro.co.id/api/recruitment/positions.json";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<PositionEntity>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PositionEntity>>() {
                    });
            List<PositionEntity> positionEntities = response.getBody();
            if (positionEntities != null && !positionEntities.isEmpty()) {
                String csvFilePath = "jobs.csv";
                try (FileWriter csvWriter = new FileWriter(csvFilePath)) {
                    csvWriter.append(
                            "id,type,url,created_at,company,company_url,location,title,description,how_to_apply,company_logo\n");
                    for (PositionEntity entity : positionEntities) {
                        String cleanedDescription = removesTag(entity.getDescription());
                        String csvLine = String.join(",",
                                entity.getId(), entity.getType(), entity.getUrl(), entity.getCreated_at(),
                                entity.getCompany(), entity.getCompany_url(), entity.getLocation(),
                                entity.getTitle(), cleanedDescription, entity.getHow_to_apply(),
                                entity.getCompany_logo());

                        csvWriter.append(csvLine);
                        csvWriter.append("\n");
                    }
                }
                return ResponseEntity.ok("Data downloaded and saved as " + csvFilePath);
            } else {
                return ResponseEntity.ok("No data available from the API.");
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = "HTTP Client Error: " + e.getRawStatusCode() + " - " + e.getStatusText();
            Helpers.log("error", "In Server: " + errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        } catch (IOException e) {
            String errorMessage = "An error occurred: " + e.getMessage();
            Helpers.log("error", "In Server: " + errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}


