import React from 'react';
import { useAuth } from '../components/auth';

const MyPage = () => {
  const { userInfo, loading } = useAuth();

  if (loading) {
    return <p>로딩 중...</p>;
  }

  if (!userInfo) {
    return <p>로그인이 필요합니다.</p>;
  }

  return (
    <div className="mypage-container">
      <h2>마이페이지</h2>
      <div className="user-info">
        <p>이름: {userInfo.name}</p>
        <p>이메일: {userInfo.email}</p>
        <p>그룹 ID: {userInfo.groupId}</p>
        <p>상태: {userInfo.active ? '활성' : '비활성'}</p>
        <p>관리자 여부: {userInfo.admin ? '관리자' : '일반 유저'}</p>
      </div>
    </div>
  );
};

export default MyPage;
