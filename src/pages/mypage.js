import React, { useEffect, useState } from 'react';
import axios from 'axios';  // 데이터 불러올 때 사용
import '../styles/mypage.css';      // 스타일 파일을 따로 관리

const MyPage = () => {
    const [userData, setUserData] = useState({
        receipts: [],
        affiliation: '',
        teamExpenses: []
    });

    useEffect(() => {
        // 로그인한 사용자의 정보를 불러오는 API 호출
        const fetchUserData = async () => {
            try {
                const response = await axios.get('/api/user/myinfo');
                setUserData(response.data);
            } catch (error) {
                console.error('Failed to fetch user data:', error);
            }
        };

        fetchUserData();
    }, []);

    return (
        <div className="mypage-container">
            <h1>My Page</h1>
            
            {/* 내가 올린 영수증 섹션 */}
            <section className="receipts-section">
                <h2>My Receipts</h2>
                <div className="receipts-list">
                    {userData.receipts.length > 0 ? (
                        userData.receipts.map((receipt, index) => (
                            <div key={index} className="receipt-item">
                                <img src={receipt.imageUrl} alt={`Receipt ${index + 1}`} className="receipt-image" />
                                <div className="receipt-info">
                                    <p><strong>Date:</strong> {receipt.date}</p>
                                    <p><strong>Description:</strong> {receipt.description}</p>
                                    <p><strong>Amount:</strong> {receipt.amount} KRW</p>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>No receipts uploaded yet.</p>
                    )}
                </div>
            </section>

            {/* 나의 소속 */}
            <section className="affiliation-section">
                <h2>My Affiliation</h2>
                <p>{userData.affiliation || 'No affiliation information available.'}</p>
            </section>

            {/* 소속된 팀의 지출 내역 */}
            <section className="team-expenses-section">
                <h2>Team Expenses</h2>
                {userData.teamExpenses.length > 0 ? (
                    <ul>
                        {userData.teamExpenses.map((expense, index) => (
                            <li key={index}>
                                <span>{expense.date} - </span>
                                <span>{expense.description}: </span>
                                <span>{expense.amount} KRW</span>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No team expenses recorded yet.</p>
                )}
            </section>
        </div>
    );
};

export default MyPage;
