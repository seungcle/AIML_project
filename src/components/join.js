import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const Join = () => {
    const { userId } = useParams();  // URL에서 userId 값을 가져옴
    const [joinData, setJoinData] = useState(null);

    useEffect(() => {
        // userId를 사용해 해당 사용자의 가입 정보를 불러오는 API 호출
        const fetchJoinData = async () => {
            try {
                const response = await axios.get(`/api/user/${userId}/join`);
                setJoinData(response.data);
            } catch (error) {
                console.error('Failed to fetch join data:', error);
            }
        };

        fetchJoinData();
    }, [userId]);

    return (
        <div>
            <h1>Join Page for User {userId}</h1>
            {/* 가입 데이터 렌더링 */}
            {joinData ? (
                <div>{joinData.details}</div>
            ) : (
                <p>Loading join information...</p>
            )}
        </div>
    );
};

export default Join;

