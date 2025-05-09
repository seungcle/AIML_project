import React, { useState } from 'react';
import GroupJoinRequests from '../../components/group/GroupJoinRequests'; // 경로 수정
import '../../styles/group.css';

function GroupManagement() {
  const [showJoinRequests, setShowJoinRequests] = useState(false);

  const handleViewJoinRequests = () => {
    setShowJoinRequests((prev) => !prev);
  };

  const handleViewExpenseHistory = () => {
    alert('전체 지출 내역 보기 기능은 추후 구현 예정입니다.');
  };

  return (
    <div className="group-container">
      <h2 className="group-title">그룹 관리</h2>

      <div className="group-form">
        <button onClick={handleViewJoinRequests}>
          {showJoinRequests ? '닫기' : '그룹 신청 목록 보기'}
        </button>
        <button onClick={handleViewExpenseHistory}>전체 지출 내역 보기</button>
      </div>

      {showJoinRequests && <GroupJoinRequests />}
    </div>
  );
}

export default GroupManagement;
