// App.js
import React, { useEffect, useState } from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import Home from './pages/home';
import SignupPage from './pages/signUp';
import LoginPage from './pages/login';
import Invite from './components/invite.js';
import Join from './components/join.js';
import MyPage from './pages/mypage.js';
import Logout from './components/logout';
import { getAccessToken, refreshAccessToken, clearTokens } from './components/auth';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const accessToken = getAccessToken();
    if (accessToken) {
      setIsLoggedIn(true);
    } else {
      setIsLoggedIn(false);
      handleTokenRefresh();
    }
  }, []);

  const handleTokenRefresh = async () => {
    const refreshed = await refreshAccessToken();
    if (refreshed) {
      setIsLoggedIn(true);
    } else {
      setIsLoggedIn(false);
      clearTokens();
    }
  };

  return (
    <div>
      <nav>
        <Link to="/"><button>홈</button></Link>
        {isLoggedIn ? (
          <>
            <Link to="/mypage/1"><button>마이페이지</button></Link>
            <Logout setIsLoggedIn={setIsLoggedIn} />
          </>
        ) : (
          <>
            <Link to="/login"><button>로그인</button></Link>
            <Link to="/signup"><button>회원가입</button></Link>
          </>
        )}
      </nav>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/invite/:userId" element={<Invite />} />  
        <Route path="/join/:userId" element={<Join />} />      
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage setIsLoggedIn={setIsLoggedIn} />} />
        <Route
          path="/mypage/:userId"
          element={
            isLoggedIn ? (
              <MyPage />
            ) : (
              <LoginPage setIsLoggedIn={setIsLoggedIn} />
            )
          }
        />
      </Routes>
    </div>
  );
}

export default App;
