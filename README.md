# A221491_AmirAizatBinHarith_Nelson_Project2

## 🌱 EcoEducation

An Android app built with **Jetpack Compose** that teaches users about
renewable energy through interactive lessons, quizzes, a live map of
Malaysian energy sites, and real-time weather data — extending Project 1
with cloud sync, local persistence, internet APIs, and hardware sensors.

---

## 🎯 SDG Theme

**SDG 7 — Affordable and Clean Energy**

Many people lack practical knowledge about renewable energy sources such as
solar, wind, hydro, and biomass, and rarely connect these topics to real
locations or real-time conditions. EcoEducation addresses this by teaching
these topics through interactive lessons and quizzes, showing real
Malaysian energy sites on a GPS-enabled map, pulling live weather data to
show solar generation potential at each site, and tracking learning
progress locally and in the cloud.

---

## 📱 Screens (9 total)

1. **Login** — Firebase Authentication sign-in
2. **Sign Up** — account creation with validation
3. **Forgot Password** — password reset via email
4. **Onboarding** — collects name, university, and course on first login
5. **Home** — overview stats + topic progress cards
6. **Progress** — detailed per-topic chapter progress & quiz scores
7. **Eco Map** — interactive map of Malaysian renewable energy sites with
   live GPS location and live weather data per site
8. **Quiz** — arcade-style quiz game (Easy / Medium / Hard) with results
   and leaderboard
9. **Profile / Edit Profile** — user profile with photo, editable details

---

## 🔧 Technical Pillars

| Pillar | Implementation |
|---|---|
| **Local Persistence** | Room Database (v3) — `TopicProgressEntity`, `UserProfileEntity`, `QuizGameEntity` |
| **Cloud Integration** | Firebase Firestore — syncs profile, topic progress, and quiz game results per user (`users/{uid}/...`) |
| **Web API** | [Open-Meteo](https://open-meteo.com/) REST API — live temperature, humidity, wind, and weather code per energy site |
| **Sensor — GPS** | `GpsMyLocationProvider` + `MyLocationNewOverlay` (OSMDroid) — shows live user location on the Eco Map |
| **Sensor — Camera** | CameraX / gallery picker — profile photo captured, encoded as Base64, and stored in Room |

---

## 🛠️ Tech Stack

- Kotlin
- Jetpack Compose + Material 3
- Navigation Compose
- Room Database (with migration support)
- Firebase Authentication & Firestore
- Coroutines & Flow / StateFlow
- OSMDroid (OpenStreetMap)
- Open-Meteo REST API

---

## 🚀 Setup Instructions

1. Clone this repository
2. Open the project in **Android Studio** (latest stable version)
3. Add your `google-services.json` file to the `app/` module
   (from your Firebase project console — required for Authentication & Firestore)
4. Make sure the following permissions are declared in `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   <uses-permission android:name="android.permission.CAMERA" />
   ```
5. Run the app on an emulator or physical device (API 26+ recommended)
6. On first launch: **Sign Up** → complete the **Onboarding** form → explore the app

> 💡 If using an emulator, set a mock GPS location via Extended Controls →
> Location → Send, so the Eco Map's blue location dot and weather data load correctly.

---

## 📺 Demo & Documentation

- **e-Portfolio (includes all Lab & Project VSR videos):**
  https://aizat200.github.io/eportfolio/
- **Project 2 VSR:** included in the e-Portfolio above

---

## 🙏 Acknowledgements

- [Open-Meteo](https://open-meteo.com/) — free weather API, no key required
- [OSMDroid](https://osmdroid.github.io/osmdroid/) — OpenStreetMap rendering for Android
- AI assistance (Claude) was used to help debug Firestore sync logic, Room
  migrations, and structure ViewModels; all code was reviewed and understood
  before submission.
