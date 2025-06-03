import React, { useState } from 'react';
import axios from 'axios';
import { getAccessToken } from '../auth/Auth';
import CustomModal from '../common/CustomModal';
import { useNavigate } from 'react-router-dom';

function DeleteGroupButton({ groupId }) {
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [showErrorModal, setShowErrorModal] = useState(false);
  const navigate = useNavigate();

  const handleDelete = async () => {
    try {
      const token = getAccessToken();
      const res = await axios.delete(
        `${process.env.REACT_APP_API_URL}/group/delete/${groupId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      alert(res.data); // 예: "그룹이 삭제되었습니다."
      navigate('/');
    } catch (err) {
      const message = err.response?.data || '삭제 중 오류가 발생했습니다.';
      setModalMessage(message);
      setShowErrorModal(true);
    } finally {
      setShowModal(false);
    }
  };

  return (
    <>
      <button className="btn" onClick={() => setShowModal(true)}>
        🗑️ 그룹 삭제하기
      </button>

      <CustomModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        title="정말 삭제하시겠습니까?"
        actions={[
          <button className="btn" onClick={handleDelete} key="yes">삭제</button>,
          <button className="btn" onClick={() => setShowModal(false)} key="no">취소</button>
        ]}
      >
        <p className="form-info-text">
          그룹을 삭제하면 되돌릴 수 없어요. 정말 삭제하시겠어요?
        </p>
      </CustomModal>

      <CustomModal
        isOpen={showErrorModal}
        onClose={() => setShowErrorModal(false)}
        title="❗오류"
        actions={[
          <button className="btn" onClick={() => setShowErrorModal(false)} key="close">닫기</button>
        ]}
      >
        <p className="form-info-text">{modalMessage}</p>
      </CustomModal>
    </>
  );
}

export default DeleteGroupButton;
