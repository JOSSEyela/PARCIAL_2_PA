async function handle(res) {
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.message || `Error ${res.status}`);
  return body;
}

export function shortenLink(url) {
  return fetch('/api/links', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ url }),
  }).then(handle);
}

export function fetchLinks() {
  return fetch('/api/links').then(handle);
}
