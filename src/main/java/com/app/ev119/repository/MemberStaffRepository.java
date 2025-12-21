package com.app.ev119.repository;

import com.app.ev119.domain.entity.MemberStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberStaffRepository extends JpaRepository<MemberStaff, Long> {
    default void saveMemberStaff(MemberStaff memberStaff){
        save(memberStaff);
    }

    default void saveMemberStaffs(List<MemberStaff> allergies){
        saveAll(allergies);
    }

    public List<MemberStaff> findByMember_Id(Long memberId);

    default List<MemberStaff> findAllMemberStaffs(){
        return findAll();
    }

    public void deleteByMember_Id(Long memberId);
}
