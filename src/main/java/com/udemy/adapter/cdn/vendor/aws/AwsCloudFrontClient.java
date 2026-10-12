package com.udemy.adapter.cdn.vendor.aws;

public class AwsCloudFrontClient {
    private final String distributionDomain;
    private final String keyPairId;

    public AwsCloudFrontClient(String distributionDomain, String keyPairId) {
        this.distributionDomain = distributionDomain;
        this.keyPairId = keyPairId;
    }

    public String signUrlWithKeyPair(String s3ObjectPath, long expiryEpochSeconds, String signaturePolicy) {
        return "https://" + distributionDomain + "/" + s3ObjectPath +
                "?Key-Pair-Id=" + keyPairId +
                "&Expires=" + expiryEpochSeconds +
                "&Signature=" + Integer.toHexString((s3ObjectPath + keyPairId).hashCode());
    }

    public String getDistributionDomain() {
        return distributionDomain;
    }
}
