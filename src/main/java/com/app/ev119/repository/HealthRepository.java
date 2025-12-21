package com.app.ev119.repository;

import com.app.ev119.domain.entity.Health;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthRepository extends JpaRepository<Health, Long> {
    default void saveHealth(Health health){
        save(health);
    }

    public Health findByMember_Id(Long memberId);

    default List<Health> findAllHealthList(){
        return findAll();
    }

    public void deleteByMember_Id(Long memberId);
}
