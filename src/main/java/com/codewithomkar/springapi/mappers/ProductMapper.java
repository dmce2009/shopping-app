package com.codewithomkar.springapi.mappers;

import com.codewithomkar.springapi.dtos.ProductDTO;
import com.codewithomkar.springapi.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "id", ignore = true)
    void updateProduct(ProductDTO productDTO, @MappingTarget Product product);
}
