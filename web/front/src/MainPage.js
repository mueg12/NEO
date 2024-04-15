import React from 'react';
import { Link } from 'react-router-dom';

function MainPage() {
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
  <div
    style={{
      width: 450,
      height: 325,
      position: "absolute",
      left: 1454,
      top: 734,
      borderRadius: 30,
      background: "linear-gradient(to bottom, #d74949 0%, #712626 100%)",
    }}
  />
  <img
    src="image-3.png"
    style={{
      width: 450,
      height: 325,
      position: "absolute",
      left: 974,
      top: 734,
      borderRadius: 30,
      objectFit: "cover",
    }}
  />
  <img
    src="image-2.png"
    style={{
      width: 450,
      height: 325,
      position: "absolute",
      left: 494,
      top: 734,
      borderRadius: 30,
      objectFit: "cover",
    }}
  />
  <img
    src="image-1.png"
    style={{
      width: 450,
      height: 325,
      position: "absolute",
      left: 14,
      top: 734,
      borderRadius: 30,
      objectFit: "cover",
    }}
  />
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
  <div style={{ width: 300, height: 80 }}>
    <div
      style={{
        width: 300,
        height: 80,
        position: "absolute",
        left: "465.5px",
        top: "586.5px",
        borderRadius: 5,
        borderWidth: 3,
        borderColor: "rgba(255,255,255,0.9)",
      }}
    />
    <p
      style={{
        position: "absolute",
        left: 501,
        top: 604,
        fontSize: 40,
        fontWeight: 600,
        textAlign: "center",
        color: "rgba(255,255,255,0.9)",
      }}
    >
      서버 참여하기
    </p>
  </div>
  <div style={{ width: 300, height: 80 }}>
    <div
      style={{
        width: 300,
        height: 80,
        position: "absolute",
        left: "95.5px",
        top: "586.5px",
        borderRadius: 5,
        borderWidth: 3,
        borderColor: "rgba(255,255,255,0.9)",
      }}
    />
    <p
      style={{
        position: "absolute",
        left: 150,
        top: 604,
        fontSize: 40,
        fontWeight: 600,
        textAlign: "center",
        color: "rgba(255,255,255,0.9)",
      }}
    >
      서버 만들기
    </p>
  </div>
  <p
    style={{
      position: "absolute",
      left: 108,
      top: 334,
      fontSize: 65,
      fontWeight: 600,
      textAlign: "center",
      color: "rgba(255,255,255,0.97)",
    }}
  >
    모든 게이머를 위한 완벽한 공간
  </p>
  <div
    style={{
      width: 100,
      height: 100,
      position: "absolute",
      left: 786,
      top: 926,
      overflow: "hidden",
    }}
  />
</div>
  );
}

export default MainPage;