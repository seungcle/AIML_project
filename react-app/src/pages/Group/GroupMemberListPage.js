import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getAccessToken } from '../../components/auth/Auth';
import '../../styles/layout.css';
import '../../styles/card.css';
import '../../styles/form.css';

function GroupMemberListPage() {
  const { groupId } = useParams();
  const [members, setMembers] = useState([]);
  const [limits, setLimits] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // 멤버 목록 불러오기
  useEffect(() => {
    const fetchMembers = async () => {
      try {
        const token = getAccessToken();
        const res = await fetch(`${process.env.REACT_APP_API_URL}/group/${groupId}/members`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) throw new Error('멤버 목록을 불러오는 데 실패했습니다.');

        const data = await res.json();
        setMembers(data);
        setLoading(false);
      } catch (err) {
        console.error(err);
        setError(err.message);
        setLoading(false);
      }
    };

    fetchMembers();
  }, [groupId]);

  // 한도 입력 변경
  const handleLimitChange = (memberId, value) => {
    setLimits((prev) => ({ ...prev, [memberId]: value }));
  };

  // 한도 저장
  const saveLimit = async (memberId) => {
    const amount = limits[memberId];
    if (!amount) return alert('금액을 입력하세요.');

    try {
      const token = getAccessToken();
      const res = await fetch(`${process.env.REACT_APP_API_URL}/group/${groupId}/limit/${memberId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ amount: Number(amount) }),
      });

      if (res.ok) {
        alert('✅ 저장 완료!');
      } else {
        const data = await res.json();
        alert(`❌ 실패: ${data.message || '오류가 발생했습니다.'}`);
      }
    } catch (err) {
      console.error(err);
      alert('❌ 서버 통신 실패');
    }
  };

  if (loading) return <p className="form-info-text">불러오는 중...</p>;
  if (error) return <p className="form-error-text">{error}</p>;

  return (
    <div className="page-container">
      <div className="card">
        <h2>👥 그룹 멤버 관리</h2>
        <p className="form-info-text">각 멤버의 지출 한도를 설정할 수 있어요.</p>

        <div className="member-list">
          {members.map((member) => (
            <div key={member.id} className="card" style={{ padding: '1rem', marginBottom: '1rem' }}>
              <div style={{ marginBottom: '0.5rem' }}>
                <strong>{member.name}</strong> <span style={{ color: '#6b7280' }}>({member.email})</span>
              </div>
              <div className="form-group" style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <input
                  type="number"
                  placeholder="지출 한도 (원)"
                  className="form-input"
                  value={limits[member.id] || ''}
                  onChange={(e) => handleLimitChange(member.id, e.target.value)}
                  style={{ maxWidth: '150px' }}
                />
                <button className="btn" onClick={() => saveLimit(member.id)}>
                  저장
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default GroupMemberListPage;