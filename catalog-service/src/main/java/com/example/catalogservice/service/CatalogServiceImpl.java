package com.example.catalogservice.service;

import com.example.catalogservice.service.jpa.CatalogEntity;
import com.example.catalogservice.service.jpa.CatalogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CatalogServiceImpl implements CatalogService{

    CatalogRepository catalogRepository;
    Environment env;

    @Autowired
    public CatalogServiceImpl(CatalogRepository catalogRepository, Environment env) {
        this.catalogRepository=catalogRepository;
        this.env=env;
    }

    @Override
    public Iterable<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }
}
