package com.app.ev119.service;

import com.app.ev119.domain.dto.DiseaseDTO;
import com.app.ev119.domain.dto.HealthDTO;
import com.app.ev119.domain.entity.Disease;
import com.app.ev119.domain.entity.Health;
import com.app.ev119.domain.entity.Member;
import com.app.ev119.repository.DiseaseRepository;
import com.app.ev119.repository.HealthRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class HealthServiceImpl implements HealthService {

    @PersistenceContext
    private EntityManager entityManager;

    private final HealthRepository healthRepository;
    private final DiseaseRepository diseaseRepository;

    @Override
    public HealthDTO findHealth(Long memberId) {
        Health health = healthRepository.findByMember_Id(memberId);

        HealthDTO healthDTO = new HealthDTO();
        healthDTO.setId(health.getId());
        healthDTO.setHealthBloodRh(health.getHealthBloodRh());
        healthDTO.setHealthGender(health.getHealthGender());
        healthDTO.setHealthWeight(health.getHealthWeight());
        healthDTO.setHealthHeight(health.getHealthHeight());
        healthDTO.setHealthBloodAbo(health.getHealthBloodAbo());
        healthDTO.setMemberId(health.getId());

        List<Disease> diseases = diseaseRepository.findByHealth_Id(health.getId());
        List<DiseaseDTO> diseaseDTOList = diseases.stream().map((disease -> {
            DiseaseDTO diseaseDTO = new DiseaseDTO();
            diseaseDTO.setHealthId(healthDTO.getId());
            diseaseDTO.setDiseaseName(disease.getDiseaseName());
            diseaseDTO.setId(disease.getId());
            return diseaseDTO;
        })).toList();

        healthDTO.setDiseases(diseaseDTOList);

        return healthDTO;
    }

    @Override
    public void updateHealth(Long memberId, HealthDTO healthDTO) {
        Member member = entityManager.find(Member.class, memberId);
        Health health = healthRepository.findByMember_Id(memberId);
        health.setHealthBloodRh(healthDTO.getHealthBloodRh());
        health.setHealthBloodAbo(healthDTO.getHealthBloodAbo());
        health.setHealthGender(healthDTO.getHealthGender());
        health.setHealthWeight(healthDTO.getHealthWeight());
        health.setHealthHeight(healthDTO.getHealthHeight());
        health.setMember(member);

        healthRepository.saveHealth(health);

        member.setHealth(health);
        entityManager.merge(member);
        entityManager.flush();
    }

    @Override
    public void addDisease(Long memberId, String diseaseName) {
        Health health = healthRepository.findByMember_Id(memberId);

        Disease disease = new Disease();
        disease.setDiseaseName(diseaseName);
        disease.setHealth(health);
        diseaseRepository.saveDisease(disease);

        List<Disease> diseaseList = diseaseRepository.findByHealth_Id(health.getId());

        health.setDiseases(diseaseList);
        healthRepository.saveHealth(health);
    }

    @Override
    public void removeDisease(Long memberId, DiseaseDTO diseaseDTO){
        Health health = healthRepository.findByMember_Id(memberId);

        // id가 있는 경우 id로 조회하여 삭제
        if (diseaseDTO.getId() != null) {
            Disease disease = diseaseRepository.findById(diseaseDTO.getId())
                    .orElseThrow(() -> new RuntimeException("삭제할 질병을 찾을 수 없습니다."));
            
            // Health와의 연관관계 확인
            if (disease.getHealth() != null && disease.getHealth().getId().equals(health.getId())) {
                diseaseRepository.delete(disease);
            } else {
                throw new RuntimeException("해당 건강정보의 질병이 아닙니다.");
            }
        } else {
            // id가 없는 경우 diseaseName과 healthId로 조회하여 삭제
            if (diseaseDTO.getDiseaseName() != null && !diseaseDTO.getDiseaseName().isEmpty()) {
                List<Disease> diseases = diseaseRepository.findByHealth_Id(health.getId());
                Disease targetDisease = diseases.stream()
                        .filter(d -> d.getDiseaseName().equals(diseaseDTO.getDiseaseName()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("삭제할 질병을 찾을 수 없습니다."));
                
                diseaseRepository.delete(targetDisease);
            } else {
                throw new RuntimeException("질병 정보가 올바르지 않습니다.");
            }
        }

        // Health 엔티티의 diseases 리스트 업데이트
        List<Disease> updatedDiseases = diseaseRepository.findByHealth_Id(health.getId());
        health.setDiseases(updatedDiseases);
        healthRepository.saveHealth(health);
    }
}
