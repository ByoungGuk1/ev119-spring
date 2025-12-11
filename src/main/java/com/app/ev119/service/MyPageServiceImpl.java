package com.app.ev119.service;

import com.app.ev119.domain.dto.AllergyDTO;
import com.app.ev119.domain.dto.response.member.LoginResponseDTO;
import com.app.ev119.domain.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MyPageServiceImpl implements MyPageService {

    @PersistenceContext
    private EntityManager entityManager;

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

        QHealth qHealth = QHealth.health;
        QDisease qDisease = QDisease.disease;

        Health health = jpaQueryFactory
                .selectFrom(qHealth)
                .where(qHealth.member.id.eq(member.getId()))
                .fetchOne();

        if (health == null) {
            return null;
        }

        List<Disease> diseaseList = jpaQueryFactory
                .selectFrom(qDisease)
                .where(qDisease.health.id.eq(health.getId()))
                .fetch();

        health.setDiseases(diseaseList);
        return health;
    }

    @Override
    public Health addDisease(Health health, String diseaseName) {
        Health foundHealth = entityManager.find(Health.class, health.getId());
        if (foundHealth == null) {
            throw new IllegalArgumentException("Health not found. id=" + health.getId());
        }

        Disease newDisease = new Disease();
        newDisease.setDiseaseName(diseaseName);
        newDisease.setHealth(foundHealth);
        entityManager.persist(newDisease);

        Member member = foundHealth.getMember();
        return this.findHealthByMember(member);
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
        Long memberId = medications.get(0).getMember().getId();
        Member member = entityManager.find(Member.class, memberId);

        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QMedication qMedication = QMedication.medication;

        List<Medication> oldList = query.selectFrom(qMedication)
                .where(qMedication.member.id.eq(memberId))
                .fetch();

        oldList.forEach(entityManager::remove);

        medications.forEach(medication -> {
            medication.setId(null);
            medication.setMember(member);
            entityManager.persist(medication);
        });

        return this.findMedicationByMember(member);
    }

    @Override
    public List<AllergyDTO> findAllergyByMember(Authentication tokenDTO) {
        Object memberId = tokenDTO.getPrincipal();
        Long id = (Long) memberId;

        QAllergy qAllergy = QAllergy.allergy;

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        List<Allergy> allergies = jpaQueryFactory
                .select(qAllergy.allergy)
                .from(qAllergy)
                .where(qAllergy.member.id.eq(id))
                .stream().toList();

        List<AllergyDTO> allergyDTOList = allergies.stream().map(data -> {
            AllergyDTO newData = new AllergyDTO();
            newData.setId(data.getId());
            newData.setAllergyName(data.getAllergyName());
            newData.setAllergyType(data.getAllergyType());
            newData.setMemberId(data.getMember().getId());
            return newData;
        }).toList();

        return allergyDTOList;
    }

    @Override
    public List<Allergy> editAllergy(List<Allergy> allergies) {
    Long memberId = allergies.get(0).getMember().getId();
    Member member = entityManager.find(Member.class, memberId);

    JPAQueryFactory query = new JPAQueryFactory(entityManager);
    QAllergy qAllergy = QAllergy.allergy;

    List<Allergy> oldList = query.selectFrom(qAllergy)
            .where(qAllergy.member.id.eq(memberId))
            .fetch();

    oldList.forEach(entityManager::remove);

    allergies.forEach(medication -> {
        medication.setId(null);
        medication.setMember(member);
        entityManager.persist(medication);
    });

//    return this.findAllergyByMember(member.getMemberEmail());
        return List.of();
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
        Long memberId = emergencyPhones.get(0).getMember().getId();
        Member member = entityManager.find(Member.class, memberId);

        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QEmergencyPhone qEmergencyPhone = QEmergencyPhone.emergencyPhone;

        List<EmergencyPhone> oldList = query.selectFrom(qEmergencyPhone)
                .where(qEmergencyPhone.member.id.eq(memberId))
                .fetch();

        oldList.forEach(entityManager::remove);

        emergencyPhones.forEach(medication -> {
            medication.setId(null);
            medication.setMember(member);
            entityManager.persist(medication);
        });

        return this.findEmergencyPhoneByMember(member);
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

        return this.findVisitedByMember(entityManager.find(Member.class, visited.getMember().getId()));
    }

    @Override
    public List<Visited> removeVisited(Visited visited) {
        Visited foundVisited = entityManager.find(Visited.class, visited.getId());
        Member member = visited.getMember();
        entityManager.remove(foundVisited);
        return this.findVisitedByMember(entityManager.find(Member.class, member.getId()));
    }
}
