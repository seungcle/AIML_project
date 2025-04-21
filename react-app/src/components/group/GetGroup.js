import React, { useEffect, useState } from 'react';
import { getAccessToken, refreshAccessToken } from '../auth/Auth';

function GetGroup({ onSelectGroup }) {
  const [groups, setGroups] = useState([]); // 그룹 목록 상태
  const [error, setError] = useState(''); // 오류 메시지 상태
  const [isLoading, setIsLoading] = useState(false); // 로딩 상태

  // 그룹 목록 가져오기
  useEffect(() => {
    const fetchGroups = async () => {
      setIsLoading(true);
      try {
        let token = getAccessToken();
        if (!token) {
          const refreshed = await refreshAccessToken();
          if (refreshed) {
            token = getAccessToken();
          } else {
            setError('로그인이 필요합니다.');
            setIsLoading(false);
            return;
          }
        }

        const response = await fetch(`${process.env.REACT_APP_API_URL}/group`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });

        if (response.ok) {
          const data = await response.json();
          setGroups(data); // 그룹 목록 저장
        } else if (response.status === 403) {
          setError('접근 권한이 없습니다. 다시 로그인하세요.');
        } else {
          const errorData = await response.json();
          setError(errorData.message || '그룹 목록을 불러오지 못했습니다.');
        }
      } catch (err) {
        console.error('그룹 조회 중 오류:', err);
        setError('서버 오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchGroups();
  }, []);

  // 그룹 가입 요청
  const handleJoinGroup = async (groupId) => {
    setIsLoading(true);
    try {
      let token = getAccessToken();
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
          token = getAccessToken();
        } else {
          setError('로그인이 필요합니다.');
          setIsLoading(false);
          return;
        }
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/group/join/${groupId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        alert(`그룹 가입 요청 성공: ${data.status}`);
      } else if (response.status === 403) {
        setError('접근 권한이 없습니다. 다시 로그인하세요.');
      } else {
        const errorData = await response.json();
        setError(errorData.message || '그룹 가입 요청 실패. 다시 시도하세요.');
      }
    } catch (err) {
      console.error('그룹 가입 요청 중 오류:', err);
      setError('서버 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 로딩 중일 때 UI
  if (isLoading) {
    return <p>로딩 중...</p>;
  }

  // 에러 발생 시 UI
  if (error) {
    return <p>{error}</p>;
  }

  // 그룹 목록과 가입 버튼 UI
  return (
    <div>
      <h3>그룹 목록</h3>
      <ul>
        {groups.map((group) => (
          <li key={group.id}>
            {group.name || '이름 없음'} 
            <button onClick={() => handleJoinGroup(group.id)}>가입</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default GetGroup;
