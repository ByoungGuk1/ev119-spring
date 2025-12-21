package com.app.ev119.repository;

import com.app.ev119.domain.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    default void saveMedication(Medication medication){
        save(medication);
    }

    default void saveMedications(List<Medication> allergies){
        saveAll(allergies);
    }

    public List<Medication> findByMember_Id(Long memberId);

    default List<Medication> findAllMedications(){
        return findAll();
    }

    public void deleteByMember_Id(Long memberId);
}
