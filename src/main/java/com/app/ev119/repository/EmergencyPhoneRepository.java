package com.app.ev119.repository;

import com.app.ev119.domain.entity.EmergencyPhone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyPhoneRepository extends JpaRepository<EmergencyPhone, Long> {
    default void saveEmergencyPhone(EmergencyPhone emergencyPhone){
        save(emergencyPhone);
    }

    default void saveEmergencyPhones(List<EmergencyPhone> allergies){
        saveAll(allergies);
    }

    public List<EmergencyPhone> findByMember_Id(Long memberId);

    default List<EmergencyPhone> findAllEmergencyPhones(){
        return findAll();
    }

    public void deleteByMember_Id(Long memberId);
}
