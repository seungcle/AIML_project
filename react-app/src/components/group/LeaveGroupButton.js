import React, { useState } from 'react';
import axios from 'axios';
import { getAccessToken } from '../auth/Auth';
import CustomModal from '../common/CustomModal';
import { useNavigate } from 'react-router-dom';
import '../../styles/button.css';

function LeaveGroupButton({ onSuccess }) {
  const [showModal, setShowModal] = useState(false);
  const [errorModal, setErrorModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const navigate = useNavigate();

  const handleLeave = async () => {
    try {
      const token = getAccessToken();
      const res = await axios.post(
        `${process.env.REACT_APP_API_URL}/group/leave`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      alert(res.data);
      setShowModal(false);
      if (onSuccess) {
        onSuccess();
      } else {
        navigate('/');
      }
    } catch (err) {
      const message = err.response?.data || '오류가 발생했습니다.';
      setModalMessage(message);
      setErrorModal(true);
      setShowModal(false);
    }
  };

  return (
    <>
      <button className="btn btn-danger-light" onClick={() => setShowModal(true)}>
        ❌ 그룹 탈퇴
      </button>

      <CustomModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        title="정말 탈퇴하시겠어요?"
        actions={[
          <button key="cancel" className="btn" onClick={() => setShowModal(false)}>
            취소
          </button>,
          <button key="confirm" className="btn btn-danger-light" onClick={handleLeave}>
            확인
          </button>,
        ]}
      >
        <p>그룹 탈퇴 시 모든 그룹 기능을 이용할 수 없습니다.</p>
      </CustomModal>

      <CustomModal
        isOpen={errorModal}
        onClose={() => setErrorModal(false)}
        title="🚫 오류"
        actions={[
          <button key="close" className="btn" onClick={() => setErrorModal(false)}>
            닫기
          </button>,
        ]}
      >
        <p>{modalMessage}</p>
      </CustomModal>
    </>
  );
}

export default LeaveGroupButton;
