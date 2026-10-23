import { useEffect, useRef } from 'react';

/**
 * Mantiene una conexión WebSocket con /ws/cache y llama a onTtl(segundos)
 * cada vez que el backend publica el tiempo restante de la caché Redis.
 * Se reconecta automáticamente si la conexión se cae (p. ej. backend apagado).
 */
export function useCacheSocket(onTtl) {
  const handlerRef = useRef(onTtl);
  handlerRef.current = onTtl;

  useEffect(() => {
    let socket;
    let retry;
    let disposed = false;

    const connect = () => {
      const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
      socket = new WebSocket(`${protocol}://${window.location.host}/ws/cache`);
      socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        if (data.type === 'CACHE_TTL') handlerRef.current(data.remainingSeconds);
      };
      socket.onclose = () => {
        if (!disposed) retry = setTimeout(connect, 3000);
      };
    };

    connect();
    return () => {
      disposed = true;
      clearTimeout(retry);
      // En desarrollo, StrictMode desmonta el efecto enseguida: cerrar un socket que aún
      // está en handshake provoca "closed before receiving a handshake response".
      // Se espera a que abra y entonces se cierra.
      if (socket.readyState === WebSocket.CONNECTING) {
        socket.onopen = () => socket.close();
        socket.onmessage = null;
      } else {
        socket.close();
      }
    };
  }, []);
}
