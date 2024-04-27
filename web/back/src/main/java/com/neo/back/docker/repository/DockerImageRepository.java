package com.neo.back.docker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neo.back.docker.entity.DockerImage;
import java.util.List;
import com.neo.back.springjwt.entity.User;


@Repository
public interface DockerImageRepository extends JpaRepository<DockerImage, Long> {
    List<DockerImage> findByUser(User user);
}
