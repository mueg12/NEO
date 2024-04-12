package com.neo.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> {
            // .env 파일의 키를 Spring 프로퍼티 명명 규칙에 맞게 변환
            // 예: SPRING_DATASOURCE_URL -> spring.datasource.url
            String propName = entry.getKey().toLowerCase().replace('_', '.');
            System.setProperty(propName, entry.getValue());
        });
		SpringApplication.run(BackApplication.class, args);
	}

}
