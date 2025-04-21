// ReceiptList.js
import React, { useEffect, useState } from 'react';
import { getAccessToken, refreshAccessToken } from '../../components/auth/Auth'; // 토큰 관리 함수들
import { useAuth } from '../../components/auth/Auth'; // 사용자 정보와 로그인 상태를 확인하기 위한 useAuth 훅

function ReceiptList() {
  const { isLoggedIn, loading } = useAuth();
  const [receipts, setReceipts] = useState([]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchReceipts = async () => {
      try {
        let token = getAccessToken();

        // 토큰이 없거나 만료된 경우 갱신 시도
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

        // API 요청
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

    // 사용자가 로그인 되어 있을 때만 영수증 목록을 가져옵니다.
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
    <div>
      <h2>영수증 목록</h2>
      {receipts.length > 0 ? (
        <table>
          <thead>
            <tr>
              <th>날짜</th>
              <th>가게 이름</th>
              <th>금액</th>
              <th>주소</th>
            </tr>
          </thead>
          <tbody>
            {receipts.map((receipt) => (
              <tr key={receipt.id}>
                <td>{receipt.date}</td>
                <td>{receipt.storeName}</td>
                <td>{receipt.amount}</td>
                <td>{receipt.storeAddress}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>현재 등록된 영수증이 없습니다.</p>
      )}
    </div>
  );
}

export default ReceiptList;