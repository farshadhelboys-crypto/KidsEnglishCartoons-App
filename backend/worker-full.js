/**
 * Kids English Cartoons Worker – paste this entire file into Cloudflare Dashboard → Edit code
 * URL: https://acrtoonfarinaz.farshadhelboys.workers.dev
 *
 * - Curated safe English cartoons for ages 4–8 (direct MP4, play + download)
 * - Auto-discovers more public-domain English cartoons from Archive.org
 * - Merges and returns full list for the Android app
 */

const CURATED = [
  {
    id: "kidsongs-farm",
    title: "Kidsongs - A Day At Old MacDonald's Farm",
    description: "Fun English songs and farm animals for kids ages 4-8. Learn animal names and simple songs.",
    ageMin: 4, ageMax: 8, durationSec: 1800,
    videoUrl: "https://archive.org/download/kidsongs-series/A.%20Kidsongs%20A%20Day%20At%20Old%20MacDonald%20s%20Farm.mp4",
    thumbnailUrl: "https://archive.org/services/img/kidsongs-series",
    category: "Songs", language: "English", source: "Archive.org"
  },
  {
    id: "somewhere-dreamland",
    title: "Somewhere in Dreamland (1936)",
    description: "Classic public domain color cartoon. Soft story perfect for young children.",
    ageMin: 4, ageMax: 8, durationSec: 540,
    videoUrl: "https://archive.org/download/pdcartooncollection/Fleischer%20Color%20Classic%20Somewhere%20in%20Dreamland%201936)%20(old%20cartoon%20vintage%20public%20domain).mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Classic", language: "English", source: "Archive.org"
  },
  {
    id: "little-lambkins",
    title: "Little Lambkins (1940)",
    description: "Fleischer Color Classic - gentle adventure for preschoolers.",
    ageMin: 4, ageMax: 7, durationSec: 480,
    videoUrl: "https://archive.org/download/pdcartooncollection/Fleischer%20Color%20Classic%20Little%20Lambkins%201940%20(old%20free%20cartoon%20public%20domain).mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Classic", language: "English", source: "Archive.org"
  },
  {
    id: "old-mother-hubbard",
    title: "Old Mother Hubbard (1935)",
    description: "ComiColor cartoon based on the nursery rhyme. Great for English learning.",
    ageMin: 4, ageMax: 8, durationSec: 420,
    videoUrl: "https://archive.org/download/pdcartooncollection/COMICOLOR%20-%201935%20-%20_Old%20Mother%20Hubbard_.mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Nursery", language: "English", source: "Archive.org"
  },
  {
    id: "simple-simon",
    title: "Simple Simon (ComiColor)",
    description: "Fun short cartoon with simple English dialogue and music.",
    ageMin: 4, ageMax: 8, durationSec: 360,
    videoUrl: "https://archive.org/download/pdcartooncollection/ComiColor_%20Simple%20Simon.mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Nursery", language: "English", source: "Archive.org"
  },
  {
    id: "brementown-musicians",
    title: "The Bremen Town Musicians",
    description: "UB Iwerks ComiColor – classic fairy tale cartoon, safe for young kids.",
    ageMin: 4, ageMax: 8, durationSec: 480,
    videoUrl: "https://archive.org/download/pdcartooncollection/Brementown%20Musicians%20UB%20Iwerks%20ComiColor.mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Fairy Tale", language: "English", source: "Archive.org"
  },
  {
    id: "hawaiian-birds",
    title: "Hawaiian Birds (1936)",
    description: "Fleischer Color Classic – gentle musical cartoon suitable for young children.",
    ageMin: 4, ageMax: 8, durationSec: 420,
    videoUrl: "https://archive.org/download/pdcartooncollection/Fleischer%20cartoon%20Color%20Classic%20Hawaiian%20Birds%201936(old%20cartoons%20vintage%20public%20domain).mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Classic", language: "English", source: "Archive.org"
  },
  {
    id: "greedy-humpty",
    title: "Greedy Humpty Dumpty (1936)",
    description: "Fleischer Color Classic nursery rhyme cartoon – English learning friendly.",
    ageMin: 4, ageMax: 8, durationSec: 420,
    videoUrl: "https://archive.org/download/pdcartooncollection/Fleischer%20cartoon%20Color%20Classic%20Greedy%20Humpty%20Dumpty%201936%20(old%20cartoon%20vintage%20public%20domain).mp4",
    thumbnailUrl: "https://archive.org/services/img/pdcartooncollection",
    category: "Nursery", language: "English", source: "Archive.org"
  }
];

// Block titles that are not suitable for ages 4-8
const BLOCK = [/horror/i, /terror/i, /war\b/i, /kill/i, /death/i, /adult/i, /nude/i, /sex/i, /violent/i, /gore/i, /zombie/i, /slasher/i];

