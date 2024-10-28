import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // 페이지 이동을 위한 훅
import '../styles/login.css';

function Login() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    if (id && password) {
      try {
        const response = await fetch('/api/login', {  // 서버 주소를 프록시 설정에 따라 상대 경로로 변경
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            id: id,
            password: password,
          }),
        });

        if (response.ok) {
          const result = await response.json();
          setMessage(result.message || '로그인 성공!');
          localStorage.setItem('isLoggedIn', true); // 로그인 상태를 localStorage에 저장
          navigate('/'); // 홈으로 이동
        } else {
          setMessage('아이디 또는 패스워드가 잘못되었습니다.');
        }
      } catch (error) {
        setMessage('서버 오류가 발생했습니다.');
      }
    } else {
      setMessage('아이디와 패스워드를 입력해주세요.');
    }
  };

  return (
    <main>
      <h2>로그인</h2>
      <input
        type="text"
        placeholder="아이디"
        value={id}
        onChange={(e) => setId(e.target.value)}
      />
      <input
        type="password"
        placeholder="패스워드"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button onClick={handleLogin}>로그인</button>
      <p>{message}</p>
    </main>
  );
}

export default Login;




