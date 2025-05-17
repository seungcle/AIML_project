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
import GroupReceiptList from './pages/Receipt/GroupReceiptList';
import PasswordVerifyPage from './pages/Auth/PasswordVerifyPage';
import ChangePasswordPage from './pages/Auth/ChangePasswordPage';
import DeleteAccountPage from './pages/Auth/DeleteAccountPage';
import GroupAnalysisPage from './pages/Group/GroupAnalysisPage';
import TopStoresPage from './pages/Receipt/TopStoresPage';
import CategoryStatsPage from './pages/Receipt/CategoryStatsPage';
import GroupMemberListPage from './pages/Group/GroupMemberListPage'; // ✅ 추가한 페이지 import

function AppRoutes() {
  const { userInfo, loading } = useAuth();

  if (loading) {
    return <p>로딩 중...</p>;
  }

  if (!userInfo) {
    return (
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    );
  }

  if (userInfo.admin) {
    return (
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/mypage/:userId" element={<ProtectedRoute requiredAdmin={true}><MyPage /></ProtectedRoute>} />
        <Route path="/receipt-upload" element={<ProtectedRoute requiredAdmin={true}><ReceiptUpload /></ProtectedRoute>} />
        <Route path="/group-management" element={<ProtectedRoute requiredAdmin={true}><GroupManagement /></ProtectedRoute>} />
        <Route path="/group/:groupId/members" element={<ProtectedRoute requiredAdmin={true}><GroupMemberListPage /></ProtectedRoute>} /> {/* ✅ 추가 */}
        <Route path="/my-receipts" element={<ProtectedRoute requiredAdmin={true}><ReceiptMyList /></ProtectedRoute>} />
        <Route path="/group-member-receipts/:memberId" element={<ProtectedRoute requiredAdmin={true}><GroupReceiptList /></ProtectedRoute>} />
        <Route path="/auth/password-verify" element={<ProtectedRoute><PasswordVerifyPage /></ProtectedRoute>} />
        <Route path="/auth/change-password" element={<ProtectedRoute><ChangePasswordPage /></ProtectedRoute>} />
        <Route path="/auth/delete-account" element={<ProtectedRoute><DeleteAccountPage /></ProtectedRoute>} />
        <Route path="/group/analysis" element={<ProtectedRoute><GroupAnalysisPage /></ProtectedRoute>} />
        <Route path="/group/group-receipts" element={<ProtectedRoute><GroupReceiptList /></ProtectedRoute>} />
        <Route path="/group/top-stores" element={<ProtectedRoute><TopStoresPage /></ProtectedRoute>} />
        <Route path="/group/category-stats" element={<ProtectedRoute><CategoryStatsPage /></ProtectedRoute>} />
      </Routes>
    );
  }

  if (userInfo.groupId) {
    return (
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/mypage/:userId" element={<ProtectedRoute><MyPage /></ProtectedRoute>} />
        <Route path="/receipt-upload" element={<ProtectedRoute><ReceiptUpload /></ProtectedRoute>} />
        <Route path="/receipt-history" element={<ProtectedRoute><ReceiptUpload /></ProtectedRoute>} />
        <Route path="/my-receipts" element={<ProtectedRoute><ReceiptMyList /></ProtectedRoute>} />
        <Route path="/group-member-receipts/:memberId" element={<ProtectedRoute requiredAdmin={true}><GroupReceiptList /></ProtectedRoute>} />
        <Route path="/auth/password-verify" element={<ProtectedRoute><PasswordVerifyPage /></ProtectedRoute>} />
        <Route path="/auth/change-password" element={<ProtectedRoute><ChangePasswordPage /></ProtectedRoute>} />
        <Route path="/auth/delete-account" element={<ProtectedRoute><DeleteAccountPage /></ProtectedRoute>} />
        <Route path="/group/analysis" element={<ProtectedRoute><GroupAnalysisPage /></ProtectedRoute>} />
        <Route path="/group/group-receipts" element={<ProtectedRoute><GroupReceiptList /></ProtectedRoute>} />
        <Route path="/group/top-stores" element={<ProtectedRoute><TopStoresPage /></ProtectedRoute>} />
        <Route path="/group/category-stats" element={<ProtectedRoute><CategoryStatsPage /></ProtectedRoute>} />
      </Routes>
    );
  }

  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/mypage/:userId" element={<ProtectedRoute><MyPage /></ProtectedRoute>} />
      <Route path="/create-group" element={<ProtectedRoute><CreateGroup /></ProtectedRoute>} />
      <Route path="/join-group" element={<ProtectedRoute><JoinGroup /></ProtectedRoute>} />
      <Route path="/auth/password-verify" element={<ProtectedRoute><PasswordVerifyPage /></ProtectedRoute>} />
      <Route path="/auth/change-password" element={<ProtectedRoute><ChangePasswordPage /></ProtectedRoute>} />
      <Route path="/auth/delete-account" element={<ProtectedRoute><DeleteAccountPage /></ProtectedRoute>} />
      <Route path="/group/analysis" element={<ProtectedRoute><GroupAnalysisPage /></ProtectedRoute>} />
      <Route path="/group/group-receipts" element={<ProtectedRoute><GroupReceiptList /></ProtectedRoute>} />
      <Route path="/group/top-stores" element={<ProtectedRoute><TopStoresPage /></ProtectedRoute>} />
      <Route path="/group/category-stats" element={<ProtectedRoute><CategoryStatsPage /></ProtectedRoute>} />
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
