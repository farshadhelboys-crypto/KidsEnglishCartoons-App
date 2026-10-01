/**
 * Paste into Cloudflare Worker: https://acrtoonfarinaz.farshadhelboys.workers.dev
 * Modern English kids cartoons (official YouTube) for ages 4–8
 * Peppa Pig, PAW Patrol, Super Wings + optional free direct videos
 */

const CARTOONS = [
  {
    id: "peppa-sharing",
    title: "Peppa Pig – Sharing Is Caring (Full Episodes)",
    description: "Official Peppa Pig English episodes. Perfect for ages 4–8.",
    ageMin: 4, ageMax: 8, durationSec: 3600,
    youtubeId: "e5Ef8rOUWUo",
    videoUrl: "https://www.youtube.com/watch?v=e5Ef8rOUWUo",
    thumbnailUrl: "https://img.youtube.com/vi/e5Ef8rOUWUo/hqdefault.jpg",
    category: "Peppa Pig", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "peppa-summer",
    title: "Peppa Pig – Summer Adventures",
    description: "Official full episodes compilation from Peppa Pig channel.",
    ageMin: 4, ageMax: 8, durationSec: 14400,
    youtubeId: "6-xa1WJ4cjc",
    videoUrl: "https://www.youtube.com/watch?v=6-xa1WJ4cjc",
    thumbnailUrl: "https://img.youtube.com/vi/6-xa1WJ4cjc/hqdefault.jpg",
    category: "Peppa Pig", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "peppa-secret-door",
    title: "Peppa Pig – Secret Door & Mystery Stairs",
    description: "Official Peppa Pig English full episodes.",
    ageMin: 4, ageMax: 8, durationSec: 7200,
    youtubeId: "Uc8knK0ONgk",
    videoUrl: "https://www.youtube.com/watch?v=Uc8knK0ONgk",
    thumbnailUrl: "https://img.youtube.com/vi/Uc8knK0ONgk/hqdefault.jpg",
    category: "Peppa Pig", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "peppa-walkie",
    title: "Peppa Pig – Walkie Talkies (1 Hour)",
    description: "Official Peppa Pig full episodes compilation.",
    ageMin: 4, ageMax: 8, durationSec: 3600,
    youtubeId: "ESCtnG1Jxrk",
    videoUrl: "https://www.youtube.com/watch?v=ESCtnG1Jxrk",
    thumbnailUrl: "https://img.youtube.com/vi/ESCtnG1Jxrk/hqdefault.jpg",
    category: "Peppa Pig", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "paw-mighty-twins",
    title: "PAW Patrol – Mighty Pups Meet the Mighty Twins",
    description: "Official PAW Patrol full episode. English for kids 4–8.",
    ageMin: 4, ageMax: 8, durationSec: 1400,
    youtubeId: "NcrX0Kv9YTQ",
    videoUrl: "https://www.youtube.com/watch?v=NcrX0Kv9YTQ",
    thumbnailUrl: "https://img.youtube.com/vi/NcrX0Kv9YTQ/hqdefault.jpg",
    category: "PAW Patrol", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "paw-jungle",
    title: "PAW Patrol – Jungle Pups Hidden Jungle",
    description: "Official PAW Patrol full episode from official channel.",
    ageMin: 4, ageMax: 8, durationSec: 1400,
    youtubeId: "D33Tg3A-L4E",
    videoUrl: "https://www.youtube.com/watch?v=D33Tg3A-L4E",
    thumbnailUrl: "https://img.youtube.com/vi/D33Tg3A-L4E/hqdefault.jpg",
    category: "PAW Patrol", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "paw-sea-octopus",
    title: "PAW Patrol – Sea Patrol Baby Octopus",
    description: "Official full episode. Great English for preschoolers.",
    ageMin: 4, ageMax: 8, durationSec: 1400,
    youtubeId: "bFkuy5yAMig",
    videoUrl: "https://www.youtube.com/watch?v=bFkuy5yAMig",
    thumbnailUrl: "https://img.youtube.com/vi/bFkuy5yAMig/hqdefault.jpg",
    category: "PAW Patrol", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "paw-fire-monster",
    title: "PAW Patrol – Fire Rescue Movie Monster",
    description: "Official PAW Patrol English episode.",
    ageMin: 4, ageMax: 8, durationSec: 1400,
    youtubeId: "EbJBUniF99A",
    videoUrl: "https://www.youtube.com/watch?v=EbJBUniF99A",
    thumbnailUrl: "https://img.youtube.com/vi/EbJBUniF99A/hqdefault.jpg",
    category: "PAW Patrol", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "superwings-delivery",
    title: "Super Wings – The Delivery King",
    description: "Official Super Wings English episode. Adventure for ages 4–8.",
    ageMin: 4, ageMax: 8, durationSec: 670,
    youtubeId: "JLZW0G3ryeM",
    videoUrl: "https://www.youtube.com/watch?v=JLZW0G3ryeM",
    thumbnailUrl: "https://img.youtube.com/vi/JLZW0G3ryeM/hqdefault.jpg",
    category: "Super Wings", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "superwings-heritage",
    title: "Super Wings – Exploring World Heritage",
    description: "Official Super Wings best episodes compilation (English).",
    ageMin: 4, ageMax: 8, durationSec: 2640,
    youtubeId: "Eza1Xyijikc",
    videoUrl: "https://www.youtube.com/watch?v=Eza1Xyijikc",
    thumbnailUrl: "https://img.youtube.com/vi/Eza1Xyijikc/hqdefault.jpg",
    category: "Super Wings", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "superwings-ep02",
    title: "Super Wings – Great Gondolas (ENG)",
    description: "Official Super Wings English episode.",
    ageMin: 4, ageMax: 8, durationSec: 720,
    youtubeId: "aWZJXi3nuFM",
    videoUrl: "https://www.youtube.com/watch?v=aWZJXi3nuFM",
    thumbnailUrl: "https://img.youtube.com/vi/aWZJXi3nuFM/hqdefault.jpg",
    category: "Super Wings", language: "English", source: "YouTube Official", type: "youtube"
  },
  {
    id: "superwings-bath",
    title: "Super Wings – Boonying's Bath Time (ENG)",
    description: "Official Super Wings English episode for young kids.",
    ageMin: 4, ageMax: 8, durationSec: 720,
    youtubeId: "C1dg0IqouRA",
    videoUrl: "https://www.youtube.com/watch?v=C1dg0IqouRA",
    thumbnailUrl: "https://img.youtube.com/vi/C1dg0IqouRA/hqdefault.jpg",
    category: "Super Wings", language: "English", source: "YouTube Official", type: "youtube"
  }
];

export default {
  async fetch(request) {
    const url = new URL(request.url);
    const cors = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type",
      "Content-Type": "application/json",
      "Cache-Control": "public, max-age=1800"
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: cors });
    }

    if (url.pathname === "/" || url.pathname === "/cartoons") {
      const q = (url.searchParams.get("q") || "").toLowerCase();
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
  }
};
