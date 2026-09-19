package com.shubhampanda.urlshortener.service;

import com.shubhampanda.urlshortener.dto.CreateUrlResponse;
import com.shubhampanda.urlshortener.entity.Url;
import com.shubhampanda.urlshortener.exception.UrlNotFoundException;
import com.shubhampanda.urlshortener.repository.UrlRepository;
import com.shubhampanda.urlshortener.util.ShortCodeGenerator;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final ShortCodeGenerator codeGenerator;

    public UrlService(UrlRepository repository, ShortCodeGenerator codeGenerator) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
    }

    public String resolve(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(Url::getOriginalUrl)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
    }

    public CreateUrlResponse create(String originalUrl) {
        while (true) {
            String shortCode = codeGenerator.generate();
            try {
                // Each repository save has its own transaction, so a collision is
                // rolled back before the next attempt starts.
                Url saved = repository.saveAndFlush(new Url(originalUrl, shortCode));
                return new CreateUrlResponse(saved.getOriginalUrl(), saved.getShortCode());
            } catch (DataIntegrityViolationException exception) {
                if (!repository.existsByShortCode(shortCode)) {
                    throw exception;
                }
            }
        }
    }
}
