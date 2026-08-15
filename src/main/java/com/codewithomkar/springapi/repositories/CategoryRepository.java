package com.codewithomkar.springapi.repositories;

import com.codewithomkar.springapi.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}