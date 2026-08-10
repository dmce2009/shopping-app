package com.codewithomkar.springapi.mappers;

import com.codewithomkar.springapi.dtos.RegisterUserRequest;
import com.codewithomkar.springapi.dtos.UpdateUserRequest;
import com.codewithomkar.springapi.dtos.UserDto;
import com.codewithomkar.springapi.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
//    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest userRequest);

    void update(UpdateUserRequest updateUserRequest,  @MappingTarget User user);
}
