package com.udemy.adapter.cdn;

import com.udemy.adapter.cdn.vendor.cloudflare.CloudflareStreamClient;

import java.util.Objects;

public class CloudflareStreamAdapter implements CdnStreamAdapter {
    private final CloudflareStreamClient cloudflareClient;

    public CloudflareStreamAdapter(CloudflareStreamClient cloudflareClient) {
        this.cloudflareClient = Objects.requireNonNull(cloudflareClient, "cloudflareClient must not be null");
    }

    public CloudflareStreamAdapter(String customerSubdomain, String apiSecretToken) {
        this(new CloudflareStreamClient(customerSubdomain, apiSecretToken));
    }

    @Override
    public String generateSecureStreamUrl(String videoPathOrUid, String studentId, int expirySeconds) {
        return cloudflareClient.generateHlsManifestWithToken(videoPathOrUid, studentId, expirySeconds);
    }

    @Override
    public String getProviderName() {
        return "CLOUDFLARE_STREAM";
    }
}
