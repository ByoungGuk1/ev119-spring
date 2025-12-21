package com.app.ev119.service;

import com.app.ev119.domain.dto.*;
import com.app.ev119.domain.dto.request.MemberDTO;
import com.app.ev119.domain.dto.response.ChangePasswordDTO;
import com.app.ev119.domain.entity.*;
import com.app.ev119.exception.MyPageException;
import com.app.ev119.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MyPageServiceImpl implements MyPageService {
    private final MemberSocialRepository memberSocialRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    private final AddressRepository addressRepository;
    private final AllergyRepository allergyRepository;
    private final DiseaseRepository diseaseRepository;
    private final EmergencyPhoneRepository emergencyPhoneRepository;
    private final HealthRepository healthRepository;
    private final MedicationRepository medicationRepository;
    private final MemberStaffRepository memberStaffRepository;
    private final StaffCertRepository staffCertRepository;
    private final VisitedRepository visitedRepository;

    @Override
    public MemberDTO getMember(Long memberId) {
        Member foundMember = entityManager.find(Member.class, memberId);
        if (foundMember == null) {
            throw new MyPageException("존재하지 않는 회원입니다. memberId: " + memberId);
        }
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(foundMember.getId());
        memberDTO.setMemberName(foundMember.getMemberName());
        memberDTO.setMemberPhone(foundMember.getMemberPhone());
        memberDTO.setMemberEmail(foundMember.getMemberEmail());
        return memberDTO;
    }

    @Override
    public void modifyMember(Long memberId, MemberDTO member) {
        if (member == null) {
            throw new MyPageException("수정할 회원 정보가 없습니다.");
        }
        Member foundMember = entityManager.find(Member.class, memberId);
        if (foundMember == null) {
            throw new MyPageException("존재하지 않는 회원입니다. memberId: " + memberId);
        }
        foundMember.setMemberName(member.getMemberName());
        foundMember.setMemberPhone(member.getMemberPhone());
    }

    @Override
    public void removeMember(Long memberId) {
        Member foundMember = entityManager.find(Member.class, memberId);
        if (foundMember == null) {
            throw new MyPageException("존재하지 않는 회원입니다. memberId: " + memberId);
        }
        memberStaffRepository.findByMember_Id(memberId).forEach(memberStaff -> {
            staffCertRepository.deleteByMemberStaff_Id(memberStaff.getId());
        });
        memberStaffRepository.deleteByMember_Id(memberId);
        Health health = healthRepository.findByMember_Id(memberId);
        if (health != null) {
            diseaseRepository.deleteByHealth_Id(health.getId());
        }
        healthRepository.deleteByMember_Id(memberId);
        addressRepository.deleteByMember_Id(memberId);
        allergyRepository.deleteByMember_Id(memberId);
        emergencyPhoneRepository.deleteByMember_Id(memberId);
        medicationRepository.deleteByMember_Id(memberId);
        visitedRepository.deleteByMember_Id(memberId);
        memberSocialRepository.deleteByMemberId(memberId);
        
        entityManager.flush();
        entityManager.remove(foundMember);
    }

    @Override
    public void modifyPassword(Long memberId, ChangePasswordDTO password) {
        if (password == null || password.getNewPassword() == null || password.getNewPassword().isEmpty()) {
            throw new MyPageException("비밀번호가 비어있습니다.");
        }
        Member foundMember = entityManager.find(Member.class, memberId);
        if (foundMember == null) {
            throw new MyPageException("존재하지 않는 회원입니다. memberId: " + memberId);
        }
        String encodedPassword = passwordEncoder.encode(password.getNewPassword());
        foundMember.setMemberPassword(encodedPassword);
    }

    @Override
    public Long findIdByToken(Authentication tokenDTO) {
        if (tokenDTO == null || tokenDTO.getPrincipal() == null) {
            throw new MyPageException("인증 정보가 없습니다.");
        }
        Object principal = tokenDTO.getPrincipal();
        return (Long) principal;
    }
}
