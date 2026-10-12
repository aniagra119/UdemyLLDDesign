package com.udemy.adapter.cdn;

public interface CdnStreamAdapter {
    String generateSecureStreamUrl(String videoPathOrUid, String studentId, int expirySeconds);
    String getProviderName();
}
