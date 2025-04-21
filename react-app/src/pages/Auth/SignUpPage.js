import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/card.css';
import '../../styles/button.css';
import '../../styles/layout.css';
import '../../styles/form.css';

function SignUpPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const navigate = useNavigate();

  const handleSignup = async () => {
    if (email && password && name) {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/member/signup`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ email, password, name }),
        });

        if (response.ok) {
          alert('회원가입이 완료되었습니다!');
          navigate('/');
        } else {
          alert('회원가입에 실패했습니다.');
        }
      } catch (error) {
        alert('서버 오류가 발생했습니다.');
      }
    } else {
      alert('모든 필드를 입력해주세요.');
    }
  };

  return (
    <main className="page-container">
      <div className="card" style={{ maxWidth: '400px', margin: '0 auto' }}>
        <h2 style={{ marginBottom: '1rem' }}>회원가입</h2>
        <label htmlFor="email">이메일</label>
        <input
          className="form-input"
          type="email"
          id="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <label htmlFor="password">패스워드</label>
        <input
          className="form-input"
          type="password"
          id="password"
          placeholder="패스워드"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <label htmlFor="name">이름</label>
        <input
          className="form-input"
          type="text"
          id="name"
          placeholder="이름"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <button className="btn" type="button" onClick={handleSignup} style={{ width: '100%', marginTop: '1rem' }}>
          회원가입
        </button>
      </div>
    </main>
  );
}

export default SignUpPage;
