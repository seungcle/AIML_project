import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Home from './pages/home';
import SignupPage from './pages/signUp';
import LoginPage from './pages/login';
import Invite from './components/invite.js';
import Join from './components/join.js';
import MyPage from './pages/mypage.js';

function App() {
  return (
      <div>
        <nav>
          {/* 네비게이션을 위한 버튼들 */}
          {/* <Link to="/"><button>홈</button></Link> */}
        </nav>

        {/* 라우터 설정 */}
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/invite/:userId" element={<Invite />} />  
          <Route path="/join/:userId" element={<Join />} />      
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/mypage/:userId" element={<MyPage />} />
        </Routes>
      </div>
  );
}

export default App;


