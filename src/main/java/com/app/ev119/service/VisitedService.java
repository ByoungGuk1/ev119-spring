package com.app.ev119.service;

import com.app.ev119.domain.dto.VisitedDTO;

import java.util.List;

public interface VisitedService {
    public List<VisitedDTO> findVisitedLogs (Long memberId);
    public void addVisitedLog(Long memberId, VisitedDTO visitedDTO);
    public void removeVisitedLog(Long memberId, VisitedDTO visitedDTO);
    public void removeAllVisitedLog(Long memberId);
}
