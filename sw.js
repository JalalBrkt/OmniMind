/*
* OmniMind Professional Service Worker (sw.js)
* Strategy: Network-First with Offline Fallback to index.html
*/

importScripts('https://storage.googleapis.com/workbox-cdn/releases/5.1.2/workbox-sw.js');

const CACHE = "omnimind-vault-v8";

// Since it's a single-file app, index.html is our only required asset and fallback
const offlineFallbackPage = "index.html";

// 1. Install Event: Precache the essential assets
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE)
      .then((cache) => {
        return cache.addAll([
          offlineFallbackPage,
          'manifest.json',
          'icon-192.png',
          'icon-512.png'
        ]);
      })
      .then(() => self.skipWaiting()) // Activate new service worker immediately
  );
});

// 2. Activate Event: Clean up old caches
self.addEventListener('activate', (event) => {
  event.waitUntil(
    (async () => {
      // Enable navigation preload if supported
      if (self.registration.navigationPreload) {
        await self.registration.navigationPreload.enable();
      }
      
      // Delete old versions of the cache
      const cacheNames = await caches.keys();
      await Promise.all(
        cacheNames.filter(name => name !== CACHE)
                  .map(name => caches.delete(name))
      );
    })()
  );
});

// 3. Fetch Event: Intercept network requests
self.addEventListener('fetch', (event) => {
  if (event.request.mode === 'navigate') {
    event.respondWith((async () => {
      try {
        // Try using the navigation preload response first
        const preloadResp = await event.preloadResponse;
        if (preloadResp) return preloadResp;

        // Try to fetch from the network
        return await fetch(event.request);
      } catch (error) {
        // If network fails, return the cached index.html
        const cache = await caches.open(CACHE);
        return await cache.match(offlineFallbackPage);
      }
    })());
  }
});

// Handle messages (e.g., from the main page to skip waiting)
self.addEventListener("message", (event) => {
  if (event.data && event.data.type === "SKIP_WAITING") {
    self.skipWaiting();
  }
});
