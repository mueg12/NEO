package com.neo.back.docker.repository;
import com.neo.back.docker.entity.MinecreftServerSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MinecreftServerSettingRepository extends JpaRepository<MinecreftServerSetting, Long> {

    Optional<MinecreftServerSetting> findById(Long id);

}
