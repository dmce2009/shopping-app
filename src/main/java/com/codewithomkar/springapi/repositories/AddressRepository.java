package com.codewithomkar.springapi.repositories;

import com.codewithomkar.springapi.entities.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}