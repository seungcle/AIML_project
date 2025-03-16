// Home.js에서 상태 변경에 따른 업데이트 반영
import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/home.css';
import { useAuth } from '../components/auth'; // 로그인 상태 확인을 위한 useAuth 훅

function Home() {
  const { userInfo, isLoggedIn, loading } = useAuth(); // 로그인 상태 및 사용자 정보 확인
  const navigate = useNavigate();

  useEffect(() => {
    // 홈 화면에 접근할 때 userInfo가 변경되면 자동 리렌더링
    console.log('Home page updated:', { userInfo, isLoggedIn });
  }, [userInfo, isLoggedIn]);

  // 로딩 중인 경우
  if (loading) {
    return <p>로딩 중...</p>;
  }

  // 버튼 클릭 핸들러
  const handleCreateGroup = () => {
    navigate('/create-group'); // 그룹 만들기 페이지로 이동
  };

  const handleJoinGroup = () => {
    navigate('/join-group'); // 그룹 가입 페이지로 이동
  };

  const handleReceiptUpload = () => {
    navigate('/receipt-upload'); // 영수증 업로드 페이지로 이동
  };

  const handleGroupManagement = () => {
    navigate('/group-management'); // 그룹 관리 페이지로 이동
  };

  const handleViewReceipts = () => {
    navigate('/receipts'); // 영수증 조회 페이지로 이동
  };

  return (
    <div>
      <main className="home-container">
        {!isLoggedIn || !userInfo ? ( // 로그인이 안 되어 있을 때
          <>
            <section id="intro" className="home-section">
              <h2>소개</h2>
              <p>~은 기업 비용 관리를 위한 통합 솔루션입니다...</p>
            </section>
            <section id="features" className="home-section">
              <h2>주요 기능</h2>
              <p>~은 지출 관리, 법인카드 관리 등 여러 기능을 제공합니다...</p>
            </section>
          </>
        ) : userInfo.admin ? ( // 관리자 회원일 때
          <section id="actions" className="home-section">
            <h2>관리자 관리</h2>
            <div className="button-container">
              <button onClick={handleGroupManagement}>그룹 관리하기</button>
              <button onClick={handleReceiptUpload}>영수증 업로드</button>
              <button onClick={handleViewReceipts}>영수증 조회</button>
            </div>
          </section>
        ) : userInfo.groupId ? ( // 그룹에 가입된 일반 회원일 때
          <section id="actions" className="home-section">
            <h2>그룹 관리</h2>
            <div className="button-container">
              <button onClick={handleReceiptUpload}>영수증 업로드</button>
              <button onClick={handleViewReceipts}>영수증 조회</button>
            </div>
          </section>
        ) : ( // 그룹에 가입되지 않은 일반 회원일 때
          <section id="actions" className="home-section">
            <h2>그룹 관리</h2>
            <div className="button-container">
              <button onClick={handleCreateGroup}>그룹 만들기</button>
              <button onClick={handleJoinGroup}>그룹 가입하기</button>
            </div>
          </section>
        )}
      </main>
    </div>
  );
}

export default Home;
