package com.azure.face.demo.model.repository;

import com.azure.face.demo.model.Matches;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchesRepository extends JpaRepository<Matches,Long> {
}
