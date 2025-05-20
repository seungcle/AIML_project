import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Logout from '../auth/Logout';
import { useAuth } from '../auth/Auth';
import { Bell } from 'lucide-react';
import {
  fetchNotifications,
  markNotificationAsRead,
  subscribeToNotifications,
} from '../../api/notifications';
import '../../styles/button.css';
import '../../styles/navbar.css';

function Navbar() {
  const { userInfo, loading } = useAuth();
  const [showDropdown, setShowDropdown] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const toggleDropdown = () => setShowDropdown((prev) => !prev);

  useEffect(() => {
    if (!userInfo?.id) return;

    // 1. 초기 알림 로드
    const loadNotifications = async () => {
      const data = await fetchNotifications(userInfo.id);
      setNotifications(data);
      setUnreadCount(data.filter((n) => !n.read).length);
    };
    loadNotifications();

    // 2. 실시간 알림 구독
    const controller = subscribeToNotifications(userInfo.id, (newNotification) => {
      setNotifications((prev) => [newNotification, ...prev]);
      setUnreadCount((prev) => prev + 1);
    });

    // 3. 언마운트 시 SSE 연결 해제
    return () => {
      if (controller) controller.abort();
    };
  }, [userInfo]);

  const markAsRead = async (id) => {
    const success = await markNotificationAsRead(id);
    if (success) {
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
      );
      setUnreadCount((prev) => Math.max(prev - 1, 0));
    }
  };

  return (
    <header className="navbar">
      <div className="navbar-logo">
        <Link to="/">홈페이지</Link>
      </div>

      <nav className="navbar-buttons">
        {loading ? (
          <p>로딩 중...</p>
        ) : userInfo ? (
          <>
            <div className="notification-wrapper">
              <button onClick={toggleDropdown} className="notification-button">
                <Bell size={20} color="#111827" />
                {unreadCount > 0 && (
                  <span className="notification-badge">
                    {unreadCount > 20 ? '20+' : unreadCount}
                  </span>
                )}
              </button>

              {showDropdown && (
                <div className="notification-dropdown">
                  {notifications.length === 0 ? (
                    <p className="no-notification">📭 새로운 알림이 없습니다.</p>
                  ) : (
                    notifications.map((n) => (
                      <div
                        key={n.id}
                        className="notification-item"
                        onClick={() => markAsRead(n.id)}
                        style={{
                          fontWeight: n.read ? 'normal' : 'bold',
                          cursor: 'pointer',
                        }}
                      >
                        {n.message}
                      </div>
                    ))
                  )}
                </div>
              )}
            </div>

            <Link to={`/mypage/${userInfo.id}`} className="btn">마이페이지</Link>
            <Logout />
          </>
        ) : (
          <>
            <Link to="/signup" className="btn">회원가입</Link>
            <Link to="/login" className="btn">로그인</Link>
          </>
        )}
      </nav>
    </header>
  );
}

export default Navbar;
