package com.azure.face.demo.service;

import com.azure.face.demo.exception.UploadException;
import com.azure.face.demo.model.MatchedPerson;
import com.azure.face.demo.model.Matches;
import com.azure.face.demo.model.Persons;
import com.azure.face.demo.model.repository.MatchesRepository;
import com.azure.face.demo.model.repository.PersonsRepository;
import com.azure.face.demo.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class DetectionService {

    @Autowired
    private MatchesRepository matchesRepository;
    @Autowired
    private PersonsRepository personsRepository;
    @Autowired
    private AzureService azureService;

    @Value("${app.upload.dir}")
    public String uploadDir;
    @Value("${app.baseurl}")
    public String baseUrl;

    public MatchedPerson findPerson(MultipartFile photo, String IP){


        String personName = null;

        try {

            if (photo.isEmpty()){

                log.warn("Photo not uploaded");
                throw new UploadException("Photo not uploaded!");
            }


            Path copyLocation = Utils.handleUpload(photo,uploadDir + File.separator + "detect");
            String faceAzureId = azureService.detectFace(copyLocation.toFile());

            Matches matches = new Matches();
            matches.setImageName(photo.getOriginalFilename());
            matches.setUploadTime(new Date());
            matchesRepository.save(matches);


            if(faceAzureId == null){

                log.warn("face not detected on uploaded photo " + photo.getOriginalFilename());
                throw  new UploadException("Could not  detect face on uploaded photo");
            }

            String personAzureId = azureService.
                    recognizeFace(faceAzureId);


            if(personAzureId != null){

                log.info("found person with azure id " + personAzureId);
                Optional<Persons> opt = personsRepository.findByPersonAzureId(personAzureId);
                if(opt.isPresent()){

                    Persons person = opt.get();
                    personName = person.getName();
                    matches.setPerson(person);
                    matchesRepository.save(matches);

                }

            } else {

                log.warn("face not recognized");

            }

            log.info("found " + personName);

            MatchedPerson person = new MatchedPerson();
            person.setPersonName(personName);
            person.setPhoto(copyLocation.getFileName().toString());
            person.setPhotoFullUrl(baseUrl + "/detect/" + copyLocation.getFileName().toString());
            return person;

        } catch (IOException e){

            log.error("problem with photo processing " + e.toString());
            throw new UploadException("Problem with photo processing! Please upload again.");
        }

    }

}
