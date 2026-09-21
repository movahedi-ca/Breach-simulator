package com.example.data

import com.example.model.DoctrinePlaybook

object DoctrineData {
    val playbooks: List<DoctrinePlaybook> = listOf(
        DoctrinePlaybook(
            id = "gdpr-art-33-34",
            title = "GDPR Articles 33 & 34 (EU Breach Notification)",
            authority = "European Data Protection Board (EDPB)",
            deadlineWindow = "Strict 72 Hours from awareness",
            statutoryPenalties = "Up to €20,000,000 or 4% of total worldwide annual turnover",
            overview = "Article 33 mandates notification to the relevant Lead Supervisory Authority without undue delay and, where feasible, not later than 72 hours after becoming aware of a personal data breach. Article 34 mandates notification to data subjects without undue delay when high risk to rights and freedoms.",
            mandatoryRequirements = listOf(
                "Nature of the personal data breach including categories and approximate number of data subjects.",
                "Name and contact details of Data Protection Officer (DPO) or other contact point.",
                "Description of likely consequences of the personal data breach.",
                "Description of measures taken or proposed to address and mitigate the breach."
            ),
            incidentPlaybookChecklist = listOf(
                "T+00h: Log detection timestamp in centralized incident register.",
                "T+24h: Execute initial data classification (is PII / financial / special category data involved?).",
                "T+48h: Prepare provisional regulatory submission if forensic scope is not yet 100% complete (phased submission is permitted under Art 33(4)).",
                "T+72h: Submit formal breach notification to Lead Supervisory Authority (DPA)."
            ),
            commonPitfalls = listOf(
                "Waiting for forensic investigation to completely finish before notifying regulator (violates 72-hour window).",
                "Failing to document reasons for delay if notified after 72 hours.",
                "Conflating internal awareness with board-level awareness (awareness begins when SOC/incident team has reasonable certainty)."
            )
        ),
        DoctrinePlaybook(
            id = "sec-item-105",
            title = "SEC Cyber Disclosure (Form 8-K Item 1.05)",
            authority = "U.S. Securities and Exchange Commission (SEC)",
            deadlineWindow = "4 Business Days after determination of materiality",
            statutoryPenalties = "Enforcement sanctions, civil injunctions, shareholder derivative liability",
            overview = "Public companies must disclose any cybersecurity incident determined to be material under Item 1.05 of Form 8-K within 4 business days. The determination of materiality must be made without unreasonable delay following discovery.",
            mandatoryRequirements = listOf(
                "Describe the material aspects of the nature, scope, and timing of the incident.",
                "Disclose material impact or reasonably likely material impact on financial condition and results of operations.",
                "Companies are NOT required to disclose specific technical or tactical information that would impede incident response."
            ),
            incidentPlaybookChecklist = listOf(
                "Convene Disclosure Committee / Legal Counsel immediately upon identification of major breach.",
                "Evaluate qualitative and quantitative factors for materiality (revenue impact, customer trust, IP loss).",
                "Formally document the timestamp and rationale of the materiality determination.",
                "Draft and file Form 8-K with SEC EDGAR system within 4 business days."
            ),
            commonPitfalls = listOf(
                "Unreasonably delaying the materiality determination to push back the 4-day clock.",
                "Failing to file an amended 8-K (Item 1.05(d)) if previously unavailable information becomes determined.",
                "Over-disclosing internal network diagrams or credentials that assist the active adversary."
            )
        ),
        DoctrinePlaybook(
            id = "hipaa-breach-rule",
            title = "HIPAA Breach Notification Rule (45 CFR §§ 164.400–414)",
            authority = "U.S. Department of Health and Human Services (HHS OCR)",
            deadlineWindow = "60 Calendar Days (or Immediate media notice if 500+ individuals in a state)",
            statutoryPenalties = "Up to $1.9M+ per violation category annually, corrective action plans",
            overview = "Covered entities and business associates must provide notification of a breach of unsecured Protected Health Information (PHI) to affected individuals, HHS OCR, and prominent media outlets if 500 or more residents of a state are impacted.",
            mandatoryRequirements = listOf(
                "Written notification by first-class mail (or secure email if agreed) to each affected individual.",
                "Notice must include what happened, types of PHI involved, steps individuals should take, and mitigation steps.",
                "Toll-free telephone number active for at least 90 days for customer inquiries.",
                "Prominent media release in the relevant state/jurisdiction if 500+ individuals affected."
            ),
            incidentPlaybookChecklist = listOf(
                "Perform 4-Factor Risk Assessment (nature of data, unauthorized recipient, whether viewed/acquired, mitigation).",
                "Verify whether safe harbor applies (encrypted PHI with keys intact is exempt from breach status).",
                "Notify HHS OCR electronic portal within 60 calendar days.",
                "Coordinate call center capacity prior to public mailing blast."
            ),
            commonPitfalls = listOf(
                "Assuming business associates do not need to notify the covered entity (BA must notify covered entity without unreasonable delay).",
                "Failing to establish toll-free line before sending notification letters.",
                "Ignoring state health privacy laws that impose shorter deadlines than federal HIPAA."
            )
        ),
        DoctrinePlaybook(
            id = "nist-sp-800-61",
            title = "NIST SP 800-61 Rev. 2: Incident Handling Guide",
            authority = "National Institute of Standards and Technology (NIST)",
            deadlineWindow = "Operational Lifecycle (Continuous Phase Progression)",
            statutoryPenalties = "Industry Standard of Due Care (Federal compliance & defense benchmark)",
            overview = "The four-phase lifecycle for computer security incident response: 1. Preparation, 2. Detection and Analysis, 3. Containment, Eradication, and Recovery, and 4. Post-Incident Activity (Lessons Learned).",
            mandatoryRequirements = listOf(
                "Prioritize containment strategies based on potential damage and evidence preservation.",
                "Maintain strict Chain of Custody for digital evidence to ensure admissibility in legal proceedings.",
                "Establish clear escalation matrices and cross-functional communication channels (Legal, PR, Exec, Ops).",
                "Perform documented Lessons Learned session within 14 days of incident closure."
            ),
            incidentPlaybookChecklist = listOf(
                "Phase 1: Acquire volatile memory (RAM) and disk images BEFORE power-cycling compromised endpoints.",
                "Phase 2: Segment affected network enclaves and isolate compromised directory accounts.",
                "Phase 3: Verify clean backup state and deploy enhanced EDR monitoring prior to system restoration.",
                "Phase 4: Document incident root-cause, gap analysis, and budget remediation roadmap."
            ),
            commonPitfalls = listOf(
                "Prematurely eradicating adversary tools before understanding full network persistence mechanisms.",
                "Powering off infected servers, destroying volatile memory evidence and active C2 process handles.",
                "Skipping the post-incident review (AAR) and leaving known root vulnerabilities open to reinfection."
            )
        ),
        DoctrinePlaybook(
            id = "nydfs-23-nycrr-500",
            title = "NYDFS Cybersecurity Regulation (23 NYCRR 500.17)",
            authority = "New York State Department of Financial Services (NYDFS)",
            deadlineWindow = "Strict 72 Hours from determination",
            statutoryPenalties = "Civil money penalties per day, enforcement orders, license suspension",
            overview = "Covered financial institutions must notify the NYDFS superintendent as promptly as possible but in no event later than 72 hours from a determination that a cybersecurity event has occurred that impacts nonpublic information or has a reasonable likelihood of materially harming any material part of normal operations.",
            mandatoryRequirements = listOf(
                "Notice must be submitted electronically via the secure NYDFS Cybersecurity Portal.",
                "Covered entities must notify NYDFS within 24 hours of making any extortion or ransom payment.",
                "Written explanation of reasons why payment was necessary, alternatives considered, and sanction screening.",
                "Annual certification of compliance submitted by CISO and highest governing board member."
            ),
            incidentPlaybookChecklist = listOf(
                "T+00h: Identify whether nonpublic personal information or critical banking operational availability is compromised.",
                "T+24h: If extortion demands exist, conduct mandatory OFAC SDN sanctions screening before any payment negotiations.",
                "T+48h: Prepare electronic notification for NYDFS Secure Portal.",
                "T+72h: Submit statutory notice to Superintendent of Financial Services."
            ),
            commonPitfalls = listOf(
                "Failing to report extortion payments within the separate 24-hour notification window under the 2023 amendment.",
                "Failing to screen threat actors against OFAC SDN lists, creating strict-liability federal sanctions exposure.",
                "Delaying notification because an affiliate or third-party service provider was the entity breached."
            )
        ),
        DoctrinePlaybook(
            id = "pipeda-law25-breach",
            title = "PIPEDA & Law 25: Real Risk of Significant Harm (Canadian Privacy)",
            authority = "Office of the Privacy Commissioner of Canada (OPC) & Quebec CAI",
            deadlineWindow = "As soon as feasible (Federal PIPEDA) / Without delay (Quebec Law 25)",
            statutoryPenalties = "Federal fines up to $100,000 per violation; Quebec CAI penal fines up to $25,000,000 or 4% of worldwide turnover",
            overview = "Federal PIPEDA Division 1.1 and Quebec Law 25 mandate reporting of confidentiality incidents that present a 'Real Risk of Significant Harm' (RROSH) to individuals. Factors include the sensitivity of the personal information and the probability that it will be misused.",
            mandatoryRequirements = listOf(
                "Assessment of RROSH considering data sensitivity and probability of malicious exploitation.",
                "Mandatory notification to the Privacy Commissioner of Canada (OPC) or Quebec CAI.",
                "Direct notification to affected individuals containing required statutory elements unless contrary to law enforcement.",
                "Mandatory internal Breach Register maintained for a minimum of 24 months, even for below-threshold incidents."
            ),
            incidentPlaybookChecklist = listOf(
                "T+00h: Isolate affected records and invoke corporate privacy officer / legal counsel.",
                "T+12h: Execute RROSH matrix (classify data sensitivity: financial, biometric, health, credentials).",
                "T+24h: If RROSH is established, draft OPC and CAI regulatory reporting forms.",
                "T+48h: Prepare individual breach notification notices with credit monitoring and remediation advice.",
                "T+72h: Submit formal filing to OPC / CAI and record comprehensive log in 24-month statutory register."
            ),
            commonPitfalls = listOf(
                "Assuming low volume negates RROSH: a single highly sensitive record can meet the statutory threshold.",
                "Failing to maintain the mandatory 24-month breach record log for non-notified incidents.",
                "Overlooking Quebec Law 25 extraterritorial obligations for any residents of Quebec."
            ),
            expertAdvisoryNote = "Curated by Mohammad Movahedi (CIPP/C - movahedi.ca): Canadian privacy law places personal accountability on organizations for demonstrating defensible RROSH assessments. If in doubt, conduct a structured risk matrix or consult with an accredited Canadian privacy specialist at movahedi.ca."
        ),
        DoctrinePlaybook(
            id = "aida-ai-governance",
            title = "AIDA & EU AI Act: High-Impact AI System Incident Response",
            authority = "Artificial Intelligence and Data Commissioner (AIDA) & EU AI Office",
            deadlineWindow = "Immediate containment & statutory notification upon critical safety failure",
            statutoryPenalties = "AIDA administrative monetary penalties up to 3% of gross global revenues; EU AI Act up to €35M / 7% of worldwide turnover",
            overview = "Emerging AI safety standards require organizations operating high-impact AI systems or autonomous agentic workflows to monitor for catastrophic failures, algorithmic bias, model hallucinations causing legal injury, and unauthorized training data ingestion.",
            mandatoryRequirements = listOf(
                "Continuous logging of model inputs, outputs, inference latency, and automated decision rationales.",
                "Establish human-in-the-loop kill switches and runtime prompt-injection interception shields.",
                "Documented incident reporting to the AI and Data Commissioner for critical harms.",
                "Regular third-party algorithmic impact assessments (AIA) and red-team audits."
            ),
            incidentPlaybookChecklist = listOf(
                "T+00h: Detect anomalous model output, hallucination burst, or unauthorized data retrieval via AI security platform.",
                "T+02h: Trigger model rollback or activate deterministic fallback rules to freeze autonomous agent actions.",
                "T+12h: Secure inference logs, embedding vectors, and user telemetry for algorithmic root-cause analysis.",
                "T+48h: Document bias delta, data exposure scope, and notify affected parties if automated decisions caused material harm."
            ),
            commonPitfalls = listOf(
                "Treating generative AI systems as conventional software without semantic boundary monitoring.",
                "Failing to log intermediate agent tool-calls and API access credentials.",
                "Relying solely on LLM self-moderation without independent runtime guardrails."
            ),
            expertAdvisoryNote = "Curated by Mohammad Movahedi (movahedi.ca): Responsible AI governance bridges technical model risk and corporate accountability. Need an algorithmic safety audit or AI governance framework for your product? Explore AI advisory services at movahedi.ca."
        )
    )
}
