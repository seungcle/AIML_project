import { getAccessToken, refreshAccessToken } from '../components/auth/Auth';

const getMemberInfo = async () => {
  try {
    // Access token 가져오기
    let token = getAccessToken();

    // 토큰이 없거나 만료된 경우 갱신 시도
    if (!token) {
      const refreshed = await refreshAccessToken();
      if (refreshed) {
        token = getAccessToken();
      } else {
        console.warn('로그인이 필요합니다.');
        return null;
      }
    }

    // API 호출 설정
    const response = await fetch(`${process.env.REACT_APP_API_URL}/member/current`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if (response.ok) {
      return await response.json();
    } else if (response.status === 403) {
      console.error('접근 권한이 없습니다. 다시 로그인하세요.');
    } else {
      console.error(`유저 정보를 가져오는데 실패했습니다. 상태 코드: ${response.status}`);
    }
  } catch (error) {
    console.error('서버로부터 응답을 받지 못했습니다.', error);
  }
  return null;
};

export default getMemberInfo;