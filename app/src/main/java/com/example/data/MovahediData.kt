package com.example.data

data class MovahediService(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val tags: List<String>,
    val url: String = "https://movahedi.ca"
)

data class MovahediInsight(
    val id: String,
    val title: String,
    val category: String,
    val readTime: String,
    val summary: String,
    val keyTakeaway: String,
    val url: String = "https://movahedi.ca"
)

object MovahediData {
    const val CONSULTANT_NAME = "Mohammad Movahedi"
    const val CREDENTIALS = "CIPP/C • Six Sigma Black Belt"
    const val CURRENT_ROLE = "Data Protection & Security Specialist"
    const val SPECIALIZATION = "Privacy & AI Governance Consultant"
    const val LOCATION = "Toronto, Canada"
    const val WEBSITE_URL = "https://movahedi.ca"
    const val SERVICES_URL = "https://movahedi.ca"
    const val DISCOVERY_CALL_URL = "https://movahedi.ca"

    val services: List<MovahediService> = listOf(
        MovahediService(
            id = "privacy-governance",
            title = "Privacy & Data Governance",
            subtitle = "PIPEDA, Quebec Law 25, Bill C-27 & GDPR",
            description = "Comprehensive enterprise privacy architectures, mandatory breach assessment (RROSH criteria), Privacy Impact Assessments (PIAs/DPIAs), and cross-border transfer compliance.",
            tags = listOf("PIPEDA", "Law 25", "GDPR", "DPIA / PIA", "DSAR Workflows"),
            url = "https://movahedi.ca"
        ),
        MovahediService(
            id = "ai-governance",
            title = "AI Governance & Safety Controls",
            subtitle = "Responsible AI, AIDA & EU AI Act Readiness",
            description = "Operationalizing responsible artificial intelligence frameworks, algorithmic bias audits, AI security platforms, model risk containment, and executive policy creation.",
            tags = listOf("AIDA Readiness", "EU AI Act", "Algorithmic Risk", "LLM Guardrails"),
            url = "https://movahedi.ca"
        ),
        MovahediService(
            id = "incident-readiness",
            title = "Incident Response & Crisis Tabletop",
            subtitle = "Executive Tabletop Facilitation & Breach Doctrine",
            description = "Tailored tabletop crisis simulations for boards and executive teams. Hardening breach notification playbooks across Canadian, European, and US regulatory disclosure clocks.",
            tags = listOf("Tabletop Exercises", "72h Notification", "Forensic Integrity", "Regulator Briefings"),
            url = "https://movahedi.ca"
        ),
        MovahediService(
            id = "process-excellence",
            title = "Process Excellence for Security & GRC",
            subtitle = "Lean Six Sigma Applied to Cyber Operations",
            description = "Applying Six Sigma Black Belt methodologies to eliminate operational waste, accelerate Mean Time to Detect (MTTD) and Mean Time to Respond (MTTR), and build defensible compliance records.",
            tags = listOf("Six Sigma Black Belt", "MTTR Reduction", "GRC Optimization", "Root Cause Analysis"),
            url = "https://movahedi.ca"
        ),
        MovahediService(
            id = "custom-ai-tools",
            title = "Custom B2B AI & Data Tools",
            subtitle = "Privacy-Preserving Analytics & GRC Automation",
            description = "Prototyping and deploying specialized intelligence tools: automated data discovery and classification, privacy-preserving analytics, and intelligent compliance reporting.",
            tags = listOf("GRC Automation", "Data Classification", "Privacy Analytics", "B2B Prototyping"),
            url = "https://movahedi.ca"
        ),
        MovahediService(
            id = "fractional-leadership",
            title = "Fractional Leadership & Strategic Advisory",
            subtitle = "Interim CPO / CISO Advisory for High-Growth Firms",
            description = "Embedded fractional privacy and security leadership for organizations scaling into regulated markets. Board reporting, vendor risk oversight, and audit readiness.",
            tags = listOf("Fractional CPO", "Board Advisory", "Vendor Due Diligence", "Audit Defense"),
            url = "https://movahedi.ca"
        )
    )

    val insights: List<MovahediInsight> = listOf(
        MovahediInsight(
            id = "bill-c27-aida",
            title = "Navigating Bill C-27 and AIDA: High-Impact AI & Privacy Readiness",
            category = "Canadian Privacy & AI",
            readTime = "6 min read",
            summary = "A deep-dive into Canada's proposed Consumer Privacy Protection Act (CPPA) and Artificial Intelligence and Data Act (AIDA), outlining statutory duties for high-impact AI systems.",
            keyTakeaway = "Organizations must maintain algorithmic risk audits, bias testing logs, and human-oversight fail-safes before deployment.",
            url = "https://movahedi.ca"
        ),
        MovahediInsight(
            id = "rv-bykovets-forensics",
            title = "R v Bykovets: Constitutional Privacy in IP Addresses and Digital Forensics",
            category = "Constitutional Law & Forensics",
            readTime = "8 min read",
            summary = "Analysis of the Supreme Court of Canada 2024 landmark ruling establishing reasonable expectation of privacy in IP addresses under Section 8 of the Charter.",
            keyTakeaway = "Corporate incident response teams must document clean chain-of-custody without relying on warrantless voluntary law enforcement disclosures.",
            url = "https://movahedi.ca"
        ),
        MovahediInsight(
            id = "ai-security-platforms",
            title = "Why Enterprise Incident Response Demands AI Security Platforms",
            category = "AI Security & GRC",
            readTime = "5 min read",
            summary = "Evaluating emerging threat vectors against LLMs, agentic workflows, and fine-tuning pipelines. Why conventional perimeter defenses miss prompt injection and model extraction.",
            keyTakeaway = "Implement runtime guardrails and semantic anomaly inspection to prevent model-mediated exfiltration.",
            url = "https://movahedi.ca"
        ),
        MovahediInsight(
            id = "lean-six-sigma-cyber",
            title = "Applying Lean Six Sigma to Cut Cybersecurity Incident MTTR",
            category = "Process Excellence",
            readTime = "7 min read",
            summary = "Leveraging value stream mapping and DMAIC cycles to uncover friction between SOC triage, legal counsel privilege assessment, and PR communications.",
            keyTakeaway = "Standardizing communication handoffs and pre-authorizing containment actions yields up to a 60% drop in response cycle time.",
            url = "https://movahedi.ca"
        )
    )
}
