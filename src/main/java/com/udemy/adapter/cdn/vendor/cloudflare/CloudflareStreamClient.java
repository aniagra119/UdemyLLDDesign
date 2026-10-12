package com.udemy.adapter.cdn.vendor.cloudflare;

public class CloudflareStreamClient {
    private final String customerSubdomain;
    private final String apiSecretToken;

    public CloudflareStreamClient(String customerSubdomain, String apiSecretToken) {
        this.customerSubdomain = customerSubdomain;
        this.apiSecretToken = apiSecretToken;
    }

    public String generateHlsManifestWithToken(String mediaUid, String userIdentifier, int ttlSeconds) {
        String token = Integer.toHexString((mediaUid + userIdentifier + apiSecretToken).hashCode());
        return "https://videodelivery.net/" + mediaUid + "/manifest/video.m3u8?token=" + token + "&user=" + userIdentifier;
    }

    public String getCustomerSubdomain() {
        return customerSubdomain;
    }
}
