package com.app.ev119.oauth2;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoMemberInfo implements OAuth2MemberInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        Object id = attributes.get("id"); // Long
        return id != null ? String.valueOf(id) : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getEmail() {
        Map<String, Object> account = getKakaoAccount();
        if (account == null) return null;
        return (String) account.get("email");
    }

    @Override
    public String getName() {
        Map<String, Object> account = getKakaoAccount();
        if (account == null) return null;

        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        if (profile == null) return null;

        return (String) profile.get("nickname");
    }

    @Override
    public String getMobNo() {
        return null;
    }
}
