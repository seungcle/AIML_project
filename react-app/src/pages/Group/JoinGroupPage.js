import React, { useState } from 'react';
import axios from 'axios';
import { getAccessToken, refreshAccessToken } from '../../components/auth/Auth';
import GetGroup from '../../components/group/GetGroup';
import '../../styles/group.css';

function JoinGroup() {
  const [selectedGroup, setSelectedGroup] = useState(null); // 그룹 객체로 변경
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleJoinGroup = async () => {
    if (!selectedGroup) {
      setMessage('가입할 그룹을 선택하세요.');
      return;
    }

    let token = getAccessToken();
    if (!token) {
      const refreshed = await refreshAccessToken();
      if (refreshed) {
        token = getAccessToken();
      } else {
        setMessage('로그인이 필요합니다.');
        return;
      }
    }

    const url = `${process.env.REACT_APP_API_URL}/group/join/${selectedGroup.id}`;
    try {
      setIsLoading(true);
      await axios.post(url, {}, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });
      setMessage(`✅ 그룹 '${selectedGroup.name}'에 가입 요청을 보냈습니다.`);
    } catch (error) {
      if (error.response?.status === 400) {
        setMessage(`이미 '${selectedGroup.name}' 그룹에 가입 요청을 보냈습니다.`);
      } else if (error.response?.status === 401) {
        setMessage('인증이 필요합니다. 다시 로그인하세요.');
      } else if (error.response?.status === 403) {
        setMessage('권한이 없습니다. 관리자에게 문의하세요.');
      } else {
        setMessage(error.response?.data?.message || '서버 오류가 발생했습니다.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="group-container">
      <h2 className="group-title">그룹 가입하기</h2>
      <div className="group-form">
        <GetGroup onSelectGroup={(group) => setSelectedGroup(group)} />

        {selectedGroup && (
          <div className="group-selected">
            <p>선택한 그룹: <strong>{selectedGroup.name}</strong></p>
            <button onClick={handleJoinGroup} className="group-join-btn" disabled={isLoading}>
              {isLoading ? '가입 요청 중...' : '가입 요청 보내기'}
            </button>
          </div>
        )}

        {isLoading && (
          <p className="group-message">요청을 처리 중입니다...</p>
        )}

        {message && (
          <p
            className="group-message"
            style={{ color: message.includes('✅') ? 'green' : 'red' }}
          >
            {message}
          </p>
        )}
      </div>
    </div>
  );
}

export default JoinGroup;
