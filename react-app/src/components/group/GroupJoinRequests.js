import React, { useEffect, useState } from 'react';
import { useAuth, getAccessToken } from '../components/auth/Auth';

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
            token = getAccessToken(); // 갱신된 토큰을 다시 가져옴
          } else {
            setError('토큰 갱신에 실패했습니다. 다시 로그인해주세요.');
            setLoading(false);
            return;
          }
        }

        // 콘솔 로그 추가 - 실제로 전송하는 토큰 확인
        console.log('Authorization Header:', `Bearer ${token}`);

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
        console.error('오류 발생:', err);
        if (err.response && err.response.status === 403) {
          setError('접근 권한이 없습니다. 관리자 권한이 필요합니다.');
        } else {
          setError('서버 오류가 발생했습니다.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchJoinRequests();
  }, [userInfo, refreshAccessToken]);

  const handleApprove = async (joinRequestId) => {
    try {
      let token = getAccessToken();
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
          token = getAccessToken();
        } else {
          setError('토큰 갱신에 실패했습니다. 다시 로그인해주세요.');
          return;
        }
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/group/requests/approve/${joinRequestId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setJoinRequests(joinRequests.filter(request => request.id !== joinRequestId));
        setSuccessMessage('가입 요청이 승인되었습니다.');
      } else {
        setError('가입 요청 승인에 실패했습니다.');
      }
    } catch (err) {
      console.error('오류 발생:', err);
      setError('서버 오류가 발생했습니다.');
    }
  };

  const handleReject = async (joinRequestId) => {
    try {
      let token = getAccessToken();
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
          token = getAccessToken();
        } else {
          setError('토큰 갱신에 실패했습니다. 다시 로그인해주세요.');
          return;
        }
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/group/requests/reject/${joinRequestId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setJoinRequests(joinRequests.filter(request => request.id !== joinRequestId));
        setSuccessMessage('가입 요청이 거절되었습니다.');
      } else {
        setError('가입 요청 거절에 실패했습니다.');
      }
    } catch (err) {
      console.error('오류 발생:', err);
      setError('서버 오류가 발생했습니다.');
    }
  };

  if (loading) {
    return <p>로딩 중...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div>
      <h2>가입 신청 목록</h2>
      {successMessage && <p className="success-message">{successMessage}</p>}
      {joinRequests.length > 0 ? (
        <table>
          <thead>
            <tr>
              <th>신청 ID</th>
              <th>회원 이름</th>
              <th>회원 이메일</th>
              <th>그룹 이름</th>
              <th>상태</th>
              <th>작업</th>
            </tr>
          </thead>
          <tbody>
            {joinRequests.map((request) => (
              <tr key={request.id}>
                <td>{request.id}</td>
                <td>{request.memberName}</td>
                <td>{request.memberEmail}</td>
                <td>{request.groupName}</td>
                <td>{request.status}</td>
                <td>
                  <button onClick={() => handleApprove(request.id)}>승인하기</button>
                  <button onClick={() => handleReject(request.id)}>거절하기</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>현재 가입 신청이 없습니다.</p>
      )}
    </div>
  );
}

export default GroupJoinRequests;
