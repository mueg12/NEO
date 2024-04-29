package com.neo.back.docker.service;

import com.neo.back.docker.entity.GameServerSetting;
import com.neo.back.docker.repository.GameServerSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServerPropertyService {

    private final GameServerSettingRepository repository;

    public GameServerPropertyService(GameServerSettingRepository repository) {
        this.repository = repository;
    }

    public GameServerSetting save(GameServerSetting property) {
        return repository.save(property);
    }

    public GameServerSetting loadSettings() {
        // 처음 데이터 반환하게 함.
        // 더미 데이터 일단은 넣어두고 테스트
        return repository.findById(1L).orElseThrow(() -> new RuntimeException("Settings not found"));
    }

}
