import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../components/auth/Auth';
import '../../styles/card.css';
import '../../styles/button.css';
import '../../styles/layout.css';

function Home() {
  const { userInfo, isLoggedIn, loading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    console.log('Home page updated:', { userInfo, isLoggedIn });
  }, [userInfo, isLoggedIn]);

  if (loading) return <p>로딩 중...</p>;

  const handleCreateGroup = () => navigate('/create-group');
  const handleJoinGroup = () => navigate('/join-group');
  const handleReceiptUpload = () => navigate('/receipt-upload');
  const handleGroupManagement = () => navigate('/group-management');
  const handleViewReceipts = () => navigate('/receipts');

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
      {/* 🎯 히어로 섹션 */}
      <section className="hero" style={{ textAlign: 'center', marginBottom: '3rem' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>홍익 Receipt</h1>
        <p style={{ color: '#6b7280' }}>지출 관리를 쉽게, 팀과 함께.</p>
      </section>

      {/* 🔶 카드 섹션 */}
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
        </section>
      ) : userInfo.admin ? (
        <div className="card" style={{ maxWidth: '450px', margin: '0 auto' }}>
          <h2 style={{ marginBottom: '1rem' }}>관리자 관리</h2>
          <div
            className="button-container"
            style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}
          >
            <button className="btn btn-sm" style={buttonStyle} onClick={handleGroupManagement}>
              그룹 관리하기
            </button>
            <button className="btn btn-sm" style={buttonStyle} onClick={handleReceiptUpload}>
              영수증 업로드
            </button>
            <button className="btn btn-sm" style={buttonStyle} onClick={handleViewReceipts}>
              영수증 조회
            </button>
          </div>
        </div>
      ) : userInfo.groupId ? (
        <div className="card" style={{ maxWidth: '450px', margin: '0 auto' }}>
          <h2 style={{ marginBottom: '1rem' }}>그룹 관리</h2>
          <div
            className="button-container"
            style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}
          >
            <button className="btn btn-sm" style={buttonStyle} onClick={handleReceiptUpload}>
              영수증 업로드
            </button>
            <button className="btn btn-sm" style={buttonStyle} onClick={handleViewReceipts}>
              영수증 조회
            </button>
          </div>
        </div>
      ) : (
        <div className="card" style={{ maxWidth: '450px', margin: '0 auto' }}>
          <h2 style={{ marginBottom: '1rem' }}>그룹 관리</h2>
          <div
            className="button-container"
            style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}
          >
            <button className="btn btn-sm" style={buttonStyle} onClick={handleCreateGroup}>
              그룹 만들기
            </button>
            <button className="btn btn-sm" style={buttonStyle} onClick={handleJoinGroup}>
              그룹 가입하기
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default Home;
