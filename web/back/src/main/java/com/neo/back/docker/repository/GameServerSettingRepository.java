package com.neo.back.docker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neo.back.docker.entity.GameServerSetting;

@Repository
public interface GameServerSettingRepository extends JpaRepository <GameServerSetting, String> {
    
}
