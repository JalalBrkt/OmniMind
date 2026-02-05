/* OmniMind Pro Service Worker (PWABuilder Optimized) */
const CACHE_NAME = 'omnimind-v11-offline';
/* OmniMind Pro Service Worker (PWABuilder Standard) */
const CACHE_NAME = 'omnimind-v10-offline';
const OFFLINE_URL = 'index.html';

const ASSETS_TO_CACHE = [
  OFFLINE_URL,
  'manifest.json',
  'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;800&display=swap',
  'https://cdn-icons-png.flaticon.com/512/3665/3665923.png'
];

// 1. Install: Pre-cache offline assets
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      console.log('[ServiceWorker] Pre-caching offline page');
      return cache.addAll(ASSETS_TO_CACHE);
    })
  );
  self.skipWaiting();
});

// 2. Activate: Clean up old caches
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keyList) => {
      return Promise.all(keyList.map((key) => {
        if (key !== CACHE_NAME) {
          console.log('[ServiceWorker] Removing old cache', key);
          return caches.delete(key);
        }
      }));
    })
  );
  self.clients.claim();
});

// 3. Fetch: Offline Support (Stale-While-Revalidate)
self.addEventListener('fetch', (event) => {
  if (event.request.mode === 'navigate') {
    event.respondWith(
      (async () => {
        try {
          // Always try the network first for navigation requests to get latest data
          const networkResponse = await fetch(event.request);
          return networkResponse;
        } catch (error) {
          // If network fails, serve offline page
          const preloadResponse = await event.preloadResponse;
          if (preloadResponse) {
            return preloadResponse;
          }

          const networkResponse = await fetch(event.request);
          return networkResponse;
        } catch (error) {
          console.log('[ServiceWorker] Fetch failed; returning offline page instead.', error);
          const cache = await caches.open(CACHE_NAME);
          const cachedResponse = await cache.match(OFFLINE_URL);
          return cachedResponse;
        }
      })()
    );
  } else {
    // For other assets, try cache first, then network
    // Cache-First Strategy for assets
    event.respondWith(
      caches.match(event.request).then((response) => {
        return response || fetch(event.request);
      })
    );
  }
});

// 4. Push Notifications Support
self.addEventListener('push', (event) => {
  if (event.data) {
    const data = event.data.json();
    const options = {
      body: data.body,
      icon: data.icon || 'https://cdn-icons-png.flaticon.com/512/3665/3665923.png',
      badge: 'https://cdn-icons-png.flaticon.com/512/3665/3665923.png',
      vibrate: [100, 50, 100],
      data: {
        dateOfArrival: Date.now(),
        primaryKey: '2'
      }
    };
    event.waitUntil(
      self.registration.showNotification(data.title, options)
    );
  }
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  event.waitUntil(
    clients.openWindow('index.html')
  );
});
