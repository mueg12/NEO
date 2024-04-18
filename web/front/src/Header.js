import React from 'react';
import { Link } from 'react-router-dom';

// Header
const Header = () => {
  return (
    <header style={{ width: '100%', height: 120, position: 'relative' }}>
      <div
        style={{
          width: '100%',
          height: 120,
          position: "absolute",
          left: 0,
          top: 0,
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
          userSelect: "none",
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
          userSelect: "none",
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
          userSelect: "none",
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
        <Link 
          to="/MainPage"
          style={{
            textDecoration: 'none',
            color: 'inherit',
            cursor: 'pointer',
          }}
        >NEO</Link>
      </p>
    </header>
  );
};

export default Header;
