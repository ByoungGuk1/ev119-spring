package com.app.ev119.repository;

import com.app.ev119.domain.entity.Visited;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitedRepository extends JpaRepository<Visited, Long> {
    default void saveVisited(Visited visited){
        save(visited);
    }

    default void saveVisitedList(List<Visited> allergies){
        saveAll(allergies);
    }

    public List<Visited> findByMember_Id(Long memberId);

    default List<Visited> findAllVisitedList(){
        return findAll();
    }

    public void deleteById(Long id);

    public void deleteByMember_Id(Long memberId);
}
