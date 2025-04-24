import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth, getAccessToken, refreshAccessToken } from '../../components/auth/Auth';
import '../../styles/group.css';

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

      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) token = getAccessToken();
        else {
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
        setUserInfo((prev) => ({
          ...prev,
          groupId: data.id,
          admin: true,
        }));
        alert('그룹이 성공적으로 생성되었습니다!');
        navigate('/');
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
    <div className="group-container">
      <h2 className="group-title">그룹 만들기</h2>
      <div className="group-form">
        <input
          type="text"
          placeholder="그룹 이름을 입력하세요"
          value={groupName}
          onChange={(e) => setGroupName(e.target.value)}
        />
        <button onClick={handleCreateGroup} disabled={isLoading}>
          {isLoading ? '생성 중...' : '그룹 생성'}
        </button>
        {message && <p className="group-message">{message}</p>}
      </div>
    </div>
  );
}

export default CreateGroup;
