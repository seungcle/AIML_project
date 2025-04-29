import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import '../../styles/card.css';
import '../../styles/layout.css';

const GroupMemberReceiptList = () => {
  const { memberId, year: urlYear, month: urlMonth } = useParams();
  const today = new Date();
  const thisYear = today.getFullYear();
  const thisMonth = String(today.getMonth() + 1).padStart(2, '0');

  const [receipts, setReceipts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [year, setYear] = useState(urlYear || thisYear);
  const [month, setMonth] = useState(urlMonth || thisMonth);

  // 🔥 총합 계산
  const totalAmount = receipts.reduce((sum, receipt) => sum + receipt.amount, 0);

  useEffect(() => {
    const fetchReceipts = async () => {
      if (!memberId || memberId === 'undefined') return;
      setLoading(true);
      try {
        const response = await axios.get(
          `${process.env.REACT_APP_API_URL}/receipts/stats/member/${memberId}/receipts/${year}/${month}`
        );
        setReceipts(response.data);
        setError(null);
      } catch (err) {
        if (err.response && err.response.status === 400) {
          setError('요청한 기간의 데이터가 없습니다.');
        } else {
          setError('데이터를 불러오는데 실패했습니다.');
        }
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchReceipts();
  }, [memberId, year, month]);

  const handleYearChange = (e) => setYear(e.target.value);
  const handleMonthChange = (e) => setMonth(e.target.value);

  const isFuture = (y, m) => {
    if (parseInt(y) > thisYear) return true;
    if (parseInt(y) === thisYear && parseInt(m) > parseInt(thisMonth)) return true;
    return false;
  };

  if (!memberId || memberId === 'undefined') {
    return (
      <div className="page-container" style={{ textAlign: 'center', marginTop: '3rem' }}>
        <h2 style={{ color: 'red' }}>잘못된 접근입니다.</h2>
        <p>회원 정보를 다시 확인하거나, 홈으로 돌아가주세요.</p>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
        그룹 내 개인 영수증 목록
      </h2>

      {/* 연도/월 선택 */}
      <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
        <select value={year} onChange={handleYearChange} style={{ marginRight: '1rem', padding: '0.5rem', fontSize: '1rem' }}>
          {[2023, 2024, 2025].map((y) => (
            <option key={y} value={y}>{y}년</option>
          ))}
        </select>

        <select value={month} onChange={handleMonthChange} style={{ padding: '0.5rem', fontSize: '1rem' }}>
          {Array.from({ length: 12 }, (_, i) => String(i + 1).padStart(2, '0')).map((m) => (
            <option
              key={m}
              value={m}
              disabled={isFuture(year, m)}
            >
              {m}월
            </option>
          ))}
        </select>
      </div>

      {/* 🔥 총합 표시 */}
      {!loading && !error && receipts.length > 0 && (
        <div style={{ 
          textAlign: 'center', 
          marginBottom: '2rem', 
          fontSize: '1.2rem', 
          fontWeight: 'bold', 
          color: '#4f46e5',
          backgroundColor: '#eef2ff',
          padding: '1rem',
          borderRadius: '0.75rem',
        }}>
          이번달 총 지출: {totalAmount.toLocaleString()}원
        </div>
      )}

      {/* 리스트 */}
      {loading ? (
        <p style={{ textAlign: 'center' }}>로딩 중...</p>
      ) : error ? (
        <p style={{ textAlign: 'center', color: 'red' }}>{error}</p>
      ) : receipts.length === 0 ? (
        <p style={{ textAlign: 'center' }}>등록된 영수증이 없습니다.</p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {receipts.map((receipt) => (
            <div
              key={receipt.receiptId}
              className="card"
              style={{
                padding: '1.5rem',
                borderRadius: '1rem',
                boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                backgroundColor: '#ffffff',
              }}
            >
              <p><strong>상점명:</strong> {receipt.storeName}</p>
              <p><strong>주소:</strong> {receipt.storeAddress}</p>
              <p><strong>메모:</strong> {receipt.memo}</p>
              <p><strong>날짜:</strong> {receipt.date}</p>
              <p><strong>금액:</strong> {receipt.amount.toLocaleString()}원</p>
              <p><strong>카테고리:</strong> {receipt.category}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default GroupMemberReceiptList;
