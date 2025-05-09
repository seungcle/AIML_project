import React from 'react';
import '../../styles/modal.css';

function CustomModal({ isOpen, onClose, title, actions = [], children }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>×</button>
        {title && <h3 className="modal-title">{title}</h3>}
        {children && <div className="modal-body">{children}</div>}
        {actions.length > 0 && <div className="modal-buttons">{actions}</div>}
      </div>
    </div>
  );
}

export default CustomModal;
