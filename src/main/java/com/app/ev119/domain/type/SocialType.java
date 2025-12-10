package com.app.ev119.domain.type;

public enum SocialType {
    LOCAL,
    GOOGLE,
    KAKAO,
    NAVER;

    public static SocialType from(String provider) {
        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("Social provider가 비어있습니다.");
        }

        try {
            return SocialType.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 Social provider입니다: " + provider);
        }
    }
}
