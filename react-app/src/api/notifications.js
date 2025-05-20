import { fetchEventSource } from '@microsoft/fetch-event-source';
import { getAccessToken } from '../components/auth/Auth';

// 1. 알림 목록 가져오기
export const fetchNotifications = async (memberId) => {
  try {
    const token = getAccessToken();
    const res = await fetch(
      `${process.env.REACT_APP_API_URL}/notification/${memberId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        credentials: 'include',
      }
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

// 3. SSE 실시간 알림 구독 (fetchEventSource 사용)
export const subscribeToNotifications = (memberId, onMessage) => {
  const token = getAccessToken();
  if (!token) {
    console.error('❗ 액세스 토큰이 없습니다.');
    return null;
  }

  const controller = new AbortController();

  fetchEventSource(
    `${process.env.REACT_APP_API_URL}/notification/subscribe/${memberId}`,
    {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
      signal: controller.signal,
      onmessage(event) {
        try {
          const isJSON = event.data.startsWith('{') || event.data.startsWith('[');
          if (!isJSON) {
            console.warn('📢 무시된 메시지:', event.data);
            return;
          }

          const data = JSON.parse(event.data);
          onMessage(data);
        } catch (err) {
          console.error('🔔 알림 파싱 오류:', err);
        }
      },
      onerror(err) {
        console.error('🔌 SSE 연결 오류:', err);
        controller.abort();
      },
    }
  );

  return controller;
};

