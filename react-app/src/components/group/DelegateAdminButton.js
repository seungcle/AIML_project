import React, { useState } from 'react';
import axios from 'axios';
import { getAccessToken } from '../auth/Auth';
import CustomModal from '../common/CustomModal';
import { useNavigate } from 'react-router-dom'; // ✅ 추가
import '../../styles/button.css';

function DelegateAdminButton({ memberId, currentUserId, onSuccess }) {
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [errorModal, setErrorModal] = useState(false);
  const navigate = useNavigate(); // ✅ 추가

  const handleDelegate = async () => {
    try {
      const token = getAccessToken();
      const res = await axios.put(
        `${process.env.REACT_APP_API_URL}/group/transfer-admin/${memberId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      alert(res.data); // "관리자 권한이 성공적으로 위임되었습니다."
      setShowModal(false);
      if (onSuccess) onSuccess();
      navigate('/'); // ✅ 홈페이지로 이동
    } catch (err) {
      const message = err.response?.data || '오류가 발생했습니다.';
      setModalMessage(message);
      setErrorModal(true);
      setShowModal(false);
    }
  };

  return (
    <>
      <button className="btn" onClick={() => setShowModal(true)}>
        🛡️ 권한 위임
      </button>

      <CustomModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        title="관리자 권한 위임 확인"
        actions={[
          <button className="btn" onClick={handleDelegate} key="yes">위임</button>,
          <button className="btn" onClick={() => setShowModal(false)} key="no">취소</button>,
        ]}
      >
        <p className="form-info-text">이 멤버에게 관리자 권한을 넘기시겠습니까?</p>
      </CustomModal>

      <CustomModal
        isOpen={errorModal}
        onClose={() => setErrorModal(false)}
        title="❗오류"
        actions={[
          <button className="btn" onClick={() => setErrorModal(false)} key="close">닫기</button>
        ]}
      >
        <p className="form-info-text">{modalMessage}</p>
      </CustomModal>
    </>
  );
}

export default DelegateAdminButton;
