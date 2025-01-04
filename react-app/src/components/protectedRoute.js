import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './auth';

function ProtectedRoute({ children, requiredAdmin = false }) {
  const { isLoggedIn, userInfo, loading } = useAuth();
  const location = useLocation();

  // 로딩 중일 때 로딩 화면을 렌더링합니다.
  if (loading) {
    return <p>로딩 중...</p>;
  }

  // 로그인되어 있지 않으면 로그인 페이지로 리다이렉트합니다.
  if (!isLoggedIn) {
    return <Navigate to="/login" state={{ from: location }} />;
  }

  // 관리자가 필요한 페이지의 경우, 관리자가 아니라면 접근을 막습니다.
  if (requiredAdmin && userInfo && !userInfo.admin) {
    return <p>접근 권한이 없습니다.</p>;
  }

  // 조건에 맞는 경우 컴포넌트를 JSX로 감싸서 렌더링합니다.
  return children;
}

export default ProtectedRoute;
