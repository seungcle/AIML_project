// src/pages/Home.js
import React from 'react';
import Navbar from '../components/navbar';
import '../styles/home.css';

function Home() {
  return (
    <div>
      <Navbar />
      <main className="home-container">
        <section id="intro" className="home-section">
          <h2>소개</h2>
          <p>스팬딧은 기업 비용 관리를 위한 통합 솔루션입니다...</p>
        </section>
        <section id="features" className="home-section">
          <h2>주요 기능</h2>
          <p>스팬딧은 지출 관리, 법인카드 관리, 자동화된 보고서를 제공합니다...</p>
        </section>
        <section id="clients" className="home-section">
          <h2>고객 사례</h2>
          <p>다양한 성공적인 기업들이 스팬딧을 통해 비용 관리 효율성을 높였습니다...</p>
        </section>
      </main>
    </div>
  );
}

export default Home;



