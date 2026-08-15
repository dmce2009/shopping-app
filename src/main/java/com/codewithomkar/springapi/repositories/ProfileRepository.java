package com.codewithomkar.springapi.repositories;

import com.codewithomkar.springapi.entities.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}