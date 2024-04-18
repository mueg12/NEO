import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import MainPage from './MainPage';
import Login from './Login';
import UpdateTest from './UpdateTest';
import './App.css';
import participants from './participants';
import SelectCapacity from './SelectCapacity';
import ServerDashboard from './ServerDashboard';
import EstimatedToken from './EstimatedToken';

function App() {
  return (    
    <Router>
      <div className="App">
        {/* route rule */}
        <Routes>
          <Route path="/MainPage" element={<MainPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/Update" element={<UpdateTest />} />
          <Route path="/participants" element={<participants />} />
          <Route path="/SelectCapacity" element={<SelectCapacity />} />
          <Route path="/ServerDashboard" element={<ServerDashboard />} />
          <Route path="/EstimatedToken" element={<EstimatedToken />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
