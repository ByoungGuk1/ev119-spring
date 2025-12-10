package com.app.ev119.service;

import com.app.ev119.domain.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MyPageServiceImpl implements MyPageService {

    private final MyPageService myPageService;
    @PersistenceContext
    private EntityManager entityManager;

//    @Override
//    public Member findMemberByToken(Authentication tokenDTO) {
//        Object p = tokenDTO.getPrincipal();
//        Long memberId = null;
//        String memberEmail = null;
//        Member member = null;
//        if (p instanceof LoginResponseDTO urdto) {
//            memberId = urdto.getMemberId();
//            memberEmail = entityManager.find(Member.class, memberId).getMemberEmail();
//        }
//        if (tokenDTO instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oat) {
//            var attrs = ((org.springframework.security.oauth2.core.user.OAuth2User) oat.getPrincipal()).getAttributes();
//            String reg = oat.getAuthorizedClientRegistrationId();
//            if ("google".equals(reg)) memberEmail =  (String) attrs.get("email");
//            if ("naver".equals(reg))  memberEmail =  (String) ((java.util.Map<?,?>) attrs.get("response")).get("email");
//            if ("kakao".equals(reg))  memberEmail =  (String) ((java.util.Map<?,?>) attrs.get("kakao_account")).get("email");
//        }
//
//        member = entityManager.find(Member.class, memberEmail);
//        if(member == null){
//            throw new RuntimeException("멤버 찾기 중 오류 : MyPageServiceImpl->findMemberByToken");
//        }
//
//        return member;
//    }

    @Override
    public Member modifyMember(Member member) {
        Member editMember = entityManager.find(Member.class, member.getId());
        entityManager.detach(editMember);
        editMember.setMemberEmail(member.getMemberEmail());
        editMember.setMemberPhone(member.getMemberPhone());
        editMember.setMemberName(member.getMemberName());
        entityManager.merge(editMember);
        return entityManager.find(Member.class, member.getId());
    }

    @Override
    public void removeMember(Member member) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        QMember qMember = QMember.member;
        QMemberSocial qMemberSocial = QMemberSocial.memberSocial;
        QMemberStaff qMemberStaff = QMemberStaff.memberStaff;
        QStaffCert qStaffCert = QStaffCert.staffCert;
        QHealth qHealth = QHealth.health;
        QDisease qDisease = QDisease.disease;
        QMedication qMedication = QMedication.medication;
        QAllergy qAllergy = QAllergy.allergy;
        QEmergencyPhone qEmergencyPhone = QEmergencyPhone.emergencyPhone;
        QAddress qAddress = QAddress.address;

        List<Tuple> memberAllDatas = jpaQueryFactory.select(qMember, qMemberSocial, qMemberStaff, qStaffCert, qHealth, qDisease, qMedication, qAllergy, qEmergencyPhone, qAddress)
                .from(qMember)
                .join(qMemberSocial)
                .on(qMember.id.eq(qMemberSocial.member.id))
                .join(qMemberStaff)
                .on(qMember.id.eq(qMemberStaff.member.id))
                .join(qStaffCert)
                .on(qMemberStaff.id.eq(qStaffCert.memberStaff.id))
                .join(qHealth)
                .on(qMember.id.eq(qHealth.member.id))
                .join(qDisease)
                .on(qHealth.id.eq(qDisease.health.id))
                .join(qMedication)
                .on(qMember.id.eq(qMedication.member.id))
                .join(qAllergy)
                .on(qMember.id.eq(qAllergy.member.id))
                .join(qEmergencyPhone)
                .on(qMember.id.eq(qEmergencyPhone.member.id))
                .join(qAddress)
                .on(qMember.id.eq(qAddress.member.id))
                .where(qMember.id.eq(member.getId()))
                .fetch();

        List<Long> memberSocialList = memberAllDatas.stream().map((data) -> data.get(qMemberSocial).getId()).toList();
        List<Long> memberStaffList = memberAllDatas.stream().map((data) -> data.get(qMemberStaff).getId()).toList();
        List<Long> staffCertList = memberAllDatas.stream().map((data) -> data.get(qStaffCert).getId()).toList();
        List<Long> healthList = memberAllDatas.stream().map((data) -> data.get(qHealth).getId()).toList();
        List<Long> diseaseList = memberAllDatas.stream().map((data) -> data.get(qDisease).getId()).toList();
        List<Long> medicationList = memberAllDatas.stream().map((data) -> data.get(qMedication).getId()).toList();
        List<Long> allergyList = memberAllDatas.stream().map((data) -> data.get(qAllergy).getId()).toList();
        List<Long> emergencyPhoneList = memberAllDatas.stream().map((data) -> data.get(qEmergencyPhone).getId()).toList();
        List<Long> addressList = memberAllDatas.stream().map((data) -> data.get(qAddress).getId()).toList();

        memberSocialList.forEach((id)->entityManager.remove(entityManager.find(MemberSocial.class, id)));
        staffCertList.forEach((id)->entityManager.remove(entityManager.find(StaffCert.class, id)));
        memberStaffList.forEach((id)->entityManager.remove(entityManager.find(MemberStaff.class, id)));
        diseaseList.forEach((id)->entityManager.remove(entityManager.find(Disease.class, id)));
        healthList.forEach((id)->entityManager.remove(entityManager.find(Health.class, id)));
        medicationList.forEach((id)->entityManager.remove(entityManager.find(Medication.class, id)));
        allergyList.forEach((id)->entityManager.remove(entityManager.find(Allergy.class, id)));
        emergencyPhoneList.forEach((id)->entityManager.remove(entityManager.find(EmergencyPhone.class, id)));
        addressList.forEach((id)->entityManager.remove(entityManager.find(Address.class, id)));

        entityManager.remove(member);
    }

    @Override
    public void modifyPassword(Member member) {
        Member selectedMember = entityManager.find(Member.class, member.getId());
        selectedMember.setMemberPassword(member.getMemberPassword());
    }

    @Override
    public Health findHealthByMember(Member member) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        QMember qMember = QMember.member;
        QHealth qHealth = QHealth.health;
        QDisease qDisease = QDisease.disease;

        List<Tuple> memberHealthAndDisease = jpaQueryFactory
                .select(qHealth, qDisease)
                .from(qMember)
                .join(qHealth)
                .on(qMember.id.eq(qHealth.member.id))
                .join(qDisease)
                .on(qHealth.id.eq(qDisease.health.id))
                .where(qMember.id.eq(member.getId()))
                .fetch();

        Health selectedHealth = new Health();
        List<Disease> diseaseList = memberHealthAndDisease.stream().map((data) -> (Disease)data.get(qDisease)).toList();
        selectedHealth.setDiseases(diseaseList);

        return selectedHealth;
    }

    @Override
    public Health addDisease(Health health, String diseaseName) {
        Disease newDisease = new Disease();
        Health foundHealth = entityManager.find(Health.class, health.getId());
        newDisease.setDiseaseName(diseaseName);
        newDisease.setHealth(foundHealth);
        entityManager.persist(newDisease);

        return myPageService.findHealthByMember(entityManager.find(Member.class, health.getId()));
    }

    @Override
    public List<Medication> findMedicationByMember(Member member) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        QMember qMember = QMember.member;
        QMedication qMedication = QMedication.medication;

        List<Medication> memberMedication = jpaQueryFactory
                .select(qMedication)
                .from(qMember)
                .join(qMedication)
                .on(qMember.id.eq(qMedication.member.id))
                .where(qMember.id.eq(member.getId()))
                .fetch();

        return memberMedication;
    }

    @Override
    public List<Medication> editMedication(List<Medication> medications) {
        medications.forEach(medication -> {
            Medication editMedication = new Medication();
            editMedication.setMedicationName(medication.getMedicationName());
            editMedication.setMember(medication.getMember());
            entityManager.persist(editMedication);
        });

        return myPageService.findMedicationByMember(entityManager.find(Member.class, medications.get(0).getMember().getId()));
    }

    @Override
    public List<Allergy> findAllergyByMember(Member member) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        QMember qMember = QMember.member;
        QAllergy qAllergy = QAllergy.allergy;

        List<Allergy> memberAllergy = jpaQueryFactory
                .select(qAllergy)
                .from(qMember)
                .join(qAllergy)
                .on(qMember.id.eq(qAllergy.member.id))
                .where(qMember.id.eq(member.getId()))
                .fetch();

        return memberAllergy;
    }

    @Override
    public List<Allergy> editAllergy(List<Allergy> allergies) {
        allergies.forEach(allergy -> {
        Allergy editAllergy = new Allergy();
        editAllergy.setAllergyName(allergy.getAllergyName());
        editAllergy.setAllergyType(allergy.getAllergyType());
        editAllergy.setMember(allergy.getMember());
        entityManager.persist(editAllergy);
    });

        return myPageService.findAllergyByMember(entityManager.find(Member.class, allergies.get(0).getMember().getId()));
}

    @Override
    public List<EmergencyPhone> findEmergencyPhoneByMember(Member member) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        QMember qMember = QMember.member;
        QEmergencyPhone qEmergencyPhone = QEmergencyPhone.emergencyPhone;

        List<EmergencyPhone> memberEmergencyPhone = jpaQueryFactory
                .select(qEmergencyPhone)
                .from(qMember)
                .join(qEmergencyPhone)
                .on(qMember.id.eq(qEmergencyPhone.member.id))
                .where(qMember.id.eq(member.getId()))
                .fetch();

        return memberEmergencyPhone;
    }

    @Override
    public List<EmergencyPhone> editEmergencyPhone(List<EmergencyPhone> emergencyPhones) {
        emergencyPhones.forEach(emergencyPhone -> {
            EmergencyPhone editEmergencyPhone = new EmergencyPhone();
            editEmergencyPhone.setEmergencyPhoneName(emergencyPhone.getEmergencyPhoneName());
            editEmergencyPhone.setEmergencyPhoneRelationship(emergencyPhone.getEmergencyPhoneRelationship());
            editEmergencyPhone.setEmergencyPhoneNumber(emergencyPhone.getEmergencyPhoneNumber());
            editEmergencyPhone.setMember(emergencyPhone.getMember());
            entityManager.persist(editEmergencyPhone);
        });

        return myPageService.findEmergencyPhoneByMember(entityManager.find(Member.class, emergencyPhones.get(0).getMember().getId()));
    }

    @Override
    public List<Visited> findVisitedByMember(Member member) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        QMember qMember = QMember.member;
        QVisited qVisited = QVisited.visited;

        List<Visited> memberVisited = jpaQueryFactory
                .select(qVisited)
                .from(qMember)
                .join(qVisited)
                .on(qMember.id.eq(qVisited.member.id))
                .where(qMember.id.eq(member.getId()))
                .fetch();

        return memberVisited;
    }

    @Override
    public List<Visited> addVisited(Member member, Visited visited) {
        visited.setMember(member);
        entityManager.persist(visited);

        return myPageService.findVisitedByMember(entityManager.find(Member.class, visited.getMember().getId()));
    }

    @Override
    public List<Visited> removeVisited(Visited visited) {
        Member member = visited.getMember();
        entityManager.remove(visited);
        return myPageService.findVisitedByMember(entityManager.find(Member.class, member.getId()));
    }
}
