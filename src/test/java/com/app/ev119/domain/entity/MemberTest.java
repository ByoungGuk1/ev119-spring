package com.app.ev119.domain.entity;

import com.app.ev119.domain.type.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import java.util.List;

@SpringBootTest @Slf4j
@Transactional @Commit
class MemberTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    void insertAllMemberData(){
//        Member member1 = new Member();
//
//        member1.setMemberName("김길동");
//        member1.setMemberEmail("test1234@gmail.com");
//        member1.setMemberPassword(passwordEncoder.encode("1234"));
//        member1.setMemberPhone("01013572468");
//        entityManager.persist(member1);

//        Member member = entityManager.find(Member.class, member1.getId());

//        Health health = new Health();
//        health.setHealthBloodRh(BloodRh.PLUS);
//        health.setHealthBloodAbo(BloodAbo.B);
//        health.setHealthHeight(169.5);
//        health.setHealthWeight(51.5);
//        health.setHealthGender(GenderType.M);
//        health.setMember(member);
//        entityManager.persist(health);

        Member member = entityManager.find(Member.class, 1L);
        log.info("member={}", member);

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        QHealth qHealth = QHealth.health;
        List<Health> healthList = jpaQueryFactory.selectFrom(qHealth).where(qHealth.member.id.eq(member.getId())).fetch();
        Long healthId = healthList.get(0).getId();

        Health health = entityManager.find(Health.class, healthId);
        log.info("health={}", health);

        Address address = new Address();
        address.setAddressType(AddressType.HOME);
        address.setAddressStreet("지번주소");
        address.setAddressRoad("도로명 주소");
        address.setAddressZipcode("12321");
        address.setAddressLatitude("위도");
        address.setAddressLongitude("경도");
        address.setMember(member);
        entityManager.persist(address);

        Disease disease = new Disease();
        disease.setDiseaseName("질병 이름");
        disease.setHealth(health);
        entityManager.persist(disease);

        Medication medication = new Medication();
        medication.setMedicationName("약물 명");
        medication.setMedicationUsage("용법");
        medication.setMedicationTakingtime("복용시간");
        medication.setMember(member);
        entityManager.persist(medication);

        Allergy allergy = new Allergy();
        allergy.setAllergyType(AllergyType.MEDICINE);
        allergy.setAllergyName("알레르기 명");
        allergy.setMember(member);
        entityManager.persist(allergy);

        EmergencyPhone emergencyPhone = new EmergencyPhone();
        emergencyPhone.setEmergencyPhoneName("이름");
        emergencyPhone.setEmergencyPhoneRelationship("관계");
        emergencyPhone.setEmergencyPhoneNumber("01001230123");
        emergencyPhone.setMember(member);
        entityManager.persist(emergencyPhone);

        MemberSocial memberSocial = new MemberSocial();
        memberSocial.setMemberSocialProviderId("프로바이터 ID");
        memberSocial.setMemberSocialProvider(SocialType.LOCAL);
        memberSocial.setMember(member);
        entityManager.persist(memberSocial);

        MemberStaff memberStaff = new MemberStaff();
        memberStaff.setMemberStaffIndustry("면허");
        memberStaff.setMember(member);
        entityManager.persist(memberStaff);

        StaffCert staffCert = new StaffCert();
        staffCert.setStaffCertType(StaffCertType.LICENSE);
        staffCert.setStaffCertContent("인증 내용");
//        staffCert.setStaffCertDate(new Date());
        staffCert.setMemberStaff(memberStaff);
        entityManager.persist(staffCert);


        Visited visited = new Visited();
//        visited.setVisitedDate();
        visited.setVisitedName("병원명");
        visited.setVisitedDepartment("진료과");
        visited.setVisitedType(VisitedType.CLINIC);
        visited.setVisitedReason("방문 사유");
        visited.setVisitedDiagnosis("진단명");
        visited.setVisitedTreatmentContent("치료 내용");
        visited.setVisitedDoctor("담당 의사");
        visited.setMember(member);
        entityManager.persist(visited);

        log.info("inserted all member data: {}", member);
    }

    @Test
    public void getAllMemberData(){
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
        QVisited qVisited = QVisited.visited;

        List<Tuple> memberAllDatas = jpaQueryFactory.select(qMember, qMemberSocial, qMemberStaff, qStaffCert, qHealth, qDisease, qMedication, qAllergy, qEmergencyPhone, qAddress, qVisited)
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
                .join(qVisited)
                .on(qMember.id.eq(qVisited.member.id))
                .fetch();

        List<Long> list = memberAllDatas.stream().map((data)->data.get(qAddress).getId()).toList();
        list.forEach((id)->entityManager.remove(entityManager.find(Address.class, id)));
        memberAllDatas.forEach((data)->log.info("{}", data));
    }

    @Test
    public void insertMemberSocial(){
        Member member1 = new Member();
        member1.setMemberEmail("test123123@gmail.com");
        entityManager.persist(member1);
        MemberSocial memberSocial = new MemberSocial();
        memberSocial.setMemberSocialProviderId("소셜 아이디가 입력됨");
        memberSocial.setMemberSocialProvider(SocialType.LOCAL);
        memberSocial.setMember(member1);
        entityManager.persist(memberSocial);
    }

    @Test
    public void getMemberSocial(){
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        QMember qMember = QMember.member;
        QMemberSocial qMemberSocial = QMemberSocial.memberSocial;
        List<Tuple> tuples = jpaQueryFactory
                .select(qMember, qMemberSocial)
                .from(qMember)
                .join(qMemberSocial)
                .on(qMember.id.eq(qMemberSocial.member.id))
                .fetch();
        tuples.forEach((tuple)->log.info("{}", tuple));
    }

    @Test
    void insertAdminAccount() {
        log.info("🔥 insertAdminAccount START");

        try {
            Member admin = new Member();
            admin.setMemberName("EV119 관리자");
            admin.setMemberEmail("admin" + System.currentTimeMillis() + "@ev119.com");
            admin.setMemberPassword(passwordEncoder.encode("admin1234"));
            admin.setMemberPhone("010" + (int)(Math.random()*9000+1000) + "8888"); // 중복 회피
            admin.setMemberType(MemberType.ADMIN);

            entityManager.persist(admin);
            entityManager.flush();

            log.info("🔥 AFTER FLUSH, id={}", admin.getId());
        } catch (Exception e) {
            log.error("❌ flush 실패", e);
            throw e;
        }
    }




}