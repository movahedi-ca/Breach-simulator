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
        )
    )
}
