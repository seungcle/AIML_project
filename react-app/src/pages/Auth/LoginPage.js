import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../components/auth/Auth';
import '../../styles/card.css';
import '../../styles/button.css';
import '../../styles/layout.css';
import '../../styles/form.css';

function LoginPage() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleLogin = async () => {
    if (id && password) {
      try {
        setLoading(true);
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
          await login(result.access_token, result.refresh_token);
          navigate('/');
        } else {
          setMessage('아이디 또는 패스워드가 잘못되었습니다.');
        }
      } catch (error) {
        console.error('로그인 중 서버 오류:', error);
        setMessage('서버 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    } else {
      setMessage('아이디와 패스워드를 입력해주세요.');
    }
  };

  return (
    <main className="page-container">
      <div className="card" style={{ maxWidth: '400px', margin: '0 auto' }}>
        <h2 style={{ marginBottom: '1rem' }}>로그인</h2>
        <input
          type="text"
          placeholder="이메일"
          value={id}
          onChange={(e) => setId(e.target.value)}
          className="form-input"
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="form-input"
        />
        <button
          className="btn"
          onClick={handleLogin}
          disabled={loading}
          style={{ width: '100%', marginTop: '1rem' }}
        >
          {loading ? '로그인 중...' : '로그인'}
        </button>
        <p style={{ marginTop: '0.5rem', color: '#ef4444' }}>{message}</p>
      </div>
    </main>
  );
}

export default LoginPage;
