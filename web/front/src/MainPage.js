import React from 'react';
import { Link } from 'react-router-dom';

function MainPage() {
  return (
    <div className="main-page">
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
        height: 720,
        position: "absolute",
        left: 0,
        top: 1140,
        overflow: "hidden",
        background: "#fff",
      }}
    />
    <div
      style={{
        width: 1920,
        height: 720,
        position: "absolute",
        left: 0,
        top: 1140,
        overflow: "hidden",
        background: "#fff",
      }}
    />
    <div
      style={{
        width: 1920,
        height: 720,
        position: "absolute",
        left: 0,
        top: 1140,
        overflow: "hidden",
        background: "#fff",
      }}
    >
      <p
        style={{
          position: "absolute",
          left: 713,
          top: 176,
          fontSize: 55,
          fontWeight: 600,
          textAlign: "center",
          color: "#000",
        }}
      >
        马卡巴卡，阿妈哈马
      </p>
    </div>
    <div
      style={{
        width: 1920,
        height: 90,
        position: "absolute",
        left: 0,
        top: 0,
        overflow: "hidden",
        background: "#fff",
      }}
    >
      <div
        style={{
          width: 1920,
          height: 90,
          position: "absolute",
          left: "-1px",
          top: "-1px",
          background: "linear-gradient(to right, #d7aeff 0%, #816999 100%)",
        }}
      />
    </div>
    <p
      style={{
        position: "absolute",
        left: 1750,
        top: -20,
        fontSize: 40,
        fontWeight: 600,
        textAlign: "center",
        color: "#fff",
      }}
    >
      <Link to="/login">로그인</Link>
    </p>
  </div>
  </div>
  );
}

export default MainPage;