function isKidSafe(title, desc) {
  const t = (title || "") + " " + (desc || "");
  return !BLOCK.some((re) => re.test(t));
}

async function searchArchive(query, rows = 12) {
  const url =
    "https://archive.org/advancedsearch.php?q=" +
    encodeURIComponent(query) +
    "&fl[]=identifier&fl[]=title&fl[]=description&fl[]=year&fl[]=mediatype" +
    "&sort[]=downloads+desc&rows=" +
    rows +
    "&page=1&output=json";
  try {
    const res = await fetch(url, { headers: { "User-Agent": "KidsCartoonsWorker/1.0" } });
    if (!res.ok) return [];
    const data = await res.json();
    return (data.response && data.response.docs) || [];
  } catch (e) {
    return [];
  }
}

async function findMp4(identifier) {
  try {
    const res = await fetch("https://archive.org/metadata/" + encodeURIComponent(identifier), {
      headers: { "User-Agent": "KidsCartoonsWorker/1.0" }
    });
    if (!res.ok) return null;
    const data = await res.json();
    const files = data.files || [];
    // Prefer smaller H.264 / MPEG4 under ~200MB for kids app
    const mp4s = files.filter((f) => {
      const n = (f.name || "").toLowerCase();
      const fmt = (f.format || "").toLowerCase();
      return n.endsWith(".mp4") || fmt.includes("mpeg4") || fmt.includes("h.264");
    });
    if (!mp4s.length) return null;
    // Prefer medium size
    mp4s.sort((a, b) => {
      const sa = parseInt(a.size || "0", 10);
      const sb = parseInt(b.size || "0", 10);
      return sa - sb;
    });
    const pick = mp4s.find((f) => parseInt(f.size || "0", 10) > 500000) || mp4s[0];
    if (!pick || !pick.name) return null;
    return "https://archive.org/download/" + encodeURIComponent(identifier) + "/" + encodeURIComponent(pick.name);
  } catch (e) {
    return null;
  }
}

async function discoverMore() {
  const queries = [
    'collection:(animationandcartoons) AND mediatype:movies AND language:eng',
    'collection:(pdcartooncollection) OR title:("color classic") AND mediatype:movies',
    '(title:kidsongs OR title:("nursery") OR subject:children) AND mediatype:movies AND language:eng'
  ];
  const seen = new Set(CURATED.map((c) => c.id));
  const found = [];

  for (const q of queries) {
    if (found.length >= 15) break;
    const docs = await searchArchive(q, 10);
    for (const doc of docs) {
      if (found.length >= 15) break;
      const id = doc.identifier;
      if (!id || seen.has(id)) continue;
      if (!isKidSafe(doc.title, doc.description)) continue;
      seen.add(id);
      const videoUrl = await findMp4(id);
      if (!videoUrl) continue;
      found.push({
        id: id,
        title: (doc.title || id).toString().slice(0, 120),
        description: ((doc.description || "Public domain English cartoon suitable for young children.").toString()).slice(0, 280),
        ageMin: 4,
        ageMax: 8,
        durationSec: 0,
        videoUrl: videoUrl,
        thumbnailUrl: "https://archive.org/services/img/" + encodeURIComponent(id),
        category: "Discovered",
        language: "English",
        source: "Archive.org (auto)"
      });
    }
  }
  return found;
}

async function buildList(forceDiscover) {
  let extra = [];
  try {
    extra = await discoverMore();
  } catch (e) {
    extra = [];
  }
  const all = CURATED.concat(extra);
  // dedupe by id
  const map = new Map();
  for (const c of all) map.set(c.id, c);
  return Array.from(map.values());
}

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    const cors = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type",
      "Content-Type": "application/json",
      "Cache-Control": "public, max-age=3600"
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: cors });
    }

    if (url.pathname === "/" || url.pathname === "/cartoons") {
      const force = url.searchParams.get("refresh") === "1";
      const list = await buildList(force);
      const q = (url.searchParams.get("q") || "").toLowerCase();
      const filtered = q
        ? list.filter(
            (c) =>
              c.title.toLowerCase().includes(q) ||
              (c.description || "").toLowerCase().includes(q) ||
              (c.category || "").toLowerCase().includes(q)
          )
        : list;

      return new Response(
        JSON.stringify({
          updatedAt: new Date().toISOString(),
          count: filtered.length,
          curated: CURATED.length,
          discovered: filtered.length - CURATED.length,
          cartoons: filtered
        }),
        { headers: cors }
      );
    }

    if (url.pathname === "/health") {
      return new Response(
        JSON.stringify({ ok: true, curated: CURATED.length, worker: "acrtoonfarinaz" }),
        { headers: cors }
      );
    }

    return new Response(JSON.stringify({ error: "Not found" }), { status: 404, headers: cors });
  },

  async scheduled(event, env, ctx) {
    // Daily cron: warm discovery so next app open is faster
    ctx.waitUntil(buildList(true));
  }
};
