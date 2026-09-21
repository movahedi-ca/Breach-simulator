<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Breach Tabletop — Cybersecurity Incident Response Simulator

Breach Tabletop is an interactive cybersecurity crisis simulation platform built natively for Android using Kotlin and Jetpack Compose. It prepares executive leadership, CISOs, incident commanders, and security analysts for high-stakes cybersecurity emergencies.

---

## Key Features

- **War Room Command Center**: Real-time readiness metrics, active threat intelligence feeds, and quick access to incident playbooks and historical drills.
- **Realistic Crisis Scenarios**:
  - `OP_BLACKOUT`: Double-extortion ransomware and dark web leaks (UNC3944 / FIN11).
  - `OP_POISON_PILL`: CI/CD pipeline poisoning and supply chain compromise.
  - `OP_VELVET_SHADOW`: Unencrypted M&A executive laptop theft.
  - `OP_REPLY_ALL`: Customer financial PII spill and regulator notification.
  - `OP_OPEN_VAULT`: Misconfigured cloud storage bucket exposure (4.2M records).
  - `OP_INSIDE_JOB`: Disgruntled database architect sabotage and logic bomb.
- **Dynamic Decision Injects**: Multi-phase dilemmas with real-time operational time tracking, breaking news alerts, and competing stakeholder advice (CISO, Legal Counsel, CFO, CTO, IR Lead).
- **Live Crisis Telemetry**: Dynamic tracking of incident costs ($USD), time elapsed, public trust percentage, legal liability tier, and forensic chain of custody.
- **Statutory Doctrine Playbooks**: Comprehensive compliance guides covering SEC Form 8-K Item 1.05, GDPR Articles 33/34, NYDFS 23 NYCRR 500, HIPAA, and NIST SP 800-61 Rev. 2.
- **After-Action Reports (AAR)**: Comprehensive post-incident scorecards with letter grades, 5-dimensional competency radar ratings, executive summaries, and lessons learned.
- **Local Persistence**: Secure offline history tracking with Room Database.
- **Executive Cybersecurity Advisory**: Subtle, professional advisory touchpoints connecting to **[movahedi.ca](https://movahedi.ca)** for virtual CISO services, regulatory compliance audits, and custom tabletop exercises.

---

## Technical Documentation & Architecture

Detailed technical specifications, data models, state machines, and testing methodologies are documented in [ARCHITECTURE.md](ARCHITECTURE.md).

---

## Test Suite & Quality Assurance

The codebase includes an extensive **300+ automated test suite** running via Robolectric:

- **100 UX Audit Tests** (`UserExperienceAuditTest.kt`): Covers navigation flows, layout adaptiveness, telemetry boundaries, decision consequences, feedback dialogs, and advisory touchpoints.
- **100+ QA Tests** (`QualityAssuranceChecksTest.kt`): Validates theme contrast tokens, typography hierarchies, state resilience, and scoring math.
- **100+ Security Tests** (`SecurityChecksTest.kt`): Ensures least-privilege permissions, safe intent handling, and Room SQL injection protection.

To run the full test suite:
```bash
gradle :app:testDebugUnitTest
```

---

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)

1. Open Android Studio.
2. Select **Open** and choose the directory containing this project.
3. Allow Android Studio to sync Gradle and build dependencies.
4. Run the app on an emulator or physical device.
