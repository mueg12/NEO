package com.neo.back.springjwt.repository;

import com.neo.back.springjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    //jpa 구문 존재하는지
    Boolean existsByUsername(String username);


    UserEntity save(UserEntity user);
    Optional<UserEntity> findById(Long id);
    UserEntity findByUsername(String username);
    List<UserEntity> findAll();

}
