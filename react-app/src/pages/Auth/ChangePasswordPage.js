import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { clearTokens, useAuth } from '../../components/auth/Auth';
import { changePassword } from '../../api/user';
import '../../styles/form.css';
import '../../styles/button.css';
import '../../styles/layout.css';

const ChangePasswordPage = () => {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { setUserInfo } = useAuth();

  const handleChangePassword = async () => {
    if (newPassword !== confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }
    if (newPassword.length < 8) {
      setError('비밀번호는 최소 8자 이상이어야 합니다.');
      return;
    }

    try {
      await changePassword(newPassword, confirmPassword);
      alert('비밀번호가 성공적으로 변경되었습니다. 다시 로그인 해주세요.');
      clearTokens();
      setUserInfo(null);
      navigate('/login');
    } catch (err) {
      console.error('비밀번호 변경 실패:', err);
      const serverMessage = err.response?.data?.message || '비밀번호 변경 중 오류가 발생했습니다.';
      setError(serverMessage);
    }
  };

  return (
    <main className="page-container">
      <div className="card" style={{ maxWidth: '400px', margin: '0 auto' }}>
        <h2 style={{ marginBottom: '1rem' }}>비밀번호 변경</h2>

        <label htmlFor="newPassword">새 비밀번호</label>
        <input
          id="newPassword"
          type="password"
          className="form-input"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          placeholder="새 비밀번호 입력"
        />

        <label htmlFor="confirmPassword" style={{ marginTop: '1rem' }}>비밀번호 확인</label>
        <input
          id="confirmPassword"
          type="password"
          className="form-input"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          placeholder="비밀번호 다시 입력"
        />

        {error && <p style={{ color: 'red', marginTop: '0.5rem' }}>{error}</p>}

        <button
          className="btn"
          onClick={handleChangePassword}
          style={{ marginTop: '1.5rem', width: '100%' }}
        >
          비밀번호 변경
        </button>
      </div>
    </main>
  );
};

export default ChangePasswordPage;
