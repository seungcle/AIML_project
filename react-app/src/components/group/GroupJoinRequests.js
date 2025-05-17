import React, { useEffect, useState } from 'react';
import { useAuth, getAccessToken } from '../auth/Auth';
import '../../styles/card.css';
import '../../styles/form.css';
import '../../styles/button.css';

function GroupJoinRequests() {
  const { userInfo, refreshAccessToken } = useAuth();
  const [joinRequests, setJoinRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    const fetchJoinRequests = async () => {
      if (!userInfo?.admin || !userInfo.groupId) {
        setError('접근 권한이 없습니다.');
        setLoading(false);
        return;
      }

      try {
        let token = getAccessToken();
        if (!token && !(await refreshAccessToken())) {
          setError('토큰 갱신 실패');
          setLoading(false);
          return;
        }
        token = getAccessToken();

        const res = await fetch(`${process.env.REACT_APP_API_URL}/group/requests/${userInfo.groupId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const data = await res.json();
        if (res.ok) setJoinRequests(data);
        else setError(data.message || '요청 목록 불러오기 실패');
      } catch {
        setError('서버 오류');
      } finally {
        setLoading(false);
      }
    };

    fetchJoinRequests();
  }, [userInfo, refreshAccessToken]);

  const handleApprove = async (id) => {
    try {
      let token = getAccessToken();
      if (!token && !(await refreshAccessToken())) return;
      token = getAccessToken();

      const res = await fetch(`${process.env.REACT_APP_API_URL}/group/requests/approve/${id}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      });

      if (res.ok) {
        setJoinRequests((prev) => prev.filter((r) => r.id !== id));
        setSuccessMessage('✅ 가입 요청을 승인했어요!');
      } else setError('승인 실패');
    } catch {
      setError('서버 오류');
    }
  };

  const handleReject = async (id) => {
    try {
      let token = getAccessToken();
      if (!token && !(await refreshAccessToken())) return;
      token = getAccessToken();

      const res = await fetch(`${process.env.REACT_APP_API_URL}/group/requests/reject/${id}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      });

      if (res.ok) {
        setJoinRequests((prev) => prev.filter((r) => r.id !== id));
        setSuccessMessage('❌ 요청을 거절했습니다.');
      } else setError('거절 실패');
    } catch {
      setError('서버 오류');
    }
  };

  if (loading) return <p className="form-info-text">불러오는 중...</p>;
  if (error) return <p className="form-info-text" style={{ color: 'red' }}>{error}</p>;

  return (
    <div className="form-group" style={{ gap: '1rem' }}>
      <h3 className="form-title">📥 가입 신청 목록</h3>

      {successMessage && (
        <p className="form-info-text" style={{ color: '#10b981' }}>{successMessage}</p>
      )}

      {joinRequests.length === 0 ? (
        <p className="form-info-text">가입 요청이 없습니다.</p>
      ) : (
        joinRequests.map((req) => (
          <div key={req.id} className="card" style={{ padding: '1rem', marginBottom: '1rem' }}>
            <div style={{ marginBottom: '0.5rem' }}>
              <strong>{req.memberName}</strong>
              <span style={{ color: '#6b7280', marginLeft: '0.5rem' }}>{req.memberEmail}</span>
            </div>
            <p className="form-info-text" style={{ fontSize: '0.9rem' }}>
              그룹: {req.groupName} / 상태: 🕓 {req.status === 'Pending' ? '대기중' : req.status}
            </p>
            <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.75rem' }}>
              <button
                className="btn-sm"
                style={{ backgroundColor: '#10b981', color: 'white', borderRadius: '8px' }}
                onClick={() => handleApprove(req.id)}
              >
                ✅ 승인
              </button>
              <button
                className="btn-sm"
                style={{
                  backgroundColor: '#f3f4f6',
                  border: '1px solid #d1d5db',
                  color: '#b91c1c',
                  borderRadius: '8px',
                }}
                onClick={() => handleReject(req.id)}
              >
                ❌ 거절
              </button>
            </div>
          </div>
        ))
      )}
    </div>
  );
}

export default GroupJoinRequests;
