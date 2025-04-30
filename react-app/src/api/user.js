import axios from 'axios';
import { getAccessToken } from '../components/auth/Auth';

// 🔐 비밀번호 변경 요청
export const changePassword = async (newPassword, confirmPassword) => {
  const token = getAccessToken();
  return axios.post(
    `${process.env.REACT_APP_API_URL}/member/change-password`,
    { newPassword, confirmPassword },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
};

// ✅ 현재 비밀번호 검증 요청
export const verifyPassword = async (password) => {
  const token = getAccessToken();
  return axios.post(
    `${process.env.REACT_APP_API_URL}/member/verify-password`,
    { password },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
};

// ❌ 회원 탈퇴 요청 (비밀번호는 이미 검증된 상태에서 호출)
export const deleteAccount = async () => {
    const token = getAccessToken();
    return axios.post(
      `${process.env.REACT_APP_API_URL}/member/delete-account`,
      {}, // ← 빈 body (필요 없다면)
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  };
  
