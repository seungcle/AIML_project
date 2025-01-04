import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from './pages/home';
import SignupPage from './pages/signUp';
import LoginPage from './pages/login';
import MyPage from './pages/mypage';
import Navbar from './components/navbar';
import CreateGroup from './pages/createGroup';
import JoinGroup from './pages/joinGroup';
import ReceiptUpload from './pages/receiptUpload';
import GroupManagement from './pages/groupManagement';
import { AuthProvider, useAuth } from './components/auth';
import ProtectedRoute from './components/protectedRoute';
import ReceiptList from './pages/receiptList';


function AppRoutes() {
  const { userInfo, loading } = useAuth();

  if (loading) {
    return <p>로딩 중...</p>;
  }

  if (!userInfo) {
    // 비로그인 상태
    return (
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    );
  }

  if (userInfo.admin) {
    // 관리자 회원인 경우
    return (
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/mypage/:userId" element={<ProtectedRoute requiredAdmin={true}><MyPage /></ProtectedRoute>} />
        <Route path="/receipt-upload" element={<ProtectedRoute requiredAdmin={true}><ReceiptUpload /></ProtectedRoute>} />
        <Route path="/group-management" element={<ProtectedRoute requiredAdmin={true}><GroupManagement /></ProtectedRoute>} />
        <Route path="/receipts" element={<ProtectedRoute requiredAdmin={true}><ReceiptList /></ProtectedRoute>} />
      </Routes>
    );
  }

  if (userInfo.groupId) {
    // 그룹에 가입된 일반 회원인 경우
    return (
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/mypage/:userId" element={<ProtectedRoute><MyPage /></ProtectedRoute>} />
        <Route path="/receipt-upload" element={<ProtectedRoute><ReceiptUpload /></ProtectedRoute>} />
        <Route path="/receipt-history" element={<ProtectedRoute><ReceiptUpload /></ProtectedRoute>} />
        <Route path="/receipts" element={<ProtectedRoute><ReceiptList /></ProtectedRoute>} />
      </Routes>
    );
  }

  // 그룹에 가입되지 않은 일반 회원인 경우
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/mypage/:userId" element={<ProtectedRoute><MyPage /></ProtectedRoute>} />
      <Route path="/create-group" element={<ProtectedRoute><CreateGroup /></ProtectedRoute>} />
      <Route path="/join-group" element={<ProtectedRoute><JoinGroup /></ProtectedRoute>} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <Navbar />
      <AppRoutes />
    </AuthProvider>
  );
}

export default App;
