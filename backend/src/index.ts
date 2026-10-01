/**
 * Cloudflare Worker – Kids English Cartoons API
 * Free tier. Serves curated safe English cartoons for ages 4-8.
 * Cron runs daily at 06:00 UTC (can be extended to pull new public-domain items).
 */

export interface Env {}

interface Cartoon {
  id: string;
  title: string;
  description: string;
  ageMin: number;
  ageMax: number;
  durationSec: number;
  videoUrl: string;
  thumbnailUrl: string;
  category: string;
  language: string;
  source: string;
}

const CARTOONS: Cartoon[] = [
  {
    id: "kidsongs-farm",
    title: "Kidsongs - A Day At Old MacDonald's Farm",
    description: "Fun English songs and farm animals for kids ages 4-8. Learn animal names and simple songs.",
    ageMin: 4,
    ageMax: 8,
    durationSec: 1800,
    videoUrl: "https://archive.org/download/kidsongs-series/A.%20Kidsongs%20A%20Day%20At%20Old%20MacDonald%20s%20Farm.mp4",
    thumbnailUrl: "https://archive.org/services/img/kidsongs-series",
    category: "Songs",
    language: "English",
    source: "Archive.org"
  },
  {
    id: "somewhere-dreamland",
    title: "Somewhere in Dreamland (1936)",
    description: "Classic public domain color cartoon. Soft story perfect for young children.",
    ageMin: 4,
    ageMax: 8,
    durationSec: 540,
    videoUrl: "https://archive.org/download/pdcartooncollection/Fleischer%20Color%20Classic%20Somewhere%20in%20Dreamland%201936)%20(old%20cartoon%20vintage%20public%20domain).mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Classic",
    language: "English",
    source: "Archive.org"
  },
  {
    id: "little-lambkins",
    title: "Little Lambkins (1940)",
    description: "Fleischer Color Classic - gentle adventure for preschoolers.",
    ageMin: 4,
    ageMax: 7,
    durationSec: 480,
    videoUrl: "https://archive.org/download/pdcartooncollection/Fleischer%20Color%20Classic%20Little%20Lambkins%201940%20(old%20free%20cartoon%20public%20domain).mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Classic",
    language: "English",
    source: "Archive.org"
  },
  {
    id: "old-mother-hubbard",
    title: "Old Mother Hubbard (1935)",
    description: "ComiColor cartoon based on the nursery rhyme. Great for English learning.",
    ageMin: 4,
    ageMax: 8,
    durationSec: 420,
    videoUrl: "https://archive.org/download/pdcartooncollection/COMICOLOR%20-%201935%20-%20_Old%20Mother%20Hubbard_.mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Nursery",
    language: "English",
    source: "Archive.org"
  },
  {
    id: "simple-simon",
    title: "Simple Simon (ComiColor)",
    description: "Fun short cartoon with simple English dialogue and music.",
    ageMin: 4,
    ageMax: 8,
    durationSec: 360,
    videoUrl: "https://archive.org/download/pdcartooncollection/ComiColor_%20Simple%20Simon.mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Nursery",
    language: "English",
    source: "Archive.org"
  },
  {
    id: "brementown-musicians",
    title: "The Bremen Town Musicians",
    description: "UB Iwerks ComiColor – classic fairy tale cartoon, safe for young kids.",
    ageMin: 4,
    ageMax: 8,
    durationSec: 480,
    videoUrl: "https://archive.org/download/pdcartooncollection/Brementown%20Musicians%20UB%20Iwerks%20ComiColor.mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Fairy Tale",
    language: "English",
    source: "Archive.org"
  }
];

export default {
  async fetch(request: Request, env: Env, ctx: ExecutionContext): Promise<Response> {
    const url = new URL(request.url);
    const cors = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type",
      "Content-Type": "application/json"
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: cors });
    }

    if (url.pathname === "/" || url.pathname === "/cartoons") {
      const q = url.searchParams.get("q")?.toLowerCase() || "";
      let list = CARTOONS;
      if (q) {
        list = CARTOONS.filter(
          (c) =>
            c.title.toLowerCase().includes(q) ||
            c.description.toLowerCase().includes(q) ||
            c.category.toLowerCase().includes(q)
        );
      }
      return new Response(
        JSON.stringify({
          updatedAt: new Date().toISOString(),
          count: list.length,
          cartoons: list
        }),
        { headers: cors }
      );
    }

    if (url.pathname === "/health") {
      return new Response(JSON.stringify({ ok: true, cartoons: CARTOONS.length }), { headers: cors });
    }

    return new Response(JSON.stringify({ error: "Not found" }), { status: 404, headers: cors });
  },

  async scheduled(event: ScheduledEvent, env: Env, ctx: ExecutionContext) {
    // Daily cron: placeholder for future auto-discovery of new public-domain kids content.
    // Currently the curated list is static; extend here to scrape Archive.org collections if desired.
    console.log("Daily refresh tick at", new Date().toISOString());
  }
};
