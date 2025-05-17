import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../../components/auth/Auth';
import '../../styles/button.css';
import '../../styles/card.css';
import '../../styles/layout.css';
import '../../styles/form.css';

const TopStoresPage = () => {
  const { userInfo } = useAuth();
  const today = new Date();
  const thisYear = today.getFullYear();
  const thisMonth = String(today.getMonth() + 1).padStart(2, '0');

  const [stores, setStores] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [year, setYear] = useState(thisYear);
  const [month, setMonth] = useState(thisMonth);

  useEffect(() => {
    if (!userInfo?.groupId) return;

    const fetchTopStores = async () => {
      setLoading(true);
      try {
        const response = await axios.get(
          `${process.env.REACT_APP_API_URL}/receipts/stats/group/${userInfo.groupId}/stores/${year}/${month}`
        );
        setStores(response.data);
        setError(null);
      } catch (err) {
        console.error('데이터 불러오기 실패:', err);
        setStores([]);
        if (err.response && err.response.status === 400) {
          setError('해당 기간에 데이터가 없습니다.');
        } else {
          setError('서버 오류가 발생했습니다.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchTopStores();
  }, [userInfo, year, month]);

  const handleYearChange = (e) => setYear(e.target.value);
  const handleMonthChange = (e) => setMonth(e.target.value);

  return (
    <main className="page-container">
      <h1 style={{ textAlign: 'center', fontSize: '1.8rem', fontWeight: '700', marginBottom: '2rem' }}>
        📊 그룹 상위 지출 점포 목록
      </h1>

      <section style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem' }}>
        <div>
          <select className="select-box" value={year} onChange={handleYearChange}>
            {[2023, 2024, 2025].map((y) => (
              <option key={y} value={y}>{y}년</option>
            ))}
          </select>

          <select className="select-box" value={month} onChange={handleMonthChange}>
            {Array.from({ length: 12 }, (_, i) => String(i + 1).padStart(2, '0')).map((m) => (
              <option key={m} value={m}>{m}월</option>
            ))}
          </select>
        </div>
      </section>

      {loading && <p style={{ textAlign: 'center' }}>로딩 중...</p>}
      {error && <p style={{ textAlign: 'center', color: 'red' }}>{error}</p>}
      {!loading && !error && stores.length === 0 && (
        <p style={{ textAlign: 'center', color: '#6b7280' }}>조회된 데이터가 없습니다.</p>
      )}

      {!loading && !error && stores.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
          {stores.map((store, index) => (
            <div
              key={index}
              className="card"
              style={{
                padding: '1.5rem',
                borderRadius: '1.2rem',
                backgroundColor: '#ffffff',
                boxShadow: '0 2px 6px rgba(0, 0, 0, 0.05)',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontSize: '1.1rem', fontWeight: '600', color: '#111827' }}>
                   {store.storeName}
                </h3>
                <span style={{ backgroundColor: '#f3f4f6', padding: '0.4rem 0.8rem', borderRadius: '999px', fontSize: '0.85rem', color: '#6b7280' }}>
                  #{index + 1} 순위
                </span>
              </div>
              <p style={{ marginTop: '0.8rem', fontSize: '0.95rem', color: '#374151' }}>이용 횟수: <strong>{store.usageCount}</strong>회</p>
              <p style={{ marginTop: '0.2rem', fontSize: '0.95rem', color: '#4f46e5', fontWeight: '600' }}>
                총 지출: {(store.totalSpent ?? 0).toLocaleString()}원
              </p>
            </div>
          ))}
        </div>
      )}
    </main>
  );
};

export default TopStoresPage;
