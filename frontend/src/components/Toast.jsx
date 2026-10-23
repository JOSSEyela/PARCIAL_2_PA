import { useEffect } from 'react';

const ICONS = { success: '✓', info: 'i', error: '!' };

/** Alerta flotante sutil que se cierra sola. */
export default function Toast({ message, kind = 'success', onClose, duration = 3500 }) {
  useEffect(() => {
    const t = setTimeout(onClose, duration);
    return () => clearTimeout(t);
  }, [onClose, duration]);

  return (
    <div className={`toast toast-${kind}`} role="status">
      <span className="toast-icon">{ICONS[kind]}</span>
      <span className="toast-text">{message}</span>
      <button className="toast-close" onClick={onClose} aria-label="Cerrar">×</button>
    </div>
  );
}
