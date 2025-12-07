package com.app.ev119.repository;

import com.app.ev119.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl extends MemberRepositoryAdapter {
//
//    // Spring Data JPA가 자동 생성한 프록시를 주입받음
//    private final MemberRepository memberRepository;
//
//    // 회원 조회
//    @Override
//    public Optional<Member> findById(Long id) {
//        return memberRepository.findById(id);
//    }
//
//    // 전체 조회
//    @Override
//    public List<Member> findAll() {
//        return memberRepository.findAll();
//    }
//
//    // 이메일로 회원 찾기
//    @Override
//    public Optional<Member> findByMemberEmail(String email) {
//        return memberRepository.findByMemberEmail(email);
//    }
//
//    // 이메일로 회원 ID 찾기
//    @Override
//    public Long findIdByMemberEmail(String email) {
//        return memberRepository.findIdByMemberEmail(email);
//    }
//
//    // 이름과 이메일로 회원 찾기
//    @Override
//    public Optional<Member> findByMemberNameAndMemberEmail(String memberName, String memberEmail) {
//        return memberRepository.findByMemberNameAndMemberEmail(memberName, memberEmail);
//    }
//
//    // 이름으로 이메일 목록 찾기
//    @Override
//    public List<String> findEmailsByMemberName(String name) {
//        return memberRepository.findEmailsByMemberName(name);
//    }
//
//    // 회원가입 시 이메일 중복 체크
//    @Override
//    public boolean existsByMemberEmail(String email) {
//        return memberRepository.existsByMemberEmail(email);
//    }
//
//    // 회원가입
//    @Override
//    public Member save(Member member) {
//        return memberRepository.save(member);
//    }
//
//    // 회원 탈퇴
//    @Override
//    public void deleteById(Long id) {
//        memberRepository.deleteById(id);
//    }
//
//    // 회원 삭제
//    @Override
//    public void delete(Member member) {
//        memberRepository.delete(member);
//    }
//
//    // JpaRepository 기본 메서드들
//    @Override
//    public boolean existsById(Long aLong) {
//        return memberRepository.existsById(aLong);
//    }
//
//    @Override
//    public <S extends Member> List<S> saveAll(Iterable<S> entities) {
//        return memberRepository.saveAll(entities);
//    }
//
//    @Override
//    public List<Member> findAllById(Iterable<Long> longs) {
//        return memberRepository.findAllById(longs);
//    }
//
//    @Override
//    public long count() {
//        return memberRepository.count();
//    }
//
//    @Override
//    public void deleteAllById(Iterable<? extends Long> longs) {
//        memberRepository.deleteAllById(longs);
//    }
//
//    @Override
//    public void deleteAll(Iterable<? extends Member> entities) {
//        memberRepository.deleteAll(entities);
//    }
//
//    @Override
//    public void deleteAll() {
//        memberRepository.deleteAll();
//    }
//
//    @Override
//    public void flush() {
//        memberRepository.flush();
//    }
//
//    @Override
//    public <S extends Member> S saveAndFlush(S entity) {
//        return memberRepository.saveAndFlush(entity);
//    }
//
//    @Override
//    public <S extends Member> List<S> saveAllAndFlush(Iterable<S> entities) {
//        return memberRepository.saveAllAndFlush(entities);
//    }
//
//    @Override
//    public void deleteAllInBatch(Iterable<Member> entities) {
//        memberRepository.deleteAllInBatch(entities);
//    }
//
//    @Override
//    public void deleteAllByIdInBatch(Iterable<Long> longs) {
//        memberRepository.deleteAllByIdInBatch(longs);
//    }
//
//    @Override
//    public void deleteAllInBatch() {
//        memberRepository.deleteAllInBatch();
//    }
//
//    @Override
//    public Member getReferenceById(Long aLong) {
//        return memberRepository.getReferenceById(aLong);
//    }
}
