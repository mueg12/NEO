import React from 'react';
//import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainPage from './MainPage';
import Login from './Login';
import UpdateTest from './UpdateTest';
import './App.css';
//import LoginForm from './LoginForm';

function App() {
  return (    
    <Router>
      <div className="App">
        {/* route rule */}
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/Update" element={<UpdateTest />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
