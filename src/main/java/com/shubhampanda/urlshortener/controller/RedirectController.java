package com.shubhampanda.urlshortener.controller;

import java.net.URI;

import com.shubhampanda.urlshortener.service.UrlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;

import com.shubhampanda.urlshortener.dto.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {

    private final UrlService service;

    public RedirectController(UrlService service) {
        this.service = service;
    }

    @Operation(summary = "Resolve a short URL", description = "Redirects to the original URL associated with the short code.")
    @ApiResponse(responseCode = "302", description = "Redirect to the original URL", content = @Content,
            headers = @Header(name = "Location", description = "Original URL", schema = @Schema(type = "string", format = "uri")))
    @ApiResponse(responseCode = "404", description = "Short code not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = service.resolve(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
