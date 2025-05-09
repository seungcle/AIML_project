import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home/HomePage';
import SignupPage from './pages/Auth/SignUpPage';
import LoginPage from './pages/Auth/LoginPage';
import MyPage from './pages/Mypage/MyPage';
import Navbar from './components/layout/Navbar';
import CreateGroup from './pages/Group/CreateGroupPage';
import JoinGroup from './pages/Group/JoinGroupPage';
import ReceiptUpload from './pages/Receipt/ReceiptUploadPage';
import GroupManagement from './pages/Group/GroupManagementPage';
import { AuthProvider, useAuth } from './components/auth/Auth';
import ProtectedRoute from './components/auth/ProtectedRoute';
import ReceiptMyList from './pages/Receipt/ReceiptListMyPage';
import GroupMemberReceiptList from './pages/Receipt/GroupMemberReceiptList';
import PasswordVerifyPage from './pages/Auth/PasswordVerifyPage'; 
import ChangePasswordPage from './pages/Auth/ChangePasswordPage'; 
import DeleteAccountPage from './pages/Auth/DeleteAccountPage';   

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
        <Route path="/my-receipts" element={<ProtectedRoute requiredAdmin={true}><ReceiptMyList /></ProtectedRoute>} />
        <Route path="/group-member-receipts/:memberId/:year/:month" element={<ProtectedRoute requiredAdmin={true}><GroupMemberReceiptList /></ProtectedRoute>} />
        {/* 관리자도 비밀번호 변경 / 탈퇴 가능 */}
        <Route path="/auth/password-verify" element={<ProtectedRoute><PasswordVerifyPage /></ProtectedRoute>} />
        <Route path="/auth/change-password" element={<ProtectedRoute><ChangePasswordPage /></ProtectedRoute>} />
        <Route path="/auth/delete-account" element={<ProtectedRoute><DeleteAccountPage /></ProtectedRoute>} />
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
        <Route path="/my-receipts" element={<ProtectedRoute><ReceiptMyList /></ProtectedRoute>} />
        <Route path="/group-member-receipts/:memberId/:year/:month" element={<ProtectedRoute requiredAdmin={true}><GroupMemberReceiptList /></ProtectedRoute>} />
        {/* 일반 회원도 비밀번호 변경 / 탈퇴 가능 */}
        <Route path="/auth/password-verify" element={<ProtectedRoute><PasswordVerifyPage /></ProtectedRoute>} />
        <Route path="/auth/change-password" element={<ProtectedRoute><ChangePasswordPage /></ProtectedRoute>} />
        <Route path="/auth/delete-account" element={<ProtectedRoute><DeleteAccountPage /></ProtectedRoute>} />
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
      {/* 가입 안된 회원도 비밀번호 변경 / 탈퇴 가능 */}
      <Route path="/auth/password-verify" element={<ProtectedRoute><PasswordVerifyPage /></ProtectedRoute>} />
      <Route path="/auth/change-password" element={<ProtectedRoute><ChangePasswordPage /></ProtectedRoute>} />
      <Route path="/auth/delete-account" element={<ProtectedRoute><DeleteAccountPage /></ProtectedRoute>} />
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
