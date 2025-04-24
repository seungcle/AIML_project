import React, { useEffect, useState } from 'react';
import { useAuth, getAccessToken } from '../auth/Auth';
import '../../styles/group.css';

function GroupJoinRequests() {
  const { userInfo, refreshAccessToken } = useAuth();
  const [joinRequests, setJoinRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    const fetchJoinRequests = async () => {
      try {
        if (!userInfo || !userInfo.admin || !userInfo.groupId) {
          setError('접근 권한이 없습니다.');
          setLoading(false);
          return;
        }

        let token = getAccessToken();
        if (!token) {
          const refreshed = await refreshAccessToken();
          if (refreshed) {
            token = getAccessToken();
          } else {
            setError('토큰 갱신에 실패했습니다. 다시 로그인해주세요.');
            setLoading(false);
            return;
          }
        }

        const response = await fetch(`${process.env.REACT_APP_API_URL}/group/requests/${userInfo.groupId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });

        if (response.ok) {
          const data = await response.json();
          setJoinRequests(data);
        } else {
          const errorData = await response.json();
          setError(errorData.message || '가입 신청 목록을 불러오는 데 실패했습니다.');
        }
      } catch (err) {
        setError('서버 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchJoinRequests();
  }, [userInfo, refreshAccessToken]);

  const handleApprove = async (id) => {
    try {
      let token = getAccessToken();
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
          token = getAccessToken();
        } else {
          setError('토큰 갱신 실패. 다시 로그인해주세요.');
          return;
        }
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/group/requests/approve/${id}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setJoinRequests(prev => prev.filter(r => r.id !== id));
        setSuccessMessage('가입 요청이 승인되었습니다.');
      } else {
        setError('승인 실패');
      }
    } catch {
      setError('서버 오류');
    }
  };

  const handleReject = async (id) => {
    try {
      let token = getAccessToken();
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
          token = getAccessToken();
        } else {
          setError('토큰 갱신 실패. 다시 로그인해주세요.');
          return;
        }
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/group/requests/reject/${id}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setJoinRequests(prev => prev.filter(r => r.id !== id));
        setSuccessMessage('가입 요청이 거절되었습니다.');
      } else {
        setError('거절 실패');
      }
    } catch {
      setError('서버 오류');
    }
  };

  if (loading) return <p>로딩 중...</p>;
  if (error) return <p className="group-message">{error}</p>;

  return (
    <div className="join-requests-wrapper">
      <h2 className="group-title">가입 신청 목록</h2>
      {successMessage && <p className="success-message">{successMessage}</p>}
      {joinRequests.length > 0 ? (
        <table className="join-requests-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>이름</th>
              <th>이메일</th>
              <th>그룹</th>
              <th>상태</th>
              <th>작업</th>
            </tr>
          </thead>
          <tbody>
            {joinRequests.map((r) => (
              <tr key={r.id}>
                <td>{r.id}</td>
                <td>{r.memberName}</td>
                <td>{r.memberEmail}</td>
                <td>{r.groupName}</td>
                <td>{r.status}</td>
                <td>
                  <button className="btn-approve" onClick={() => handleApprove(r.id)}>승인</button>
                  <button className="btn-reject" onClick={() => handleReject(r.id)}>거절</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p className="group-message">현재 가입 요청이 없습니다.</p>
      )}
    </div>
  );
}

export default GroupJoinRequests;
