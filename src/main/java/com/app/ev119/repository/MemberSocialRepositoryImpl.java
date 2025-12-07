package com.app.ev119.repository;

import com.app.ev119.domain.entity.MemberSocial;
import com.app.ev119.domain.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberSocialRepositoryImpl extends MemberSocialRepositoryAdapter {
//
//    // Spring Data JPA가 자동 생성한 프록시를 주입받음
//    private final MemberSocialRepository memberSocialRepository;
//
//    // 소셜 정보 추가
//    @Override
//    public MemberSocial save(MemberSocial memberSocial) {
//        return memberSocialRepository.save(memberSocial);
//    }
//
//    // 소셜 프로바이더 조회
//    @Override
//    public List<SocialType> findMemberSocialProvidersById(Long memberId) {
//        return memberSocialRepository.findMemberSocialProvidersById(memberId);
//    }
//
//    // 프로바이더로 유저 아이디 조회
//    @Override
//    public Long findMemberIdByProvider(SocialType provider, String providerId) {
//        return memberSocialRepository.findMemberIdByProvider(provider, providerId);
//    }
//
//    // 회원 ID로 소셜 데이터 조회
//    @Override
//    public List<MemberSocial> findByMemberId(Long memberId) {
//        return memberSocialRepository.findByMemberId(memberId);
//    }
//
//    // 회원 ID로 소셜 데이터 제거
//    @Override
//    public void deleteByMemberId(Long memberId) {
//        memberSocialRepository.deleteByMemberId(memberId);
//    }
//
//    // JpaRepository 기본 메서드들
//    @Override
//    public Optional<MemberSocial> findById(Long aLong) {
//        return memberSocialRepository.findById(aLong);
//    }
//
//    @Override
//    public boolean existsById(Long aLong) {
//        return memberSocialRepository.existsById(aLong);
//    }
//
//    @Override
//    public List<MemberSocial> findAll() {
//        return memberSocialRepository.findAll();
//    }
//
//    @Override
//    public <S extends MemberSocial> List<S> saveAll(Iterable<S> entities) {
//        return memberSocialRepository.saveAll(entities);
//    }
//
//    @Override
//    public List<MemberSocial> findAllById(Iterable<Long> longs) {
//        return memberSocialRepository.findAllById(longs);
//    }
//
//    @Override
//    public long count() {
//        return memberSocialRepository.count();
//    }
//
//    @Override
//    public void deleteById(Long aLong) {
//        memberSocialRepository.deleteById(aLong);
//    }
//
//    @Override
//    public void delete(MemberSocial entity) {
//        memberSocialRepository.delete(entity);
//    }
//
//    @Override
//    public void deleteAllById(Iterable<? extends Long> longs) {
//        memberSocialRepository.deleteAllById(longs);
//    }
//
//    @Override
//    public void deleteAll(Iterable<? extends MemberSocial> entities) {
//        memberSocialRepository.deleteAll(entities);
//    }
//
//    @Override
//    public void deleteAll() {
//        memberSocialRepository.deleteAll();
//    }
//
//    @Override
//    public void flush() {
//        memberSocialRepository.flush();
//    }
//
//    @Override
//    public <S extends MemberSocial> S saveAndFlush(S entity) {
//        return memberSocialRepository.saveAndFlush(entity);
//    }
//
//    @Override
//    public <S extends MemberSocial> List<S> saveAllAndFlush(Iterable<S> entities) {
//        return memberSocialRepository.saveAllAndFlush(entities);
//    }
//
//    @Override
//    public void deleteAllInBatch(Iterable<MemberSocial> entities) {
//        memberSocialRepository.deleteAllInBatch(entities);
//    }
//
//    @Override
//    public void deleteAllByIdInBatch(Iterable<Long> longs) {
//        memberSocialRepository.deleteAllByIdInBatch(longs);
//    }
//
//    @Override
//    public void deleteAllInBatch() {
//        memberSocialRepository.deleteAllInBatch();
//    }
//
//    @Override
//    public MemberSocial getReferenceById(Long aLong) {
//        return memberSocialRepository.getReferenceById(aLong);
//    }
}
