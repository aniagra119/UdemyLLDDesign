package com.udemy.adapter.cdn;

import com.udemy.adapter.cdn.vendor.aws.AwsCloudFrontClient;

import java.time.Instant;
import java.util.Objects;

public class AwsCloudFrontAdapter implements CdnStreamAdapter {
    private final AwsCloudFrontClient cloudFrontClient;

    public AwsCloudFrontAdapter(AwsCloudFrontClient cloudFrontClient) {
        this.cloudFrontClient = Objects.requireNonNull(cloudFrontClient, "cloudFrontClient must not be null");
    }

    public AwsCloudFrontAdapter(String distributionDomain, String keyPairId) {
        this(new AwsCloudFrontClient(distributionDomain, keyPairId));
    }

    @Override
    public String generateSecureStreamUrl(String videoPathOrUid, String studentId, int expirySeconds) {
        long expiryEpoch = Instant.now().getEpochSecond() + expirySeconds;
        return cloudFrontClient.signUrlWithKeyPair(videoPathOrUid, expiryEpoch, "custom-policy-" + studentId);
    }

    @Override
    public String getProviderName() {
        return "AWS_CLOUDFRONT";
    }
}
