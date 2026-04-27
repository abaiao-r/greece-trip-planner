package dev.greecetripplanner.data

import dev.greecetripplanner.data.model.Category
import dev.greecetripplanner.data.model.DayNarrative
import dev.greecetripplanner.data.model.Poi
import dev.greecetripplanner.data.model.Region
import dev.greecetripplanner.data.model.TripTemplate

/**
 * All static trip data extracted from the web planner.
 * 124 POIs, 18 regions, 13 categories, 8+ templates.
 */
object TripData {

    // ── Categories ──
    val categories = listOf(
        Category("history", "🏛️", "History", "#1c69d4"),
        Category("museum", "🏺", "Museum", "#7e57c2"),
        Category("church", "⛪", "Church", "#8d6e63"),
        Category("view", "👁️", "Views", "#e89b00"),
        Category("hike", "🥾", "Hike", "#00823b"),
        Category("food", "🍽️", "Food", "#c91432"),
        Category("bar", "🍸", "Bar/Café", "#ad1457"),
        Category("beach", "🏖️", "Beach", "#009A97"),
        Category("nature", "🌿", "Nature", "#2e7d32"),
        Category("garden", "🌳", "Garden", "#558b2f"),
        Category("village", "🏘️", "Village", "#666666"),
        Category("culture", "🎭", "Culture", "#5c6bc0"),
        Category("market", "🛍️", "Market", "#ef6c00"),
    )

    val categoryMap = categories.associateBy { it.key }

    // ── Regions ──
    val regions = listOf(
        Region("athens", "Athens", 15, 37.975, 23.727, "Capital city with world-famous ancient ruins, vibrant neighborhoods, and rooftop bars overlooking the Acropolis.", 25),
        Region("delphi", "Delphi", 10, 38.482, 22.501, "Sacred Oracle site on Mount Parnassus with temple ruins, stunning mountain views, and the charming village of Arachova.", 9),
        Region("meteora", "Meteora", 15, 39.722, 21.631, "Gravity-defying monasteries perched on towering rock pillars. One of the most dramatic landscapes in Europe.", 8),
        Region("ioannina", "Ioannina", 12, 39.665, 20.854, "Lakeside university city with Ottoman fortress, island monastery, and gateway to the Pindus mountains.", 8),
        Region("zagori", "Zagori", 20, 39.910, 20.753, "Remote mountain villages connected by stone bridges and ancient paths. Home to Vikos Gorge and Dragon Lake.", 10),
        Region("thessaloniki", "Thessaloniki", 12, 40.640, 22.944, "Second city of Greece. Byzantine churches, Roman ruins, waterfront promenade, and the best food scene in the country.", 16),
        Region("vergina", "Vergina", 5, 40.486, 22.315, "Ancient Macedonian capital with Philip II's royal tomb and gold treasures. Underground UNESCO museum.", 2),
        Region("pella", "Pella", 5, 40.762, 22.524, "Birthplace of Alexander the Great. Stunning floor mosaics and palace ruins.", 2),
        Region("halkidiki", "Halkidiki", 25, 40.080, 23.680, "Three-pronged peninsula with turquoise beaches, pine forests, and traditional fishing villages.", 6),
        Region("olympus", "Mt Olympus", 15, 40.103, 22.502, "Home of the gods. Greece's highest peak with gorge hikes, alpine monasteries, and a lively base town.", 6),
        Region("pelion", "Pelion", 20, 39.385, 23.050, "Lush green peninsula. Stone villages, ancient forest paths, and hidden beaches from Mamma Mia.", 8),
        Region("volos", "Volos", 10, 39.362, 22.943, "Port city famous for tsipouro tavernas (free meze!) and waterfront promenades facing Pelion.", 3),
        Region("thermopylae", "Thermopylae", 5, 38.796, 22.535, "Pass of the 300 Spartans. Leonidas monument and natural hot springs. Quick highway stop.", 2),
        // Peloponnese
        Region("nafplio", "Nafplio & Argolid", 15, 37.567, 22.803, "First capital of modern Greece. Venetian fortress, ancient Mycenae, Epidaurus theater, and Corinth Canal.", 6),
        Region("monemvasia", "Monemvasia", 10, 36.688, 23.056, "Gibraltar of Greece. Medieval rock fortress rising from the sea, hidden town behind the walls.", 3),
        Region("mystras", "Mystras & Sparta", 10, 37.075, 22.366, "UNESCO Byzantine ghost city on a hillside above Sparta. Last capital of the Byzantine Empire.", 3),
        Region("mani", "Mani Peninsula", 15, 36.650, 22.390, "Tower villages, sea caves, and the mythological entrance to Hades at Cape Tainaron.", 5),
        Region("olympia", "Ancient Olympia", 10, 37.639, 21.630, "Birthplace of the Olympic Games. Temple ruins, stadium, and world-class museum.", 4),
    )

    val regionMap = regions.associateBy { it.key }

