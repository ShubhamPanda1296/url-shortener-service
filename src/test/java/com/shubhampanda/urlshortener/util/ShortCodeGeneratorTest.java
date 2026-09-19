package com.shubhampanda.urlshortener.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShortCodeGeneratorTest {

    @Test
    void generatesSixCharacterAlphanumericCodes() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        for (int i = 0; i < 1000; i++) {
            assertThat(generator.generate()).hasSize(6).matches("[A-Za-z0-9]{6}");
        }
    }
}
