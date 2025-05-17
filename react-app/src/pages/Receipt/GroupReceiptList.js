import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../../components/auth/Auth';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from 'recharts';
import '../../styles/card.css';
import '../../styles/layout.css';
import '../../styles/button.css';
import '../../styles/form.css';

const GroupReceiptList = () => {
  const { userInfo } = useAuth();
  const { memberId } = useParams();
  const today = new Date();
  const thisYear = today.getFullYear();
  const thisMonth = String(today.getMonth() + 1).padStart(2, '0');

  const [receipts, setReceipts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [year, setYear] = useState(thisYear);
  const [month, setMonth] = useState(thisMonth);
  const [selectedDate, setSelectedDate] = useState(null);

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

  const handleDownloadExcel = async () => {
    if (!userInfo?.groupId) {
      alert('그룹 ID를 확인할 수 없습니다.');
      return;
    }

    try {
      const response = await axios.get(
        `${process.env.REACT_APP_API_URL}/receipts/stats/group/${userInfo.groupId}/export/${year}/${month}`,
        { responseType: 'blob' }
      );
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `group-receipts-${year}-${month}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('엑셀 다운로드 실패:', error);
      alert('엑셀 파일 다운로드에 실패했습니다.');
    }
  };

  const handleBarClick = (e) => {
    if (e && e.activeLabel) {
      const clickedDate = e.activeLabel;
      setSelectedDate((prev) => (prev === clickedDate ? null : clickedDate));
    }
  };

  // 날짜별 지출 합계 계산
  const dailySpendingData = receipts.reduce((acc, receipt) => {
    const date = receipt.date.slice(0, 10);
    const existing = acc.find((item) => item.date === date);
    if (existing) {
      existing.amount += receipt.amount;
    } else {
      acc.push({ date, amount: receipt.amount });
    }
    return acc;
  }, []);

  const filteredReceipts = selectedDate
    ? receipts.filter((receipt) => receipt.date.slice(0, 10) === selectedDate)
    : [];

  if (!memberId || memberId === 'undefined') {
    return (
      <div className="page-container" style={{ textAlign: 'center', marginTop: '4rem' }}>
        <h2 style={{ fontSize: '1.5rem', color: '#ef4444' }}>잘못된 접근입니다</h2>
        <p style={{ marginTop: '0.5rem', color: '#6b7280' }}>회원 정보를 다시 확인하거나 홈으로 돌아가주세요.</p>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h1 style={{ textAlign: 'center', fontSize: '1.8rem', fontWeight: '700', marginBottom: '2rem' }}>
        그룹 내 개인 영수증 목록
      </h1>

      {/* 연도/월 선택 */}
      <section style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1.5rem', marginBottom: '3rem' }}>
        <div>
          <select className="select-box" value={year} onChange={handleYearChange}>
            {[2023, 2024, 2025].map((y) => (
              <option key={y} value={y}>{y}년</option>
            ))}
          </select>

          <select className="select-box" value={month} onChange={handleMonthChange}>
            {Array.from({ length: 12 }, (_, i) => String(i + 1).padStart(2, '0')).map((m) => (
              <option key={m} value={m} disabled={isFuture(year, m)}>
                {m}월
              </option>
            ))}
          </select>
        </div>
      </section>

      {/* 총합 + 그래프 */}
      {!loading && !error && receipts.length > 0 && (
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
            이번달 총 지출: {totalAmount.toLocaleString()}원
          </div>

          {/* 그래프 */}
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
              <BarChart data={dailySpendingData} onClick={handleBarClick} maxBarSize={40}>
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

      {/* 선택된 날짜 영수증 리스트 */}
      {selectedDate && filteredReceipts.length > 0 && (
        <div style={{ marginBottom: '2rem' }}>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 'bold', marginBottom: '1rem' }}>
            {selectedDate} 지출 상세
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {filteredReceipts.map((receipt) => (
              <div
                key={receipt.receiptId}
                className="card"
                style={{
                  padding: '1.5rem',
                  borderRadius: '1.2rem',
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
        </div>
      )}

      {/* 엑셀 다운로드 */}
      <div style={{ marginTop: '3rem', textAlign: 'center' }}>
        <p style={{ fontSize: '0.95rem', marginBottom: '0.5rem', color: '#6b7280' }}>
          선택한 기간의 그룹 전체 영수증을 엑셀 파일로 저장할 수 있어요.
        </p>
        <button className="btn-light" onClick={handleDownloadExcel} style={{ padding: '0.7rem 1.2rem' }}>
          📥 엑셀 다운로드
        </button>
      </div>
    </div>
  );
};

export default GroupReceiptList;
