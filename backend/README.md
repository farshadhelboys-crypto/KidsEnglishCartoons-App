# Kids English Cartoons – Cloudflare Worker

## Deploy (free)

```bash
cd backend
npm install
npx wrangler login
npx wrangler deploy
```

After deploy, copy the Worker URL (e.g. `https://kids-english-cartoons.<your-subdomain>.workers.dev`) and put it in `android/.../ApiService.kt` as BASE_URL.

## Endpoints

- `GET /cartoons` – full list
- `GET /cartoons?q=farm` – search
- `GET /health` – health check

Cron runs daily at 06:00 UTC (free tier allows limited cron).
