// components/Logout.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';

function Logout({ setIsLoggedIn }) {
  const navigate = useNavigate();

  const handleLogout = async () => {
    const refreshToken = localStorage.getItem('refresh_token');

    try {
      const response = await fetch('/logout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          refresh_token: refreshToken,
        }),
      });

      if (response.ok) {
        // 로그아웃 성공 시, 토큰과 상태 제거
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('user_id');
        localStorage.removeItem('email');
        Cookies.remove('authToken'); // 쿠키 제거
        setIsLoggedIn(false);
        navigate('/login'); // 로그아웃 후 로그인 페이지로 이동
      } else {
        console.error('로그아웃 실패');
      }
    } catch (error) {
      console.error('서버 오류:', error);
    }
  };

  return (
    <button onClick={handleLogout}>로그아웃</button>
  );
}

export default Logout;
