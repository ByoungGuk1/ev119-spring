package com.app.ev119.repository;

import com.app.ev119.domain.entity.StaffCert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffCertRepository extends JpaRepository<StaffCert, Long> {
    default void saveStaffCert(StaffCert staffCert){
        save(staffCert);
    }

    default void saveStaffCerts(List<StaffCert> allergies){
        saveAll(allergies);
    }

    public List<StaffCert> findByMemberStaff_Id(Long memberStaffId);

    default List<StaffCert> findAllStaffCerts(){
        return findAll();
    }

    public void deleteByMemberStaff_Id(Long memberStaffId);
}
