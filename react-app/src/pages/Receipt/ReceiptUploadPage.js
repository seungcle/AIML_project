import React, { useState } from 'react';
import { getAccessToken } from '../../components/auth/Auth';
import '../../styles/form.css';
import '../../styles/card.css';

function ReceiptUploadPage() {
  const [file, setFile] = useState(null);
  const [uploadedImage, setUploadedImage] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

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

      const response = await fetch(`${process.env.REACT_APP_API_URL}/receipts/process`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${accessToken}` },
        body: formData,
      });

      if (response.ok) {
        const data = await response.json();
        setResult(data);
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

  const handleSave = () => {
    alert('📦 저장 기능은 추후 구현 예정입니다!');
  };

  const handleEdit = () => {
    alert('✏️ 수정 기능은 추후 구현 예정입니다!');
  };

  return (
    <div className="page-container">
      {/* 업로드 박스 */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <h2>영수증 업로드</h2>
        <p style={{ color: '#6b7280', marginBottom: '1rem' }}>사진을 업로드하고 OCR 변환을 시작해보세요!</p>

        {/* 파일 선택 */}
        <label className="upload-button">
          📎 파일 선택하기
          <input type="file" accept="image/*" onChange={handleFileChange} style={{ display: 'none' }} />
        </label>

        {/* 메시지 */}
        {message && (
          <p style={{ marginTop: '1rem', color: '#4B5563' }}>{message}</p>
        )}
      </div>

      {/* 업로드된 이미지 */}
      {uploadedImage && (
        <div className="card" style={{ marginBottom: '2rem' }}>
          <h3>업로드된 이미지</h3>
          <img src={uploadedImage} alt="preview" style={{ maxWidth: '100%', borderRadius: '12px', marginTop: '1rem' }} />

          {/* OCR 변환 버튼 */}
          {!result && (
            <button onClick={handleOcrRequest} disabled={loading} className="btn" style={{ marginTop: '1rem' }}>
              {loading ? '처리 중...' : 'OCR 변환하기'}
            </button>
          )}
        </div>
      )}

      {/* OCR 결과 */}
      {result && (
        <div className="card" style={{ marginBottom: '2rem' }}>
          <h3>📋 OCR 결과</h3>
          <p><strong>상호명:</strong> {result.storeName}</p>
          <p><strong>주소:</strong> {result.storeAddress}</p>
          <p><strong>날짜:</strong> {result.date}</p>
          <p><strong>금액:</strong> {result.amount?.toLocaleString()}원</p>
          <p><strong>카테고리:</strong> {result.categoryName}</p>
          <p><strong>품목:</strong></p>
          <ul>
            {result.items?.map((item, idx) => (
              <li key={idx}>
                {item.itemName} - {item.quantity}개 / {item.totalPrice}원
              </li>
            ))}
          </ul>

          {/* 수정 / 저장 버튼 */}
          <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginTop: '1.5rem' }}>
            <button className="btn btn-light" onClick={handleEdit}>수정하기</button>
            <button className="btn" onClick={handleSave}>저장하기</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default ReceiptUploadPage;
