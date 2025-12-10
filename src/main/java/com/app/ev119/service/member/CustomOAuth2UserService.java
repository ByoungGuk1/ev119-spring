package com.app.ev119.service.member;

import com.app.ev119.domain.entity.Member;
import com.app.ev119.domain.entity.MemberSocial;
import com.app.ev119.domain.type.SocialType;
import com.app.ev119.oauth2.OAuth2MemberInfo;
import com.app.ev119.oauth2.OAuth2MemberInfoFactory;
import com.app.ev119.repository.MemberRepository;
import com.app.ev119.repository.MemberSocialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final MemberSocialRepository memberSocialRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google, naver, kakao ..
        String userNameAttributeName =
                userRequest.getClientRegistration()
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();


        OAuth2MemberInfo oAuth2MemberInfo = OAuth2MemberInfoFactory.of(registrationId, attributes);


        Member member = saveOrUpdate(oAuth2MemberInfo);


        Collection<? extends GrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER"));


        return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
    }

    @Transactional
    protected Member saveOrUpdate(OAuth2MemberInfo info) {
        String provider = info.getProvider();
        String providerId = info.getProviderId();
        String email = info.getEmail();

        if (provider == null || providerId == null) {
            log.warn("소셜 로그인 필수 정보 부족: provider={}, providerId={}", provider, providerId);
            throw new IllegalArgumentException("소셜 로그인 필수 정보가 부족합니다.");
        }

        SocialType socialType = SocialType.from(provider);


        Optional<MemberSocial> existingSocialOpt =
                memberSocialRepository.findByProviderAndProviderId(socialType, providerId);

        if (existingSocialOpt.isPresent()) {

            return existingSocialOpt.get().getMember();
        }

        Member member;

        if (email != null) {
            member = memberRepository.findByMemberEmail(email)
                    .orElseGet(() -> createSocialMember(info));
        } else {
            log.warn("소셜 로그인에 이메일이 없습니다. provider={}, providerId={}", provider, providerId);
            member = createSocialMember(info);
        }


        MemberSocial memberSocial = MemberSocial.builder()
                .member(member)
                .memberSocialProvider(socialType)
                .memberSocialProviderId(providerId)
                .build();

        memberSocialRepository.save(memberSocial);

        return member;
    }

    private Member createSocialMember(OAuth2MemberInfo memberInfo) {

        if (memberInfo.getEmail() == null) {
            log.warn("소셜 회원 생성 실패: 이메일 없음. provider={}, providerId={}",
                    memberInfo.getProvider(), memberInfo.getProviderId());
            throw new IllegalArgumentException("이메일 제공에 동의해야 소셜 로그인을 사용할 수 있습니다.");
        }

        Member member = Member.builder()
                .memberEmail(memberInfo.getEmail())
                .memberPassword(null)
                .memberName(memberInfo.getName())
                .build();

        return memberRepository.save(member);
    }

}
