import React, { useEffect, useState } from 'react';
import { getAccessToken, refreshAccessToken, useAuth } from '../../components/auth/Auth';
import DeleteReceiptButton from '../../components/receipt/DeleteReceiptButton';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer
} from 'recharts';
import '../../styles/card.css';
import '../../styles/layout.css';
import '../../styles/button.css';

function ReceiptMyList() {
  const { isLoggedIn, loading: authLoading } = useAuth();
  const [receipts, setReceipts] = useState([]);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [selectedDate, setSelectedDate] = useState(null);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  const fetchReceipts = async (year, month) => {
    try {
      let token = getAccessToken();
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) token = getAccessToken();
        else throw new Error('토큰 갱신 실패');
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/receipts/my?year=${year}&month=${month}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) throw new Error('영수증 불러오기 실패');

      const data = await response.json();
      setReceipts(data);
      setError('');
    } catch (err) {
      setError(err.message || '서버 오류');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (isLoggedIn) fetchReceipts(selectedYear, selectedMonth);
    else {
      setError('로그인이 필요합니다.');
      setIsLoading(false);
    }
  }, [isLoggedIn, selectedYear, selectedMonth]);

  const handleYearChange = (e) => setSelectedYear(Number(e.target.value));
  const handleMonthChange = (e) => setSelectedMonth(Number(e.target.value));
  const handleChartClick = (e) => {
    if (e?.activeLabel) {
      const clickedDate = e.activeLabel;
      setSelectedDate((prev) => (prev === clickedDate ? null : clickedDate));
    }
  };

  const handleDelete = (receiptId) => {
    setReceipts((prev) => prev.filter((r) => r.receiptId !== receiptId));
  };

  const dailySpendingData = receipts.reduce((acc, receipt) => {
    const date = receipt.date.slice(0, 10);
    const existing = acc.find((item) => item.date === date);
    if (existing) existing.amount += receipt.amount;
    else acc.push({ date, amount: receipt.amount });
    return acc;
  }, []);

  const totalAmount = receipts.reduce((sum, r) => sum + r.amount, 0);

  if (authLoading || isLoading) return <p>로딩 중...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div className="page-container">
      <h1 style={{ textAlign: 'center', fontSize: '1.8rem', fontWeight: '700', marginBottom: '2rem' }}>
        내 영수증 목록
      </h1>

      <section style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1.5rem', marginBottom: '3rem' }}>
        <div>
          <select className="select-box" value={selectedYear} onChange={handleYearChange}>
            {[selectedYear - 1, selectedYear, selectedYear + 1].map((y) => (
              <option key={y} value={y}>{y}년</option>
            ))}
          </select>

          <select className="select-box" value={selectedMonth} onChange={handleMonthChange}>
            {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
              <option key={m} value={m}>{m}월</option>
            ))}
          </select>
        </div>
      </section>

      {!isLoading && receipts.length > 0 && (
        <>
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
            {selectedYear}년 {selectedMonth}월 총 지출: {totalAmount.toLocaleString()}원
          </div>

          <div style={{
            width: '100%',
            height: 300,
            backgroundColor: '#ffffff',
            borderRadius: '1rem',
            padding: '1rem',
            boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
            marginBottom: '2rem'
          }}>
            <ResponsiveContainer>
              <BarChart data={dailySpendingData} onClick={handleChartClick} maxBarSize={40}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="amount" fill="#4f46e5" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </>
      )}

      {selectedDate && receipts.filter(r => r.date.slice(0, 10) === selectedDate).length > 0 && (
        <div style={{ marginBottom: '2rem' }}>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 'bold', marginBottom: '1rem' }}>
            {selectedDate} 지출 상세
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {receipts.filter(r => r.date.slice(0, 10) === selectedDate).map((receipt) => (
              <div
                key={receipt.receiptId}
                className="card"
                style={{ position: 'relative', padding: '1.5rem', borderRadius: '1.2rem', backgroundColor: '#ffffff' }}
              >
                <DeleteReceiptButton receiptId={receipt.receiptId} onDelete={handleDelete} />
                <p><strong>상점명:</strong> {receipt.storeName}</p>
                <p><strong>주소:</strong> {receipt.storeAddress}</p>
                {receipt.memo && <p><strong>메모:</strong> {receipt.memo}</p>}
                <p><strong>날짜:</strong> {receipt.date}</p>
                <p><strong>금액:</strong> {receipt.amount.toLocaleString()}원</p>
                <p><strong>카테고리:</strong> {receipt.categoryName}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default ReceiptMyList;
