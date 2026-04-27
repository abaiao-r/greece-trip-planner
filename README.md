# 🇬🇷 Greece Trip Planner

Android app for planning an 8-day Greece road trip (Athens → Delphi → Meteora → Zagori → Thessaloniki → Pelion → Athens, Jun 14–21 2026).

## Features

- **103 Points of Interest** across 13 regions with descriptions, tips & time estimates
- **Plan mode**: Day-by-day itinerary builder with region picker, time budget bar, category filters
- **Show mode**: Full itinerary overview with day cards
- **8 pre-built templates**: Culture Buff, Nature Lover, History Nerd, Foodie Trail, etc.
- **Map view**: OSMDroid map with CartoDB Voyager tiles, POI markers & route lines
- **Share**: Export itinerary as text via any Android share target
- **Persistence**: Room database saves your trip across sessions
- **Links**: Google Maps, Google Search & Google Images for every POI

## Tech Stack

- **Kotlin** 2.1, **Jetpack Compose** (Material 3)
- **MVVM** with `StateFlow` + `combine`
- **Hilt** for dependency injection
- **Room** for local persistence
- **OSMDroid** (no API key required)
- **Navigation Compose** for screen routing
- **Coil** for image loading

## Build

```bash
./gradlew assembleDebug
```

## License

MIT
