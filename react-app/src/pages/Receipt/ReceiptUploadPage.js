import React, { useState } from 'react';
import { getAccessToken } from '../../components/auth/Auth';
import '../../styles/form.css';

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

  const handleUpload = async () => {
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
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
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

  return (
    <div className="page-container">
      <h2>영수증 업로드</h2>

      <input type="file" accept="image/*" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={loading} className="btn">
        {loading ? '처리 중...' : '영수증 등록하기'}
      </button>

      {message && <p style={{ marginTop: '1rem', color: '#4B5563' }}>{message}</p>}

      {uploadedImage && (
        <div style={{ marginTop: '1.5rem' }}>
          <h4>업로드된 이미지</h4>
          <img src={uploadedImage} alt="preview" style={{ maxWidth: '100%', borderRadius: '12px' }} />
        </div>
      )}

      {result && (
        <div style={{ marginTop: '2rem', background: '#f1f5f9', padding: '1rem', borderRadius: '12px' }}>
          <h4>📋 OCR 결과</h4>
          <p><strong>상호명:</strong> {result.storeName}</p>
          <p><strong>주소:</strong> {result.storeAddress}</p>
          <p><strong>날짜:</strong> {result.date}</p>
          <p><strong>금액:</strong> {result.amount.toLocaleString()}원</p>
          <p><strong>카테고리:</strong> {result.categoryName}</p>
          <p><strong>품목:</strong></p>
          <ul>
            {result.items?.map((item, idx) => (
              <li key={idx}>{item.itemName} - {item.quantity}개 / {item.totalPrice}원</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default ReceiptUploadPage;
