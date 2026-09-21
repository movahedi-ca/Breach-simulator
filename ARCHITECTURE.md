# Breach Tabletop — Architecture & Technical Specification

## 1. Executive Summary

**Breach Tabletop** is a cybersecurity incident response and tabletop simulation platform built natively for Android using **Kotlin**, **Jetpack Compose**, and **Material Design 3**. It trains executives, CISOs, incident commanders, and security engineers to manage complex, multi-million-dollar cyber emergencies under realistic operational, financial, and regulatory constraints.

The application incorporates real-time regulatory compliance clocks (SEC Form 8-K, GDPR Art. 33/34, NYDFS 500, HIPAA), simulated threat actors (UNC3944, Volt Typhoon, nation-state APTs), live financial and telemetry metrics, and post-incident After-Action Reports (AAR) with competency-based scoring.

Furthermore, it integrates strategic executive cybersecurity advisory channels connecting to **[movahedi.ca](https://movahedi.ca)** for virtual CISO services, regulatory readiness audits, and custom enterprise tabletop exercises.

---

## 2. High-Level Architecture

The project follows the **Clean Architecture** and **MVVM (Model-View-ViewModel)** architectural patterns, leveraging Kotlin Coroutines and reactive `StateFlow` streams.

```
┌─────────────────────────────────────────────────────────────┐
│                      UI Presentation Layer                  │
│  MainActivity  •  Jetpack Compose Screens  •  M3 Theme      │
│  (Home, Dossier, War Room Sim, AAR Debrief, Doctrine, Logs) │
└──────────────────────────────┬──────────────────────────────┘
                               │ Observes StateFlow / Dispatches Events
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   ViewModel / State Machine                 │
│                 BreachSimulatorViewModel                    │
│   • Live Telemetry Engine    • Decision History Tracker     │
│   • Phase Progression Flow   • AAR Compilation Engine       │
└──────────────────────────────┬──────────────────────────────┘
                               │ Queries & Persists
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    Data & Persistence Layer                 │
│   • SimulationRepository     • AppDatabase (Room / SQLite)  │
│   • SimulationDao            • ScenariosData (Static Feeds) │
│   • DoctrineData (Playbooks) • SimulationRecord (Entities)  │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Core Modules & Directory Layout

```
app/src/main/java/com/example/
├── MainActivity.kt               # Single Activity host with edge-to-edge support & Crossfade nav
├── data/
│   ├── AppDatabase.kt            # Room Database definition with schema migrations
│   ├── DoctrineData.kt           # Regulatory compliance doctrine & statutory playbooks
│   ├── ScenariosData.kt          # Curated multi-phase cyber crisis scenarios
│   ├── SimulationDao.kt          # Room DAO for simulation run persistence & querying
│   ├── SimulationRecord.kt       # Room Entity for drill history & scorecards
│   └── SimulationRepository.kt   # Clean abstraction layer between ViewModel and Room
├── model/
│   ├── Enums.kt                  # Severity, ScenarioCategory, Competency, LegalRisk, ForensicIntegrity, Screen
│   └── ScenarioModels.kt         # IncidentScenario, SimulationPhase, BreakingInject, DecisionChoice, AAR
├── ui/
│   ├── components/
│   │   ├── AdvisoryComponents.kt # movahedi.ca promotional & advisory cards across screens
│   │   └── SharedComponents.kt   # Tactical cards, badges, buttons, meters, and formatters
│   ├── screens/
│   │   ├── AarReportScreen.kt    # After-Action Report with letter grades, radars, and lessons learned
│   │   ├── DoctrineScreen.kt     # Statutory playbook browser, requirements checklists & advisory
│   │   ├── HistoryScreen.kt      # Historical drill logs, scorecards, deletion, and drill review
│   │   ├── HomeScreen.kt         # War Room dashboard, readiness score, threat feed & banner
│   │   ├── ScenarioDetailScreen.kt # Pre-drill incident dossier, impacted assets & regulatory clocks
│   │   └── SimulationScreen.kt   # Active crisis simulation, live telemetry meters & inject dialogs
│   └── theme/
│       ├── Color.kt              # Cyber War Room tactical dark palette (SlateDark, CyberCyan, AlertRed)
│       ├── Theme.kt              # Material 3 dark theme implementation with surface mappings
│       └── Type.kt               # Monospace-accented typography hierarchy
```

---

## 4. State Management & Simulation Engine

### 4.1. Navigation State Machine
Navigation is driven by the `Screen` enum:
- `HOME`: Main War Room command center.
- `SCENARIO_DETAIL`: Pre-simulation dossier and briefing.
- `SIMULATION`: Active war room simulation with live injects and choices.
- `AAR_REPORT`: Post-simulation executive debrief and grade analysis.
- `DOCTRINE_LIST` & `DOCTRINE_DETAIL`: Statutory playbook guide.
- `HISTORY_LOGS`: Persistent Room database logs of historical drills.

All navigation transitions utilize Jetpack Compose `Crossfade` to prevent flickering and optimize surface transactions.

### 4.2. Live Telemetry Model (`LiveMetrics`)
During active simulation, every decision instantaneously recalculates:
1. **Financial Cost ($USD)**: Starts at scenario baseline; accumulates incident response, ransom, business interruption, and consulting costs.
2. **Operational Time Elapsed (Hours)**: Tracks time spent negotiating, isolating subnets, or forensic imaging.
3. **Public Trust (0–100%)**: Reflects market confidence, brand equity, and customer sentiment.
4. **Legal Risk Level (`LegalRiskLevel`)**: Shifts between `MINIMAL`, `MODERATE`, `ELEVATED`, and `CRITICAL`.
5. **Forensic Integrity (`ForensicIntegrity`)**: Tracks evidentiary chain of custody (`INTACT`, `PARTIALLY_COMPROMISED`, or `TAINTED`).

### 4.3. Competency Evaluation Framework
Every tactical choice impacts five weighted competencies:
- **Triage & Scoping (`DETECTION_TRIAGE`)**: 20% weight.
- **Containment & Isolation (`CONTAINMENT_SPEED`)**: 25% weight.
- **Regulatory & Reporting (`LEGAL_COMPLIANCE`)**: 25% weight.
- **Executive & Public Comms (`CRISIS_COMMS`)**: 15% weight.
- **Forensics & Continuity (`BUSINESS_RESILIENCE`)**: 15% weight.

Scores are averaged across choices upon scenario completion to compute the final letter grade (`A+`, `A`, `B`, `C`, `D`, `F`) and comprehensive executive recommendations.

---

## 5. movahedi.ca Advisory Integration

To serve as a subtle and high-value advisory vehicle for **[movahedi.ca](https://movahedi.ca)**, executive consulting touchpoints are strategically integrated:

1. **War Room Hero Advisory (`WarRoomMovahediBanner`)**:
   - Location: War Room home screen below the readiness meter.
   - Purpose: Directs leadership to virtual CISO advisory and enterprise custom tabletop workshops.

2. **Statutory Doctrine Advisory (`MovahediDoctrineAdvisoryCard`)**:
   - Location: Top of the Regulatory Doctrine browser.
   - Purpose: Recommends expert compliance audits for SEC Form 8-K, GDPR, and NYDFS 500 readiness.

3. **Executive Debrief Advisory (`MovahediExecutiveAdvisoryCard`)**:
   - Location: Directly beneath the letter grade and executive summary in the After-Action Report.
   - Purpose: Dynamically provides personalized counsel recommending red team audits, tabletop facilitation, or IR retainer reviews depending on the user's letter grade.

All advisory touchpoints feature standard HTTPS intent launch handlers (`Intent.ACTION_VIEW`) with proper `FLAG_ACTIVITY_NEW_TASK` protection and safety fallbacks.

---

## 6. Testing & Quality Assurance Infrastructure

The codebase features an exhaustive **300+ test automated test harness** executed via Robolectric:

1. **User Experience Audit Suite (`UserExperienceAuditTest.kt` — 100 Tests)**:
   - **Pillar 1**: Information Architecture & Navigation UX (Tests 1–10).
   - **Pillar 2**: War Room Command Center & Hero Telemetry (Tests 11–20).
   - **Pillar 3**: Scenario Discovery, Categorization & Threat Feed (Tests 21–30).
   - **Pillar 4**: Incident Dossier & Pre-Drill Briefing (Tests 31–40).
   - **Pillar 5**: Crisis Simulation Header & Live War Room Meters (Tests 41–50).
   - **Pillar 6**: Tactical Dilemmas, Breaking Injects & Operational Time (Tests 51–60).
   - **Pillar 7**: Strategic Decision Choices & Impact Previews (Tests 61–70).
   - **Pillar 8**: Decision Feedback Dialog & Doctrine Assessment (Tests 71–80).
   - **Pillar 9**: After-Action Report (AAR) & Executive Scoring (Tests 81–90).
   - **Pillar 10**: Doctrine, Archives, Empty States & Advisory UX (Tests 91–100).

2. **Quality Assurance Checks (`QualityAssuranceChecksTest.kt` — 100+ Tests)**:
   - Verifies visual color contrast tokens, typography sizing, state persistence, error boundaries, and scoring math.

3. **Security & Compliance Checks (`SecurityChecksTest.kt` — 100+ Tests)**:
   - Verifies zero dangerous permissions, strict intent sanitation, SQL injection protection via Room query binding, and absence of external code loading.

---

## 7. Verification Commands

To compile and verify the entire test suite locally:

```bash
# Run all unit and Robolectric tests
gradle :app:testDebugUnitTest

# Assemble debug APK
gradle :app:assembleDebug
```
