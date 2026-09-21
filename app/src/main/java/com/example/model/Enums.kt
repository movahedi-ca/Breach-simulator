package com.example.model

enum class IncidentSeverity(val label: String) {
    CRITICAL("CRITICAL TIER-1"),
    HIGH("HIGH SEVERITY"),
    MEDIUM("MEDIUM RISK")
}

enum class ScenarioCategory(val label: String) {
    RANSOMWARE("Ransomware & Extortion"),
    SUPPLY_CHAIN("Supply Chain Compromise"),
    DATA_LEAK("Customer PII Spill"),
    INSIDER_THREAT("Privilege Abuse"),
    CLOUD_EXPOSURE("Cloud Misconfiguration"),
    EXECUTIVE_THEFT("Physical Device Theft")
}

enum class Competency(val label: String, val weight: Int) {
    DETECTION_TRIAGE("Triage & Scoping", 20),
    CONTAINMENT_SPEED("Containment & Isolation", 25),
    LEGAL_COMPLIANCE("Regulatory & Reporting", 25),
    CRISIS_COMMS("Executive & Public Comms", 15),
    BUSINESS_RESILIENCE("Forensics & Continuity", 15)
}

enum class LegalRiskLevel(val label: String) {
    MINIMAL("Minimal Exposure"),
    MODERATE("Moderate Risk"),
    ELEVATED("Severe Penalties Likely"),
    CRITICAL("Catastrophic Liability")
}

enum class ForensicIntegrity(val label: String) {
    INTACT("Chain of Custody Intact"),
    PARTIALLY_COMPROMISED("Volatile Memory Lost"),
    TAINTED("Evidence Inadmissible")
}

enum class Screen {
    HOME,
    SCENARIO_DETAIL,
    SIMULATION,
    AAR_REPORT,
    DOCTRINE_LIST,
    DOCTRINE_DETAIL,
    HISTORY_LOGS,
    ADVISORY
}
