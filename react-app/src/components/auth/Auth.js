import React, { createContext, useContext, useState, useEffect } from 'react';
import Cookies from 'js-cookie';
import getMemberInfo from '../../utils/GetMember'; // 사용자 정보를 가져오는 함수

const AuthContext = createContext();

// 토큰 관리 함수들
export const getAccessToken = () => Cookies.get('access_token');
export const getRefreshToken = () => Cookies.get('refresh_token');
export const setAccessToken = (token) =>
  Cookies.set('access_token', token, { secure: true, sameSite: 'Lax', path: '/' });
export const setRefreshToken = (token) =>
  Cookies.set('refresh_token', token, { secure: true, sameSite: 'Lax', path: '/' });
export const clearTokens = () => {
  Cookies.remove('access_token', { path: '/' });
  Cookies.remove('refresh_token', { path: '/' });
};

// 액세스 토큰 갱신 함수
export const refreshAccessToken = async () => {
  const refreshToken = getRefreshToken();
  if (refreshToken) {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refresh_token: refreshToken }),
      });
      if (response.ok) {
        const data = await response.json();
        setAccessToken(data.access_token);
        console.log('Access token successfully refreshed.');
        return true;
      } else {
        console.error('Token refresh failed:', response.status, response.statusText);
      }
    } catch (error) {
      console.error('토큰 갱신 중 오류 발생:', error);
    }
  } else {
    console.warn('Refresh token is missing. Cannot refresh access token.');
  }
  clearTokens();
  return false;
};

// AuthProvider 컴포넌트
export function AuthProvider({ children }) {
  const [isLoggedIn, setIsLoggedIn] = useState(!!getAccessToken());
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuthState = async () => {
      try {
        if (getAccessToken()) {
          // 액세스 토큰이 있을 때 사용자 정보를 가져옵니다.
          await loadUserInfo();
        } else if (getRefreshToken()) {
          // 액세스 토큰이 없지만 리프레시 토큰이 있을 때, 갱신 시도
          const refreshed = await refreshAccessToken();
          if (refreshed) {
            await loadUserInfo();
          } else {
            clearTokens();
            setIsLoggedIn(false);
            setUserInfo(null);
          }
        }
      } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        setIsLoggedIn(false);
        setUserInfo(null);
      } finally {
        setLoading(false);
      }
    };
    initializeAuthState();
  }, []);

  // 사용자 정보를 로드하는 함수
  const loadUserInfo = async () => {
    try {
      const data = await getMemberInfo();
      if (data) {
        setUserInfo(data);
        setIsLoggedIn(true);
      } else {
        setIsLoggedIn(false);
        setUserInfo(null);
      }
    } catch (error) {
      console.error('사용자 정보를 가져오는 중 오류가 발생했습니다.', error);
      setIsLoggedIn(false);
      setUserInfo(null);
    }
  };

  // 로그인 시 사용자 정보와 토큰 저장
  const login = async (accessToken, refreshToken) => {
    setAccessToken(accessToken);
    setRefreshToken(refreshToken);
    await loadUserInfo(); // 사용자 정보 로드 후 로그인 상태 업데이트
    setIsLoggedIn(true); // 로그인 상태를 명시적으로 true로 설정
  };


  return (
    <AuthContext.Provider value={{ isLoggedIn, userInfo, loading, setUserInfo, refreshAccessToken, login }}>
      {children}
    </AuthContext.Provider>
  );
}

// useAuth 훅
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth는 AuthProvider 내에서 사용되어야 합니다.');
  }
  return context;
};
