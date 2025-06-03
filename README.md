# AIML_project

Receipt Management Web for Company
https://hongikreceipt.kr/

<img width="419" alt="Image" src="https://github.com/user-attachments/assets/aada196f-5e5d-4ade-a9c2-6970589d8032" />
<img width="419" alt="Image" src="https://github.com/user-attachments/assets/ea6b9fb7-8f03-4db6-9c06-c64ae08c0ecb" />

Tool
- Web: React, HTML, CSS, JavaScript, Python (OpenAI), Vercel (웹), Render (AI 서버)
- Server: IntelliJ IDEA,  Spring Boot, MySQL, Docker + AWS EC2 + GitHub Actions, Postman
- Collaboration: Git, GitHub

Motivation
- 본 프로젝트는 회사 내 구성원이 사용하는 법인카드 사용 내역을 엑셀이나 수기로 정리하는 기존의 불편함을 줄이는 것에서 시작된 프로젝트다. 기존의 방식에서 일어날 수 있는 불필요한 시간 소모 소요, 오기재 가능성을 획기적으로 줄이고 통계 기능 등을 더해 효율적인 서비스를 만들고자 했다.

Development
- 유저는 그룹 관리자와 그룹 멤버로 구성되며, 그룹을 개설하거나 가입 신청을 통해 멤버가 되어 영수증 사진을 등록하여 사용 내역을 웹상에서 쉽게 관리할 수 있다. OCR 처리된 영수증의 내역을 분석하여 카테고리 분류를 자동으로 처리하며, 영수증 내역을 직접 수정할 수도 있다. 유저가 등록한 사용 내역을 통계화하여 제공한다.

Algorithm
- <영수증 사진 등록 처리>
사용자가 웹에서 영수증 사진 업로드->
Clova OCR API로 텍스트 추출->점포명, 아이템 항목을AI 서버에 전송 → 카테고리 분류 결과를 서버에 response->
웹에서 결과 확인 후 저장하면MySQL DB에 저장

- <실시간 알림 기능> 
클라이언트가 서버와 단방향 연결을 유지하여 영수증 등록, 지출 한도 초과 등의 이벤트를 실시간으로 자동 수신
