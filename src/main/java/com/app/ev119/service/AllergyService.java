package com.app.ev119.service;

import com.app.ev119.domain.dto.AllergyDTO;

import java.util.List;

public interface AllergyService {
    public List<AllergyDTO> findAllergies(Long memberId);
    public void modifyAllergies(Long memberId, List<AllergyDTO> allergyDTOs);
}
