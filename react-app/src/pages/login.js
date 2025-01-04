import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../components/auth'; // useAuth 훅 사용
import '../styles/login.css';

function Login() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth(); // useAuth 훅에서 login 함수 가져옴

  const handleLogin = async () => {
    if (id && password) {
      try {
        setLoading(true); // 로딩 시작
        const response = await fetch(`${process.env.REACT_APP_API_URL}/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
          body: JSON.stringify({
            email: id,
            password: password,
          }),
        });

        if (response.ok) {
          const result = await response.json();
          setMessage('로그인 성공!');

          // login 함수를 사용하여 토큰과 사용자 정보를 설정
          await login(result.access_token, result.refresh_token);

          // 홈 화면으로 이동
          navigate('/');
        } else {
          setMessage('아이디 또는 패스워드가 잘못되었습니다.');
        }
      } catch (error) {
        console.error('로그인 중 서버 오류:', error);
        setMessage('서버 오류가 발생했습니다.');
      } finally {
        setLoading(false); // 로딩 종료
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
      <button onClick={handleLogin} disabled={loading}>
        {loading ? '로그인 중...' : '로그인'}
      </button>
      <p>{message}</p>
    </main>
  );
}

export default Login;