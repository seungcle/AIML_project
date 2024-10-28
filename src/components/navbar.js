import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/navbar.css';

function Navbar() {
  return (
    <header className="navbar">
      <div className="navbar-logo">
        <Link to="/">이름짓기</Link>
      </div>
      <nav className="navbar-buttons">
        <Link to="/signup" className="navbar-button">회원가입</Link>
        <Link to="/login" className="navbar-button">로그인</Link>
      </nav>
    </header>
  );
}

export default Navbar;