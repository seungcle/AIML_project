import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { verifyPassword } from '../../api/user';
import '../../styles/form.css';
import '../../styles/button.css';
import '../../styles/layout.css';

const PasswordVerifyPage = () => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleVerify = async () => {
    try {
      await verifyPassword(currentPassword);
      navigate('/auth/change-password');
    } catch (err) {
      if (err.response?.status === 401) {
        setError('비밀번호가 일치하지 않습니다.');
      } else {
        setError('서버 오류가 발생했습니다.');
      }
    }
  };

  return (
    <main className="page-container">
      <div className="card" style={{ maxWidth: '400px', margin: '0 auto' }}>
        <h2 style={{ marginBottom: '1.5rem' }}>비밀번호 확인</h2>

        <div style={{ marginBottom: '1.5rem' }}>
          <label htmlFor="currentPassword" style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
            현재 비밀번호
          </label>
          <input
            id="currentPassword"
            type="password"
            className="form-input"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            placeholder="현재 비밀번호 입력"
          />
        </div>

        {error && <p style={{ color: 'red', marginBottom: '1rem' }}>{error}</p>}

        <button
          className="btn"
          onClick={handleVerify}
          style={{ width: '100%' }}
        >
          확인
        </button>
      </div>
    </main>
  );
};

export default PasswordVerifyPage;
