package com.app.ev119.repository;

import com.app.ev119.domain.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
    default void saveAllergy(Allergy allergy){
        save(allergy);
    }

    default void saveAllergies(List<Allergy> allergies){
        saveAll(allergies);
    }

    public List<Allergy> findByMember_Id(Long memberId);

    default List<Allergy> findAllAllergies(){
        return findAll();
    }

    public void deleteByMember_Id(Long memberId);
}
