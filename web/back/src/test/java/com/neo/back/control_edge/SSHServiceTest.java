package com.neo.back.control_edge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import com.neo.back.control_edge.service.EdgeServer;
import com.neo.back.control_edge.service.SSHService;

import io.github.cdimascio.dotenv.Dotenv;


@JsonTest
public class SSHServiceTest {

	// SSHService 단위 테스트 : 한 엣지 서버에 대한 출력 예시 (디버그를 통해 확인 가능)
    @Test
	void getEdgeServerData(){

		Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> {
            // .env 파일의 키를 Spring 프로퍼티 명명 규칙에 맞게 변환
            // 예: SPRING_DATASOURCE_URL -> spring.datasource.url
            String propName = entry.getKey().toLowerCase().replace('_', '.');
            System.setProperty(propName, entry.getValue());
			// System.out.println(entry.getKey() + ":" + entry.getValue());
        });

		String host = System.getProperty("naver.edgeserver.1.ip");
		String ID = System.getProperty("naver.edgeserver.1.id");
		String user = System.getProperty("naver.edgeserver.1.user.id");
		String password = System.getProperty("naver.edgeserver.1.password");	

		SSHService sshService = new SSHService();

		sshService.getDataOfEdgeServer(host, user, password,ID);

	}

	@Test
	void selectingEdgeServer(){

		Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> {
            // .env 파일의 키를 Spring 프로퍼티 명명 규칙에 맞게 변환
            // 예: SPRING_DATASOURCE_URL -> spring.datasource.url
            String propName = entry.getKey().toLowerCase().replace('_', '.');
            System.setProperty(propName, entry.getValue());
			// System.out.println(entry.getKey() + ":" + entry.getValue());
        });

		SSHService sshService = new SSHService();

		EdgeServer selecting = sshService.selectingEdgeServer();

		System.out.println("selectingEdgeServer of "+selecting.getEdgeServerID());
	}
}
