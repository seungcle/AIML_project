import React from 'react';
import { useAuth } from '../../components/auth/Auth';
import '../../styles/card.css';
import '../../styles/layout.css';

const MyPage = () => {
  const { userInfo, loading } = useAuth();

  if (loading) {
    return <p style={{ textAlign: 'center' }}>로딩 중...</p>;
  }

  if (!userInfo) {
    return <p style={{ textAlign: 'center' }}>로그인이 필요합니다.</p>;
  }

  return (
    <div className="page-container">
      <div className="card" style={{ maxWidth: '500px', margin: '0 auto' }}>
        <h2 style={{ marginBottom: '1.5rem' }}>마이페이지</h2>
        <div style={{ lineHeight: '1.8', fontSize: '1rem' }}>
          <p><strong>이름:</strong> {userInfo.name}</p>
          <p><strong>이메일:</strong> {userInfo.email}</p>
          <p><strong>그룹 ID:</strong> {userInfo.groupId ?? '없음'}</p>
          <p><strong>상태:</strong> {userInfo.active ? '✅ 활성' : '❌ 비활성'}</p>
          <p><strong>권한:</strong> {userInfo.admin ? '👑 관리자' : '🙋‍♂️ 일반 유저'}</p>
        </div>
      </div>
    </div>
  );
};

export default MyPage;
