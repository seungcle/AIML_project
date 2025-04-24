import React, { useEffect, useState } from 'react';
import { getAccessToken, refreshAccessToken } from '../auth/Auth';

function GetGroup({ onSelectGroup }) {
  const [groups, setGroups] = useState([]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

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
          setGroups(data);
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

  if (isLoading) return <p>그룹 목록을 불러오는 중...</p>;
  if (error) return <p className="group-message">{error}</p>;

  return (
    <div className="group-list">
      {groups.length === 0 ? (
        <p className="group-message">등록된 그룹이 없습니다.</p>
      ) : (
        groups.map((group) => (
          <div
            key={group.id}
            className="group-item"
            onClick={() => onSelectGroup(group)} // 👈 그룹 객체 전체 전달
            style={{ cursor: 'pointer' }}
          >
            <span className="group-item-name">{group.name}</span>
            <button className="group-join-btn">선택</button>
          </div>
        ))
      )}
    </div>
  );
}

export default GetGroup;
