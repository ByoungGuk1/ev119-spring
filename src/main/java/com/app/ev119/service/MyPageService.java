package com.app.ev119.service;

import com.app.ev119.domain.dto.request.MemberDTO;
import com.app.ev119.domain.dto.response.ChangePasswordDTO;
import org.springframework.security.core.Authentication;

public interface MyPageService {
    public MemberDTO getMember(Long memberId);
    public void modifyMember(Long memberId, MemberDTO member);
    public void removeMember(Long memberId);
    public void modifyPassword(Long memberId, ChangePasswordDTO password);
    public Long findIdByToken(Authentication tokenDTO);
}
