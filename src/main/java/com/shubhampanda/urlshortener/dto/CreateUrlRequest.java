package com.shubhampanda.urlshortener.dto;

import com.shubhampanda.urlshortener.validation.HttpUrl;

import jakarta.validation.constraints.NotBlank;

public record CreateUrlRequest(@NotBlank @HttpUrl String url) {
}
