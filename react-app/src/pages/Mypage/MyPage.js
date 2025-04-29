import React from 'react';
import { useAuth } from '../../components/auth/Auth';
import { useNavigate } from 'react-router-dom';
import '../../styles/card.css';
import '../../styles/layout.css';
import '../../styles/button.css'; // 버튼 스타일 가져오기

const MyPage = () => {
  const { userInfo, loading } = useAuth();
  const navigate = useNavigate();

  const handleViewMyReceipts = () => {
    navigate('/my-receipts');
  };

  if (loading) {
    return <p style={{ textAlign: 'center' }}>로딩 중...</p>;
  }

  if (!userInfo) {
    return <p style={{ textAlign: 'center' }}>로그인이 필요합니다.</p>;
  }

  return (
    <div className="page-container">
      {/* 기본 정보 카드 */}
      <div
        className="card"
        style={{
          maxWidth: '500px',
          margin: '0 auto',
          marginBottom: '2rem',
          borderRadius: '1rem',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        }}
      >
        <h2 style={{ marginBottom: '1.2rem', fontSize: '1.5rem' }}>마이페이지</h2>
        <div style={{ lineHeight: '1.8', fontSize: '1rem', color: '#333' }}>
          <p><strong>이름:</strong> {userInfo.name}</p>
          <p><strong>이메일:</strong> {userInfo.email}</p>
          <p><strong>그룹 ID:</strong> {userInfo.groupId ?? '없음'}</p>
          <p><strong>상태:</strong> {userInfo.active ? '✅ 활성' : '❌ 비활성'}</p>
          <p><strong>권한:</strong> {userInfo.admin ? '👑 관리자' : '🙋‍♂️ 일반 유저'}</p>
        </div>
      </div>

      {/* 내가 올린 영수증 카드 */}
      <div
        className="card"
        style={{
          maxWidth: '500px',
          margin: '0 auto',
          padding: '2rem',
          borderRadius: '1rem',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
          textAlign: 'center',
        }}
      >
        <h3 style={{ marginBottom: '1rem', fontSize: '1.3rem', color: '#222' }}>내가 올린 영수증</h3>
        <p style={{ marginBottom: '1.5rem', fontSize: '1rem', color: '#666' }}>
          업로드한 영수증 목록을 간편하게 확인해보세요!
        </p>
        <button
          className="btn"
          onClick={handleViewMyReceipts}
          style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <span role="img" aria-label="receipt">🧾</span>
          영수증 보기
        </button>
      </div>
    </div>
  );
};

export default MyPage;
