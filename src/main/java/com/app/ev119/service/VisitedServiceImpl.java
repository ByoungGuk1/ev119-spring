package com.app.ev119.service;

import com.app.ev119.domain.dto.VisitedDTO;
import com.app.ev119.domain.entity.Member;
import com.app.ev119.domain.entity.Visited;
import com.app.ev119.exception.MyPageException;
import com.app.ev119.exception.VisitedException;
import com.app.ev119.repository.VisitedRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class VisitedServiceImpl implements VisitedService {
    @PersistenceContext
    private EntityManager entityManager;
    private final VisitedRepository visitedRepository;

    @Override
    public List<VisitedDTO> findVisitedLogs(Long memberId) {
        if (memberId == null) {
            throw new MyPageException("회원 ID가 없습니다.");
        }
        List<Visited> visitedList = visitedRepository.findByMember_Id(memberId);

        List<VisitedDTO> visitedDTOList = visitedList.stream().map((data) -> {
            VisitedDTO visitedDTO = new VisitedDTO();
            visitedDTO.setId(data.getId());
            visitedDTO.setVisitedName(data.getVisitedName());
            visitedDTO.setVisitedType(data.getVisitedType());
            visitedDTO.setMemberId(data.getMember().getId());
            visitedDTO.setVisitedDate(data.getVisitedDate());
            visitedDTO.setVisitedDepartment(data.getVisitedDepartment());
            visitedDTO.setVisitedDiagnosis(data.getVisitedDiagnosis());
            visitedDTO.setVisitedDoctor(data.getVisitedDoctor());
            visitedDTO.setVisitedReason(data.getVisitedReason());
            visitedDTO.setVisitedTreatmentContent(data.getVisitedTreatmentContent());
            return visitedDTO;
        }).toList();
        return visitedDTOList;
    }

    @Override
    public void addVisitedLog(Long memberId, VisitedDTO visitedDTO) {
        if (visitedDTO == null) {
            throw new VisitedException("방문 이력 정보가 없습니다.");
        }
        Member member = entityManager.find(Member.class, memberId);
        if (member == null) {
            throw new MyPageException("존재하지 않는 회원입니다. memberId: " + memberId);
        }
        Visited visit = new Visited();
        visit.setMember(member);
        visit.setVisitedDate(visitedDTO.getVisitedDate());
        visit.setVisitedType(visitedDTO.getVisitedType());
        visit.setVisitedDepartment(visitedDTO.getVisitedDepartment());
        visit.setVisitedDiagnosis(visitedDTO.getVisitedDiagnosis());
        visit.setVisitedDoctor(visitedDTO.getVisitedDoctor());
        visit.setVisitedReason(visitedDTO.getVisitedReason());
        visit.setVisitedTreatmentContent(visitedDTO.getVisitedTreatmentContent());
        visit.setVisitedName(visitedDTO.getVisitedName());
        visitedRepository.saveVisited(visit);

        List<Visited> visitedList = visitedRepository.findByMember_Id(member.getId());
        member.setVisited(visitedList);
        entityManager.merge(member);
    }

    @Override
    public void removeVisitedLog(Long memberId, VisitedDTO visitedDTO) {
        if (visitedDTO == null || visitedDTO.getId() == null) {
            throw new VisitedException("삭제할 방문 이력 정보가 없습니다.");
        }
        visitedRepository.deleteById(visitedDTO.getId());
        List<Visited> visitedList = visitedRepository.findByMember_Id(memberId);
        Member member = entityManager.find(Member.class, memberId);
        member.setVisited(visitedList);
        entityManager.merge(member);
    }

    @Override
    public void removeAllVisitedLog(Long memberId) {
        if (memberId == null) {
            throw new MyPageException("회원 ID가 없습니다.");
        }
        visitedRepository.deleteByMember_Id(memberId);
        List<Visited> visitedList = visitedRepository.findByMember_Id(memberId);
        Member member = entityManager.find(Member.class, memberId);
        member.setVisited(visitedList);
        entityManager.merge(member);
    }

}
