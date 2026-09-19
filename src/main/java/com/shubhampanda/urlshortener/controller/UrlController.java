package com.shubhampanda.urlshortener.controller;

import com.shubhampanda.urlshortener.dto.CreateUrlRequest;
import com.shubhampanda.urlshortener.dto.CreateUrlResponse;
import com.shubhampanda.urlshortener.service.UrlService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @Operation(summary = "Create a short URL", description = "Stores an HTTP or HTTPS URL and returns its short code.")
    @ApiResponse(responseCode = "201", description = "Short URL created")
    @ApiResponse(responseCode = "400", description = "Missing or invalid URL, or malformed request body", content = @Content)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUrlResponse create(@Valid @RequestBody CreateUrlRequest request) {
        return service.create(request.url());
    }
}
