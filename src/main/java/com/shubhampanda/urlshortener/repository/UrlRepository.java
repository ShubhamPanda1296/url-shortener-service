package com.shubhampanda.urlshortener.repository;

import java.util.Optional;

import com.shubhampanda.urlshortener.entity.Url;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsByShortCode(String shortCode);

    Optional<Url> findByShortCode(String shortCode);
}
