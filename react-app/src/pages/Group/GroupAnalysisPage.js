import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../components/auth/Auth';
import '../../styles/button.css';
import '../../styles/card.css';
import '../../styles/layout.css';

const GroupAnalysisPage = () => {
  const navigate = useNavigate();
  const { userInfo } = useAuth();

  const handleGroupReceipts = () => {
    if (!userInfo?.id) {
      alert('회원 정보가 없습니다.');
      return;
    }
    navigate(`/group-member-receipts/${userInfo.id}`);
  };

  return (
    <main className="page-container">
      <h2 className="page-title" style={{ textAlign: 'center', marginBottom: '2rem' }}>
        그룹 통계 분석
      </h2>

      <div className="card-grid">
        {/* 카드 1 */}
        <div className="analysis-card">
          <h3>📋 그룹 전체 영수증 조회</h3>
          <p>
            우리 그룹 전체의 영수증 내역을 조회하고 확인할 수 있어요.
          </p>
          <button type="button" className="btn" onClick={handleGroupReceipts}>
            그룹 영수증 보기
          </button>
        </div>

        {/* 카드 2 */}
        <div className="analysis-card">
          <h3>🏪 상위 지출 점포 조회</h3>
          <p>
            우리 그룹이 가장 많이 돈을 쓴 상점을 확인할 수 있어요.
          </p>
          <button type="button" className="btn" onClick={() => navigate('/group/top-stores')}>
            상위 점포 보기
          </button>
        </div>

        {/* 카드 3 */}
        <div className="analysis-card">
          <h3>📈 카테고리별 지출 분석</h3>
          <p>
            그룹의 지출을 카테고리별로 나눠서 분석할 수 있어요.
          </p>
          <button type="button" className="btn" onClick={() => navigate('/group/category-stats')}>
            카테고리 통계 보기
          </button>
        </div>
      </div>
    </main>
  );
};

export default GroupAnalysisPage;
