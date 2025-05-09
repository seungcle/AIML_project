// src/api/notification.js

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
