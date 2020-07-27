package com.azure.face.demo.service;

import com.azure.face.demo.model.Azure.APIResponse;
import com.azure.face.demo.model.Azure.Candidate;
import com.azure.face.demo.model.Persons;
import com.azure.face.demo.model.repository.PersonsRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Service
public class AzureService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PersonsRepository personRepository;
    @Value("${azure.key}")
    private String key;
    @Value("${app.upload.dir}")
    private String uploadDir;
    @Value("${azure.api}")
    private String apiUrl;
    @Value("${azure.face.group}")
    String azureGroup;

    public String addPerson(String name) {

        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("name", name);
        HttpEntity<?> request = new HttpEntity<Object>(personJsonObject.toString());
        try {
            ResponseEntity<APIResponse> ar = restTemplate.postForEntity(apiUrl + "/face/v1.0/persongroups/"+ azureGroup +"/persons",
                    request, APIResponse.class);
            if (ar.getStatusCodeValue() == 200) {
                return ar.getBody().getPersonId();
            } else {
                log.error(ar.getBody().getError().getMessage());
            }
        } catch (Exception e) {
            log.error("error " + e.toString());
        }
        return null;
    }


    public void delPerson(String personAzureId) {

        try {
            HttpEntity<?> request = new HttpEntity<Object>(new HttpHeaders());
            ResponseEntity<APIResponse> ar = restTemplate.exchange(apiUrl + "/face/v1.0/persongroups/" + azureGroup +
                            "/persons/" + personAzureId,
                    HttpMethod.DELETE, request, APIResponse.class);


            if (ar.getStatusCodeValue() != 200) {
                log.error(ar.getBody().getError().getMessage());
            }
        } catch (Exception e) {
            log.error("error " + e.toString());
        }

    }


    public void addFace(Persons p) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            HttpEntity<byte[]> request = new HttpEntity<>(Files.readAllBytes(new File(uploadDir + File.separator +
                    p.getImageName()).toPath()), headers);
            ResponseEntity<APIResponse> ar = restTemplate.postForEntity(apiUrl +
                            "/face/v1.0/persongroups/" + azureGroup  + "/persons/" + p.getPersonAzureId() +
                            "/persistedFaces?detectionModel=detection_02",
                    request, APIResponse.class);


            if (ar.getStatusCodeValue() == 200) {

                log.info("added face to Azure DB " + ar.getBody().getFaceId());
                p.setAzureId(ar.getBody().getFaceId());
                personRepository.save(p);

            } else {
                log.error(ar.getBody().getError().getMessage());
            }
        } catch (IOException e) {
            log.error("error " + e.toString());
        }


    }


    public void train() {

        try {

            HttpEntity<?> request = new HttpEntity<Object>(new HttpHeaders());
            ResponseEntity<APIResponse> ar = restTemplate.postForEntity(apiUrl + "/face/v1.0/persongroups/" + azureGroup
                            + "/train/",
                    request, APIResponse.class);

            if (ar.getStatusCodeValue() != 200) {
                log.error(ar.getBody().getError().getMessage());
            }

        } catch (Exception e) {
            log.error("error " + e.toString());
        }

    }


    public Boolean isTrainingInProgress() {

        try {

            HttpEntity<?> request = new HttpEntity<Object>(new HttpHeaders());
            ResponseEntity<APIResponse> ar = restTemplate.exchange(apiUrl + "/face/v1.0/persongroups/" + azureGroup
                            + "/training/",
                    HttpMethod.GET, request, APIResponse.class);

            if (ar.getStatusCodeValue() != 200) {
                log.error(ar.getBody().getError().getMessage());

            } else {
                return ar.getBody().getStatus().equalsIgnoreCase("running");
            }


        } catch (Exception e) {
            log.error("error " + e.toString());
        }


        return false;

    }


    public String detectFace(File face) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            HttpEntity<byte[]> request = new HttpEntity<>(Files.readAllBytes(face.toPath()), headers);
            ResponseEntity<APIResponse[]> ar = restTemplate.postForEntity(apiUrl +
                            "/face/v1.0/detect?detectionModel=detection_02&recognitionModel=recognition_02",
                    request, APIResponse[].class);


        if (ar.getStatusCodeValue() == 200) {

            if(ar.getBody().length > 0){

                return ar.getBody()[0].getDetectedFaceId();

            }

        } else {
            log.error(ar.getBody()[0].getError().getMessage());
        }

        } catch(Exception e) {
            log.error("error " + e.toString());
       }

      return null;
   }


    public String recognizeFace(String faceId) {

        try {

            log.info("Searching for match for faceId " + faceId);
            JSONObject personJsonObject = new JSONObject();
            personJsonObject.put("personGroupId", azureGroup);
            personJsonObject.put("maxNumOfCandidatesReturned", 1);
            personJsonObject.put("faceIds", new JSONArray().put(faceId));
            HttpEntity<?> request = new HttpEntity<Object>(personJsonObject.toString());
            ResponseEntity<APIResponse[]> ar = restTemplate.postForEntity(apiUrl +
                            "/face/v1.0/identify",
                    request, APIResponse[].class);


            if (ar.getStatusCodeValue() == 200) {


                log.info("MATCH !");

                return ar.getBody()[0].getCandidates().stream().
                        findFirst().orElse(new Candidate()).getPersonId();



            } else {
                log.error(ar.getBody()[0].getError().getMessage());
            }

        } catch(Exception e) {
            log.error("error " + e.toString());
        }

        return null;
    }


    @Scheduled(fixedDelay = 20000)
    public void  addFaces(){

        log.info("JOB Add faces to Azure started");
        List<Persons> persons = personRepository.findAllByAzureIdNull();
        persons.stream().forEach(p ->  addFace(p));
        if(persons.size() > 0) {
            train();
            while (isTrainingInProgress()) {
                log.info("AI training in progress");
                int i = 0;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                if (i > 20) {
                    break;
                }
            }
            log.info("AI training done");
        }
    }

}

