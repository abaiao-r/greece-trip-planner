# 🇬🇷 Greece Trip Planner

Android app for planning an 8-day Greece road trip (Jun 14–21 2026). Build your perfect itinerary from 103+ POIs across 13 regions, with time budgets, route planning, and offline maps.

## Features

- **103+ Points of Interest** across 13 regions with descriptions, tips & time estimates
- **9 pre-built templates** organized by route:
  - **Route A (Monument)**: Northern Cultural, History Deep
  - **Route B (Coast)**: Coast & Beach, Food & Beach
  - **Route C (Nature)**: Zagori Adventure, Olympus-Pelion, Epirus Explorer
  - **Route D (Peloponnese)**: Peloponnese circuit
  - **Mix**: Grand Tour
- **Plan mode**: Day-by-day itinerary builder with region picker, time budget bar, category filters
- **Show mode**: Full itinerary overview with day narratives, stats bar, and POI details
- **Map view**: OSMDroid map with CartoDB Voyager tiles, POI markers, route polylines & km labels
- **Custom POIs**: Add your own activities with custom duration
- **User notes**: Add personal notes to any day
- **Stats dashboard**: Total km, active days, regions, POIs, fuel cost estimate
- **Offline tiles**: 100 MB tile cache for offline map viewing
- **Share**: Export itinerary as text via any Android share target
- **Persistence**: Room database saves your trip across sessions
- **Dark mode**: Three-way toggle (auto / dark / light)
- **Accessibility**: Content descriptions on tabs, buttons, and progress indicators

## Architecture

```
app/src/main/java/dev/greecetripplanner/
├── data/
│   ├── model/          # TripDay, Poi, Region, Category, TripTemplate, DayNarrative
│   ├── db/             # Room (TripDatabase, TripDayEntity, TripDayDao)
│   ├── repository/     # TripRepository
│   └── TripData.kt     # All static data (regions, POIs, templates, narratives)
├── di/                 # Hilt modules (DatabaseModule)
├── ui/
│   ├── TripViewModel.kt     # Shared state: days, active day, stats, dark mode
│   ├── screens/plan/         # PlanScreen + PlanViewModel
│   ├── screens/show/         # ShowScreen (read-only itinerary)
│   ├── screens/map/          # MapScreen + MapComposable
│   ├── components/           # TemplateBar, DayTabs, BudgetBar, PoiCard, etc.
│   ├── Navigation.kt         # NavHost with 3 routes
│   └── Theme.kt              # Material 3 light/dark themes
└── util/               # TripHelpers, ShareUtils
```

## Tech Stack

- **Kotlin** 2.1, **Jetpack Compose** (Material 3, BOM 2024.12)
- **MVVM** with `StateFlow` + `combine`
- **Hilt** 2.53 for dependency injection
- **Room** 2.6 for local persistence
- **OSMDroid** 6.1 (no API key required)
- **Navigation Compose** for screen routing
- **Coil** for image loading
- **KotlinX Serialization** for JSON

## Build

```bash
# Debug
./gradlew assembleDebug

# Release (uses debug keystore)
./gradlew assembleRelease

# Run tests
./gradlew test
```

## License

MIT
