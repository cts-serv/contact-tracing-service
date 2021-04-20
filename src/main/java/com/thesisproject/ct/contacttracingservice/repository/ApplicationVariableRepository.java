package com.thesisproject.ct.contacttracingservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thesisproject.ct.contacttracingservice.entity.ApplicationVariableEntity;

public interface ApplicationVariableRepository extends JpaRepository<ApplicationVariableEntity, UUID> {
	public List<ApplicationVariableEntity> findAllByVariableGroupAndEnabled(String variableGroup, boolean enabled);
	public void deleteAllByVariableGroup(String variableGroup);
}
