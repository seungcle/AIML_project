// Navbar.js
import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/navbar.css';
import Logout from './logout';
import { useAuth } from './auth'; // Context API 가져오기

function Navbar() {
  const { userInfo, loading } = useAuth(); // 로그인 상태 및 사용자 정보 가져오기

  // 로딩 중일 때 로딩 화면을 렌더링합니다.
  if (loading) {
    return (
      <header className="navbar">
        <div className="navbar-logo">
          <Link to="/">이름짓기</Link>
        </div>
        <nav className="navbar-buttons">
          <p>로딩 중...</p>
        </nav>
      </header>
    );
  }

  return (
    <header className="navbar">
      <div className="navbar-logo">
        <Link to="/">이름짓기</Link>
      </div>
      <nav className="navbar-buttons">
        {userInfo ? (
          <>
            <Link to={`/mypage/${userInfo.id}`} className="navbar-button">마이페이지</Link>
            <Logout />
          </>
        ) : (
          <>
            <Link to="/signup" className="navbar-button">회원가입</Link>
            <Link to="/login" className="navbar-button">로그인</Link>
          </>
        )}
      </nav>
    </header>
  );
}

export default Navbar;
