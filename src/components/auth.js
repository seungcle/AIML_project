// components/auth.js
import Cookies from 'js-cookie';

export const getAccessToken = () => Cookies.get('access_token');

export const getRefreshToken = () => Cookies.get('refresh_token');

export const setAccessToken = (token) => Cookies.set('access_token', token);

export const setRefreshToken = (token) => Cookies.set('refresh_token', token);

export const clearTokens = () => {
  Cookies.remove('access_token');
  Cookies.remove('refresh_token');
};

export const refreshAccessToken = async () => {
  const refreshToken = getRefreshToken();
  if (refreshToken) {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/refresh`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ refresh_token: refreshToken }),
      });
      if (response.ok) {
        const data = await response.json();
        setAccessToken(data.access_token);
        return true;
      }
    } catch (error) {
      console.error('토큰 갱신 오류:', error);
    }
  }
  return false;
};
