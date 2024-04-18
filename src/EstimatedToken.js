import React, { useState } from 'react';
import Header from './Header';
import { Link } from 'react-router-dom';

function EstimatedToken() {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedVersion, setSelectedVersion] = useState('');

  const handleVersionSelect = (version) => {
    setSelectedVersion(version);
  };

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
      <Header />
      <div
        style={{
          width: 1200,
          height: 400,
          position: "absolute",
          left: 30,
          top: 582,
          borderRadius: 10,
          background: "#d9d9d9",
        }}
      />
      <div
        style={{
          width: 586,
          height: 802,
          position: "absolute",
          left: 1276,
          top: 135,
          borderRadius: 10,
          background: "#d9d9d9",
        }}
      />
      <div style={{ width: 1200, height: 400 }}>
        <div
          style={{
            width: 1200,
            height: 400,
            position: "absolute",
            left: "30.5px",
            top: "136.5px",
            borderRadius: 10,
            background: "#d9d9d9",
          }}
        />
      </div>
      <div style={{ width: 300, height: 80 }}>
        <div
          style={{
            width: 300,
            height: 80,
            position: "absolute",
            left: "1561.5px",
            top: "961.5px",
            borderRadius: 5,
            borderWidth: 3,
            borderColor: "rgba(white,255,255,0.9)",
            background: "red",
          }}
        />
        <p
          style={{
            position: "absolute",
            left: 1625,
            top: 932,
            fontSize: 40,
            fontWeight: 600,
            textAlign: "center",
            color: "rgba(255,255,255,0.9)",
            userSelect: 'none',
          }}
        >
          <Link 
          to={{
            pathname: "/ServerDashboard",
            state: { selectedVersion }
          }}
          style={{
            textDecoration: 'none',
            color: 'inherit',
            cursor: 'pointer',
          }}
        >서버 시작</Link>
        </p>
      </div>
      <div style={{ width: 531, height: 123 }}>
        <details
          style={{
            width: 531,
            height: 140,
            position: "absolute",
            left: "1307.5px",
            top: "172.5px",
            borderRadius: 10,
            background: "#d1aae9",
          }}
        >
          <summary style={{ cursor: "pointer", position: "absolute", left: "120px", top: "15px", fontSize: "45px", color: "#000", userSelect: "none" }} onClick={() => setIsOpen(!isOpen)}>
            버전 선택
          </summary>
          {isOpen && (
            <div
              style={{
                position: "absolute",
                left: "0px",
                top: "135px",
                width: "531px",
                height: "300px",
                background: "#d1aae9",
                borderRadius: "0px",
                boxShadow: "0px 0px 10px rgba(0,0,0,0.3)",
                zIndex: 1, 
              }}
            >
              {['v1.20.4', 'v1.19.2', 'v1.16.5'].map(version => (
                <p
                  key={version}
                  style={{
                    fontSize: "43px",
                    userSelect: "none",
                    lineHeight: "1",
                    cursor: "pointer",
                    margin: "20px 0",
                  }}
                  onClick={() => handleVersionSelect(version)}
                >
                  {version}
                </p>
              ))}            
            </div>
          )}
        </details>
      </div>
      {selectedVersion && (
        <p
          style={{
            position: "absolute",
            left: 1450,
            top: 230,
            fontSize: 30,
            fontWeight: 600,
            color: "#000",
            zIndex: 2,
          }}
        >
          선택한 버전: {selectedVersion}
        </p>
      )}
    </div>
  );
}

export default EstimatedToken;
