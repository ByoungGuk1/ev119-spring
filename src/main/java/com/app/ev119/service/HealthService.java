package com.app.ev119.service;

import com.app.ev119.domain.dto.DiseaseDTO;
import com.app.ev119.domain.dto.HealthDTO;

public interface HealthService {
    public HealthDTO findHealth(Long memberId);
    public void updateHealth(Long memberId, HealthDTO healthDTO);
    public void addDisease(Long memberId, String diseaseName);
    public void removeDisease(Long memberId, DiseaseDTO diseaseDTO);
}
