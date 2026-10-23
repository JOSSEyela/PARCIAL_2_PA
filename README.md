# Acortador de enlaces — Arquitectura hexagonal + Abstract Factory

| Capa | Tecnología |
|---|---|
| Backend | Java 21 + Spring Boot 4 (Web MVC, WebSocket, Data MongoDB, Data Redis) |
| Frontend | React 18 + Vite + WebSocket nativo |
| Base de datos | MongoDB (enlaces) + Redis (caché del listado, 30 s) |

## Cómo ejecutar

```bash
# 1. Bases de datos
#    MongoDB: servicio local de Windows en localhost:27017 (base "acortador")
#    Redis:   contenedor Docker (requiere Docker Desktop abierto)
docker compose up -d

# 2. Backend  → http://localhost:8080
cd backend
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run

# 3. Frontend → http://localhost:5173
cd frontend
npm install
npm run dev
```

> Sin Docker se puede probar todo en memoria: `./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.infrastructure=memory`

## Estructura (igual a la del diagrama)

```
backend/src/main/java/com/example/acortador
├── app
│   ├── App.java                       ← inicia Spring
│   └── config
│       ├── BeanConfiguration.java     ← conecta servicios ⇄ fábrica abstracta (al arrancar)
│       └── WebConfig.java             ← CORS
├── domain
│   ├── model/ShortLink.java           ← entidad pura
│   └── exception/…
├── application
│   ├── ports
│   │   ├── in/   ShortenLinkUseCase, ListShortLinksUseCase, ResolveShortLinkUseCase, PublishCacheStatusUseCase
│   │   └── out/  ShortLinkRepository, ShortLinkCache, ShortCodeGenerator, CacheEventPublisher
│   ├── factory/LinkInfrastructureFactory.java   ← ABSTRACT FACTORY (interfaz)
│   ├── dto/LinkListResult.java
│   └── service
│       ├── ShortLinkService.java      ← lógica: acortar, listar (Mongo → Redis), resolver
│       └── CacheMonitorService.java   ← calcula TTL restante y lo publica
└── infrastructure
    ├── factory
    │   ├── MongoRedisInfrastructureFactory.java  ← fábrica concreta (por defecto)
    │   └── InMemoryInfrastructureFactory.java    ← fábrica concreta alternativa
    ├── persistence
    │   ├── mongo/  ShortLinkRepositoryMongo (implementa ShortLinkRepository), ShortLinkDocument, SpringData…
    │   └── memory/ ShortLinkRepositoryInMemory
    ├── cache
    │   ├── redis/  ShortLinkCacheRedis (implementa ShortLinkCache, TTL 30 s)
    │   └── memory/ ShortLinkCacheInMemory
    ├── generator/Base62ShortCodeGenerator.java
    ├── websocket/  WebSocketConfig, CacheTtlWebSocketHandler (implementa CacheEventPublisher), CacheTtlScheduler
    └── web
        ├── controllers/ ShortLinkController (/api/links), RedirectController (/{codigo}), GlobalExceptionHandler
        └── dto/
```

### Abstract Factory

`LinkInfrastructureFactory` crea una **familia** de adaptadores compatibles entre sí:
`createRepository()`, `createCache()`, `createCodeGenerator()`.

- `MongoRedisInfrastructureFactory` → MongoDB + Redis + Base62.
- `InMemoryInfrastructureFactory` → memoria + memoria + Base62.

La propiedad `app.infrastructure` (`mongo-redis` | `memory`) decide qué fábrica concreta crea Spring.
`BeanConfiguration` le pide los productos a la fábrica y se los entrega a los servicios; la capa
`application` nunca conoce las clases concretas.

## Flujos

**Acortar** — `POST /api/links {url}` → `ShortLinkController` → `ShortLinkService.shorten()` →
`ShortLinkRepository` (MongoDB). Se invalida la caché del listado. El frontend muestra una alerta flotante.

**Ver enlaces** — `GET /api/links` → `ShortLinkService.listAll()`:
1. **1ª consulta**: no hay caché → lee MongoDB, guarda el listado en Redis con TTL de 30 s → `source: "MONGODB"`.
2. **2ª consulta** (dentro de los 30 s): lee de Redis → `source: "REDIS"` + segundos restantes.
   El frontend abre la alerta de caché.

**Tiempo real** — `CacheTtlScheduler` (cada 1 s) → `CacheMonitorService` → `CacheEventPublisher`
(`CacheTtlWebSocketHandler`) envía `{"type":"CACHE_TTL","remainingSeconds":n}` por `ws://…/ws/cache`.
La alerta se actualiza con cada mensaje y, al llegar a 0, avisa que la caché expiró.

**Redirección** — `GET http://localhost:8080/{codigo}` → 302 a la URL original.
