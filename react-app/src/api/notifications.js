// src/api/notification.js

// 1. 알림 목록 가져오기
export const fetchNotifications = async (memberId) => {
  try {
    const res = await fetch(
      `${process.env.REACT_APP_API_URL}/notification/${memberId}`,
      { credentials: 'include' }
    );
    if (!res.ok) throw new Error('알림 불러오기 실패');
    return await res.json();
  } catch (err) {
    console.error(err);
    return [];
  }
};

// 2. 알림 읽음 처리
export const markNotificationAsRead = async (id) => {
  try {
    const res = await fetch(
      `${process.env.REACT_APP_API_URL}/notification/read/${id}`,
      {
        method: 'POST',
        credentials: 'include',
      }
    );
    if (!res.ok) throw new Error('알림 읽음 처리 실패');
    return true;
  } catch (err) {
    console.error(err);
    return false;
  }
};

// 3. SSE 실시간 알림 구독
export const subscribeToNotifications = (memberId, onMessage) => {
  try {
    const eventSource = new EventSource(
      `${process.env.REACT_APP_API_URL}/notification/subscribe/${memberId}`,
      { withCredentials: true }
    );

    eventSource.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        onMessage(data); // 알림 수신 시 콜백 실행
      } catch (err) {
        console.error('🔔 알림 데이터 파싱 오류:', err);
      }
    };

    eventSource.onerror = (err) => {
      console.error('🔌 SSE 연결 오류:', err);
      eventSource.close();
    };

    return eventSource; // 컴포넌트에서 연결 해제용으로 반환
  } catch (err) {
    console.error('🔌 SSE 구독 실패:', err);
    return null;
  }
};
