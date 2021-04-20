package com.thesisproject.ct.contacttracingservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thesisproject.ct.contacttracingservice.entity.UserProfileEntity;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, UUID> {
	public List<UserProfileEntity> findAllByIdNumberLikeOrFirstNameLikeOrMiddleNameLikeOrLastNameLike(String idNumber, String firstName, String middleName, String lastName);
}
