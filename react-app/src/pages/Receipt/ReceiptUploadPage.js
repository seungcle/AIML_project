import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAccessToken } from '../../components/auth/Auth';
import { useAuth } from '../../components/auth/Auth';
import CustomModal from '../../components/common/CustomModal';
import '../../styles/form.css';
import '../../styles/card.css';
import '../../styles/modal.css';

const CATEGORY_OPTIONS = ['식비', '교통비', '숙박비', '전자기기', '소모품비', '기타'];
const CATEGORY_MAP = {
  '식비': 1,
  '교통비': 2,
  '숙박비': 3,
  '전자기기': 4,
  '소모품비': 5,
  '기타': 6
};

function ReceiptUploadPage() {
  const { userInfo } = useAuth();
  const navigate = useNavigate();

  const [file, setFile] = useState(null);
  const [uploadedImage, setUploadedImage] = useState(null);
  const [result, setResult] = useState(null);
  const [editableResult, setEditableResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    setFile(selectedFile);

    if (selectedFile) {
      const reader = new FileReader();
      reader.onload = () => setUploadedImage(reader.result);
      reader.readAsDataURL(selectedFile);
    }
  };

  const handleOcrRequest = async () => {
    if (!file) return setMessage('📎 파일을 선택해주세요.');
    if (file.size > 10 * 1024 * 1024) return setMessage('❗10MB 이하의 파일만 업로드할 수 있어요.');

    const formData = new FormData();
    formData.append('file', file);

    try {
      setLoading(true);
      setMessage('서버로 전송 중...');

      const accessToken = getAccessToken();
      if (!accessToken) return setMessage('로그인이 필요합니다.');

      const response = await fetch(`${process.env.REACT_APP_API_URL}/receipts/preview`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${accessToken}` },
        body: formData,
      });

      if (response.ok) {
        const data = await response.json();
        setResult(data);
        setEditableResult({ ...data });
        setMessage('✅ OCR 결과를 불러왔어요.');
        setFile(null);
      } else {
        const errorData = await response.json();
        setMessage(`🚫 오류: ${errorData.message || '다시 시도해주세요.'}`);
      }
    } catch (err) {
      console.error('업로드 실패:', err);
      setMessage('🚫 서버 오류가 발생했어요.');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    if (!editableResult || !userInfo?.id) return;

    const accessToken = getAccessToken();
    if (!accessToken) {
      setModalMessage('로그인이 필요합니다.');
      setShowModal(true);
      return;
    }

    const payload = {
      ...editableResult,
      userId: userInfo.id,
      categoryId: CATEGORY_MAP[editableResult.categoryName] || null
    };

    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/receipts/save`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      const data = await response.json();

      if (response.ok && !data.errorMessage) {
        setModalMessage('✅ 영수증이 저장되었어요.\n이제 그룹 지출 목록에서 확인하실 수 있어요!');
        setShowModal(true);
        setResult(null);
        setEditableResult(null);
        setUploadedImage(null);
        setFile(null);
      } else {
        setModalMessage(data.errorMessage || '알 수 없는 오류가 발생했습니다.');
        setShowModal(true);
      }
    } catch (error) {
      console.error('저장 실패:', error);
      setModalMessage('🚫 저장 중 서버 오류가 발생했어요.');
      setShowModal(true);
    }
  };

  const closeModal = () => {
    setShowModal(false);
    if (modalMessage.includes('저장되었어요')) navigate('/');
  };

  const handleEditChange = (field, value) => {
    setEditableResult((prev) => ({ ...prev, [field]: value }));
  };

  const handleItemChange = (idx, field, value) => {
    const updatedItems = editableResult.items.map((item, i) => {
      if (i === idx) {
        const newItem = { ...item, [field]: value };
        if (field === 'quantity' || field === 'unitPrice') {
          newItem.totalPrice = newItem.quantity * newItem.unitPrice;
        }
        return newItem;
      }
      return item;
    });
    setEditableResult((prev) => ({ ...prev, items: updatedItems }));
  };

  return (
    <div className="page-container">
      <div className="card" style={{ marginBottom: '2rem' }}>
        <h2>영수증 업로드</h2>
        <p style={{ color: '#6b7280', marginBottom: '1rem' }}>사진을 업로드하고 OCR 변환을 시작해보세요!</p>
        <label className="file-input-label">
          📎 파일 선택하기
          <input type="file" accept="image/*" onChange={handleFileChange} className="file-input" />
        </label>
        {message && <p style={{ marginTop: '1rem', color: '#4B5563' }}>{message}</p>}
      </div>

      {uploadedImage && (
        <div className="card" style={{ marginBottom: '2rem' }}>
          <h3>업로드된 이미지</h3>
          <img src={uploadedImage} alt="preview" style={{ maxWidth: '100%', borderRadius: '12px', marginTop: '1rem' }} />
          {!result && (
            <button onClick={handleOcrRequest} disabled={loading} className="btn" style={{ marginTop: '1rem' }}>
              {loading ? '처리 중...' : 'OCR 변환하기'}
            </button>
          )}
        </div>
      )}

      {editableResult && (
        <div className="card" style={{ marginBottom: '2rem' }}>
          <h3>📋 OCR 결과</h3>

          <div className="form-group">
            <input className="form-input" placeholder="상호명" value={editableResult.storeName || ''} onChange={(e) => handleEditChange('storeName', e.target.value)} />
            <input className="form-input" placeholder="주소" value={editableResult.storeAddress || ''} onChange={(e) => handleEditChange('storeAddress', e.target.value)} />
            <input type="date" className="form-input" value={editableResult.date || ''} onChange={(e) => handleEditChange('date', e.target.value)} />
            <input type="number" className="form-input" placeholder="금액" value={editableResult.amount || ''} onChange={(e) => handleEditChange('amount', parseInt(e.target.value))} />
            <select className="form-input" value={editableResult.categoryName || ''} onChange={(e) => handleEditChange('categoryName', e.target.value)}>
              <option value="">카테고리 선택</option>
              {CATEGORY_OPTIONS.map((cat) => <option key={cat} value={cat}>{cat}</option>)}
            </select>
            <input className="form-input" placeholder="메모 (선택)" value={editableResult.memo || ''} onChange={(e) => handleEditChange('memo', e.target.value)} />
          </div>

          <div className="form-group" style={{ marginTop: '1rem' }}>
            <strong>품목 목록</strong>
            {editableResult.items.map((item, idx) => (
              <div key={idx} className="item-edit-box">
                <input className="form-input" placeholder="품목명" value={item.itemName} onChange={(e) => handleItemChange(idx, 'itemName', e.target.value)} />
                <input className="form-input" type="number" placeholder="수량" value={item.quantity} onChange={(e) => handleItemChange(idx, 'quantity', parseInt(e.target.value))} />
                <input className="form-input" type="number" placeholder="단가" value={item.unitPrice} onChange={(e) => handleItemChange(idx, 'unitPrice', parseInt(e.target.value))} />
              </div>
            ))}
          </div>

          <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginTop: '1.5rem' }}>
            <button className="btn" onClick={handleSave}>저장하기</button>
          </div>
        </div>
      )}

      <CustomModal isOpen={showModal} onClose={closeModal} title="알림">
        <p>{modalMessage}</p>
        <div className="modal-buttons">
          <button onClick={closeModal}>확인</button>
        </div>
      </CustomModal>
    </div>
  );
}

export default ReceiptUploadPage;
