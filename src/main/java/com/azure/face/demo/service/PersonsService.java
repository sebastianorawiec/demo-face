package com.azure.face.demo.service;

import com.azure.face.demo.exception.UploadException;
import com.azure.face.demo.model.Persons;
import com.azure.face.demo.model.repository.PersonsRepository;
import com.azure.face.demo.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;


@Slf4j
@Service
public class PersonsService {

    @Value("${app.upload.dir}")
    public String uploadDir;

    @Autowired
    private PersonsRepository personsRepository;


    @Autowired
    private AzureService azureService;



    public Page<Persons> getAll(Pageable p){

        return personsRepository.findAll(p);
    }

    public void removePerson(Long id){

        Persons persons = personsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Could not  find person with id:" + id));
        azureService.delPerson(persons.getPersonAzureId());
        personsRepository.delete(persons);

    }

    public void addPerson(Persons person) {


        try {

            if(personsRepository.findByName(person.getName()).isPresent()){
                throw new UploadException("Person with name " + person.getName() + " already exists ");
            }


            MultipartFile file = person.getPhoto();

            Path path;
            if(!file.isEmpty()) {
               path = Utils.handleUpload(file, uploadDir);
            } else {
                throw new UploadException("Please upload photo");
            }

            if(person.getId() == null){
                person.setCreated(new Date());

            }
            // detect face on uploaded photo
            String faceId = azureService.detectFace(path.toFile());
            if(faceId == null ) {
                throw new UploadException("Could not find face on uploaded photo");
            }

            String azureId = azureService.addPerson(person.getName());

            if(azureId == null) {
                throw new UploadException("Could not add person to Azure face recognition service");
            }

            person.setPersonAzureId(azureId);
            person.setImageName(file.getOriginalFilename());
            personsRepository.save(person);

        } catch (IOException e) {

            log.error(e.toString());
            throw new UploadException("Could not add person to face recognition DB!");
        }
    }
}
