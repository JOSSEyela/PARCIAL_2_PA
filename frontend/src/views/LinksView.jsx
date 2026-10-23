export default function LinksView({ links, source, loading }) {
  return (
    <section className="card wide">
      <div className="card-head">
        <h1>Enlaces acortados</h1>
        {source && (
          <span className={`badge ${source === 'REDIS' ? 'badge-redis' : 'badge-mongo'}`}>
            {source === 'REDIS' ? 'Desde caché Redis' : 'Desde MongoDB'}
          </span>
        )}
      </div>

      {loading ? (
        <p className="muted">Cargando…</p>
      ) : links.length === 0 ? (
        <p className="muted">Aún no hay enlaces. Acorta uno desde “Acortar enlace”.</p>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Enlace original</th>
                <th>Enlace acortado</th>
              </tr>
            </thead>
            <tbody>
              {links.map((link) => (
                <tr key={link.id}>
                  <td className="mono small">{link.id}</td>
                  <td className="truncate" title={link.originalUrl}>{link.originalUrl}</td>
                  <td>
                    <a href={link.shortUrl} target="_blank" rel="noreferrer">{link.shortUrl}</a>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
