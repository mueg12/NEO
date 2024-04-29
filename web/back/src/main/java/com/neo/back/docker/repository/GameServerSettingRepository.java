package com.neo.back.docker.repository;
import com.neo.back.docker.entity.GameServerSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameServerSettingRepository extends JpaRepository<GameServerSetting, Long> {



}