    // ── Drive time matrix (hours between region pairs) ──
    private val driveTimePairs = mapOf(
        // Athens hub
        "athens|delphi" to 2.5, "athens|thermopylae" to 2.0,
        "athens|volos" to 3.0, "athens|pelion" to 3.5,
        "athens|olympus" to 4.5, "athens|meteora" to 4.0,
        "athens|ioannina" to 5.0, "athens|zagori" to 5.5,
        "athens|thessaloniki" to 5.0, "athens|vergina" to 5.0,
        "athens|pella" to 5.5, "athens|halkidiki" to 6.0,
        // Delphi
        "delphi|thermopylae" to 1.0, "delphi|volos" to 2.5,
        "delphi|pelion" to 3.0, "delphi|olympus" to 3.5,
        "delphi|meteora" to 3.0, "delphi|ioannina" to 3.0,
        "delphi|zagori" to 3.5, "delphi|thessaloniki" to 4.5,
        "delphi|vergina" to 3.5, "delphi|pella" to 4.0,
        "delphi|halkidiki" to 5.5,
        // Thermopylae
        "thermopylae|volos" to 1.5, "thermopylae|pelion" to 2.0,
        "thermopylae|olympus" to 2.5, "thermopylae|meteora" to 2.5,
        "thermopylae|ioannina" to 4.0, "thermopylae|zagori" to 4.0,
        "thermopylae|thessaloniki" to 3.5, "thermopylae|vergina" to 3.5,
        "thermopylae|pella" to 4.0, "thermopylae|halkidiki" to 4.5,
        // Volos / Pelion
        "volos|pelion" to 0.5, "volos|olympus" to 1.5,
        "volos|meteora" to 2.0, "volos|ioannina" to 4.0,
        "volos|zagori" to 4.5, "volos|thessaloniki" to 3.0,
        "volos|vergina" to 2.5, "volos|pella" to 3.0,
        "volos|halkidiki" to 4.0,
        "pelion|olympus" to 2.0, "pelion|meteora" to 2.5,
        "pelion|ioannina" to 4.5, "pelion|zagori" to 5.0,
        "pelion|thessaloniki" to 3.5, "pelion|vergina" to 3.0,
        "pelion|pella" to 3.5, "pelion|halkidiki" to 4.5,
        // Olympus
        "olympus|meteora" to 2.0, "olympus|ioannina" to 3.5,
        "olympus|zagori" to 4.0, "olympus|thessaloniki" to 1.5,
        "olympus|vergina" to 1.5, "olympus|pella" to 2.0,
        "olympus|halkidiki" to 3.0,
        // Meteora
        "meteora|ioannina" to 2.0, "meteora|zagori" to 2.5,
        "meteora|thessaloniki" to 2.5, "meteora|vergina" to 2.0,
        "meteora|pella" to 2.5, "meteora|halkidiki" to 3.5,
        // Ioannina / Zagori
        "ioannina|zagori" to 0.5, "ioannina|thessaloniki" to 3.0,
        "ioannina|vergina" to 3.0, "ioannina|pella" to 3.5,
        "ioannina|halkidiki" to 4.0,
        "zagori|thessaloniki" to 3.5, "zagori|vergina" to 3.0,
        "zagori|pella" to 3.5, "zagori|halkidiki" to 4.5,
        // Thessaloniki
        "thessaloniki|vergina" to 1.0, "thessaloniki|pella" to 0.75,
        "thessaloniki|halkidiki" to 1.5,
        // Vergina / Pella
        "vergina|pella" to 0.75, "vergina|halkidiki" to 2.0,
        "pella|halkidiki" to 2.0,
        // Peloponnese network
        "athens|nafplio" to 2.0, "athens|monemvasia" to 4.0,
        "athens|mystras" to 2.5, "athens|mani" to 4.0,
        "athens|olympia" to 3.5,
        "nafplio|monemvasia" to 2.5, "nafplio|mystras" to 1.75,
        "nafplio|mani" to 3.0, "nafplio|olympia" to 2.5,
        "monemvasia|mystras" to 1.5, "monemvasia|mani" to 1.5,
        "monemvasia|olympia" to 4.0,
        "mystras|mani" to 1.5, "mystras|olympia" to 3.0,
        "mani|olympia" to 3.5,
        "olympia|delphi" to 2.5, "nafplio|delphi" to 3.0,
    )

    fun driveHours(a: String, b: String): Double {
        if (a == b) return 0.0
        return driveTimePairs["$a|$b"]
            ?: driveTimePairs["$b|$a"]
            ?: 3.0 // fallback
    }

    // ── Drive distance (km) between regions ──
    private val driveKmPairs = mapOf(
        // Known from HTML itinerary
        "athens|delphi" to 180, "delphi|ioannina" to 280,
        "ioannina|zagori" to 45, "zagori|meteora" to 165,
        "meteora|olympus" to 155, "olympus|athens" to 410,
        "olympus|thessaloniki" to 90, "thessaloniki|pelion" to 220,
        "athens|thermopylae" to 200, "thermopylae|volos" to 120,
        // Peloponnese
        "athens|nafplio" to 150, "nafplio|monemvasia" to 200,
        "monemvasia|mystras" to 100, "mystras|mani" to 80,
        "mani|olympia" to 250, "olympia|athens" to 310,
        "nafplio|mystras" to 130, "athens|monemvasia" to 350,
        "athens|mystras" to 220, "athens|mani" to 300,
        "athens|olympia" to 310, "nafplio|olympia" to 200,
    )

    fun driveKm(a: String, b: String): Int {
        if (a == b) return 0
        return driveKmPairs["$a|$b"]
            ?: driveKmPairs["$b|$a"]
            ?: (driveHours(a, b) * 80).toInt()
    }

