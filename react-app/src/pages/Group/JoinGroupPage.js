import React, { useState } from 'react';
import axios from 'axios';
import { getAccessToken, refreshAccessToken } from '../../components/auth/Auth';
import GetGroup from '../../components/group/GetGroup';

function JoinGroup() {
  const [selectedGroupId, setSelectedGroupId] = useState(null);
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleJoinGroup = async () => {
    if (!selectedGroupId) {
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

    const url = `${process.env.REACT_APP_API_URL}/group/join/${selectedGroupId}`;
    try {
      setIsLoading(true);
      const response = await axios.post(
        url,
        {},
        {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setMessage(`그룹 가입 요청 성공! 상태: ${response.data.status}`);
      setSelectedGroupId(null);
    } catch (error) {
      console.error('그룹 가입 요청 중 오류:', error);
      if (error.response?.status === 400) {
        setMessage('잘못된 요청입니다. 입력 값을 확인하세요.');
      } else if (error.response?.status === 401) {
        setMessage('인증이 필요합니다. 다시 로그인하세요.');
      } else if (error.response?.status === 403) {
        setMessage('권한이 없습니다. 관리자로부터 권한을 요청하세요.');
      } else {
        setMessage(error.response?.data?.message || '서버 오류가 발생했습니다.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h2>그룹 가입하기</h2>
      <GetGroup onSelectGroup={(groupId) => setSelectedGroupId(groupId)} />
      {selectedGroupId && (
        <div>
          <p>선택한 그룹 ID: {selectedGroupId}</p>
          <button onClick={handleJoinGroup} disabled={isLoading}>
            {isLoading ? '가입 요청 중...' : '그룹 가입'}
          </button>
        </div>
      )}
      {isLoading && <p>요청을 처리 중입니다...</p>}
      {message && <p style={{ color: message.includes('성공') ? 'green' : 'red' }}>{message}</p>}
    </div>
  );
}

export default JoinGroup;