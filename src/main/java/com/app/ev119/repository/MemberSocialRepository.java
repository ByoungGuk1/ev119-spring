package com.app.ev119.repository;

import com.app.ev119.domain.entity.MemberSocial;
import com.app.ev119.domain.type.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberSocialRepository extends JpaRepository<MemberSocial, Long> {

    // 소셜 정보 추가
    public MemberSocial save(MemberSocial memberSocial);

    // 소셜 프로바이더 조회
    @Query("SELECT ms.memberSocialProvider FROM MemberSocial ms WHERE ms.member.id = :memberId")
    public List<SocialType> findMemberSocialProvidersById(@Param("memberId") Long memberId);

    // 프로바이더로 유저 아이디 조회
    @Query("SELECT ms.member.id FROM MemberSocial ms WHERE ms.memberSocialProvider = :provider AND ms.memberSocialProviderId = :providerId")
    public Long findMemberIdByProvider(@Param("provider") SocialType provider, @Param("providerId") String providerId);

    // 회원 ID로 소셜 데이터 조회
    @Query("SELECT ms FROM MemberSocial ms WHERE ms.member.id = :memberId")
    public List<MemberSocial> findByMemberId(@Param("memberId") Long memberId);

    // 회원 ID로 소셜 데이터 제거
    @Modifying
    @Query("DELETE FROM MemberSocial ms WHERE ms.member.id = :memberId")
    public void deleteByMemberId(@Param("memberId") Long memberId);
}
