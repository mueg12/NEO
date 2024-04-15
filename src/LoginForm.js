import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function LoginForm({ onLogin }) {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const correctUsername = 'user';
  const correctPassword = '123456';

  const handleSubmit = (event) => {
    event.preventDefault();
    if (username === correctUsername && password === correctPassword) {
      navigate('/');
    } else {
      alert('다시 입력하세요!');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>ID:</label>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
      </div>
      <div>
        <label>PASSWORD:</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <button type="submit">로그인</button>
    </form>
  );
}

export default LoginForm;
