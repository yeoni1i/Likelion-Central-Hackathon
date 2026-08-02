package com.likelion.hackatonbe.domain.model.dto;
public record ModelManifestResponse(
        String version, String url, String sha256, long sizeBytes,
        double defaultThreshold, int featureSpecVersion, String channel,
        int sampleRateHz, long inferenceBudgetMs, String quantization
) {}
