import React, { useState } from 'react';
import '../styles/signUp.css';  // CSS 파일 가져오기

function SignUp() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [message, setMessage] = useState('');

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
          const result = await response.json();
          setMessage(result.message || '회원가입 완료!');
        } else {
          setMessage('회원가입에 실패했습니다.');
        }
      } catch (error) {
        setMessage('서버 오류가 발생했습니다.');
      }
    } else {
      setMessage('모든 필드를 입력해주세요.');
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
      <p>{message}</p>
    </main>
  );
}

export default SignUp;
