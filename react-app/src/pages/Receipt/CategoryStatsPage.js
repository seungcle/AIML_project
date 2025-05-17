import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../../components/auth/Auth';
import {
  PieChart, Pie, Tooltip, ResponsiveContainer, Cell
} from 'recharts';
import '../../styles/button.css';
import '../../styles/card.css';
import '../../styles/layout.css';
import '../../styles/form.css';

const COLORS = ['#6366f1', '#60a5fa', '#34d399', '#fbbf24', '#f87171', '#a78bfa'];

const GroupCategoryStatsPage = () => {
  const { userInfo } = useAuth();
  const today = new Date();
  const thisYear = today.getFullYear();
  const thisMonth = String(today.getMonth() + 1).padStart(2, '0');

  const [stats, setStats] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [year, setYear] = useState(thisYear);
  const [month, setMonth] = useState(thisMonth);

  useEffect(() => {
    if (!userInfo?.groupId) return;

    const fetchGroupStats = async () => {
      setLoading(true);
      try {
        const response = await axios.get(
          `${process.env.REACT_APP_API_URL}/receipts/stats/group/${userInfo.groupId}/category/${year}/${month}`
        );

        // 객체 -> 배열 변환
        const formattedStats = Object.entries(response.data).map(([categoryName, totalAmount]) => ({
          categoryName,
          totalAmount,
        }));

        setStats(formattedStats);
        setError(null);
      } catch (err) {
        console.error('데이터 불러오기 실패:', err);
        setStats([]);
        if (err.response && err.response.status === 400) {
          setError('해당 기간에 데이터가 없습니다.');
        } else {
          setError('서버 오류가 발생했습니다.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchGroupStats();
  }, [userInfo, year, month]);

  const handleYearChange = (e) => setYear(e.target.value);
  const handleMonthChange = (e) => setMonth(e.target.value);

  const total = Array.isArray(stats)
    ? stats.reduce((sum, item) => sum + item.totalAmount, 0)
    : 0;

  return (
    <main className="page-container">
      <h1 style={{ textAlign: 'center', fontSize: '1.8rem', fontWeight: '700', marginBottom: '2rem' }}>
        그룹 카테고리별 지출 통계
      </h1>

      <section style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1.5rem', marginBottom: '2.5rem' }}>
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

      {!loading && !error && stats.length > 0 && (
        <div className="card" style={{ padding: '2rem', maxWidth: '500px', margin: '0 auto' }}>
          <div style={{ textAlign: 'center', fontWeight: '600', fontSize: '1.1rem', marginBottom: '1rem' }}>
            총 지출: {total.toLocaleString()}원
          </div>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={stats}
                dataKey="totalAmount"
                nameKey="categoryName"
                cx="50%"
                cy="50%"
                outerRadius={100}
                innerRadius={60}
                label={(entry) => entry.categoryName}
              >
                {stats.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => `${value.toLocaleString()}원`} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}

      {!loading && !error && stats.length === 0 && (
        <p style={{ textAlign: 'center', color: '#6b7280' }}>조회된 데이터가 없습니다.</p>
      )}
    </main>
  );
};

export default GroupCategoryStatsPage;
