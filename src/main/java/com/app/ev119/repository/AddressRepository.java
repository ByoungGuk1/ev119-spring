package com.app.ev119.repository;

import com.app.ev119.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    default void saveAddress(Address address){
        save(address);
    }

    default void saveAddresses(List<Address> addresses){
        saveAll(addresses);
    }

    public List<Address> findByMember_Id(Long memberId);

    default List<Address> findAllAddresses(){
        return findAll();
    }

    public void deleteByMember_Id(Long memberId);
}
