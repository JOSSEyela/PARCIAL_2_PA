import { useState } from 'react';
import { shortenLink } from '../api.js';

export default function ShortenView({ onShortened, onError }) {
  const [url, setUrl] = useState('');
  const [result, setResult] = useState(null);
  const [busy, setBusy] = useState(false);
  const [copied, setCopied] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    if (!url.trim()) return;
    setBusy(true);
    try {
      const link = await shortenLink(url);
      setResult(link);
      setCopied(false);
      setUrl('');
      onShortened(link);
    } catch (err) {
      onError(err.message);
    } finally {
      setBusy(false);
    }
  };

  const copy = async () => {
    await navigator.clipboard.writeText(result.shortUrl);
    setCopied(true);
  };

  return (
    <section className="card">
      <h1>Acorta tu enlace</h1>
      <p className="muted">Pega un enlace largo y obtén uno corto listo para compartir.</p>

      <form className="shorten-form" onSubmit={submit}>
        <input
          type="text"
          placeholder="https://ejemplo.com/un/enlace/muy/largo"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          autoFocus
        />
        <button type="submit" className="primary" disabled={busy || !url.trim()}>
          {busy ? 'Acortando…' : 'Acortar'}
        </button>
      </form>

      {result && (
        <div className="result">
          <span className="muted small">Tu enlace corto</span>
          <div className="result-row">
            <a href={result.shortUrl} target="_blank" rel="noreferrer">{result.shortUrl}</a>
            <button className="ghost" onClick={copy}>{copied ? 'Copiado ✓' : 'Copiar'}</button>
          </div>
          <span className="muted small truncate" title={result.originalUrl}>→ {result.originalUrl}</span>
        </div>
      )}
    </section>
  );
}
