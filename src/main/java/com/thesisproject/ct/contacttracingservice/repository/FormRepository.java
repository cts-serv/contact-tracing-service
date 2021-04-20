package com.thesisproject.ct.contacttracingservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thesisproject.ct.contacttracingservice.entity.FormEntity;

@Repository
public interface FormRepository extends JpaRepository<FormEntity, UUID>{

}
