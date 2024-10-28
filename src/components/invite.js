import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const Invite = () => {
    const { userId } = useParams();  // URL에서 userId 값을 가져옴
    const [inviteData, setInviteData] = useState(null);

    useEffect(() => {
        // userId를 사용해 해당 사용자의 초대 정보를 불러오는 API 호출
        const fetchInviteData = async () => {
            try {
                const response = await axios.get(`/api/user/${userId}/invite`);
                setInviteData(response.data);
            } catch (error) {
                console.error('Failed to fetch invite data:', error);
            }
        };

        fetchInviteData();
    }, [userId]);

    return (
        <div>
            <h1>Invite Page for User {userId}</h1>
            {/* 초대 데이터 렌더링 */}
            {inviteData ? (
                <div>{inviteData.details}</div>
            ) : (
                <p>Loading invite information...</p>
            )}
        </div>
    );
};

export default Invite;


