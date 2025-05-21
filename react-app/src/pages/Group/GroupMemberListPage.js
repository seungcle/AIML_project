import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getAccessToken } from '../../components/auth/Auth';
import '../../styles/layout.css';
import '../../styles/card.css';
import '../../styles/form.css';
import '../../styles/button.css'; 

function GroupMemberListPage() {
  const { groupId } = useParams();
  const [budgets, setBudgets] = useState([]);
  const [limits, setLimits] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchBudgets = async () => {
      try {
        const token = getAccessToken();
        const res = await fetch(`${process.env.REACT_APP_API_URL}/group/${groupId}/members/budgets`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) throw new Error('지출 한도 목록을 불러오는 데 실패했습니다.');

        const data = await res.json();
        setBudgets(data);

        const initialLimits = {};
        data.forEach((member) => {
          initialLimits[member.memberId] = member.budget;
        });
        setLimits(initialLimits);
        setLoading(false);
      } catch (err) {
        console.error(err);
        setError(err.message);
        setLoading(false);
      }
    };

    fetchBudgets();
  }, [groupId]);

  const handleLimitChange = (memberId, value) => {
    setLimits((prev) => ({ ...prev, [memberId]: value }));
  };

  const saveLimit = async (memberId) => {
    const amount = limits[memberId];
    if (amount === '' || amount == null) return alert('금액을 입력하세요.');

    try {
      const token = getAccessToken();
      const res = await fetch(
        `${process.env.REACT_APP_API_URL}/group/${groupId}/members/${memberId}/budget?budget=${amount}`,
        {
          method: 'PUT',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (res.ok) {
        alert('✅ 저장 완료!');
      } else {
        const text = await res.text();
        alert(`❌ 실패: ${text || '오류가 발생했습니다.'}`);
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
          {budgets.map((member) => (
            <div key={member.memberId} className="card" style={{ padding: '1rem', marginBottom: '1rem' }}>
              <div style={{ marginBottom: '0.5rem' }}>
                <strong>{member.name}</strong>{' '}
                <span style={{ color: '#6b7280' }}>({member.email})</span>
              </div>

              <p style={{ margin: '0.5rem 0', color: '#4b5563' }}>
                현재 한도: <strong>{Number(limits[member.memberId]).toLocaleString()}원</strong>
              </p>

              <div className="form-group" style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <input
                  type="number"
                  placeholder="새 지출 한도 입력"
                  className="form-input"
                  value={limits[member.memberId]}
                  onChange={(e) => handleLimitChange(member.memberId, e.target.value)}
                  style={{
                    maxWidth: '150px',
                    height: '2.5rem', // 버튼 높이와 맞춤
                    padding: '0.6rem 1rem',
                    marginBottom: 0, // 버튼과 수평 정렬 맞춤
                    fontSize: '1rem',
                  }}
                />
                <button className="btn" onClick={() => saveLimit(member.memberId)}>
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
