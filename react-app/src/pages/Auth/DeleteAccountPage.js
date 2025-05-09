import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Modal from 'react-modal';
import { clearTokens, useAuth } from '../../components/auth/Auth';
import { deleteAccount, verifyPassword } from '../../api/user';
import '../../styles/form.css';
import '../../styles/button.css';
import '../../styles/layout.css';
import '../../styles/modal.css';

Modal.setAppElement('#root'); // 접근성 설정

const DeleteAccountPage = () => {
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [isSuccessOpen, setIsSuccessOpen] = useState(false);
  const navigate = useNavigate();
  const { setUserInfo } = useAuth();

  const handleDelete = async () => {
    try {
      await verifyPassword(password);
      await deleteAccount();

      clearTokens();
      setUserInfo(null);
      setIsConfirmOpen(false);
      setIsSuccessOpen(true);
    } catch (err) {
      console.error('계정 삭제 실패:', err);
      const status = err.response?.status;
      const message = err.response?.data?.message;

      if (status === 401) {
        setError('비밀번호가 올바르지 않습니다.');
      } else if (message) {
        setError(message);
      } else {
        setError('계정 삭제 중 오류가 발생했습니다.');
      }
      setIsConfirmOpen(false);
    }
  };

  return (
    <main className="page-container">
      <div className="card" style={{ maxWidth: '400px', margin: '0 auto' }}>
        <h2 style={{ marginBottom: '1rem' }}>계정 삭제</h2>
        <p style={{ marginBottom: '1.5rem', color: '#555' }}>
          계정을 삭제하려면 비밀번호를 한 번 더 입력해주세요.
        </p>

        <div style={{ marginBottom: '1.5rem' }}>
          <label htmlFor="password" style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
            비밀번호
          </label>
          <input
            id="password"
            type="password"
            className="form-input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="비밀번호 입력"
          />
        </div>

        {error && <p style={{ color: 'red', marginBottom: '1rem' }}>{error}</p>}

        <button
          className="btn btn-danger-light"
          onClick={() => setIsConfirmOpen(true)}
          style={{ width: '100%' }}
        >
          계정 삭제
        </button>
      </div>

      {/* ✅ 삭제 확인 모달 */}
      <Modal
        isOpen={isConfirmOpen}
        onRequestClose={() => setIsConfirmOpen(false)}
        overlayClassName="modal-overlay"
        className="modal-content"
      >
        <h3 className="modal-title">정말 계정을 삭제하시겠습니까?</h3>
        <div className="modal-body">이 작업은 되돌릴 수 없습니다.</div>
        <div className="modal-buttons">
          <button className="btn btn-light" onClick={() => setIsConfirmOpen(false)}>취소</button>
          <button className="btn btn-danger-light" onClick={handleDelete}>계정 삭제</button>
        </div>
        <button className="modal-close" onClick={() => setIsConfirmOpen(false)}>×</button>
      </Modal>

      {/* ✅ 삭제 완료 모달 */}
      <Modal
        isOpen={isSuccessOpen}
        onRequestClose={() => {
          setIsSuccessOpen(false);
          navigate('/');
        }}
        overlayClassName="modal-overlay"
        className="modal-content"
      >
        <h3 className="modal-title">계정 삭제 완료</h3>
        <div className="modal-body">그동안 이용해주셔서 감사합니다.</div>
        <div className="modal-buttons">
          <button className="btn" onClick={() => {
            setIsSuccessOpen(false);
            navigate('/');
          }}>홈으로</button>
        </div>
      </Modal>
    </main>
  );
};

export default DeleteAccountPage;
