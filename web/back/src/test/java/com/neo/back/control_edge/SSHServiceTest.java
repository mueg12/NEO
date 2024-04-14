package com.neo.back.control_edge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import com.neo.back.control_edge.service.SSHService;

import io.github.cdimascio.dotenv.Dotenv;


@JsonTest
public class SSHServiceTest {

	// SSHService 단위 테스트 : 한 엣지 서버에 대한 출력 예시 (디버그를 통해 확인 가능)
    @Test
	void getEdgeServerData(){

		Dotenv dotenv = Dotenv.load();

		String host = dotenv.get("NAVER_EDGESERVER_1_IP");
		String ID = dotenv.get("NAVER_EDGESERVER_1_ID");
		String user = dotenv.get("NAVER_EDGESERVER_1_USER_ID");
		String password = dotenv.get("NAVER_EDGESERVER_1_PASSWORD");	

		SSHService sshService = new SSHService();

		sshService.getDataOfEdgeServer(host, user, password,ID);

	}
}
