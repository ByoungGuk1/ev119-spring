package com.app.ev119.repository;

import com.app.ev119.domain.entity.MemberSocial;
import com.app.ev119.domain.type.SocialType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class MemberSocialRepositoryAdapter implements MemberSocialRepository {
    @Override
    public MemberSocial save(MemberSocial memberSocial) {
        return null;
    }

    @Override
    public List<SocialType> findMemberSocialProvidersById(Long memberId) {
        return List.of();
    }

    @Override
    public Long findMemberIdByProvider(SocialType provider, String providerId) {
        return 0L;
    }

    @Override
    public List<MemberSocial> findByMemberId(Long memberId) {
        return List.of();
    }

    @Override
    public void deleteByMemberId(Long memberId) {

    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends MemberSocial> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends MemberSocial> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<MemberSocial> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public MemberSocial getOne(Long aLong) {
        return null;
    }

    @Override
    public MemberSocial getById(Long aLong) {
        return null;
    }

    @Override
    public MemberSocial getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends MemberSocial> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends MemberSocial> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends MemberSocial> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends MemberSocial> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends MemberSocial> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends MemberSocial> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends MemberSocial, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends MemberSocial> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<MemberSocial> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<MemberSocial> findAll() {
        return List.of();
    }

    @Override
    public List<MemberSocial> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(MemberSocial entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends MemberSocial> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<MemberSocial> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<MemberSocial> findAll(Pageable pageable) {
        return null;
    }
}
