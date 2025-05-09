import React, { useEffect, useState } from 'react';
import { getAccessToken, refreshAccessToken } from '../../components/auth/Auth';
import { useAuth } from '../../components/auth/Auth';
import '../../styles/card.css'; // 기존 카드 스타일 불러오기

function ReceiptMyList() {
  const { isLoggedIn, loading } = useAuth();
  const [receipts, setReceipts] = useState([]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchReceipts = async () => {
      try {
        let token = getAccessToken();

        if (!token) {
          const refreshed = await refreshAccessToken();
          if (refreshed) {
            token = getAccessToken();
          } else {
            setError('토큰 갱신에 실패했습니다. 다시 로그인해주세요.');
            setIsLoading(false);
            return;
          }
        }

        const response = await fetch(`${process.env.REACT_APP_API_URL}/receipts/my`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });

        if (response.ok) {
          const data = await response.json();
          setReceipts(data);
        } else if (response.status === 403) {
          setError('접근 권한이 없습니다. 로그인 상태를 확인해주세요.');
        } else {
          const errorData = await response.json();
          setError(errorData.message || '영수증 목록을 불러오는 데 실패했습니다.');
        }
      } catch (err) {
        console.error('오류 발생:', err);
        setError('서버 오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    if (isLoggedIn) {
      fetchReceipts();
    } else {
      setError('로그인이 필요합니다.');
      setIsLoading(false);
    }
  }, [isLoggedIn]);

  if (loading || isLoading) {
    return <p>로딩 중...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div style={{ padding: '2rem' }}>
      <h2 style={{ fontSize: '1.8rem', fontWeight: 'bold', marginBottom: '1.5rem' }}>
        영수증 목록
      </h2>
      {receipts.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {receipts.map((receipt) => (
            <div key={receipt.id} className="card" style={{ padding: '1.2rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                <span style={{ fontSize: '0.95rem', color: '#666' }}>{receipt.date}</span>
                <span style={{ fontWeight: 'bold', fontSize: '1rem' }}>{receipt.amount.toLocaleString()}원</span>
              </div>
              <div style={{ fontSize: '1.1rem', fontWeight: '500', marginBottom: '0.5rem' }}>
                {receipt.storeName}
              </div>
              <div style={{ fontSize: '0.9rem', color: '#888' }}>
                {receipt.storeAddress}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p>현재 등록된 영수증이 없습니다.</p>
      )}
    </div>
  );
}

export default ReceiptMyList;
