package com.app.ev119.repository;

import com.app.ev119.domain.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    default void saveDisease(Disease disease){
        save(disease);
    }

    default void saveAllergies(List<Disease> allergies){
        saveAll(allergies);
    }

    public List<Disease> findByHealth_Id(Long memberId);

    default List<Disease> findAllHealth(){
        return findAll();
    }

    public void deleteByHealth_Id(Long healthId);
}
