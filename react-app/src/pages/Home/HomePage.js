import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../components/auth/Auth';
import CustomModal from '../../components/common/CustomModal';
import '../../styles/card.css';
import '../../styles/button.css';
import '../../styles/layout.css';

function Home() {
  const { userInfo, isLoggedIn, loading } = useAuth();
  const navigate = useNavigate();
  const [showAuthOptions, setShowAuthOptions] = useState(false);

  useEffect(() => {
    console.log('Home page updated:', { userInfo, isLoggedIn });
    console.log('userInfo 내용:', userInfo); // ⭐ 여기 추가
  }, [userInfo, isLoggedIn]);

  if (loading) return <p>로딩 중...</p>;

  const handleCreateGroup = () => navigate('/create-group');
  const handleJoinGroup = () => navigate('/join-group');
  const handleReceiptUpload = () => navigate('/receipt-upload');
  const handleGroupManagement = () => navigate('/group-management');
  const handleGoLogin = () => setShowAuthOptions(true);

  const handleViewGroupReceipts = () => {
    if (!userInfo || !userInfo.id) {
      alert('회원 정보가 아직 로딩되지 않았습니다. 잠시 후 다시 시도해주세요.');
      return;
    }
    const currentYear = new Date().getFullYear();
    const currentMonth = String(new Date().getMonth() + 1).padStart(2, '0');
    navigate(`/group-member-receipts/${userInfo.id}/${currentYear}/${currentMonth}`);
  };


  const cardStyle = {
    lineHeight: '1.7',
    flex: '1 1 300px',
  };

  const buttonStyle = {
    width: 'fit-content',
    alignSelf: 'start',
  };

  return (
    <div className="page-container" style={{ paddingBottom: '3rem' }}>
      <section className="hero" style={{ textAlign: 'center', marginBottom: '3rem' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>홍익 Receipt</h1>
        <p style={{ color: '#6b7280' }}>지출 관리를 쉽게, 팀과 함께.</p>
      </section>

      {!isLoggedIn || !userInfo ? (
        <section className="card-grid">
          <div className="card" style={cardStyle}>
            <h2>자동 분류</h2>
            <p>OCR 인식 후 AI가 자동으로 카테고리를 분류해줘요.</p>
          </div>
          <div className="card" style={cardStyle}>
            <h2>통계 리포트</h2>
            <p>월간/연간 리포트를 CSV, PDF로 추출할 수 있어요.</p>
          </div>
          <div className="card" style={cardStyle}>
            <h2>그룹 관리</h2>
            <p>관리자는 초대/수락 기능으로 팀원들을 관리할 수 있어요.</p>
          </div>
          <div className="card" style={cardStyle}>
            <h2>지금 바로 시작해보세요</h2>
            <p>영수증을 업로드하고 지출 관리를 시작해보세요.</p>
            <button className="home-cta-button" onClick={handleGoLogin}>
              📸 영수증 업로드하기
            </button>
          </div>
        </section>
      ) : userInfo.admin ? (
        <>
          {/* 📌 관리자 카드 */}
          <div className="card" style={{ maxWidth: '450px', margin: '0 auto', marginBottom: '2rem' }}>
            <h2 style={{ marginBottom: '1rem' }}>관리자 메뉴</h2>
            <p style={{ color: '#6b7280', marginBottom: '1.2rem' }}>
              그룹을 관리하고, 팀원들의 지출 내역을 확인해보세요.
            </p>
            <div className="button-container" style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <button className="btn btn-sm" style={buttonStyle} onClick={handleGroupManagement}>
                그룹 관리하기
              </button>
              <button className="btn btn-sm" style={buttonStyle} onClick={handleViewGroupReceipts}>
                그룹 영수증 조회하기
              </button>
            </div>
          </div>

          {/* 📸 영수증 업로드 카드 */}
          <div className="card" style={{ maxWidth: '450px', margin: '0 auto' }}>
            <h2 style={{ marginBottom: '1rem' }}>지출 내역 등록</h2>
            <p style={{ color: '#6b7280' }}>
              OCR로 영수증을 분석하고 자동으로 분류해보세요.
            </p>
            <button className="btn" style={{ marginTop: '1rem' }} onClick={handleReceiptUpload}>
              📸 영수증 업로드
            </button>
          </div>
        </>
      ) : userInfo.groupId ? (
        <>
          {/* 📸 그룹 가입된 일반 유저 카드 */}
          <div className="card" style={{ maxWidth: '450px', margin: '0 auto', marginBottom: '2rem' }}>
            <h2 style={{ marginBottom: '1rem' }}>그룹 영수증 조회</h2>
            <p style={{ color: '#6b7280', marginBottom: '1.2rem' }}>
              소속된 그룹의 지출 내역을 확인해보세요.
            </p>
            <button className="btn btn-sm" style={{ ...buttonStyle, marginBottom: '1rem' }} onClick={handleViewGroupReceipts}>
              그룹 영수증 조회하기
            </button>
          </div>

          {/* 📸 영수증 업로드 카드 */}
          <div className="card" style={{ maxWidth: '450px', margin: '0 auto' }}>
            <h2 style={{ marginBottom: '1rem' }}>지출 내역 등록</h2>
            <p style={{ color: '#6b7280' }}>
              OCR로 영수증을 분석하고 자동으로 분류해보세요.
            </p>
            <button className="btn" style={{ marginTop: '1rem' }} onClick={handleReceiptUpload}>
              📸 영수증 업로드
            </button>
          </div>
        </>
      ) : (
        <div className="card" style={{ maxWidth: '450px', margin: '0 auto' }}>
          <h2 style={{ marginBottom: '1rem' }}>그룹 관리</h2>
          <p style={{ color: '#6b7280', marginBottom: '1rem' }}>
            영수증을 업로드하려면 먼저 그룹에 가입하거나 그룹을 만들어야 해요.
          </p>
          <div className="button-container" style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <button className="btn btn-sm" style={buttonStyle} onClick={handleCreateGroup}>
              그룹 만들기
            </button>
            <button className="btn btn-sm" style={buttonStyle} onClick={handleJoinGroup}>
              그룹 가입하기
            </button>
          </div>
        </div>
      )}

      {/* 🔒 비로그인 시 모달 */}
      <CustomModal
        isOpen={showAuthOptions}
        onClose={() => setShowAuthOptions(false)}
        title="서비스 이용을 위해 로그인 또는 회원가입이 필요합니다."
        actions={[
          <button key="login" onClick={() => navigate('/login')} className="btn">로그인</button>,
          <button key="signup" onClick={() => navigate('/signup')} className="btn btn-light">회원가입</button>,
        ]}
      />
    </div>
  );
}

export default Home;
