package com.app.ev119.oauth2;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class NaverMemberInfo implements OAuth2MemberInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "naver";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getResponse() {
        Object resp = attributes.get("response");
        if (resp instanceof Map) {
            return (Map<String, Object>) resp;
        }
        return attributes;
    }

    @Override
    public String getProviderId() {
        Map<String, Object> response = getResponse();
        Object id = response.get("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = getResponse();
        return (String) response.get("email");
    }

    @Override
    public String getName() {
        Map<String, Object> response = getResponse();
        return (String) response.get("name");
    }

    @Override
    public String getMobNo() {
        Map<String, Object> response = getResponse();
        return (String) response.get("mobile");
    }
}
