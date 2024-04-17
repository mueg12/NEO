import React from 'react';
import { useState ,useRef } from 'react';

function UpdateTest() {
    const [usedSpace, setUsedSpace] = useState(0);
    const [fileList, setFileList] = useState([]);
    const latestFile = fileList[fileList.length - 1];
    const fileInputRef = useRef(null);
    const handleFileUploadClick = () => {
      fileInputRef.current.click();
    };
    const totalSpace = 8;
    const usedSpaceInGB = usedSpace;
    const usedPercentage = (usedSpaceInGB / totalSpace) * 100;
    const [files, setFiles] = useState([]);


    const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    const fileSizeInGB = file.size / (1024 ** 3);
    setUsedSpace((prevUsedSpace) => prevUsedSpace + fileSizeInGB);

    setFileList((prevFileList) => [...prevFileList, file]);
    
    // 上传文件到服务器的代码...
  }
};

   return (
    <div className="Update-page">
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
  <div style={{ width: 295, height: 92 }}>
    <div
      style={{
        width: 295,
        height: 92,
        position: "absolute",
        left: "1411.5px",
        top: "37.5px",
        borderRadius: 10,
        background: "#d9d9d9",
      }}
    />
    <p
      style={{
        cursor: 'pointer',
        position: "absolute",
        left: 1450,
        top: 10,
        fontSize: 45,
        textAlign: "center",
        color: "#000",
      }}
      onClick={handleFileUploadClick}
    >
      파일 업로드
    </p>
    <input
          type="file"
          style={{ display: 'none' }}
          ref={fileInputRef}
          onChange={handleFileChange}
        />
  </div>
  <div style={{ width: 1277, height: 100 }}>
    <div
      style={{
        width: 1277,
        height: 100,
        position: "absolute",
        left: "76.5px",
        top: "37.5px",
        borderRadius: 10,
        background: "#d9d9d9",
      }}
    />
    <p
      style={{
        position: "absolute",
        left: 106,
        top: 0,
        fontSize: 50,
        textAlign: "center",
        color: "#171515",
      }}
    >
      내파일
    </p>
    <p
      style={{
        position: "absolute",
        left: 1120,
        top: 0,
        fontSize: 50,
        textAlign: "center",
        color: "#000",
      }}
    >
      {`${usedSpace.toFixed(2)}G/8G`}
    </p>
    <div
      style={{
        width: 758,
        height: 80,
        position: "absolute",
        left: "292.5px",
        top: "47.5px",
        borderRadius: 30,
        background: `linear-gradient(to right, rgba(0,0,0,0.5) ${usedPercentage}%, rgba(255,255,255,0.5) ${usedPercentage}%)`,
      }}
    />
  </div>
  <div style={{ width: 1374, height: 116 }}>
    <div
      style={{
        width: 1374,
        height: 116,
        position: "absolute",
        left: "76.5px",
        top: "209.5px",
        borderRadius: 10,
        background: "#d9d9d9",
      }}
    />
    <div
      style={{
        width: 69,
        height: 61,
        position: "absolute",
        left: "100.5px",
        top: "236.5px",
        background: "rgba(253,253,253,0.7)",
      }}
    />
    <img
      src="모드-아이콘.png"
      style={{
        width: 100,
        height: "95.86px",
        position: "absolute",
        left: "85.5px",
        top: "219.5px",
        objectFit: "cover",
      }}
    />
    <p
      style={{
        position: "absolute",
        left: 204,
        top: 245,
        fontSize: 30,
        textAlign: "center",
        color: "rgba(0,0,0,0.4)",
      }}
    >
      {latestFile ? `${(latestFile.size / (1024 ** 2)).toFixed(2)} MB - ${latestFile.type}` : '설명'}
    </p>
    <p
      style={{
        position: "absolute",
        left: 204,
        top: 195,
        fontSize: 30,
        textAlign: "center",
        color: "#000",
      }}
    >
       {latestFile ? latestFile.name : '파일이름'}
    </p>
    <p
      style={{
        position: "absolute",
        left: 1323,
        top: 205,
        fontSize: 35,
        textAlign: "center",
        color: "#000",
      }}
    >
      사용
    </p>
    <div
      style={{
        width: 40,
        height: 40,
        position: "absolute",
        left: "1267.5px",
        top: "248.5px",
        borderRadius: 10,
        borderWidth: 3,
        borderColor: "#000",
      }}
    />
  </div>
  </div>
  </div>
    ); 
} 

export default UpdateTest;