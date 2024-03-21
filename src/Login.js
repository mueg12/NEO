import React from 'react';
import LoginForm from './LoginForm';
import login from './assets/login.png';
import lock from './assets/lock.png';

function Login() {
    const handleLogin = (username, password) => {
        console.log('로그인', username, password);
      };
    
      return (
        <div className="App">
          <h2>Log in to your account</h2>
          <LoginForm onLogin={handleLogin} />
        </div>
      );
    }

export default Login;