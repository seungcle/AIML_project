import React from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import { useAuth } from './Auth'; 
import '../../styles/button.css'; 

function Logout() {
  const navigate = useNavigate();
  const { setUserInfo } = useAuth();

  const handleLogout = async () => {
    const refreshToken = Cookies.get('refresh_token');

    try {
      if (process.env.NODE_ENV === 'development') {
        console.log(`Requesting logout with URL: ${process.env.REACT_APP_API_URL}/logout`);
        console.log(`Refresh Token: ${refreshToken}`);
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/logout`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ refresh_token: refreshToken }),
      });

      if (response.ok) {
        console.log('Logout successful.');
      } else if (response.status === 401 || response.status === 403) {
        console.error('Unauthorized or forbidden. Clearing client tokens.');
      } else {
        console.error('Unexpected logout error:', response.status, response.statusText);
      }
    } catch (error) {
      console.error('Server error during logout:', error);
    } finally {
      Cookies.remove('access_token', { path: '/' });
      Cookies.remove('refresh_token', { path: '/' });
      setUserInfo(null);
      navigate('/login');
    }
  };

  return (
    <button className="btn" onClick={handleLogout}>
      로그아웃
    </button>
  );
}

export default Logout;
