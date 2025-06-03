import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth, getAccessToken } from '../../components/auth/Auth';
import GroupJoinRequests from '../../components/group/GroupJoinRequests';
import DeleteGroupButton from '../../components/group/DeleteGroupButton';
import '../../styles/card.css';
import '../../styles/form.css';
import '../../styles/layout.css';
import '../../styles/button.css';

function GroupManagement() {
  const [showJoinRequests, setShowJoinRequests] = useState(false);
  const [isDuplicateCheckEnabled, setIsDuplicateCheckEnabled] = useState(null);
  const navigate = useNavigate();
  const { userInfo } = useAuth();

  useEffect(() => {
    const fetchGroupInfo = async () => {
      if (!userInfo?.groupId) return;
      try {
        const token = getAccessToken();
        const res = await fetch(`${process.env.REACT_APP_API_URL}/group/my-group`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const data = await res.json();
          setIsDuplicateCheckEnabled(data.preventDuplicateReceipt);
        } else {
          console.warn('그룹 정보 불러오기 실패');
        }
      } catch (err) {
        console.error('그룹 정보 요청 중 에러:', err);
      }
    };

    fetchGroupInfo();
  }, [userInfo]);

  const toggleDuplicateCheck = async () => {
    if (!userInfo?.groupId) return;
    const token = getAccessToken();
    const newValue = !isDuplicateCheckEnabled;

    try {
      const res = await fetch(
        `${process.env.REACT_APP_API_URL}/group/${userInfo.groupId}/check-duplicate?enable=${newValue}`,
        {
          method: 'PUT',
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (res.ok) {
        setIsDuplicateCheckEnabled(newValue);
      } else {
        alert('변경에 실패했습니다.');
      }
    } catch (err) {
      console.error('중복 확인 설정 요청 중 에러:', err);
      alert('네트워크 오류가 발생했습니다.');
    }
  };

  const handleViewJoinRequests = () => {
    setShowJoinRequests((prev) => !prev);
  };

  const handleGoToLimitPage = () => {
    if (userInfo?.groupId) {
      navigate(`/group/${userInfo.groupId}/members`);
    } else {
      alert('그룹 ID를 불러올 수 없습니다.');
    }
  };

  const handleGoToDelegatePage = () => {
    if (userInfo?.groupId) {
      navigate(`/group/${userInfo.groupId}/delegate`);
    } else {
      alert('그룹 ID를 불러올 수 없습니다.');
    }
  };

  return (
    <div className="page-container">
      <div className="card">
        <h2 className="form-title">📋 그룹 관리</h2>
        <p className="form-info-text">아래 기능을 사용하여 그룹을 관리하세요.</p>

        <div
          className="form-group"
          style={{
            display: 'flex',
            flexDirection: 'column',
            gap: '1rem',
            alignItems: 'flex-start',
          }}
        >
          <button className="btn" style={{ width: 'fit-content' }} onClick={handleViewJoinRequests}>
            {showJoinRequests ? '⛔ 신청 목록 닫기' : '👥 그룹 신청 목록 보기'}
          </button>

          <button className="btn" style={{ width: 'fit-content' }} onClick={handleGoToLimitPage}>
            🧾 멤버별 지출 한도 설정
          </button>

          <DeleteGroupButton groupId={userInfo?.groupId} />

          <button className="btn" style={{ width: 'fit-content' }} onClick={handleGoToDelegatePage}>
            🛡️ 관리자 권한 위임하기
          </button>

          {userInfo?.groupId && isDuplicateCheckEnabled !== null && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <label className="switch">
                <input
                  type="checkbox"
                  checked={isDuplicateCheckEnabled}
                  onChange={toggleDuplicateCheck}
                />
                <span className="slider round"></span>
              </label>
              <span style={{ fontSize: '0.95rem', color: '#374151' }}>
                중복 여부: {isDuplicateCheckEnabled ? 'ON' : 'OFF'}
              </span>
            </div>
          )}
        </div>

        {showJoinRequests && (
          <div style={{ marginTop: '1.5rem' }}>
            <GroupJoinRequests />
          </div>
        )}
      </div>
    </div>
  );
}

export default GroupManagement;