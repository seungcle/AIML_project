import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth, getAccessToken, refreshAccessToken } from '../components/auth';

function CreateGroup() {
  const [groupName, setGroupName] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { setUserInfo } = useAuth();
  const navigate = useNavigate();

  const handleCreateGroup = async () => {
    if (!groupName) {
      setMessage('그룹 이름을 입력하세요.');
      return;
    }

    const url = `${process.env.REACT_APP_API_URL}/group/create`;

    try {
      setIsLoading(true);
      
      let token = getAccessToken();

      // 토큰이 없거나 만료되었을 경우 갱신 시도
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
          token = getAccessToken();
        } else {
          setMessage('로그인이 필요합니다.');
          setIsLoading(false);
          return;
        }
      }

      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ name: groupName }),
      });

      if (response.ok) {
        const data = await response.json();
        setMessage('그룹 생성 성공!');

        // 유저 정보를 업데이트하여 관리자 설정
        setUserInfo((prevUserInfo) => ({
          ...prevUserInfo,
          groupId: data.id,
          admin: true,
        }));

        // 브라우저 기본 알림 메시지 표시
        alert('그룹이 성공적으로 생성되었습니다!');

        // 홈으로 이동
        navigate('/');
      } else if (response.status === 403) {
        setMessage('접근 권한이 없습니다. 다시 로그인하세요.');
      } else {
        const errorData = await response.json();
        setMessage(errorData.message || '그룹 생성 실패. 다시 시도하세요.');
      }
    } catch (error) {
      console.error('그룹 생성 중 오류:', error);
      setMessage('서버 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h2>그룹 만들기</h2>
      <div>
        <input
          type="text"
          placeholder="그룹 이름을 입력하세요"
          value={groupName}
          onChange={(e) => setGroupName(e.target.value)}
        />
        <button onClick={handleCreateGroup} disabled={isLoading}>
          {isLoading ? '생성 중...' : '그룹 생성'}
        </button>
      </div>
      {message && (
        <div>
          <p>{message}</p>
        </div>
      )}
    </div>
  );
}

export default CreateGroup;
