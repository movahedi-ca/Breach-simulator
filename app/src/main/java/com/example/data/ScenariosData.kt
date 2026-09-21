package com.example.data

import com.example.model.*

object ScenariosData {
    val allScenarios: List<IncidentScenario> = listOf(
        IncidentScenario(
            id = "darkhydra-ransomware",
            title = "DarkHydra Double-Extortion Ransomware",
            codename = "OP_BLACKOUT",
            threatActor = "DarkHydra (UNC3944 / FIN11 affiliate)",
            severity = IncidentSeverity.CRITICAL,
            category = ScenarioCategory.RANSOMWARE,
            overview = "Active Directory compromised via VPN credential stuffing. ESXi cluster encrypted. 850GB of customer financial records & employee PII exfiltrated to a dark web drop with a 48-hour ransom demand of $4.2M in Monero.",
            impactedSystems = listOf("Active Directory Domain Controllers", "Core Production ESXi Cluster", "Billing & Payment Gateway", "Customer CRM Database"),
            regulatoryScope = listOf("SEC Form 8-K (Item 1.05)", "GDPR Article 33", "NYDFS 23 NYCRR 500", "State Data Breach Laws"),
            baselineCostUsd = 1_500_000L,
            estimatedDurationHours = 72,
            phases = listOf(
                SimulationPhase(
                    phaseNumber = 1,
                    title = "Phase 1: Detection & Emergency Triage",
                    timeLabel = "T+00:45 | SOC Escalation",
                    briefing = "At 02:15 UTC, automated EDR alerts trigger on 14 critical domain controllers. Helpdesk reports locked servers displaying '.darkhydra' extensions and ransom notes. Threat actor demands $4.2M.",
                    breakingInject = BreakingInject(
                        title = "BREAKING: Exfiltration Confirmed",
                        source = "Forensics Lead",
                        message = "NetFlow analysis confirms 850GB egressed to an overseas IP through an encrypted mega.nz transfer tunnel between 23:00 and 01:30.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What is your immediate network containment order?",
                        operationalContext = "Executing a full corporate network disconnect halts ransomware spread but brings global customer-facing services down immediately, halting all e-commerce revenue.",
                        choices = listOf(
                            DecisionChoice(
                                id = "dh_p1_c1",
                                title = "Sever All Internet Egress & Isolate Core Subnets",
                                description = "Pull external gateway interfaces, sever WAN uplinks, and isolate domain controllers immediately while preserving volatile RAM dumps on running hosts.",
                                recommendedBy = "Incident Response Lead",
                                costDeltaUsd = 450_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = -5,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Optimal containment doctrine. NIST SP 800-61 recommends immediate containment when propagation risk is high, even if operational downtime occurs. Volatile memory was preserved.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 95,
                                    Competency.CONTAINMENT_SPEED to 90,
                                    Competency.LEGAL_COMPLIANCE to 85,
                                    Competency.CRISIS_COMMS to 80,
                                    Competency.BUSINESS_RESILIENCE to 85
                                )
                            ),
                            DecisionChoice(
                                id = "dh_p1_c2",
                                title = "Selective Segmented Isolation (Keep E-Commerce Live)",
                                description = "Only isolate internal corporate VLANs. Keep customer portal live to avoid commercial breach disclosures and maintain revenue stream.",
                                recommendedBy = "Chief Commercial Officer",
                                costDeltaUsd = 1_200_000L,
                                timeDeltaHours = 12,
                                trustDeltaPercent = -15,
                                legalRiskChange = 4,
                                forensicsImpact = ForensicIntegrity.PARTIALLY_COMPROMISED,
                                feedbackDoctrine = "Severe violation of incident handling doctrine. The attacker pivoted through payment webhooks into customer databases during the delay, drastically multiplying regulatory liability.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 60,
                                    Competency.CONTAINMENT_SPEED to 35,
                                    Competency.LEGAL_COMPLIANCE to 40,
                                    Competency.CRISIS_COMMS to 50,
                                    Competency.BUSINESS_RESILIENCE to 45
                                )
                            ),
                            DecisionChoice(
                                id = "dh_p1_c3",
                                title = "Reboot Affected Servers and Wipe to Backup",
                                description = "Immediately power-cycle and re-image all flagged virtual machines using offline snapshot backups without taking forensic memory snapshots.",
                                recommendedBy = "IT Operations Lead",
                                costDeltaUsd = 800_000L,
                                timeDeltaHours = 8,
                                trustDeltaPercent = -10,
                                legalRiskChange = 3,
                                forensicsImpact = ForensicIntegrity.TAINTED,
                                feedbackDoctrine = "Disastrous forensic error. Power cycling destroyed RAM encryption keys and active C2 process artifacts. Inability to verify exfiltration scope exposes firm to maximum statutory penalties.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 40,
                                    Competency.CONTAINMENT_SPEED to 50,
                                    Competency.LEGAL_COMPLIANCE to 30,
                                    Competency.CRISIS_COMMS to 60,
                                    Competency.BUSINESS_RESILIENCE to 25
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 2,
                    title = "Phase 2: Ransom Demands & Extortion Crisis",
                    timeLabel = "T+18:00 | Executive Crisis Table",
                    briefing = "DarkHydra has uploaded 50 sample customer passports and corporate NDA agreements to a public TOR blog. They threaten full publication in 24 hours if proof of negotiation is not provided.",
                    breakingInject = BreakingInject(
                        title = "URGENT: CFO Board Call",
                        source = "Executive Committee",
                        message = "The Board of Directors is divided: C-Suite is questioning if paying a $1.5M discounted ransom is cheaper than business interruption and litigation.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What is your stance on negotiating or paying the extortion demand?",
                        operationalContext = "Under OFAC advisories, paying ransoms to sanctioned cybercrime entities can incur civil penalties of up to $20M and criminal liability regardless of corporate intent.",
                        choices = listOf(
                            DecisionChoice(
                                id = "dh_p2_c1",
                                title = "Refuse Payment; Deploy Specialized IR Negotiator for Delay",
                                description = "Engage specialized ransom negotiator strictly to buy 48 hours for forensic validation while confirming immutable offline backup restoration.",
                                recommendedBy = "External Legal Counsel",
                                costDeltaUsd = 350_000L,
                                timeDeltaHours = 14,
                                trustDeltaPercent = -5,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Sound crisis doctrine. Engaging specialized negotiators to stall without committing capital buys critical hours for data classification and legal assessment without violating OFAC sanctions.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 90,
                                    Competency.CONTAINMENT_SPEED to 85,
                                    Competency.LEGAL_COMPLIANCE to 95,
                                    Competency.CRISIS_COMMS to 90,
                                    Competency.BUSINESS_RESILIENCE to 90
                                )
                            ),
                            DecisionChoice(
                                id = "dh_p2_c2",
                                title = "Pay $2M Negotiated Ransom via Cryptocurrency Broker",
                                description = "Authorize wire transfer to rapid crypto escrow agent to purchase Monero and receive decryption key and non-disclosure pledge.",
                                recommendedBy = "VP Operations",
                                costDeltaUsd = 2_200_000L,
                                timeDeltaHours = 6,
                                trustDeltaPercent = -25,
                                legalRiskChange = 5,
                                forensicsImpact = ForensicIntegrity.PARTIALLY_COMPROMISED,
                                feedbackDoctrine = "Severe risk. No guarantee that attackers will delete data (over 70% of double-extortion victims are re-extorted or leaked anyway). Potential OFAC sanctions investigation triggered.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 50,
                                    Competency.CONTAINMENT_SPEED to 40,
                                    Competency.LEGAL_COMPLIANCE to 20,
                                    Competency.CRISIS_COMMS to 35,
                                    Competency.BUSINESS_RESILIENCE to 30
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 3,
                    title = "Phase 3: Regulatory & SEC Disclosure",
                    timeLabel = "T+48:00 | Legal & Compliance Clock",
                    briefing = "Forensic team confirms 140,000 EU residents' banking data and 420,000 US employee records were included in the exfiltrated volume. The SEC Form 8-K materiality clock is ticking.",
                    breakingInject = BreakingInject(
                        title = "REGULATORY DEADLINE ALERT",
                        source = "Chief Privacy Officer",
                        message = "GDPR requires notification to EU Data Protection Authorities within 72 hours of becoming aware. SEC Item 1.05 requires 8-K disclosure within 4 business days of materiality.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "When and how do you execute regulatory disclosures?",
                        operationalContext = "Filing premature SEC 8-K disclosures before containment is verified risks triggering stock volatility, while missing statutory deadlines triggers mandatory fines up to 4% of global turnover.",
                        choices = listOf(
                            DecisionChoice(
                                id = "dh_p3_c1",
                                title = "File Simultaneous SEC Form 8-K & GDPR DPA Notifications",
                                description = "File Form 8-K stating determination of material incident with measured scope, and notify relevant EU supervisory authorities within the 72-hour window.",
                                recommendedBy = "Chief Legal Officer",
                                costDeltaUsd = 600_000L,
                                timeDeltaHours = 6,
                                trustDeltaPercent = -10,
                                legalRiskChange = -3,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Flawless compliance execution. Transparent, timely disclosure satisfies SEC Item 1.05 and GDPR Article 33. Prevents punitive statutory fines and mitigates shareholder derivative suits.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 85,
                                    Competency.CONTAINMENT_SPEED to 85,
                                    Competency.LEGAL_COMPLIANCE to 98,
                                    Competency.CRISIS_COMMS to 90,
                                    Competency.BUSINESS_RESILIENCE to 85
                                )
                            ),
                            DecisionChoice(
                                id = "dh_p3_c2",
                                title = "Delay Disclosure Pending Complete Forensic Certainty",
                                description = "Withhold notifications until external forensic investigators complete byte-by-byte file confirmation (estimated 14-21 days).",
                                recommendedBy = "Investor Relations",
                                costDeltaUsd = 3_400_000L,
                                timeDeltaHours = 336,
                                trustDeltaPercent = -35,
                                legalRiskChange = 6,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Catastrophic regulatory breach. EU DPAs issued maximum tiered penalties under GDPR. SEC Enforcement opened formal investigation into delayed materiality determination.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 70,
                                    Competency.CONTAINMENT_SPEED to 70,
                                    Competency.LEGAL_COMPLIANCE to 10,
                                    Competency.CRISIS_COMMS to 25,
                                    Competency.BUSINESS_RESILIENCE to 40
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 4,
                    title = "Phase 4: Public Crisis Communications & Customers",
                    timeLabel = "T+60:00 | Media Firestorm",
                    briefing = "Reuters and BleepingComputer publish lead stories: 'Enterprise Giant Paralyzed by DarkHydra Ransomware: Tens of Thousands of Customer Files Leaked.' Inbound customer lines are jammed.",
                    breakingInject = BreakingInject(
                        title = "PRESS INQUIRY: Bloomberg Calling",
                        source = "Corporate Communications",
                        message = "Bloomberg reporter has copies of 3 leaked internal customer invoices and is demanding comment within 60 minutes before broadcasting.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What is your public and customer communications strategy?",
                        operationalContext = "Downplaying the incident while leaked files circulate destroys trust. Conversely, over-committing to unverified remediation timelines exposes the company to deceptive practices claims.",
                        choices = listOf(
                            DecisionChoice(
                                id = "dh_p4_c1",
                                title = "Transparent Customer Advisory & Dedicated Remediation Hub",
                                description = "Launch public incident response microsite, publish direct notice to impacted customers with free 24-month identity monitoring, and host CISO technical press briefing.",
                                recommendedBy = "Head of Crisis PR",
                                costDeltaUsd = 750_000L,
                                timeDeltaHours = 8,
                                trustDeltaPercent = 10,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Exemplary crisis management. Early candor, transparent remediation support, and executive accountability arrest reputation collapse and reduce customer churn by 60%.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 85,
                                    Competency.CONTAINMENT_SPEED to 85,
                                    Competency.LEGAL_COMPLIANCE to 90,
                                    Competency.CRISIS_COMMS to 98,
                                    Competency.BUSINESS_RESILIENCE to 90
                                )
                            ),
                            DecisionChoice(
                                id = "dh_p4_c2",
                                title = "Generic 'Routine Maintenance' Holding Statement",
                                description = "Issue a brief statement blaming service disruptions on 'routine technical maintenance' and provide no official confirmation of exfiltration.",
                                recommendedBy = "VP Marketing",
                                costDeltaUsd = 1_800_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = -30,
                                legalRiskChange = 4,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Severe breach of public trust. The threat actor immediately published leaked files to prove the company was lying, causing viral backlash and an immediate stock plunge.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 70,
                                    Competency.CONTAINMENT_SPEED to 70,
                                    Competency.LEGAL_COMPLIANCE to 30,
                                    Competency.CRISIS_COMMS to 15,
                                    Competency.BUSINESS_RESILIENCE to 50
                                )
                            )
                        )
                    )
                )
            )
        ),
        IncidentScenario(
            id = "cloudmatrix-supply-chain",
            title = "Vendor CI/CD Pipeline & Supply Chain Poisoning",
            codename = "OP_POISON_PILL",
            threatActor = "APT41 (State-Sponsored Cyber Espionage)",
            severity = IncidentSeverity.CRITICAL,
            category = ScenarioCategory.SUPPLY_CHAIN,
            overview = "Compromised third-party NPM package dependency injected a stealth backdoor into your core SaaS product build. For 18 days, all customer API tokens and tenant session credentials were intercepted.",
            impactedSystems = listOf("GitHub Enterprise CI/CD Runners", "Production Docker Registry", "Customer JWT Authentication Gateway", "Tenant API Key Vault"),
            regulatoryScope = listOf("SOC 2 Type II Non-Compliance", "PCI-DSS 4.0 Requirement 6.3", "GDPR Article 32", "FedRAMP Incident Reporting"),
            baselineCostUsd = 1_100_000L,
            estimatedDurationHours = 48,
            phases = listOf(
                SimulationPhase(
                    phaseNumber = 1,
                    title = "Phase 1: Zero-Day Discovery & Ingestion Scope",
                    timeLabel = "T+00:30 | DevSecOps Alert",
                    briefing = "An external security researcher submits a critical vulnerability bounty: your latest production releases include a malicious payload in the 'cloudmatrix-auth-utils' package harvesting bearer tokens.",
                    breakingInject = BreakingInject(
                        title = "Egress Traffic Detected",
                        source = "SIEM Security Analyst",
                        message = "Over 680,000 authorization headers were relayed to a command-and-control server hosted on bulletproof Russian infrastructure over the past 14 days.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What is your immediate credential revocation strategy?",
                        operationalContext = "Revoking all active customer API tokens instantly terminates millions of active automated workflows for 1,200 enterprise customers, freezing their business operations.",
                        choices = listOf(
                            DecisionChoice(
                                id = "cm_p1_c1",
                                title = "Emergency Global Revocation & Automated Re-keying",
                                description = "Force-expire all 1.2M API tokens and tenant session cookies immediately. Spin up dedicated re-keying automation and API rotation endpoints.",
                                recommendedBy = "Head of Product Security",
                                costDeltaUsd = 500_000L,
                                timeDeltaHours = 6,
                                trustDeltaPercent = -5,
                                legalRiskChange = -3,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Correct defensive response. When tokens are confirmed compromised in the wild, immediate revocation is the only method to prevent mass tenant lateral movement.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 92,
                                    Competency.CONTAINMENT_SPEED to 95,
                                    Competency.LEGAL_COMPLIANCE to 90,
                                    Competency.CRISIS_COMMS to 85,
                                    Competency.BUSINESS_RESILIENCE to 88
                                )
                            ),
                            DecisionChoice(
                                id = "cm_p1_c2",
                                title = "Staggered Phased Expiration over 72 Hours",
                                description = "Notify customers by email to manually rotate their keys over 3 business days to avoid operational disruption.",
                                recommendedBy = "VP Customer Success",
                                costDeltaUsd = 1_600_000L,
                                timeDeltaHours = 72,
                                trustDeltaPercent = -25,
                                legalRiskChange = 5,
                                forensicsImpact = ForensicIntegrity.PARTIALLY_COMPROMISED,
                                feedbackDoctrine = "Catastrophic delay. During the 72-hour window, the adversary used stolen high-privilege customer keys to breach 8 Fortune 500 downstream clients, triggering multi-party litigation.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 70,
                                    Competency.CONTAINMENT_SPEED to 30,
                                    Competency.LEGAL_COMPLIANCE to 40,
                                    Competency.CRISIS_COMMS to 45,
                                    Competency.BUSINESS_RESILIENCE to 35
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 2,
                    title = "Phase 2: Downstream Customer Breach Notification",
                    timeLabel = "T+12:00 | Downstream Triage",
                    briefing = "Major banking and healthcare clients whose API keys were harvested are demanding immediate forensic telemetry to determine whether their internal data was accessed.",
                    dilemma = IncidentDilemma(
                        question = "How do you handle transparency with enterprise clients and auditors?",
                        operationalContext = "Admitting that unencrypted tokens were egressed opens immediate SLA penalty clauses, while shielding logs violates contractual breach notice terms.",
                        choices = listOf(
                            DecisionChoice(
                                id = "cm_p2_c1",
                                title = "Open Audit Trail & Provide Forensic Indicators of Compromise (IoCs)",
                                description = "Release sanitized forensic logs, source IP lists, and bespoke tenant access timelines to client security teams within 12 hours.",
                                recommendedBy = "CISO",
                                costDeltaUsd = 400_000L,
                                timeDeltaHours = 8,
                                trustDeltaPercent = 10,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Gold standard supply chain transparency. Giving enterprise security teams actionable IoCs allowed them to self-contain, transforming potential lawsuits into collaborative partnerships.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 90,
                                    Competency.CONTAINMENT_SPEED to 88,
                                    Competency.LEGAL_COMPLIANCE to 95,
                                    Competency.CRISIS_COMMS to 95,
                                    Competency.BUSINESS_RESILIENCE to 92
                                )
                            ),
                            DecisionChoice(
                                id = "cm_p2_c2",
                                title = "Provide Only Boilerplate Legal Notices without Telemetry",
                                description = "Limit communication to generic legal disclaimers prepared by risk counsel stating 'an investigation is ongoing'. Refuse to share raw egress logs.",
                                recommendedBy = "Risk & Litigation Partner",
                                costDeltaUsd = 1_400_000L,
                                timeDeltaHours = 48,
                                trustDeltaPercent = -30,
                                legalRiskChange = 4,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Severe failure of customer retention. 3 anchor enterprise accounts immediately filed breach-of-contract lawsuits and revoked their vendor contracts.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 65,
                                    Competency.CONTAINMENT_SPEED to 60,
                                    Competency.LEGAL_COMPLIANCE to 60,
                                    Competency.CRISIS_COMMS to 25,
                                    Competency.BUSINESS_RESILIENCE to 50
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 3,
                    title = "Phase 3: SLSA Level 3 CI/CD Attestation & Code Signing",
                    timeLabel = "T+72:00 | Engineering Governance",
                    briefing = "External security auditors report that while the malicious NPM package has been purged, build runners lack cryptographic provenance to prevent future compromised dependencies from passing CI/CD pipelines.",
                    breakingInject = BreakingInject(
                        title = "AUDIT FINDING: Pipeline Provenance Deficit",
                        source = "Lead DevSecOps Architect",
                        message = "Enterprise clients require cryptographic proof of build integrity and SLSA Level 3 provenance before restoring SaaS integrations.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What architectural pipeline controls do you mandate across enterprise builds?",
                        operationalContext = "Enforcing cryptographic provenance and hermetic builds adds build overhead but guarantees artifact integrity.",
                        choices = listOf(
                            DecisionChoice(
                                id = "cm_p3_c1",
                                title = "Enforce SLSA 3 Hermetic Builds, Ephemeral Isolated Runners, and Cosign Signing",
                                description = "Mandate cryptographic signing of all container images with Sigstore/Cosign, require automated SBOM validation against CVE databases, and execute builds on ephemeral, single-use isolated runners.",
                                recommendedBy = "Chief Technology Officer",
                                costDeltaUsd = 280_000L,
                                timeDeltaHours = 8,
                                trustDeltaPercent = 15,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Exemplary supply chain defense. SLSA Level 3 compliance and cryptographic provenance establish unbreakable build assurance, meeting NIST SP 800-161 and ISO 27001 requirements.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 92,
                                    Competency.CONTAINMENT_SPEED to 90,
                                    Competency.LEGAL_COMPLIANCE to 95,
                                    Competency.CRISIS_COMMS to 90,
                                    Competency.BUSINESS_RESILIENCE to 98
                                )
                            ),
                            DecisionChoice(
                                id = "cm_p3_c2",
                                title = "Add Static Package Lock Pinning without Cryptographic Signatures",
                                description = "Pin NPM dependency package versions in lockfiles but omit cryptographic artifact signing or runner isolation to preserve quick release velocity.",
                                recommendedBy = "VP Engineering",
                                costDeltaUsd = 750_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = -15,
                                legalRiskChange = 3,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Insufficient defense. Static package pinning fails to protect against compromised registry hashes or upstream registry account takeovers.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 60,
                                    Competency.CONTAINMENT_SPEED to 55,
                                    Competency.LEGAL_COMPLIANCE to 45,
                                    Competency.CRISIS_COMMS to 40,
                                    Competency.BUSINESS_RESILIENCE to 40
                                )
                            )
                        )
                    )
                )
            )
        ),
        IncidentScenario(
            id = "lost-laptop-theft",
            title = "Stolen Executive Laptop: Unencrypted M&A Dossier",
            codename = "OP_VELVET_SHADOW",
            threatActor = "Targeted Physical Theft / Opportunity Actor",
            severity = IncidentSeverity.HIGH,
            category = ScenarioCategory.EXECUTIVE_THEFT,
            overview = "The Chief Strategy Officer's company MacBook was stolen from a luxury hotel conference lounge in Zurich. The device contained unencrypted drafts of an imminent $1.8B acquisition, board minutes, and cached Okta SSO session tokens.",
            impactedSystems = listOf("Executive Endpoint (macOS)", "Okta Cloud SSO Directory", "M&A Strategic File Repository", "Corporate Gmail & Slack"),
            regulatoryScope = listOf("SEC Insider Trading Regulations (Rule 10b-5)", "GDPR Article 32 (Encryption mandate)", "State Privacy Laws"),
            baselineCostUsd = 450_000L,
            estimatedDurationHours = 24,
            phases = listOf(
                SimulationPhase(
                    phaseNumber = 1,
                    title = "Phase 1: Device Assessment & Remote Wiping",
                    timeLabel = "T+01:15 | Physical Security Incident",
                    briefing = "CSO reports laptop stolen while attending a keynote. MDM system shows the device checked in 10 minutes ago from a local cellular hotspot before going offline.",
                    dilemma = IncidentDilemma(
                        question = "What immediate remote security measures do you execute?",
                        operationalContext = "Issuing an immediate remote wipe destroys on-device volatile logs that might reveal if files were copied, but protects the remaining un-synced data.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ll_p1_c1",
                                title = "Revoke Active SSO Tokens, Force MDM Remote Wipe, and Freeze Accounts",
                                description = "Instantly terminate all Okta/Google/Slack sessions, push an MDM wipe command for whenever the device reconnects, and reset all enterprise credentials.",
                                recommendedBy = "Security Operations Lead",
                                costDeltaUsd = 120_000L,
                                timeDeltaHours = 2,
                                trustDeltaPercent = 0,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Correct response. The primary goal is preventing unauthorized lateral movement into cloud repositories. Identity revocation prevents exploitation even if the physical drive is accessed.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 95,
                                    Competency.CONTAINMENT_SPEED to 95,
                                    Competency.LEGAL_COMPLIANCE to 90,
                                    Competency.CRISIS_COMMS to 85,
                                    Competency.BUSINESS_RESILIENCE to 90
                                )
                            ),
                            DecisionChoice(
                                id = "ll_p1_c2",
                                title = "Leave Account Active to Geotrack Device via Find My",
                                description = "Keep credentials active to track the thief's physical location for local police recovery.",
                                recommendedBy = "Corporate Physical Security",
                                costDeltaUsd = 850_000L,
                                timeDeltaHours = 18,
                                trustDeltaPercent = -20,
                                legalRiskChange = 5,
                                forensicsImpact = ForensicIntegrity.TAINTED,
                                feedbackDoctrine = "Severe blunder. While tracking the thief, the perpetrator opened the active email client and forwarded 15 confidential M&A pitch decks to external addresses.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 45,
                                    Competency.CONTAINMENT_SPEED to 20,
                                    Competency.LEGAL_COMPLIANCE to 30,
                                    Competency.CRISIS_COMMS to 40,
                                    Competency.BUSINESS_RESILIENCE to 25
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 2,
                    title = "Phase 2: Market Sensitivity & M&A Materiality",
                    timeLabel = "T+08:00 | Stock Exchange Pre-Market",
                    briefing = "Trading desk notices unusual options volume in target acquisition company shares. Rumors of the secret deal are spreading on financial message boards.",
                    dilemma = IncidentDilemma(
                        question = "Do you accelerate public announcement of the acquisition or maintain silence?",
                        operationalContext = "Under securities regulations, when non-public material information leaks, companies must either promptly disclose or risk severe SEC enforcement actions.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ll_p2_c1",
                                title = "Halt Stock Trading & Issue Joint Accelerating Press Release",
                                description = "Request NYSE trading halt and release joint 8-K disclosure confirming transaction terms ahead of schedule.",
                                recommendedBy = "Securities Legal Counsel",
                                costDeltaUsd = 250_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = 5,
                                legalRiskChange = -3,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Outstanding securities compliance. Timely halt prevented insider trading exploitation and protected board members from regulatory investigation.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 90,
                                    Competency.CONTAINMENT_SPEED to 90,
                                    Competency.LEGAL_COMPLIANCE to 98,
                                    Competency.CRISIS_COMMS to 92,
                                    Competency.BUSINESS_RESILIENCE to 90
                                )
                            ),
                            DecisionChoice(
                                id = "ll_p2_c2",
                                title = "Deny Leak and Proceed with Secret Schedule",
                                description = "Issue a blanket 'no comment on market rumors' and attempt to keep acquisition confidential for another 10 days.",
                                recommendedBy = "Investment Banking Advisory",
                                costDeltaUsd = 1_900_000L,
                                timeDeltaHours = 120,
                                trustDeltaPercent = -30,
                                legalRiskChange = 6,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "SEC opened formal insider trading inquiry into unusual volume, resulting in shareholder lawsuits and transaction renegotiation penalties.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 60,
                                    Competency.CONTAINMENT_SPEED to 50,
                                    Competency.LEGAL_COMPLIANCE to 15,
                                    Competency.CRISIS_COMMS to 30,
                                    Competency.BUSINESS_RESILIENCE to 40
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 3,
                    title = "Phase 3: Enterprise Policy Overhaul & Hardware Encryption",
                    timeLabel = "T+72:00 | Board Risk Committee",
                    briefing = "Local police recover the stolen hardware from a pawn shop. Forensics confirms the thief attempted multiple brute-force logins before the remote wipe signal was triggered.",
                    breakingInject = BreakingInject(
                        title = "BOARD INQUIRY: Executive Security Mandate",
                        source = "Audit Committee Chairman",
                        message = "The Board of Directors demands immediate implementation of zero-trust hardware controls across all executive travel devices to prevent recurring exposure.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What enterprise policy and technical controls do you enforce for mobile executives?",
                        operationalContext = "Balancing C-suite operational convenience against rigorous hardware-level cryptographic assurance is critical to preventing recurrent corporate espionage.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ll_p3_c1",
                                title = "Enforce Pre-Boot BitLocker PINs, Hardware FIDO2 Keys, and Dedicated Travel Laptops",
                                description = "Mandate TPM pre-boot authorization PINs, hardware security keys for all enterprise logins, and loaner travel laptops stripped of local storage for international transit.",
                                recommendedBy = "Chief Information Security Officer",
                                costDeltaUsd = 350_000L,
                                timeDeltaHours = 8,
                                trustDeltaPercent = 15,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Exemplary governance. Pre-boot cryptographic authentication ensures that cold-boot attacks and hardware theft cannot compromise at-rest storage even if physical possession is lost.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 92,
                                    Competency.CONTAINMENT_SPEED to 90,
                                    Competency.LEGAL_COMPLIANCE to 95,
                                    Competency.CRISIS_COMMS to 90,
                                    Competency.BUSINESS_RESILIENCE to 98
                                )
                            ),
                            DecisionChoice(
                                id = "ll_p3_c2",
                                title = "Maintain Standard OS Password Protection to Avoid Executive Inconvenience",
                                description = "Issue a general security advisory reminder but decline to mandate pre-boot authentication or hardware loaner policies.",
                                recommendedBy = "VP Executive Operations",
                                costDeltaUsd = 800_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = -15,
                                legalRiskChange = 3,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Repeated vulnerability. Audit committee flagged systemic failure of due care, resulting in an external cybersecurity compliance audit.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 60,
                                    Competency.CONTAINMENT_SPEED to 50,
                                    Competency.LEGAL_COMPLIANCE to 40,
                                    Competency.CRISIS_COMMS to 50,
                                    Competency.BUSINESS_RESILIENCE to 35
                                )
                            )
                        )
                    )
                )
            )
        ),
        IncidentScenario(
            id = "misdirected-email-spill",
            title = "Mass Customer Financial Record Email Blast",
            codename = "OP_REPLY_ALL",
            threatActor = "Internal Human Error & Automated Script Failure",
            severity = IncidentSeverity.MEDIUM,
            category = ScenarioCategory.DATA_LEAK,
            overview = "A monthly marketing newsletter script accidentally pulled from the wrong database staging view, emailing unmasked tax forms (W-9s, SSNs, bank routing numbers) of 64,000 customers to 380,000 public newsletter subscribers.",
            impactedSystems = listOf("Marketing Automation Platform (Mailgun/SendGrid)", "Customer Financial Staging DB", "Customer Support Center"),
            regulatoryScope = listOf("GLBA Financial Privacy Rule", "IRS PII Safeguard Guidelines", "State Breach Notification Laws (All 50 US States)", "FTC Act Section 5"),
            baselineCostUsd = 650_000L,
            estimatedDurationHours = 36,
            phases = listOf(
                SimulationPhase(
                    phaseNumber = 1,
                    title = "Phase 1: Blast Containment & Server Recall",
                    timeLabel = "T+00:15 | Outbox Ingestion",
                    briefing = "Email queue is 42% delivered (160,000 sent, 220,000 pending in queue). Customers are already calling support in confusion and anger.",
                    dilemma = IncidentDilemma(
                        question = "What is your immediate email infrastructure action?",
                        operationalContext = "Abruptly aborting the outbound MTA queue leaves thousands of emails half-sent, but stops further propagation.",
                        choices = listOf(
                            DecisionChoice(
                                id = "me_p1_c1",
                                title = "Hard Kill Outbound Mail Relay Queues & Purge Pending Spool",
                                description = "Instantly abort the marketing relay server, flush all remaining queues, and initiate API message retract requests on major providers (Microsoft 365, Google Workspace).",
                                recommendedBy = "Infrastructure Engineering Lead",
                                costDeltaUsd = 180_000L,
                                timeDeltaHours = 1,
                                trustDeltaPercent = -5,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Crucial mitigation. Flushing the spool immediately prevented over 220,000 additional disclosures, slashing downstream statutory notice exposure by more than half.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 95,
                                    Competency.CONTAINMENT_SPEED to 98,
                                    Competency.LEGAL_COMPLIANCE to 88,
                                    Competency.CRISIS_COMMS to 85,
                                    Competency.BUSINESS_RESILIENCE to 90
                                )
                            ),
                            DecisionChoice(
                                id = "me_p1_c2",
                                title = "Send Immediate Follow-Up 'Recall & Disregard' Email to All",
                                description = "Let the queue finish and immediately send an automated follow-up asking recipients to delete the previous email without reading it.",
                                recommendedBy = "Marketing Operations Director",
                                costDeltaUsd = 920_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = -20,
                                legalRiskChange = 4,
                                forensicsImpact = ForensicIntegrity.PARTIALLY_COMPROMISED,
                                feedbackDoctrine = "Streisand Effect disaster. The follow-up email drew direct attention to the unread attachments, causing tens of thousands of users who would have ignored the email to download the sensitive tax forms.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 60,
                                    Competency.CONTAINMENT_SPEED to 25,
                                    Competency.LEGAL_COMPLIANCE to 35,
                                    Competency.CRISIS_COMMS to 20,
                                    Competency.BUSINESS_RESILIENCE to 45
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 2,
                    title = "Phase 2: State AG Notifications & Identity Protection",
                    timeLabel = "T+24:00 | 50-State AG Notice",
                    briefing = "Confirmed: 28,000 customers had full Social Security Numbers and banking details delivered to external inboxes across 48 US states.",
                    dilemma = IncidentDilemma(
                        question = "What remediation package do you offer impacted customers?",
                        operationalContext = "Offering standard 1-year credit monitoring meets minimum state laws, while comprehensive 3-year identity restoration and dark web surveillance limits regulatory enforcement actions.",
                        choices = listOf(
                            DecisionChoice(
                                id = "me_p2_c1",
                                title = "Full 3-Year Identity Restoration & Expedited Multi-State AG Filings",
                                description = "Notify all 50 State Attorneys General within 24 hours, partner with major bureau for 36 months of credit lock and $1M insurance, and set up dedicated US-based call center.",
                                recommendedBy = "Chief Privacy Counsel",
                                costDeltaUsd = 420_000L,
                                timeDeltaHours = 6,
                                trustDeltaPercent = 12,
                                legalRiskChange = -3,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Superior remediation craft. Aggressive victim protection and proactive State AG outreach caused 35 state regulators to close inquiries without fines.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 90,
                                    Competency.CONTAINMENT_SPEED to 90,
                                    Competency.LEGAL_COMPLIANCE to 98,
                                    Competency.CRISIS_COMMS to 94,
                                    Competency.BUSINESS_RESILIENCE to 92
                                )
                            ),
                            DecisionChoice(
                                id = "me_p2_c2",
                                title = "Bare Minimum 12-Month Single-Bureau Monitoring",
                                description = "Offer lowest-tier 12-month credit monitoring and wait until statutory 30-60 day limits to notify State Attorneys General.",
                                recommendedBy = "Finance Risk Officer",
                                costDeltaUsd = 1_100_000L,
                                timeDeltaHours = 720,
                                trustDeltaPercent = -25,
                                legalRiskChange = 4,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "New York and California AGs levied joint $850k civil penalties for inadequate consumer protections and delayed reporting.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 70,
                                    Competency.CONTAINMENT_SPEED to 70,
                                    Competency.LEGAL_COMPLIANCE to 40,
                                    Competency.CRISIS_COMMS to 40,
                                    Competency.BUSINESS_RESILIENCE to 60
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 3,
                    title = "Phase 3: FTC Safeguards Compliance & Outbound DLP Architecture",
                    timeLabel = "T+72:00 | Root-Cause Remediation",
                    briefing = "Engineering root-cause analysis determines that developers used live production database snapshots in marketing newsletter test pipelines without masking or obfuscation.",
                    breakingInject = BreakingInject(
                        title = "FTC INQUIRY: Data Safeguards Audit",
                        source = "Federal Trade Commission",
                        message = "FTC Division of Privacy and Identity Protection issues informal inquiry demanding copy of Written Information Security Plan (WISP) and data masking policies.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "How do you restructure staging data architecture to prevent recurrence and satisfy regulators?",
                        operationalContext = "Synthetic test data generation prevents live PII leakage permanently, while automated outbound Data Loss Prevention (DLP) halts erroneous mass deliveries.",
                        choices = listOf(
                            DecisionChoice(
                                id = "me_p3_c1",
                                title = "Implement Synthetic Mock Data Pipelines & Outbound Regex DLP Gateways",
                                description = "Completely sever marketing systems from production databases, deploy automated Faker-based synthetic data generators, and install inline email DLP to block unmasked SSNs.",
                                recommendedBy = "Head of Security Architecture",
                                costDeltaUsd = 280_000L,
                                timeDeltaHours = 12,
                                trustDeltaPercent = 10,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Superior engineering remediation. Conforms to NIST SP 800-61 and FTC Safeguards Rule requirements for testing data hygiene and outbound data flow control.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 95,
                                    Competency.CONTAINMENT_SPEED to 90,
                                    Competency.LEGAL_COMPLIANCE to 96,
                                    Competency.CRISIS_COMMS to 90,
                                    Competency.BUSINESS_RESILIENCE to 95
                                )
                            ),
                            DecisionChoice(
                                id = "me_p3_c2",
                                title = "Rely on Manual Peer-Review Approval of Marketing Blast Scripts",
                                description = "Establish a manual email sign-off checklist without automated pipeline safeguards or database segregation.",
                                recommendedBy = "Director of Marketing Operations",
                                costDeltaUsd = 950_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = -20,
                                legalRiskChange = 4,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Inadequate administrative safeguard. Six months later, another manual query mistake caused customer financial statements to leak again.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 55,
                                    Competency.CONTAINMENT_SPEED to 50,
                                    Competency.LEGAL_COMPLIANCE to 30,
                                    Competency.CRISIS_COMMS to 40,
                                    Competency.BUSINESS_RESILIENCE to 30
                                )
                            )
                        )
                    )
                )
            )
        ),
        IncidentScenario(
            id = "cloud-s3-exposure",
            title = "Exposed Cloud Bucket: 4.2M Customer Records",
            codename = "OP_OPEN_VAULT",
            threatActor = "Independent Security Researcher & Opportunistic Scrapers",
            severity = IncidentSeverity.HIGH,
            category = ScenarioCategory.CLOUD_EXPOSURE,
            overview = "An AWS S3 data-lake bucket configured for analytics had public READ permissions enabled during a DevOps migration 90 days ago. A researcher informs security team they downloaded 4.2M user profiles including hashed passwords, home addresses, and order history.",
            impactedSystems = listOf("AWS S3 Data-Lake Bucket (prod-telemetry-archive)", "Customer Identity Store", "BigQuery Analytics Pipeline"),
            regulatoryScope = listOf("GDPR Article 32 (Security of Processing)", "CCPA/CPRA Statutory Damages", "FTC Consent Decree Risk"),
            baselineCostUsd = 850_000L,
            estimatedDurationHours = 30,
            phases = listOf(
                SimulationPhase(
                    phaseNumber = 1,
                    title = "Phase 1: Remediation & Threat Verification",
                    timeLabel = "T+00:20 | Responsible Disclosure",
                    briefing = "Researcher provides 100 sample records and gives a 72-hour window before public disclosure. CloudTrail logs show IP addresses from China, Germany, and Russia also accessed the bucket over the last 30 days.",
                    dilemma = IncidentDilemma(
                        question = "What is your immediate cloud access remediation?",
                        operationalContext = "Applying public access blocks instantly closes the exposure, but you must preserve CloudTrail S3 server access logs for forensic investigation.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ce_p1_c1",
                                title = "Enable Account-Level S3 Public Access Block & Snapshot CloudTrail Logs",
                                description = "Immediately apply organization-wide 'Block Public Access', rotate bucket KMS keys, and archive all S3 server access logs to an isolated forensic bucket.",
                                recommendedBy = "Cloud Security Architect",
                                costDeltaUsd = 150_000L,
                                timeDeltaHours = 1,
                                trustDeltaPercent = -5,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Textbook cloud containment. Account-wide Block Public Access prevents sibling bucket exposure while immutable CloudTrail logging ensures complete historical auditability.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 95,
                                    Competency.CONTAINMENT_SPEED to 98,
                                    Competency.LEGAL_COMPLIANCE to 90,
                                    Competency.CRISIS_COMMS to 85,
                                    Competency.BUSINESS_RESILIENCE to 95
                                )
                            ),
                            DecisionChoice(
                                id = "ce_p1_c2",
                                title = "Delete the Exposed Bucket Entirely",
                                description = "Purge the entire bucket and all its contents to immediately eliminate the vulnerability and hide the mistake.",
                                recommendedBy = "DevOps Lead",
                                costDeltaUsd = 1_500_000L,
                                timeDeltaHours = 2,
                                trustDeltaPercent = -20,
                                legalRiskChange = 6,
                                forensicsImpact = ForensicIntegrity.TAINTED,
                                feedbackDoctrine = "Spoliation of evidence! Deleting the bucket without preserving server access logs made it impossible to prove what the foreign IPs downloaded, exposing the company to class action claims for full database breach.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 50,
                                    Competency.CONTAINMENT_SPEED to 40,
                                    Competency.LEGAL_COMPLIANCE to 10,
                                    Competency.CRISIS_COMMS to 30,
                                    Competency.BUSINESS_RESILIENCE to 15
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 2,
                    title = "Phase 2: Researcher Engagement & Voluntary Bug Bounty",
                    timeLabel = "T+06:00 | Disclosure Negotiation",
                    briefing = "The researcher confirms they have not shared data, but asks for a standard $25,000 vulnerability reward and an official acknowledgment.",
                    dilemma = IncidentDilemma(
                        question = "How do you handle the security researcher?",
                        operationalContext = "Threatening researchers with the Computer Fraud and Abuse Act (CFAA) almost always leads to hostile public dumps, whereas professional coordinated disclosure maintains control.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ce_p2_c1",
                                title = "Execute Safe Harbor Agreement, Pay $25k Bounty, Coordinated Disclosure",
                                description = "Sign formal mutual non-disclosure and responsible disclosure terms, award bug bounty, and coordinate a joint technical post-mortem after customer patching.",
                                recommendedBy = "Bug Bounty Program Director",
                                costDeltaUsd = 125_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = 10,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Best practice coordinated vulnerability disclosure (CVD). Turning an adversarial leak into a verified white-hat engagement preserved brand reputation and secured data deletion confirmation.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 90,
                                    Competency.CONTAINMENT_SPEED to 88,
                                    Competency.LEGAL_COMPLIANCE to 92,
                                    Competency.CRISIS_COMMS to 96,
                                    Competency.BUSINESS_RESILIENCE to 90
                                )
                            ),
                            DecisionChoice(
                                id = "ce_p2_c2",
                                title = "Issue Legal Cease & Desist Letter Threatening Criminal Prosecution",
                                description = "Send aggressive legal notice threatening prosecution under CFAA and demanding immediate surrender of all researcher equipment.",
                                recommendedBy = "Outside IP Litigator",
                                costDeltaUsd = 1_200_000L,
                                timeDeltaHours = 12,
                                trustDeltaPercent = -35,
                                legalRiskChange = 5,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Catastrophic PR backfire. The researcher immediately tweeted the full story and leak proof, triggering viral tech media condemnation of the company for 'shooting the messenger.'",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 60,
                                    Competency.CONTAINMENT_SPEED to 50,
                                    Competency.LEGAL_COMPLIANCE to 30,
                                    Competency.CRISIS_COMMS to 10,
                                    Competency.BUSINESS_RESILIENCE to 40
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 3,
                    title = "Phase 3: Regulatory Notice & Cloud IAM Governance Overhaul",
                    timeLabel = "T+48:00 | Global Regulatory Reporting",
                    briefing = "Forensic analysis of CloudTrail server access logs reveals 4 external IP addresses downloaded 185,000 files prior to bucket remediation, affecting EU and California citizens.",
                    breakingInject = BreakingInject(
                        title = "STATUTORY NOTICE TRIGGERED",
                        source = "Data Protection Officer",
                        message = "Because exfiltration by unauthorized third parties is verified, formal notification to California AG and European DPAs is legally mandatory under CCPA and GDPR.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What regulatory and customer notification strategy do you execute?",
                        operationalContext = "Self-reporting with detailed forensic mitigation limits regulatory penalties, whereas concealing confirmed exfiltration risks max-tier statutory fines.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ce_p3_c1",
                                title = "File Formal GDPR/CCPA Notices & Deploy Automated AWS IAM Policy Sentinels",
                                description = "Notify relevant DPAs within 72 hours with precise affected counts, issue direct email advisories to affected account holders, and deploy AWS Config rules to auto-remediate public S3 buckets.",
                                recommendedBy = "Chief Information Security Officer & DPO",
                                costDeltaUsd = 320_000L,
                                timeDeltaHours = 6,
                                trustDeltaPercent = 12,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Flawless compliance and remediation posture. Timely notifications with demonstrated technical controls (AWS Config auto-remediation) satisfied regulators and averted class-action certification.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 94,
                                    Competency.CONTAINMENT_SPEED to 92,
                                    Competency.LEGAL_COMPLIANCE to 98,
                                    Competency.CRISIS_COMMS to 92,
                                    Competency.BUSINESS_RESILIENCE to 95
                                )
                            ),
                            DecisionChoice(
                                id = "ce_p3_c2",
                                title = "Limit Disclosure to Informal Blog Post and Avoid Regulatory Filings",
                                description = "Publish a vague tech blog update about 'system performance enhancements' and refrain from formal regulatory submissions.",
                                recommendedBy = "Corporate Communications VP",
                                costDeltaUsd = 1_800_000L,
                                timeDeltaHours = 72,
                                trustDeltaPercent = -35,
                                legalRiskChange = 6,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Severe regulatory violation. The California Privacy Protection Agency (CPPA) and Irish DPC opened joint formal enforcement investigations, issuing substantial administrative fines.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 50,
                                    Competency.CONTAINMENT_SPEED to 45,
                                    Competency.LEGAL_COMPLIANCE to 10,
                                    Competency.CRISIS_COMMS to 20,
                                    Competency.BUSINESS_RESILIENCE to 25
                                )
                            )
                        )
                    )
                )
            )
        ),
        IncidentScenario(
            id = "rogue-admin-insider",
            title = "Disgruntled Database Architect: Mass Exfiltration & Logic Bomb",
            codename = "OP_INSIDE_JOB",
            threatActor = "Privileged Insider (Terminated Lead DB Architect)",
            severity = IncidentSeverity.CRITICAL,
            category = ScenarioCategory.INSIDER_THREAT,
            overview = "A lead database administrator passed over for promotion planted an encrypted staging script that copied 3.4M customer records to an external cloud storage provider and scheduled a time-bomb cron job to corrupt primary databases.",
            impactedSystems = listOf("Core Customer RDS Postgres Clusters", "AWS IAM Identity Center", "HashiCorp Vault Secrets Engine", "Database Backup Archives"),
            regulatoryScope = listOf("SOX Section 404 Internal Controls", "GDPR Article 32 & 33", "SEC Item 1.05", "State Breach Laws"),
            baselineCostUsd = 1_350_000L,
            estimatedDurationHours = 48,
            phases = listOf(
                SimulationPhase(
                    phaseNumber = 1,
                    title = "Phase 1: Privilege Revocation & Bastion Access Isolation",
                    timeLabel = "T+00:10 | SOC Anomaly Alert",
                    briefing = "DLP systems detect a 40GB compressed transfer from a DBA bastion host to a personal Wasabi S3 bucket. The employee was placed on administrative leave 2 hours prior.",
                    breakingInject = BreakingInject(
                        title = "ALERT: Active SSH Sessions Detected",
                        source = "Cloud Security Operations",
                        message = "The administrator still has 3 active terminal sessions open via personal SSH keys not tied to corporate Okta SSO.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What immediate privilege termination protocol do you authorize?",
                        operationalContext = "Terminating network sessions instantly stops active downloads, but may trigger scheduled destructive scripts if not carefully scoped.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ra_p1_c1",
                                title = "Emergency IAM Hard-Revoke, Rotate Bastion Keys & Pause Scheduled Cron Jobs",
                                description = "Revoke all AWS IAM session tokens, cycle root database credentials in Vault, kill active SSH sessions, and temporarily pause unverified database cron tasks.",
                                recommendedBy = "Chief Information Security Officer",
                                costDeltaUsd = 220_000L,
                                timeDeltaHours = 2,
                                trustDeltaPercent = 5,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Exemplary insider containment doctrine. Cutting active sessions while pausing crons defuses delayed destructive logic bombs while preserving volatile command logs.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 95,
                                    Competency.CONTAINMENT_SPEED to 95,
                                    Competency.LEGAL_COMPLIANCE to 90,
                                    Competency.CRISIS_COMMS to 85,
                                    Competency.BUSINESS_RESILIENCE to 92
                                )
                            ),
                            DecisionChoice(
                                id = "ra_p1_c2",
                                title = "Passive Surveillance to Catch Exfiltration Destination",
                                description = "Leave the sessions open for another 6 hours to trace exactly where the employee is uploading files.",
                                recommendedBy = "Corporate Investigations Lead",
                                costDeltaUsd = 1_400_000L,
                                timeDeltaHours = 12,
                                trustDeltaPercent = -25,
                                legalRiskChange = 5,
                                forensicsImpact = ForensicIntegrity.PARTIALLY_COMPROMISED,
                                feedbackDoctrine = "Catastrophic delay. The logic bomb detonated at midnight, encrypting customer order history tables and deleting replica snapshots.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 60,
                                    Competency.CONTAINMENT_SPEED to 25,
                                    Competency.LEGAL_COMPLIANCE to 30,
                                    Competency.CRISIS_COMMS to 35,
                                    Competency.BUSINESS_RESILIENCE to 20
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 2,
                    title = "Phase 2: Law Enforcement Referral & Evidence Preservation",
                    timeLabel = "T+06:00 | Legal & Digital Forensics",
                    briefing = "External DFIR team confirms the exfiltrated dataset contains payment card records and unhashed SSNs. The employee's company laptop was surrendered during exit.",
                    dilemma = IncidentDilemma(
                        question = "Do you engage federal law enforcement (FBI Cyber Division) and seek civil injunction?",
                        operationalContext = "Criminal referrals make the breach matter of public court record, but enable search warrants to seize the employee's personal storage drives.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ra_p2_c1",
                                title = "Formal FBI Referral, Ex Parte TRO, and Preservation Letter",
                                description = "File formal criminal referral with FBI Cyber Division, obtain ex parte temporary restraining order to freeze external cloud buckets, and serve 18 U.S.C. § 2703(f) preservation letters.",
                                recommendedBy = "Special Outside Cybersecurity Counsel",
                                costDeltaUsd = 380_000L,
                                timeDeltaHours = 6,
                                trustDeltaPercent = 10,
                                legalRiskChange = -3,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Decisive legal action. FBI executed emergency seizure of the rogue Wasabi bucket before files could be distributed on darknet leak boards.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 90,
                                    Competency.CONTAINMENT_SPEED to 90,
                                    Competency.LEGAL_COMPLIANCE to 98,
                                    Competency.CRISIS_COMMS to 90,
                                    Competency.BUSINESS_RESILIENCE to 94
                                )
                            ),
                            DecisionChoice(
                                id = "ra_p2_c2",
                                title = "Attempt Informal Internal Settlement and Demand Letter",
                                description = "Instruct HR to offer severance bonus in exchange for signed NDA and promise to delete personal drive copies without law enforcement involvement.",
                                recommendedBy = "VP People Operations",
                                costDeltaUsd = 1_800_000L,
                                timeDeltaHours = 48,
                                trustDeltaPercent = -30,
                                legalRiskChange = 6,
                                forensicsImpact = ForensicIntegrity.TAINTED,
                                feedbackDoctrine = "Severe legal error. Paying off an extorting insider violates corporate fiduciary duties and SEC disclosure rules, leading to direct criminal investigation of executive officers.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 50,
                                    Competency.CONTAINMENT_SPEED to 40,
                                    Competency.LEGAL_COMPLIANCE to 10,
                                    Competency.CRISIS_COMMS to 25,
                                    Competency.BUSINESS_RESILIENCE to 30
                                )
                            )
                        )
                    )
                ),
                SimulationPhase(
                    phaseNumber = 3,
                    title = "Phase 3: Zero-Standing-Privilege Architecture & Dual Authorization",
                    timeLabel = "T+72:00 | Board Governance",
                    briefing = "Audit committee demands complete architectural overhaul to eliminate single points of failure in administrative privilege.",
                    breakingInject = BreakingInject(
                        title = "MANDATE: SOX 404 Deficiency Remediation",
                        source = "Internal Audit Director",
                        message = "External SOX auditors classify single-admin database access as a Material Weakness requiring immediate board-level remediation.",
                        urgent = true
                    ),
                    dilemma = IncidentDilemma(
                        question = "What identity architecture do you implement for database infrastructure?",
                        operationalContext = "Just-in-time access requires peer approval for production access, slightly reducing speed of emergency hotfixes but preventing rogue actions.",
                        choices = listOf(
                            DecisionChoice(
                                id = "ra_p3_c1",
                                title = "Implement Just-In-Time (JIT) Dual-Custodian Access & Ephemeral Vault Credentials",
                                description = "Enforce zero standing privileges (ZSP), requiring dual-custodian peer approval for all production database access with auto-expiring 15-minute Vault session tokens.",
                                recommendedBy = "Enterprise Security Architect",
                                costDeltaUsd = 300_000L,
                                timeDeltaHours = 8,
                                trustDeltaPercent = 15,
                                legalRiskChange = -2,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Highest standard of identity security. Dual authorization and ephemeral credentials mathematically eliminate unilateral rogue insider attacks, satisfying SOX 404 and ISO 27001.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 94,
                                    Competency.CONTAINMENT_SPEED to 92,
                                    Competency.LEGAL_COMPLIANCE to 98,
                                    Competency.CRISIS_COMMS to 90,
                                    Competency.BUSINESS_RESILIENCE to 98
                                )
                            ),
                            DecisionChoice(
                                id = "ra_p3_c2",
                                title = "Require Weekly Manual Access Review Spreadsheets",
                                description = "Keep existing admin credentials unchanged but institute weekly manual access review meetings between managers.",
                                recommendedBy = "IT Operations Lead",
                                costDeltaUsd = 650_000L,
                                timeDeltaHours = 4,
                                trustDeltaPercent = -10,
                                legalRiskChange = 3,
                                forensicsImpact = ForensicIntegrity.INTACT,
                                feedbackDoctrine = "Inadequate control. Manual access reviews routinely suffer from review fatigue and fail to prevent real-time malicious actions by rogue administrators.",
                                competencyScores = mapOf(
                                    Competency.DETECTION_TRIAGE to 60,
                                    Competency.CONTAINMENT_SPEED to 50,
                                    Competency.LEGAL_COMPLIANCE to 40,
                                    Competency.CRISIS_COMMS to 45,
                                    Competency.BUSINESS_RESILIENCE to 40
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}
