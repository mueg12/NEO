package com.neo.back.springjwt.repository;

import com.neo.back.springjwt.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User,String> {

    // MongoDB를 위한 메소드 정의. 기본 CRUD 메소드는 MongoRepository에서 제공됨
    Boolean existsByUsername(String username);
    Optional<User> findById(String id); // ID 타입 변경에 따라 메소드 시그니처 수정
    Optional<User> findByUsername(String username); // 반환 타입을 Optional로 변경하여 일관성 유지

}
