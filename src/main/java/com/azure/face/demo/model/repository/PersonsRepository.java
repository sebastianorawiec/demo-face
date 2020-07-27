package com.azure.face.demo.model.repository;

import com.azure.face.demo.model.Persons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonsRepository extends JpaRepository<Persons,Long> {

   Optional<Persons> findByName(String name);
   Optional<Persons> findByPersonAzureId(String azureId);
   List<Persons> findAllByAzureIdNull();
}
