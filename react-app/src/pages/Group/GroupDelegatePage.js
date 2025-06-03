import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getAccessToken, useAuth } from '../../components/auth/Auth';
import DelegateAdminButton from '../../components/group/DelegateAdminButton';
import '../../styles/card.css';
import '../../styles/form.css';
import '../../styles/layout.css';

function GroupDelegatePage() {
  const { groupId } = useParams();
  const { userInfo } = useAuth();
  const [members, setMembers] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchMembers = async () => {
      try {
        const token = getAccessToken();
        const res = await fetch(`${process.env.REACT_APP_API_URL}/group/${groupId}/members`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) throw new Error('멤버 목록을 불러오지 못했습니다.');

        const data = await res.json();
        console.log('📦 멤버 목록:', data);
        setMembers(data);
      } catch (err) {
        console.error(err);
        setError(err.message);
      }
    };

    fetchMembers();
  }, [groupId]);

  console.log('🧑 현재 로그인 유저 정보:', userInfo);

  if (error) return <p className="form-error-text">{error}</p>;

  return (
    <div className="page-container">
      <div className="card">
        <h2 className="form-title">🛡️ 관리자 권한 위임</h2>
        <p className="form-info-text">다른 멤버에게 관리자 권한을 넘길 수 있어요.</p>

        {members.map((member) => (
          <div
            key={member.id}
            className="card"
            style={{ marginBottom: '1rem', padding: '1rem' }}
          >
            <p>
              <strong>{member.name}</strong>{' '}
              <span style={{ color: '#6b7280' }}>({member.email})</span>
            </p>

            {userInfo.admin && member.id !== userInfo.id && (
              <DelegateAdminButton
                memberId={member.id}
                currentUserId={userInfo.id}
                onSuccess={() => window.location.reload()}
              />
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default GroupDelegatePage;
