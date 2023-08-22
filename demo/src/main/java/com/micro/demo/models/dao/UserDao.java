package com.micro.demo.models.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.micro.demo.models.entity.UserEntity;

public interface UserDao extends CrudRepository<UserEntity, Long> {
    
    List<UserEntity> findByEmailContains(String email);
    UserEntity findFirstByEmail(String email);

}
