package com.shubhampanda.urlshortener.repository;

import com.shubhampanda.urlshortener.entity.Url;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsByShortCode(String shortCode);
}
