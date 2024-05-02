package com.neo.back.docker.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor // Lombok 기본 생성자 생성 어노테이션
@Getter
@Setter
@ToString
public class GameServerSettingDto {

   private Long userId;

   private String containerId;

   // 모든 필드를 매개변수로 하는 생성자
   public GameServerSettingDto(Long userId, String containerId) {
      this.userId = userId;
      this.containerId = containerId;
   }

}