    // ── POIs — all 103 ──
    val pois: List<Poi> = listOf(
        // ATHENS (25)
        Poi("ath-01", "Acropolis & Parthenon", "athens", 2.5, listOf("history"), 37.9715, 23.7267, "Iconic hilltop citadel with Parthenon, Erechtheion, Propylaea. Book timed tickets.", "⚠ Go at 8 AM or after 5 PM. Queues brutal midday."),
        Poi("ath-02", "Acropolis Museum", "athens", 2.0, listOf("museum"), 37.9685, 23.7285, "Parthenon frieze, Caryatids, archaic sculptures. Glass floor over excavations. Air-conditioned."),
        Poi("ath-03", "Ancient Agora & Hephaestus Temple", "athens", 1.5, listOf("history"), 37.9750, 23.7222, "Best-preserved Greek temple. Heart of ancient Athenian democracy. Stoa of Attalos museum."),
        Poi("ath-04", "National Archaeological Museum", "athens", 2.0, listOf("museum"), 37.9891, 23.7328, "Mask of Agamemnon, Antikythera mechanism, Cycladic figurines. World-class collection."),
        Poi("ath-05", "Benaki Museum of Greek Culture", "athens", 1.5, listOf("museum"), 37.9758, 23.7420, "6,000 years of Greek culture from neolithic to modern. Rooftop café with Acropolis view."),
        Poi("ath-06", "Museum of Cycladic Art", "athens", 1.0, listOf("museum"), 37.9760, 23.7405, "Elegant Cycladic figurines, ancient Greek art. Small but superb quality."),
        Poi("ath-07", "National Garden stroll", "athens", 1.0, listOf("garden"), 37.9730, 23.7370, "Shaded 15-hectare park. Ponds, turtles, Zappeion Hall. Perfect midday escape from heat."),
        Poi("ath-08", "Philopappos Hill sunset walk", "athens", 1.0, listOf("hike", "view"), 37.9681, 23.7210, "1.5 km loop with the best Acropolis view at golden hour. Pine-shaded paths."),
        Poi("ath-09", "Areopagus Rock", "athens", 0.5, listOf("view"), 37.9722, 23.7239, "Free sunset spot with panoramic Acropolis view. Slippery marble — wear good shoes."),
        Poi("ath-10", "Lycabettus Hill hike", "athens", 1.5, listOf("hike", "view"), 37.9799, 23.7449, "Highest point in Athens (277 m). Hike 30 min or take funicular. 360° city panorama."),
        Poi("ath-11", "Plaka stroll & souvlaki", "athens", 1.5, listOf("food", "culture"), 37.9747, 23.7264, "Historic neighborhood. Narrow lanes, souvlaki at Thanasis, traditional shops."),
        Poi("ath-12", "Monastiraki Flea Market", "athens", 1.0, listOf("market"), 37.9760, 23.7240, "Antiques, vintage finds, handmade souvenirs. Best on Sundays. Haggle expected."),
        Poi("ath-13", "Temple of Olympian Zeus", "athens", 0.5, listOf("history"), 37.9693, 23.7331, "17 massive columns remain of the largest ancient Greek temple. Hadrian's Arch next door."),
        Poi("ath-14", "Panathenaic Stadium", "athens", 0.5, listOf("history"), 37.9683, 23.7414, "All-marble stadium from 330 BC. First modern Olympics (1896). Run the track."),
        Poi("ath-15", "Syntagma Square & Evzones", "athens", 0.5, listOf("culture"), 37.9755, 23.7348, "Changing of the Guard. Every hour, elaborate ceremony Sundays at 11 AM."),
        Poi("ath-16", "Psyrri dinner & bars", "athens", 2.0, listOf("bar", "food"), 37.9782, 23.7245, "Buzzing district. Traditional tavernas and craft cocktail bars. Live music some nights."),
        Poi("ath-17", "Central Market (Varvakeios)", "athens", 0.5, listOf("market", "food"), 37.9810, 23.7280, "Fish, meat, spices, olives. Chaotic and authentic. Go before noon."),
        Poi("ath-18", "Anafiotika neighborhood", "athens", 0.5, listOf("view", "culture"), 37.9725, 23.7290, "Cycladic-style white houses hidden on Acropolis slopes. Feels like an island village."),
        Poi("ath-19", "Kerameikos cemetery & museum", "athens", 1.0, listOf("history", "museum"), 37.9783, 23.7180, "Ancient cemetery with Street of Tombs. Small museum. Atmospheric, few tourists."),
        Poi("ath-20", "Church of Kapnikarea (exterior)", "athens", 0.25, listOf("church"), 37.9756, 23.7271, "11th-century Byzantine church in the middle of Ermou St. Quick photo stop."),
        Poi("ath-21", "Church of Kapnikarea (interior)", "athens", 0.5, listOf("church"), 37.9756, 23.7271, "Step inside for gold mosaics and Byzantine frescoes. Small, intimate, free entry."),
        Poi("ath-22", "A for Athens rooftop bar", "athens", 1.0, listOf("bar", "view"), 37.9757, 23.7250, "Craft cocktails with lit Acropolis view. Book ahead for sunset seats."),
        Poi("ath-23", "Ermou Street & shops", "athens", 0.75, listOf("culture", "market"), 37.9770, 23.7300, "Main shopping street. Brands, local boutiques, street performers."),
        Poi("ath-24", "Stavros Niarchos Foundation", "athens", 1.5, listOf("garden", "culture"), 37.9401, 23.6927, "Renzo Piano park. Canal, olive groves, rooftop views. National Library & Opera inside."),
        Poi("ath-25", "Cape Sounion (Temple of Poseidon)", "athens", 4.0, listOf("history", "view"), 37.6502, 24.0244, "Temple ruins on dramatic cliff over Aegean. Legendary sunset.", "⚠ 70 km each way from central Athens. Half-day commitment."),

        // DELPHI (9)
        Poi("del-01", "Archaeological Site (Temple of Apollo)", "delphi", 2.0, listOf("history"), 38.4824, 22.5010, "Sacred Way, Treasury of Athens, Temple of Apollo, theater. UNESCO. On Mount Parnassus."),
        Poi("del-02", "Tholos of Athena Pronaia", "delphi", 0.5, listOf("history", "view"), 38.4797, 22.5048, "Iconic circular temple. 10 min walk from main site. THE photo of Delphi."),
        Poi("del-03", "Delphi Museum", "delphi", 1.5, listOf("museum"), 38.4818, 22.4988, "Charioteer bronze, Sphinx of Naxos, friezes. Air-conditioned. Essential companion to the site."),
        Poi("del-04", "Ancient Theater sunset", "delphi", 0.5, listOf("view"), 38.4834, 22.5010, "Sunset over the olive valley from the theater seats. Best light in Greece."),
        Poi("del-05", "Arachova village", "delphi", 1.0, listOf("village", "food"), 38.4796, 22.5885, "Charming mountain village. Boutique shops, local cheese, wine. 10 min from Delphi."),
        Poi("del-06", "Mount Parnassus E4 trail", "delphi", 3.5, listOf("hike"), 38.5350, 22.6210, "Section of E4 European trail. Alpine meadows, panoramic views at 1,800 m. Moderate difficulty."),
        Poi("del-07", "Corycian Cave hike", "delphi", 4.5, listOf("hike"), 38.5120, 22.5680, "Ancient cave sacred to Pan. 7 km through forest. Bring flashlight.", "⚠ Full day. Start early, good shoes essential."),
        Poi("del-08", "Galaxidi coastal town", "delphi", 2.0, listOf("village", "beach"), 38.3770, 22.3791, "Quiet seaside town 35 min from Delphi. Swim, seafood lunch, harbor walk."),
        Poi("del-09", "Arachova café & views", "delphi", 0.75, listOf("bar", "view"), 38.4800, 22.5890, "Mountain café with valley views. Local loukoumades (honey doughnuts), Greek coffee."),

        // METEORA (8)
        Poi("met-01", "Great Meteoron Monastery", "meteora", 1.5, listOf("history", "church"), 39.7270, 21.6260, "Largest and oldest (14th c.). Museum, frescoes, ossuary. Dress code: covered shoulders/knees."),
        Poi("met-02", "Varlaam Monastery", "meteora", 1.0, listOf("history", "view"), 39.7252, 21.6280, "Stunning rooftop panorama, 16th-c. frescoes. Second most visited, slightly less crowded."),
        Poi("met-03", "Holy Trinity Monastery", "meteora", 1.0, listOf("history", "view"), 39.7175, 21.6350, "Most dramatic setting. Featured in James Bond. 140 steps carved in rock."),
        Poi("met-04", "Roussanou Monastery", "meteora", 0.5, listOf("church"), 39.7200, 21.6300, "Perched on a narrow pillar. Intimate and beautiful. Run by nuns. Small but memorable."),
        Poi("met-05", "Psaropetra sunset viewpoint", "meteora", 0.5, listOf("view"), 39.7217, 21.6306, "All six monasteries visible. The iconic Meteora panorama at golden hour."),
        Poi("met-06", "Meteora forest trails", "meteora", 2.0, listOf("hike"), 39.7230, 21.6230, "3-6 km loops through forest between rock pillars. Best after monastery closing time."),
        Poi("met-07", "Kalambaka town dinner", "meteora", 1.5, listOf("food"), 39.7040, 21.6276, "Tavernas with rock pillar views. Try moussaka and local wine. Relaxed atmosphere."),
        Poi("met-08", "Adrachti pinnacle hike", "meteora", 1.0, listOf("hike", "view"), 39.7190, 21.6240, "Short scramble to a dramatic rock viewpoint. Fewer tourists than main spots."),

        // IOANNINA (8)
        Poi("ioa-01", "Its Kale Fortress", "ioannina", 1.0, listOf("history", "view"), 39.6710, 20.8530, "Byzantine citadel on the lake. Two mosques, city walls, panoramic lake views."),
        Poi("ioa-02", "Ali Pasha Museum & Island", "ioannina", 1.5, listOf("history", "museum"), 39.6665, 20.8480, "Boat to lake island. Ottoman history. The room where Ali Pasha was killed. Museum + boat ride."),
        Poi("ioa-03", "Old Bazaar area dinner", "ioannina", 1.5, listOf("food"), 39.6680, 20.8525, "Souvlaki, grilled meats, pies. Try bougatsa for dessert. Cozy alleys."),
        Poi("ioa-04", "Byzantine Museum", "ioannina", 1.0, listOf("museum"), 39.6705, 20.8537, "Silverwork, manuscripts, icons. Inside the fortress. Compact but high quality."),
        Poi("ioa-05", "Lake Pamvotis promenade", "ioannina", 0.5, listOf("view"), 39.6650, 20.8510, "Lakeside walk with Pindus mountain backdrop. Beautiful late afternoon light."),
        Poi("ioa-06", "Perama Cave", "ioannina", 1.0, listOf("nature"), 39.6800, 20.8440, "One of Europe's largest caves. Stalactites, underground river. 4 km from city. Guided tour."),
        Poi("ioa-07", "Lakefront bars & tsipouro", "ioannina", 1.5, listOf("bar"), 39.6660, 20.8550, "Waterfront cafés and bars. Tsipouro with meze. Student city, lively nightlife."),
        Poi("ioa-08", "Church of Agios Nikolaos (interior)", "ioannina", 0.5, listOf("church"), 39.6700, 20.8540, "Inside the fortress. 16th-c. frescoes by Cretan painters. Dark, intimate, well-preserved."),

        // ZAGORI (10)
        Poi("zag-01", "Oxya glass viewpoint", "zagori", 0.5, listOf("view"), 39.9260, 20.7640, "Glass platform with 900 m sheer drop into Vikos Gorge. Vertigo guaranteed."),
        Poi("zag-02", "Beloi viewpoint hike", "zagori", 1.5, listOf("hike", "view"), 39.9140, 20.7680, "30 min from Vradeto. Gorge panorama from above. One of the best viewpoints in Greece."),
        Poi("zag-03", "Vikos Gorge full hike", "zagori", 7.0, listOf("hike"), 39.9100, 20.7530, "Monodendri to Vikos village. 12 km through one of the world's deepest gorges.", "⚠ Full day. Start 7 AM. Bring 2L water, food, sturdy shoes. No shade."),
        Poi("zag-04", "Plakidas Bridge (Kalogeriko)", "zagori", 0.5, listOf("history", "view"), 39.8970, 20.7560, "Triple-arched 18th-c. stone bridge. Most famous of Zagori's 60+ bridges."),
        Poi("zag-05", "Monodendri village stroll", "zagori", 1.0, listOf("village"), 39.8900, 20.7520, "Stone houses, narrow paths. Agia Paraskevi monastery on the gorge edge."),
        Poi("zag-06", "Papingo pools & village", "zagori", 2.5, listOf("nature", "village"), 39.9440, 20.7370, "Twin stone villages + natural rock pools with turquoise water. Swimming spot."),
        Poi("zag-07", "Drakolimni (Dragon Lake) hike", "zagori", 7.0, listOf("hike"), 39.9800, 20.7900, "Glacial lake at 2,050 m. 12 km from Mikro Papingo. Wild horses, zero tourists.", "⚠ Full day. Cold at altitude even in June. Sturdy shoes essential."),
        Poi("zag-08", "Vradeto Steps", "zagori", 1.0, listOf("hike", "history"), 39.9100, 20.7700, "Oldest stone staircase path in Greece (18th c.). 1,100 steps."),
        Poi("zag-09", "Kipoi stone bridges walk", "zagori", 1.0, listOf("history", "view"), 39.8860, 20.7720, "Three Ottoman-era bridges near Kipoi village. Easy streamside walk."),
        Poi("zag-10", "Zagori village taverna dinner", "zagori", 1.5, listOf("food"), 39.9000, 20.7550, "Home-cooked mountain food. Pies, grilled lamb, tsipouro. Fireplace atmosphere."),

        // THESSALONIKI (16)
        Poi("thes-01", "White Tower", "thessaloniki", 1.0, listOf("history", "view"), 40.6263, 22.9485, "City landmark, 15th-c. Ottoman tower. Museum inside, rooftop panorama of the gulf."),
        Poi("thes-02", "Rotunda (exterior)", "thessaloniki", 0.25, listOf("church", "history"), 40.6337, 22.9529, "4th-c. Roman building, later church, later mosque. Massive dome. Quick exterior view."),
        Poi("thes-03", "Rotunda (interior visit)", "thessaloniki", 0.75, listOf("church", "museum"), 40.6337, 22.9529, "Rare early Christian mosaics on the dome. UNESCO-listed. Small entrance fee."),
        Poi("thes-04", "Arch of Galerius", "thessaloniki", 0.25, listOf("history"), 40.6330, 22.9527, "Roman triumphal arch (AD 303). Carved reliefs of Galerius's Persian campaign. Street-side, free."),
        Poi("thes-05", "Aristotelous Square café", "thessaloniki", 0.75, listOf("food", "culture"), 40.6326, 22.9407, "Iconic waterfront square. Coffee, bougatsa, people-watching. The heart of the city."),
        Poi("thes-06", "Ano Poli (Upper Town) walk", "thessaloniki", 1.5, listOf("view", "culture"), 40.6430, 22.9530, "Byzantine walls, Ottoman houses, narrow streets. Panoramic views from the Trigonion Tower."),
        Poi("thes-07", "Museum of Byzantine Culture", "thessaloniki", 1.5, listOf("museum"), 40.6260, 22.9550, "UNESCO World Heritage. Icons, mosaics, early Christian artifacts. One of the best in Greece."),
        Poi("thes-08", "Archaeological Museum", "thessaloniki", 1.5, listOf("museum"), 40.6257, 22.9513, "Gold of Macedon, Vergina treasures, Derveni krater. Alexander the Great's world."),
        Poi("thes-09", "Agios Dimitrios (exterior)", "thessaloniki", 0.25, listOf("church"), 40.6385, 22.9475, "5th-c. basilica, patron saint of the city. Exterior with distinctive brickwork. Quick visit."),
        Poi("thes-10", "Agios Dimitrios (interior)", "thessaloniki", 0.75, listOf("church"), 40.6385, 22.9475, "Mosaics, silver relics of Saint Dimitrios, underground Roman forum ruins. Moving atmosphere."),
        Poi("thes-11", "Modiano Market", "thessaloniki", 1.0, listOf("market", "food"), 40.6345, 22.9430, "Renovated covered market. Fresh produce, spices, seafood, pastries. Food hall upstairs."),
        Poi("thes-12", "Ladadika bars district", "thessaloniki", 2.0, listOf("bar", "food"), 40.6320, 22.9376, "Former oil warehouse district. Restaurants, cocktail bars, live music. Buzzing at night."),
        Poi("thes-13", "Waterfront promenade (Nea Paralia)", "thessaloniki", 1.0, listOf("view"), 40.6210, 22.9520, "5 km walk along the gulf. Themed gardens, sculptures, bike path. Sunset recommended."),
        Poi("thes-14", "Umbrellas sculpture", "thessaloniki", 0.25, listOf("culture"), 40.6184, 22.9543, "Zongolopoulos's iconic steel umbrellas on the waterfront. Mandatory photo stop."),
        Poi("thes-15", "Bit Bazaar vintage market", "thessaloniki", 0.75, listOf("market"), 40.6350, 22.9445, "Vintage records, retro furniture, antiques. Quirky finds in refurbished arcades."),
        Poi("thes-16", "Thessaloniki food walk (bougatsa trail)", "thessaloniki", 1.5, listOf("food", "culture"), 40.6360, 22.9420, "Bougatsa at Bantis, koulouri, souvlaki strip. Self-guided street food circuit."),

        // VERGINA (2)
        Poi("ver-01", "Royal Tombs of Aegae (UNESCO)", "vergina", 2.0, listOf("history", "museum"), 40.4860, 22.3150, "Underground museum with Philip II's tomb, gold wreath, armor. One of Greece's greatest finds."),
        Poi("ver-02", "Vergina archaeological site", "vergina", 0.5, listOf("history"), 40.4880, 22.3120, "Palace ruins of ancient Macedonian capital. Open-air, partially excavated."),

        // PELLA (2)
        Poi("pla-01", "Pella Archaeological Site", "pella", 1.0, listOf("history"), 40.7620, 22.5240, "Birthplace of Alexander the Great. Mosaic floors, palace foundations, columned streets."),
        Poi("pla-02", "Pella Museum", "pella", 1.0, listOf("museum"), 40.7615, 22.5235, "Stunning floor mosaics (lion hunt, deer hunt), Macedonian artifacts. Modern building."),

        // HALKIDIKI (6)
        Poi("hal-01", "Kassandra west coast beaches", "halkidiki", 2.5, listOf("beach"), 39.9580, 23.4100, "Sandy beaches with turquoise water. Kallithea, Sani, Possidi. Sunbeds available."),
        Poi("hal-02", "Afytos village", "halkidiki", 1.0, listOf("village", "view"), 40.0950, 23.4330, "Stone village on a cliff. Traditional architecture, artisan shops, panoramic sea views."),
        Poi("hal-03", "Sithonia coast scenic drive", "halkidiki", 2.5, listOf("view", "beach"), 40.0100, 23.7800, "Winding road along the second peninsula. Stop at hidden coves and pine-fringed beaches."),
        Poi("hal-04", "Vourvourou beach", "halkidiki", 2.0, listOf("beach"), 40.1580, 23.7780, "Shallow turquoise lagoon with Diaporos island view. Caribbean-like water."),
        Poi("hal-05", "Sarti beach & village", "halkidiki", 1.5, listOf("beach", "food"), 40.0690, 23.9700, "Long sandy beach backed by tavernas. View of Mount Athos across the water."),
        Poi("hal-06", "Porto Koufo harbor", "halkidiki", 1.5, listOf("beach", "food"), 39.9750, 23.9050, "Hidden natural harbor. Calm swimming, fresh fish at dockside tavernas."),

        // OLYMPUS (6)
        Poi("oly-01", "Enipeas Gorge trail", "olympus", 3.5, listOf("hike"), 40.0900, 22.4900, "8 km river canyon with waterfalls, wooden bridges. Shaded, river-cooled. Moderate."),
        Poi("oly-02", "Prionia to Refuge A hike", "olympus", 7.0, listOf("hike"), 40.0859, 22.3500, "6 km ascent to 2,100 m. Alpine forest, above treeline. Serious mountain day.", "⚠ Full day. Start by 7 AM. 2L water, layers, food. Check weather."),
        Poi("oly-03", "Monastery of Agios Dionysios (exterior)", "olympus", 0.25, listOf("church"), 40.0850, 22.4700, "16th-c. monastery perched over Enipeas Gorge. Dramatic setting, quick photo stop."),
        Poi("oly-04", "Monastery of Agios Dionysios (interior)", "olympus", 0.5, listOf("church", "history"), 40.0850, 22.4700, "Frescoed chapel, monk's quarters, garden. Peaceful atmosphere inside the gorge."),
        Poi("oly-05", "Litochoro town square & dinner", "olympus", 1.5, listOf("food"), 40.1034, 22.5020, "Lively plateia at the foot of Olympus. Tavernas with mountain views. Try lamb kleftiko."),
        Poi("oly-06", "Ancient Dion", "olympus", 1.5, listOf("history", "museum"), 40.1700, 22.4860, "Sacred city of the Macedonians at Olympus' base. Mosaics, temples, excellent museum."),

        // PELION (8)
        Poi("pln-01", "Tsagarada village & plane tree", "pelion", 1.0, listOf("village"), 39.3850, 23.1150, "Mountain village with 1,000-year-old plane tree. Stone squares, shops, café."),
        Poi("pln-02", "Mylopotamos beach kalderimi", "pelion", 2.5, listOf("hike", "beach"), 39.3960, 23.1580, "2.5 km stone path through forest to white-pebble beach with natural rock arch."),
        Poi("pln-03", "Makrinitsa village", "pelion", 1.0, listOf("village", "view"), 39.3920, 22.9980, "The \"balcony of Pelion\". Stone mansions, cobbled square, panoramic views over Volos Gulf."),
        Poi("pln-04", "Damouchari beach & harbor", "pelion", 1.5, listOf("beach"), 39.4080, 23.1610, "Tiny harbor from Mamma Mia. Crystal water, taverna on the rocks. Magic."),
        Poi("pln-05", "Pelion forest kalderimi trail", "pelion", 3.0, listOf("hike"), 39.3750, 23.0800, "Ancient stone paths through chestnut and beech forest. Green and lush in June. Moderate."),
        Poi("pln-06", "Portaria village & church", "pelion", 0.75, listOf("village", "church"), 39.3930, 22.9760, "Pretty mountain village. Church with carved wooden screen. Good cafe stop."),
        Poi("pln-07", "Milies village & train", "pelion", 1.5, listOf("village", "culture"), 39.3300, 23.0400, "Stone village with library, churches. Historic narrow-gauge train runs to Ano Lehonia."),
        Poi("pln-08", "Kissos village taverna", "pelion", 1.0, listOf("food", "village"), 39.4000, 23.0900, "Authentic mountain taverna. Spetsofai (sausage stew), local wine, chestnut bread."),

        // VOLOS (3)
        Poi("vol-01", "Volos waterfront promenade", "volos", 1.0, listOf("view"), 39.3622, 22.9429, "Broad seaside promenade. Views to Pelion hills across the gulf. Sunset stroll."),
        Poi("vol-02", "Volos tsipouradika crawl", "volos", 1.5, listOf("food", "bar"), 39.3615, 22.9445, "Unique tradition: order tsipouro, get free meze plates. Hop between 2-3 spots."),
        Poi("vol-03", "Athanasakeion Archaeological Museum", "volos", 1.0, listOf("museum"), 39.3635, 22.9410, "Neolithic and Bronze Age finds from Thessaly. Painted stelae from nearby Demetrias."),

        // THERMOPYLAE (2)
        Poi("thr-01", "Leonidas monument & battlefield", "thermopylae", 0.5, listOf("history"), 38.7964, 22.5353, "Memorial to the 300 Spartans. Information panels. Right off the highway. Quick stop."),
        Poi("thr-02", "Thermopylae hot springs", "thermopylae", 0.5, listOf("nature"), 38.7930, 22.5400, "Free natural sulfur hot springs at the base of the cliffs. Bring a towel."),

        // NAFPLIO & ARGOLID (6)
        Poi("naf-01", "Corinth Canal viewpoint", "nafplio", 0.5, listOf("view"), 37.9364, 23.0058, "6 km cut through solid rock, 80 m deep. Dramatic engineering. Quick photo stop on the highway."),
        Poi("naf-02", "Ancient Mycenae", "nafplio", 1.5, listOf("history"), 37.7309, 22.7571, "Lion Gate, Treasury of Atreus (Agamemnon's Tomb). Bronze Age citadel. UNESCO.", "⚠ No shade. Go early or bring a hat."),
        Poi("naf-03", "Ancient Epidaurus theater", "nafplio", 1.5, listOf("history"), 37.5960, 23.0790, "Most acoustically perfect theater in the ancient world. Drop a coin on stage, hear it from 60 m. UNESCO."),
        Poi("naf-04", "Palamidi Fortress", "nafplio", 1.5, listOf("hike", "view"), 37.5612, 22.8030, "999 steps to the top, 216 m above sea. 360° views of the Argolid. Or drive up."),
        Poi("naf-05", "Nafplio old town & waterfront", "nafplio", 1.5, listOf("food", "view"), 37.5675, 22.8028, "Venetian old town. Narrow lanes, Syntagma Square cafes, Bourtzi island view, fresh seafood."),
        Poi("naf-06", "Bourtzi Castle view", "nafplio", 0.5, listOf("view"), 37.5700, 22.8000, "Small island fortress in the harbor. Best viewed from the waterfront promenade at sunset."),

        // MONEMVASIA (3)
        Poi("mon-01", "Lower Town medieval walk", "monemvasia", 1.5, listOf("history"), 36.6865, 23.0565, "Walk through the single gate into a hidden medieval town. Cobblestone lanes, Byzantine churches, no cars."),
        Poi("mon-02", "Upper Citadel hike", "monemvasia", 1.5, listOf("hike", "view"), 36.6890, 23.0540, "30 min climb to ruined fortress. Agia Sofia church. Sea panorama stretching to Crete on clear days."),
        Poi("mon-03", "Sunset from fortress walls", "monemvasia", 0.5, listOf("view"), 36.6880, 23.0550, "Aegean stretching to the horizon. Candlelit dinner inside the walls afterwards."),

        // MYSTRAS & SPARTA (3)
        Poi("mys-01", "Mystras Byzantine ghost city", "mystras", 3.0, listOf("history"), 37.0750, 22.3660, "UNESCO. Palace of the Despots, Pantanassa monastery. Last Byzantine capital. Enter from upper gate, walk downhill."),
        Poi("mys-02", "Peribleptos Monastery frescoes", "mystras", 0.5, listOf("church"), 37.0730, 22.3670, "Finest 14th-c. frescoes in Greece. Hidden in the rock face. Dark, intimate, extraordinary."),
        Poi("mys-03", "Sparta Leonidas statue", "mystras", 0.5, listOf("history"), 37.0813, 22.4294, "Photo with the famous statue. Small Archaeological Museum next door. Quick stop."),

        // MANI PENINSULA (5)
        Poi("man-01", "Diros Caves boat tour", "mani", 1.0, listOf("nature"), 36.6390, 22.3850, "Underground sea caves by boat. Stalactites reflecting in still water. Go early to beat queues."),
        Poi("man-02", "Vathia tower village", "mani", 0.5, listOf("village", "view"), 36.5300, 22.3800, "Abandoned stone tower village on a dramatic clifftop. Iconic Mani photos. Eerie and beautiful."),
        Poi("man-03", "Cape Tainaron", "mani", 1.5, listOf("hike", "history"), 36.3890, 22.4840, "Southernmost point of mainland Europe. Ruins of Temple of Poseidon. Ancient entrance to the Underworld."),
        Poi("man-04", "Areopoli old town", "mani", 0.5, listOf("village"), 36.6700, 22.3700, "Main Mani town. Stone architecture, Church of Taxiarches (17th c.). Good base."),
        Poi("man-05", "Gytheio waterfront dinner", "mani", 1.5, listOf("food"), 36.7580, 22.5700, "Fresh fish, octopus drying in the sun. Cranae island view. Best seafood in the Peloponnese."),

        // ANCIENT OLYMPIA (4)
        Poi("olm-01", "Ancient Olympia site", "olympia", 2.0, listOf("history"), 37.6385, 21.6297, "Temple of Zeus, Temple of Hera, gymnasium. Run on the original stadium starting blocks. UNESCO."),
        Poi("olm-02", "Olympia Archaeological Museum", "olympia", 1.5, listOf("museum"), 37.6380, 21.6290, "Hermes of Praxiteles, Temple of Zeus pediments. One of Greece's finest museums."),
        Poi("olm-03", "Methoni Castle", "olympia", 1.0, listOf("history", "view"), 36.8160, 21.7050, "Massive Venetian sea fortress. Walk to the Bourtzi tower standing in the water. Dramatic."),
        Poi("olm-04", "Voidokilia Beach", "olympia", 1.5, listOf("beach"), 36.9400, 21.6580, "Perfect omega-shaped beach. Golden sand, shallow turquoise water. One of Europe's most beautiful."),
    )

