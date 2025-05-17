import React, { useState } from 'react';
import CustomModal from '../common/CustomModal';
import { getAccessToken, refreshAccessToken } from '../auth/Auth';

function DeleteReceiptButton({ receiptId, onDelete }) {
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);

  const openModal = () => setShowModal(true);
  const closeModal = () => setShowModal(false);

  const handleDelete = async () => {
    setLoading(true);
    try {
      let token = getAccessToken();
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (refreshed) token = getAccessToken();
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/receipts/delete/${receiptId}`, {
        method: 'PATCH',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) throw new Error('삭제 실패');
      onDelete(receiptId);
      closeModal();
    } catch (err) {
      alert('삭제 중 오류가 발생했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <button onClick={openModal} style={deleteButtonStyle} aria-label="영수증 삭제">
        ❌
      </button>

      <CustomModal
        isOpen={showModal}
        onClose={closeModal}
        title="영수증 삭제 확인"
        actions={[
          <button
            key="cancel"
            className="modal-button-outline"
            onClick={closeModal}
            type="button"
          >
            🙅‍♂️ 취소할래요
          </button>,
          <button
            key="confirm"
            className="modal-button-red"
            onClick={handleDelete}
            disabled={loading}
            type="button"
          >
            {loading ? '🧼 삭제 중...' : '🧨 네, 지울게요'}
          </button>
        ]}
      >
        정말로 이 영수증을 삭제하시겠습니까?
      </CustomModal>
    </>
  );
}

const deleteButtonStyle = {
  position: 'absolute',
  top: '0.75rem',
  right: '0.75rem',
  background: 'none',
  border: 'none',
  fontSize: '1.1rem',
  cursor: 'pointer',
  color: '#ef4444',
};

export default DeleteReceiptButton;
