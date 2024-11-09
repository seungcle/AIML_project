import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import '../styles/login.css';

function Login() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    if (id && password) {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include', // 서버에서 httpOnly 쿠키를 설정하기 위해 필요
          body: JSON.stringify({
            email: id,
            password: password,
          }),
        });

        if (response.ok) {
          const result = await response.json();
          setMessage('로그인 성공!');

          // access_token과 refresh_token을 쿠키에 저장
          Cookies.set('access_token', result.access_token, { secure: true, sameSite: 'Strict' });
          Cookies.set('refresh_token', result.refresh_token, { secure: true, sameSite: 'Strict' });

          // 추가 정보 저장 (id와 email을 쿠키에 저장하지 않는 것을 권장)
          localStorage.setItem('user_id', result.id);
          localStorage.setItem('email', result.email);

          // 홈 페이지로 이동
          navigate('/');
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
    <main className="login-container">
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