    val poiMap: Map<String, Poi> = pois.associateBy { it.id }

    // ── Templates ──
    val templates = listOf(
        TripTemplate(
            key = "northern-cultural", name = "Northern Cultural", icon = "🏛️",
            description = "Athens → Delphi → Meteora → Thessaloniki → Athens",
            regions = listOf("athens", "athens", "delphi", "meteora", "thessaloniki", "thessaloniki", "athens", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-16"), 1 to listOf("ath-01", "ath-02", "ath-03", "ath-07", "ath-09", "ath-11"),
                2 to listOf("del-01", "del-02", "del-03", "del-04", "del-05"),
                3 to listOf("met-01", "met-02", "met-03", "met-05"),
                4 to listOf("thes-01", "thes-04", "thes-06", "thes-07", "thes-08"),
                5 to listOf("thes-05", "thes-11", "thes-13", "thes-09", "thes-10", "thes-12"),
                6 to emptyList(), 7 to listOf("ath-04")
            )
        ),
        TripTemplate(
            key = "zagori-adventure", name = "Zagori Adventure", icon = "🥾",
            description = "Athens → Delphi → Meteora → Zagori → Athens",
            regions = listOf("athens", "athens", "delphi", "meteora", "zagori", "zagori", "athens", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-16"), 1 to listOf("ath-01", "ath-10", "ath-08", "ath-11"),
                2 to listOf("del-01", "del-02", "del-03", "del-05"),
                3 to listOf("met-01", "met-06", "met-08", "met-05"),
                4 to listOf("zag-01", "zag-02", "zag-04", "zag-05", "zag-10"),
                5 to listOf("zag-06", "zag-08", "zag-09"),
                6 to emptyList(), 7 to listOf("ath-14")
            )
        ),
        TripTemplate(
            key = "coast-beach", name = "Coast & Beach", icon = "🏖️",
            description = "Athens → Thessaloniki → Halkidiki → Meteora → Athens",
            regions = listOf("athens", "athens", "thessaloniki", "halkidiki", "halkidiki", "meteora", "athens", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-16"), 1 to listOf("ath-01", "ath-02", "ath-11", "ath-09"),
                2 to listOf("thes-01", "thes-05", "thes-11", "thes-13", "thes-12"),
                3 to listOf("hal-01", "hal-02", "hal-04"),
                4 to listOf("hal-03", "hal-05", "hal-06"),
                5 to listOf("met-01", "met-02", "met-05", "met-07"),
                6 to emptyList(), 7 to listOf("ath-04")
            )
        ),
        TripTemplate(
            key = "olympus-pelion", name = "Olympus & Pelion", icon = "⛰️",
            description = "Athens → Delphi → Meteora → Olympus → Pelion → Athens",
            regions = listOf("athens", "athens", "delphi", "meteora", "olympus", "pelion", "athens", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-16"), 1 to listOf("ath-01", "ath-02", "ath-03", "ath-07", "ath-09", "ath-11"),
                2 to listOf("del-01", "del-02", "del-03", "del-05"),
                3 to listOf("met-01", "met-02", "met-03", "met-05"),
                4 to listOf("oly-01", "oly-05", "oly-06"),
                5 to listOf("pln-03", "pln-02", "pln-04", "pln-08"),
                6 to emptyList(), 7 to listOf("ath-14")
            )
        ),
        TripTemplate(
            key = "history-deep", name = "History Deep Dive", icon = "📜",
            description = "Athens → Delphi → Thermopylae → Vergina → Pella → Thessaloniki → Athens",
            regions = listOf("athens", "athens", "delphi", "thermopylae", "vergina", "thessaloniki", "athens", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-16"), 1 to listOf("ath-01", "ath-02", "ath-03", "ath-04", "ath-13", "ath-14"),
                2 to listOf("del-01", "del-02", "del-03", "del-04"),
                3 to listOf("thr-01", "thr-02"),
                4 to listOf("ver-01", "ver-02", "pla-01", "pla-02"),
                5 to listOf("thes-01", "thes-04", "thes-07", "thes-08", "thes-06"),
                6 to emptyList(), 7 to listOf("ath-05")
            )
        ),
        TripTemplate(
            key = "epirus-explorer", name = "Epirus Explorer", icon = "🏔️",
            description = "Athens → Meteora → Ioannina → Zagori → Zagori → Athens",
            regions = listOf("athens", "athens", "meteora", "ioannina", "zagori", "zagori", "athens", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-16"), 1 to listOf("ath-01", "ath-02", "ath-08", "ath-09", "ath-11"),
                2 to listOf("met-01", "met-02", "met-05", "met-07"),
                3 to listOf("ioa-01", "ioa-02", "ioa-05", "ioa-06", "ioa-03"),
                4 to listOf("zag-01", "zag-02", "zag-04", "zag-05", "zag-10"),
                5 to listOf("zag-06", "zag-08", "zag-09"),
                6 to emptyList(), 7 to listOf("ath-12")
            )
        ),
        TripTemplate(
            key = "food-and-beach", name = "Food & Beach", icon = "🍽️",
            description = "Athens → Volos → Pelion → Thessaloniki → Halkidiki → Athens",
            regions = listOf("athens", "athens", "volos", "pelion", "thessaloniki", "halkidiki", "athens", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-16"), 1 to listOf("ath-01", "ath-11", "ath-17", "ath-22"),
                2 to listOf("vol-01", "vol-02", "vol-03"),
                3 to listOf("pln-03", "pln-02", "pln-04", "pln-08"),
                4 to listOf("thes-05", "thes-16", "thes-11", "thes-12", "thes-13"),
                5 to listOf("hal-01", "hal-02", "hal-05", "hal-06"),
                6 to emptyList(), 7 to listOf("ath-07")
            )
        ),
        TripTemplate(
            key = "grand-tour", name = "Grand Tour", icon = "🗺️",
            description = "Athens → Delphi → Meteora → Thessaloniki → Olympus → Pelion → Athens",
            regions = listOf("athens", "athens", "delphi", "meteora", "thessaloniki", "olympus", "pelion", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-16"), 1 to listOf("ath-01", "ath-02", "ath-09", "ath-11"),
                2 to listOf("del-01", "del-02", "del-03", "del-05"),
                3 to listOf("met-01", "met-02", "met-05"),
                4 to listOf("thes-01", "thes-05", "thes-11", "thes-13"),
                5 to listOf("oly-01", "oly-05", "oly-06"),
                6 to listOf("pln-03", "pln-02", "pln-04"),
                7 to listOf("ath-14")
            )
        ),
        TripTemplate(
            key = "peloponnese", name = "Peloponnese", icon = "🏺",
            description = "Athens → Nafplio → Monemvasia → Mystras → Mani → Olympia → Athens",
            regions = listOf("athens", "nafplio", "monemvasia", "mystras", "mani", "olympia", "athens", "athens"),
            dayPois = mapOf(
                0 to listOf("ath-11", "ath-09"),
                1 to listOf("naf-01", "naf-02", "naf-03", "naf-04", "naf-05"),
                2 to listOf("mon-01", "mon-02", "mon-03"),
                3 to listOf("mys-01", "mys-02", "mys-03"),
                4 to listOf("man-01", "man-02", "man-03", "man-04", "man-05"),
                5 to listOf("olm-03", "olm-04", "olm-01", "olm-02"),
                6 to listOf("ath-01", "ath-02", "ath-08"),
                7 to listOf("ath-03")
            ),
            dayNarratives = mapOf(
                0 to DayNarrative(
                    tagline = "Evening arrival · First Greek dinner in Plaka",
                    description = "Land in Athens evening. Grab souvlaki in Plaka and catch the lit Acropolis from Areopagus Rock.",
                    tip = "💡 Hotel near Monastiraki. Souvlaki at Thanasis. Areopagus for lit Acropolis view.",
                    tags = listOf("food", "view")
                ),
                1 to DayNarrative(
                    tagline = "Corinth Canal · Ancient Mycenae · Epidaurus theater · Palamidi sunset",
                    description = "Full day in the Argolid. Drive south, stop at the Corinth Canal, explore Mycenae's Bronze Age citadel, hear a pin drop at Epidaurus theater, climb Palamidi for sunset views. Sleep in Nafplio old town.",
                    tip = "💡 7 AM depart → 8:30 Corinth Canal → 9:30 Mycenae → 12 PM Epidaurus → 2 PM Nafplio lunch → 5 PM Palamidi sunset.",
                    tags = listOf("monument", "hike", "view", "food")
                ),
                2 to DayNarrative(
                    tagline = "Gibraltar of Greece · Medieval rock fortress floating in the Aegean",
                    description = "Drive to Monemvasia, a massive rock jutting from the sea. Walk through the single gate into a hidden medieval town. Climb to the upper citadel for views stretching to Crete. Sunset dinner inside the walls.",
                    tip = "💡 Arrive ~1 PM → lunch inside walls → 3 PM lower town → 5 PM upper citadel → 8:30 PM sunset dinner.",
                    tags = listOf("monument", "hike", "view", "food")
                ),
                3 to DayNarrative(
                    tagline = "Byzantine ghost city · Frescoed churches & palace ruins",
                    description = "Mystras, a UNESCO ghost city clinging to a hillside above Sparta. Last capital of the Byzantine Empire. Ruined palaces, intact frescoed churches. Quick stop at Sparta's Leonidas statue.",
                    tip = "💡 10 AM Mystras (enter from upper gate, walk downhill) → 1 PM lunch → 3 PM Sparta.",
                    tags = listOf("monument", "view")
                ),
                4 to DayNarrative(
                    tagline = "Tower villages · Sea caves · Europe's wild south · Gateway to Hades",
                    description = "The Mani: Greece's wildest peninsula. Stone tower villages, Diros sea caves by boat, abandoned Vathia on a clifftop, and Cape Tainaron — the ancient entrance to the Underworld.",
                    tip = "💡 9 AM Diros Caves (go early) → 11 AM drive south → 12 PM Vathia → 2 PM Cape Tainaron → 5 PM Gytheio dinner.",
                    tags = listOf("monument", "view", "food")
                ),
                5 to DayNarrative(
                    tagline = "Venetian fortress · Omega beach · Birthplace of the Olympic Games",
                    description = "Long drive west with spectacular stops. Methoni Castle (Venetian sea fortress), Voidokilia (perfect omega beach), then Ancient Olympia where athletes competed for 1,000 years. Run in the original stadium.",
                    tip = "💡 8 AM depart → 10:30 Methoni → 11:30 Voidokilia swim → 2 PM Olympia site → 4 PM museum.",
                    tags = listOf("monument", "beach", "view")
                ),
                6 to DayNarrative(
                    tagline = "Highway east · Acropolis, museum & Philopappos sunset",
                    description = "Drive the Patras-Corinth-Athens highway. Arrive ~1 PM. Afternoon: Acropolis + Parthenon, Acropolis Museum. Finish at Philopappos Hill for sunset with the Acropolis glowing.",
                    tip = "💡 9 AM depart → 1 PM Athens → 2 PM Acropolis → 4:30 Museum → 8 PM Philopappos sunset.",
                    tags = listOf("monument", "view")
                ),
                7 to DayNarrative(
                    tagline = "Morning free · Afternoon flight",
                    description = "Morning: Ancient Agora & Temple of Hephaestus, or a slow breakfast in Plaka. Drop car at airport for afternoon flight.",
                    tip = "💡 Leave for airport by ~noon. Drive from center ~45 min.",
                    tags = listOf("monument", "food")
                )
            )
        ),
    )

    // ── Day colors for visual distinction ──
    val dayColors = listOf(
        0xFF1C69D4, 0xFFE89B00, 0xFF00823B, 0xFFC91432,
        0xFF7E57C2, 0xFF009A97, 0xFFEF6C00, 0xFF5C6BC0,
    )
}
