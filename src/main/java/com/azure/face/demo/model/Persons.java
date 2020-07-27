package com.azure.face.demo.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


@Data
@Entity
@Table(name = "persons")
public class Persons implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message="name  is required")
    @Size(min=3, max = 80,message = "invalid length for name - it has to be string from 3 to 80 characters")
    private String name;

    @Column(name="creation_time",updatable=false)
    private Date created;

    @Column(name="person_azure_id")
    private String personAzureId;

    @OneToMany(cascade = CascadeType.ALL,  mappedBy = "person",  orphanRemoval = true)
    private Collection<Matches> matches = new ArrayList<>();

    @Column(name="image_name")
    private String imageName;

    //image Azure id
    @Column(name="azure_id")
    private String azureId;

    @Transient
    private MultipartFile photo;

    @Transient
    private String thumbUrl;


    public String getThumbUrl(){

      return imageName.replace(".jpg",".thumb.jpg");
    }



}
