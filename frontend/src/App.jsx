import { useCallback, useState } from 'react';
import ShortenView from './views/ShortenView.jsx';
import LinksView from './views/LinksView.jsx';
import Toast from './components/Toast.jsx';
import CacheAlert from './components/CacheAlert.jsx';
import { fetchLinks } from './api.js';
import { useCacheSocket } from './hooks/useCacheSocket.js';

const CACHE_TTL = 30;

export default function App() {
  const [view, setView] = useState('shorten');
  const [toast, setToast] = useState(null);
  const [links, setLinks] = useState([]);
  const [source, setSource] = useState(null);
  const [loading, setLoading] = useState(false);
  // null = alerta oculta; número = segundos restantes de la caché Redis
  const [cacheRemaining, setCacheRemaining] = useState(null);

  const showToast = useCallback((message, kind = 'success') => {
    setToast({ message, kind, id: Date.now() });
  }, []);
  // Callbacks estables: los temporizadores de las alertas no se reinician en cada render.
  const closeToast = useCallback(() => setToast(null), []);
  const closeCacheAlert = useCallback(() => setCacheRemaining(null), []);

  // Tiempo real: el backend envía el TTL cada segundo. Solo se refleja si la alerta está visible.
  useCacheSocket((remaining) => {
    setCacheRemaining((current) => (current === null ? null : remaining));
  });

  const openLinks = async () => {
    setView('links');
    setLoading(true);
    try {
      const data = await fetchLinks();
      setLinks(data.links);
      setSource(data.source);
      if (data.source === 'REDIS') {
        setCacheRemaining(data.cacheRemainingSeconds);
      } else {
        setCacheRemaining(null);
        showToast(`Consulta a MongoDB · guardada en caché Redis por ${data.cacheRemainingSeconds}s`, 'info');
      }
    } catch (e) {
      showToast(e.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark">⛓</span> Acortador
        </div>
        <nav className="tabs">
          <button className={view === 'shorten' ? 'tab active' : 'tab'} onClick={() => setView('shorten')}>
            Acortar enlace
          </button>
          <button className={view === 'links' ? 'tab active' : 'tab'} onClick={openLinks}>
            Ver enlace acortado
          </button>
        </nav>
      </header>

      <main className="content">
        {view === 'shorten' ? (
          <ShortenView onShortened={(link) => showToast(`Enlace acortado: ${link.shortUrl}`)}
                       onError={(msg) => showToast(msg, 'error')} />
        ) : (
          <LinksView links={links} source={source} loading={loading} />
        )}
      </main>

      {toast && <Toast key={toast.id} {...toast} onClose={closeToast} />}
      {cacheRemaining !== null && (
        <CacheAlert remaining={cacheRemaining} total={CACHE_TTL} onClose={closeCacheAlert} />
      )}
    </div>
  );
}
