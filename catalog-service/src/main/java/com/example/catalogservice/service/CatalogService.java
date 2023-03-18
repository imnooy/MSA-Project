package com.example.catalogservice.service;

import com.example.catalogservice.service.jpa.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalogs();
}
