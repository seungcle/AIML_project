import React, { useState } from 'react';
import GroupJoinRequests from '../Group/JoinGroupPage';
//import { useNavigate } from 'react-router-dom';

function GroupManagement() {
  //const navigate = useNavigate();
  const [showJoinRequests, setShowJoinRequests] = useState(false);

  // 그룹 신청 목록 보기 핸들러
  const handleViewJoinRequests = () => {
    setShowJoinRequests((prev) => !prev);
  };

  // 전체 지출 내역 보기 핸들러
  const handleViewExpenseHistory = () => {
    // 아직 페이지가 없으므로, 나중에 구현 예정
    console.log('전체 지출 내역 보기 기능은 추후 구현 예정입니다.');
  };

  return (
    <div>
      <h2>그룹 관리 페이지</h2>
      <div className="button-container">
        <button onClick={handleViewJoinRequests}>그룹 신청 목록 보기</button>
        <button onClick={handleViewExpenseHistory}>전체 지출 내역 보기</button>
      </div>
      {showJoinRequests && <GroupJoinRequests />}
    </div>
  );
}

export default GroupManagement;