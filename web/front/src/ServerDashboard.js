import React from 'react';
import { Link } from 'react-router-dom';



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
  />
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
  <div style={{ width: 330, height: 123 }}>
    <div
      style={{
        width: 330,
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
        left: 1590,
        top: 920,
        fontSize: 35,
        textAlign: "center",
        color: "#000",
      }}
    >
      v1.20.4
    </p>
    <svg
      width={2}
      height={123}
      viewBox="0 0 2 123"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      style={{ position: "absolute", left: 1785, top: 887 }}
      preserveAspectRatio="none"
    >
      <line x1={1} x2={1} y2={123} stroke="black" stroke-opacity="0.6" stroke-width={2} />
    </svg>
    <svg
      width={26}
      height={21}
      viewBox="0 0 26 21"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      style={{ position: "absolute", left: "1797.5px", top: "937.5px" }}
      preserveAspectRatio="xMidYMid meet"
    >
      <path d="M13 0L25.1244 21H0.875645L13 0Z" fill="black" fill-opacity="0.6" />
    </svg>
  </div>
  <div style={{ width: 1920, height: 120 }}>
    <div
      style={{
        width: 1920,
        height: 120,
        position: "absolute",
        left: "-0.5px",
        top: "-0.5px",
        background: "#3e3535",
      }}
    />
    <p
      style={{
        position: "absolute",
        left: 546,
        top: 0,
        fontSize: 40,
        textAlign: "left",
        color: "#fff",
      }}
    >
      게임목록 조회
    </p>
    <p
      style={{
        position: "absolute",
        left: 323,
        top: 0,
        fontSize: 40,
        textAlign: "left",
        color: "#fff",
      }}
    >
      게임 조회
    </p>
    <p
      style={{
        position: "absolute",
        left: 1740,
        top: -10,
        fontSize: 45,
        fontWeight: 600,
        textAlign: "center",
        color: "#fff",
      }}
    >
      Menu
    </p>
    <p
      style={{
        position: "absolute",
        left: 97,
        top: -40,
        fontSize: 60,
        fontWeight: 600,
        textAlign: "center",
        color: "#fff",
      }}
    >
      NEO
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