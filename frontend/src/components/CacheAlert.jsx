import { useEffect } from 'react';

/**
 * Alerta con el tiempo restante de la caché Redis. El valor `remaining`
 * llega por WebSocket cada segundo; al llegar a 0 se muestra "expirada" y se cierra.
 */
export default function CacheAlert({ remaining, total, onClose }) {
  const expired = remaining <= 0;

  useEffect(() => {
    if (!expired) return;
    const t = setTimeout(onClose, 2500);
    return () => clearTimeout(t);
  }, [expired, onClose]);

  const pct = Math.max(0, Math.min(100, (remaining / total) * 100));

  return (
    <div className={`cache-alert ${expired ? 'expired' : ''}`} role="status">
      <div className="cache-alert-head">
        <span className="cache-dot" />
        <strong>{expired ? 'Caché expirada' : 'Datos servidos desde Redis'}</strong>
        <button className="toast-close" onClick={onClose} aria-label="Cerrar">×</button>
      </div>
      <p>
        {expired
          ? 'La próxima consulta irá a MongoDB.'
          : <>La caché expira en <span className="mono countdown">{remaining}s</span></>}
      </p>
      <div className="progress"><div className="progress-bar" style={{ width: `${pct}%` }} /></div>
    </div>
  );
}
