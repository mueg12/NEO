import React from 'react';
import { Link } from 'react-router-dom';
import Header from './Header';

function startGameServer() {
  const url = 'http://your-backend-server.com/start-game-server';

  fetch(url, {
    method: 'POST', 
  })
  .then(response => {
    if (response.ok) {
      return response.json();
    } else {
      throw new Error('Something went wrong with the server start request.');
    }
  })
  .then(data => {
    console.log('Server started successfully:', data);
  })
  .catch(error => {
    console.error('Error starting the server:', error);
  });
}


function ServerDashboard() {
  return (
    <div
  style={{
    width: 1920,
    height: 1080,
    position: "relative",
    overflow: "hidden",
    background: "#fff",
  }}
>
  <div
    style={{
      width: 1920,
      height: 1080,
      position: "absolute",
      left: "-1px",
      top: "-1px",
      background: "rgba(0,0,0,0.9)",
    }}
  /><Header />
  <div style={{ width: 1277, height: 100 }}>
    <div
      style={{
        width: 1277,
        height: 100,
        position: "absolute",
        left: "520.5px",
        top: "439.5px",
        borderRadius: 10,
        background: "#d9d9d9",
      }}
    />
    <div
      style={{
        width: 40,
        height: 40,
        position: "absolute",
        left: "1508.5px",
        top: "466.5px",
        borderRadius: 10,
        borderWidth: 3,
        borderColor: "#000",
      }}
    />
    <div
      style={{
        width: 150,
        height: 80,
        position: "absolute",
        left: "1268.5px",
        top: "452.5px",
        borderRadius: 30,
        background: "rgba(87,81,81,0.5)",
      }}
    />
    <div
      style={{
        width: 150,
        height: 80,
        position: "absolute",
        left: "1054.5px",
        top: "452.5px",
        borderRadius: 30,
        background: "rgba(87,81,81,0.5)",
      }}
    />
    <p
      style={{
        position: "absolute",
        left: 554,
        top: 459,
        fontSize: 50,
        textAlign: "center",
        color: "#171515",
      }}
    >
      해상도
    </p>
    <p
      style={{
        position: "absolute",
        left: 1223,
        top: 461,
        fontSize: 50,
        textAlign: "center",
        color: "#000",
      }}
    >
      x
    </p>
    <p
      style={{
        position: "absolute",
        left: 1568,
        top: 459,
        fontSize: 50,
        textAlign: "center",
        color: "#000",
      }}
    >
      전체 화면
    </p>
  </div>
  <div style={{ width: 280, height: 123 }}>
    <div
      style={{
        width: 280,
        height: 123,
        position: "absolute",
        left: "1508.5px",
        top: "887.5px",
        borderRadius: 10,
        background: "#d9d9d9",
      }}
    />
    <p
      style={{
        position: "absolute",
        left: 1547,
        top: 845,
        fontSize: 50,
        textAlign: "center",
        color: "#000",
      }}
      onClick={startGameServer}
    >
      게임시작
    </p>
    <p
      style={{
        position: "absolute",
        left: 1500,
        top: 920,
        fontSize: 35,
        textAlign: "center",
        color: "#000",
      }}
    >
      버전(백에서 끌어오기)
    </p>
   
    
  </div>
  <div style={{ width: 439, height: 850 }}>
    <div
      style={{
        width: 439,
        height: 850,
        position: "absolute",
        left: "45.5px",
        top: "183.5px",
        borderRadius: 10,
        background: "#d9d9d9",
      }}
    />
    <div style={{ width: 322, height: 179 }}>
      <p
        style={{
          position: "absolute",
          left: 105,
          top: 332,
          fontSize: 50,
          textAlign: "center",
          color: "#000",
        }}
      >
        모드 추가
      </p>
      <p
        style={{
          position: "absolute",
          left: 105,
          top: 450,
          fontSize: 50,
          textAlign: "center",
          color: "#000",
        }}
      >
        화면 설정
      </p>
      <div
        style={{
          width: 60,
          height: 60,
          position: "absolute",
          left: "365.5px",
          top: "330.5px",
          borderWidth: 3,
          borderColor: "#000",
        }}
      />
    </div>
  </div>
  <div style={{ width: 1277, height: 100 }}>
    <div
      style={{
        width: 1277,
        height: 100,
        position: "absolute",
        left: "520.5px",
        top: "183.5px",
        borderRadius: 10,
        background: "#d9d9d9",
      }}
    />
  </div>
  <div style={{ width: 1277, height: 100 }}>
    <div
      style={{
        width: 1277,
        height: 100,
        position: "absolute",
        left: "520.5px",
        top: "311.5px",
        borderRadius: 10,
        background: "#d9d9d9",
      }}
    />
    <p
      style={{
        position: "absolute",
        left: 559,
        top: 332,
        fontSize: 50,
        
        textAlign: "center",
        color: "#000",
      }}
    >
      모드 상태 창, 모드 사용하지 않는다면 색깔 다르게 표시
    </p>
  </div>
</div>
  );
}

export default ServerDashboard;