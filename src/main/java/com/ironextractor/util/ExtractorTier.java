package com.ironextractor.util;

public enum ExtractorTier {
    BASIC("basic_extractor"),
    ADVANCED("advanced_extractor"),
    EXPERT("expert_extractor");

    public final String id;

    ExtractorTier(String id) {
        this.id = id;
    }
}
