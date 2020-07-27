package com.azure.face.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@Table(name = "matches")
public class Matches implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="image_name")
    private String imageName;

    @Column(name="upload_time",updatable=false)
    private Date uploadTime;


    @JoinColumn(name = "person_id", referencedColumnName = "id", foreignKey =
    @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE SET NULL"))
    @ManyToOne
    private Persons person;

    @Transient
    private String uploadedThumbUrl;


    public String getUploadedThumbUrl(){

        return "detect/" + imageName.replace(".jpg",
                ".thumb.jpg");

    }


}
