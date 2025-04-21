import React from 'react';
import { getAccessToken } from '../../components/auth/Auth';

function SaveReceiptButton({ ocrResult }) {
  const handleSaveReceipt = async () => {
    if (!ocrResult) {
      alert('OCR 결과가 없습니다. 먼저 OCR 처리를 완료해주세요.');
      return;
    }

    try {
      const accessToken = getAccessToken();
      if (!accessToken) {
        alert('로그인이 필요합니다.');
        return;
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL}/receipts/save`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          version: ocrResult.version,
          requestId: ocrResult.requestId,
          timestamp: ocrResult.timestamp,
          images: ocrResult.images,
          amount: ocrResult.amount,
          categoryId: ocrResult.categoryId,
          date: ocrResult.date,
          userId: ocrResult.userId,
          memo: ocrResult.memo || null,
          imageId: ocrResult.imageId || null,
          storeAddress: ocrResult.storeAddress,
          storeName: ocrResult.storeName,
          deletedAt: ocrResult.deletedAt || null,
          isDeleted: ocrResult.isDeleted || false,
          errorMessage: ocrResult.errorMessage || null,
        }),
      });

      if (response.ok) {
        alert('영수증 정보가 성공적으로 저장되었습니다.');
      } else {
        const errorData = await response.json();
        alert(`저장 중 문제가 발생했습니다: ${errorData.message || '다시 시도해주세요.'}`);
      }
    } catch (error) {
      console.error('저장 요청 중 오류:', error);
      alert('서버와 연결할 수 없습니다. 인터넷 연결을 확인해주세요.');
    }
  };

  return (
    <button onClick={handleSaveReceipt}>
      영수증 정보 저장
    </button>
  );
}

export default SaveReceiptButton;
