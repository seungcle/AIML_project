import React, { useState } from 'react';
import { getAccessToken } from '../../components/auth/Auth'; // AuthProvider에서 가져옴
import SaveReceiptButton from '../../components/receipt/SaveReceiptButton';

function ReceiptUpload() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [uploadedImage, setUploadedImage] = useState(null); // 업로드된 이미지 URL
  const [responseData, setResponseData] = useState(null); // 서버에서 받은 JSON 데이터
  const [ocrResult, setOcrResult] = useState(null); // OCR 결과

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    setFile(selectedFile);

    // 이미지 미리보기 설정s
    if (selectedFile) {
      const reader = new FileReader();
      reader.onload = () => {
        setUploadedImage(reader.result);
      };
      reader.readAsDataURL(selectedFile);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage('파일을 선택해주세요.');
      return;
    }

    if (file.size > 10 * 1024 * 1024) { // 10MB 제한
      setMessage('파일 용량이 10MB를 초과합니다. 10MB 이하의 파일만 업로드할 수 있습니다.');
      return;
    }

    const formData = new FormData();
    formData.append('file', file); // 서버가 요구하는 key로 설정

    try {
      setLoading(true);

      const accessToken = getAccessToken();
      if (!accessToken) {
        setMessage('로그인이 필요합니다.');
        setLoading(false);
        return;
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/images/upload`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${accessToken}`, // 인증 헤더
        },
        body: formData, // FormData 객체 전달
      });

      if (response.ok) {
        const data = await response.json();
        setMessage('영수증이 성공적으로 업로드되었습니다.');
        setFile(null); // 파일 상태 초기화
        setResponseData(data); // JSON 데이터를 상태로 저장

        // OCR 처리 요청
        await handleOcrRequest(data);
      } else {
        const errorData = await response.json();
        setMessage(`업로드 중 문제가 발생했습니다: ${errorData.message || '다시 시도해주세요.'}`);
      }
    } catch (error) {
      setMessage('서버와 연결할 수 없습니다. 인터넷 연결을 확인해주세요.');
      console.error('업로드 중 오류:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleOcrRequest = async (uploadResponse) => {
    try {
      setLoading(true);

      const ocrRequestBody = {
        version: uploadResponse.version,
        requestId: uploadResponse.requestId,
        timestamp: uploadResponse.timestamp,
        images: uploadResponse.images,
      };

      const ocrResponse = await fetch('http://localhost:4000/api/ocr', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(ocrRequestBody),
      });

      if (ocrResponse.ok) {
        const ocrData = await ocrResponse.json();
        setOcrResult(ocrData); // OCR 결과를 상태로 저장
        setMessage('OCR 처리 완료!');
      } else {
        const errorData = await ocrResponse.json();
        setMessage(`OCR 처리 중 문제가 발생했습니다: ${errorData.message || '다시 시도해주세요.'}`);
      }
    } catch (error) {
      setMessage('OCR 서버와 연결할 수 없습니다. 인터넷 연결을 확인해주세요.');
      console.error('OCR 요청 중 오류:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>영수증 업로드 및 OCR 처리</h2>
      <input
        type="file"
        accept="image/*"
        onChange={handleFileChange} // 파일 선택 핸들러
      />
      <button onClick={handleUpload} disabled={loading}>
        {loading ? '처리 중...' : '업로드 및 OCR 요청'}
      </button>
      {message && <p>{message}</p>}

      {/* 업로드된 이미지 미리 보기 */}
      {uploadedImage && (
        <div>
          <h3>업로드된 이미지:</h3>
          <img src={uploadedImage} alt="Uploaded receipt" style={{ maxWidth: '100%', height: 'auto' }} />
        </div>
      )}

      {/* JSON 데이터 출력 */}
      {responseData && (
        <div>
          <h3>서버 응답 데이터:</h3>
          <pre style={{ background: '#f4f4f4', padding: '10px', borderRadius: '5px' }}>
            {JSON.stringify(responseData, null, 2)}
          </pre>
        </div>
      )}

      {/* OCR 결과 출력 및 저장 버튼 */}
      {ocrResult && (
        <div>
          <h3>OCR 결과:</h3>
          <pre style={{ background: '#e8f5e9', padding: '10px', borderRadius: '5px' }}>
            {JSON.stringify(ocrResult, null, 2)}
          </pre>
          <SaveReceiptButton ocrResult={ocrResult} />
        </div>
      )}
    </div>
  );
}

export default ReceiptUpload;
