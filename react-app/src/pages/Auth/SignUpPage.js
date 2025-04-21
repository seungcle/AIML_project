import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function SignUp() {
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
          body: JSON.stringify({
            email: email,
            password: password,
            name: name,
          }),
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
    <main className="signup-container">
      <h2 className="signup-title">회원가입</h2>
      <form className="signup-form">
        <div className="form-group">
          <label htmlFor="email">이메일</label>
          <input
            type="email"
            id="email"
            placeholder="이메일"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">패스워드</label>
          <input
            type="password"
            id="password"
            placeholder="패스워드"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label htmlFor="name">이름</label>
          <input
            type="text"
            id="name"
            placeholder="이름"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        <button className="custom-button" type="button" onClick={handleSignup}>회원가입</button>
      </form>
    </main>
  );
}

export default SignUp;