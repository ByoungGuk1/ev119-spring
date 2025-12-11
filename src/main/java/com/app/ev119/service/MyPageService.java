package com.app.ev119.service;

import com.app.ev119.domain.dto.AllergyDTO;
import com.app.ev119.domain.dto.response.member.LoginResponseDTO;
import com.app.ev119.domain.entity.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface MyPageService {
//    회원 정보 수정
    public Member modifyMember(Member member);
    public void removeMember(Member member);

//    비밀번호 변경
    public void modifyPassword(Member member);

//    건강정보 조회/수정
    public Health findHealthByMember(Member member);
    public Health addDisease(Health health, String diseaseName);

//    복용 약물
    public List<Medication> findMedicationByMember(Member member);
    public List<Medication> editMedication(List<Medication> medications);

//    알레르기
    public List<AllergyDTO> findAllergyByMember(Authentication tokenDTO);
    public List<Allergy> editAllergy(List<Allergy> allergies);

//    응급 연락처
    public List<EmergencyPhone> findEmergencyPhoneByMember(Member member);
    public List<EmergencyPhone> editEmergencyPhone(List<EmergencyPhone> emergencyPhones);

//    병원 방문 이력
    public List<Visited> findVisitedByMember(Member member);
    public List<Visited> addVisited(Member member, Visited visited);
    public List<Visited> removeVisited(Visited visited);
}
