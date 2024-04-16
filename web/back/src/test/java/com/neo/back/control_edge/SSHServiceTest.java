package com.neo.back.control_edge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;

import com.neo.back.control_edge.service.EdgeServer;
import com.neo.back.control_edge.service.SSHService;
import com.neo.back.config.EnvConfig;


// import io.github.cdimascio.dotenv.Dotenv;


@JsonTest
@ContextConfiguration(classes = {EnvConfig.class})
public class SSHServiceTest {

	@Value("${edgeserver.1.ip}")String host;
	@Value("${edgeserver.1.id}")String ID;
	@Value("${edgeserver.user.id}")String user;
	@Value("${edgeserver.1.password}")String password;

	// SSHService 단위 테스트 : 한 엣지 서버에 대한 출력 예시 (디버그를 통해 확인 가능)
    @Test
	void getEdgeServerData(){

		SSHService sshService = new SSHService();

		sshService.getDataOfEdgeServer(host, user, password, ID);

	}
	// SSHService 단위 테스트 : 알고리즘에 맞는 엣지 서버 선택 출력 예시 (디버그를 통해 확인 가능)
	@Test
	void selectingEdgeServer(){

		// Dotenv dotenv = Dotenv.load();
        // dotenv.entries().forEach(entry -> {
        //     // .env 파일의 키를 Spring 프로퍼티 명명 규칙에 맞게 변환
        //     // 예: SPRING_DATASOURCE_URL -> spring.datasource.url
        //     String propName = entry.getKey().toLowerCase().replace('_', '.');
        //     System.setProperty(propName, entry.getValue());
		// 	// System.out.println(entry.getKey() + ":" + entry.getValue());
        // });

		SSHService sshService = new SSHService();

		EdgeServer selecting = sshService.selectingEdgeServer();
		if(selecting != null){
			System.out.println("selectingEdgeServer of "+selecting.getEdgeServerID());
		}
		else{
			System.out.println("selectingEdgeServer of NULL");
		}
	}
}
