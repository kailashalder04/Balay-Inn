#!/usr/bin/env python3
"""
Balay-Inn Comprehensive User Guide & Support Documentation Generator
Generates:
1. Balay_Inn_User_Guide.docx (Microsoft Word format)
2. Balay_Inn_User_Guide.pdf (Adobe PDF format)
3. USER_GUIDE.md (Markdown format)
"""

import os
import sys
import zipfile
import subprocess
from xml.sax.saxutils import escape

GUIDE_TITLE = "Balay-Inn Application User Guide & Operational Manual"
GUIDE_SUBTITLE = "Comprehensive Guide for Tenants, Property Owners, Brokers, and Administrators"
VERSION = "Version 2.4 - October 2026"
PLATFORM = "Balay-Inn Rental Housing Marketplace & Transparency Platform"

SECTIONS = [
    {
        "title": "1. Introduction & Overview",
        "content": [
            ("heading2", "1.1 About Balay-Inn"),
            ("p", "Balay-Inn is an authoritative rental housing marketplace and transparency platform tailored for the Philippine rental real estate market. The platform brings trust, cost transparency, and anti-fraud protections to renters, property owners, licensed real estate brokers, and authorized property caretakers."),
            ("p", "Key differentiators of Balay-Inn include:"),
            ("bullet", "Hierarchical Geographic Discovery: Complete geographic coverage down to Region, Province, City/Municipality (including all 48 LGUs of Bohol and Cebu City), and Barangay."),
            ("bullet", "Multi-Tier Verification Ladder: An 8-level trust verification framework evaluating phone, email, PhilSys government ID, liveness checks, title deeds, PRC broker accreditation, and on-site physical property visits."),
            ("bullet", "Total Cost Transparency: Mandatory breakdown of monthly rent, association dues, advance rent, security deposits, and estimated first-month total upfront costs."),
            ("bullet", "Anti-Fraud Poster Authority: Clear distinction and legal verification between direct property owners, licensed brokers, family members, and caretakers."),
            ("bullet", "Offline-First Sync with Supabase: Built with local Room SQLite storage that operates seamlessly offline and syncs in real-time with Supabase PostgreSQL cloud backend when connected.")
        ]
    },
    {
        "title": "2. Getting Started & Account Authentication",
        "content": [
            ("heading2", "2.1 Supabase Cloud Authentication"),
            ("p", "Balay-Inn uses Supabase Auth for security, session persistence, and multi-device identity. When launching the app, users are greeted by the Login & Registration screen."),
            ("bullet", "Sign In: Enter your registered email address and secure password. Tap 'Sign In with Supabase' to access your account."),
            ("bullet", "Register New Account: Switch to the 'Register' tab, select your primary role (Tenant, Property Owner, Licensed Broker, or Caretaker), provide your full legal name, email, phone number, and password."),
            ("bullet", "Session Persistence: Your secure authentication tokens are safely stored in Android Encrypted Preferences and validated automatically on startup."),
            ("heading2", "2.2 Quick Persona Switcher (Demo / Testing Mode)"),
            ("p", "For rapid testing and stakeholder review, Balay-Inn includes instant demo persona buttons:"),
            ("bullet", "Maria Santos (Direct Owner): Manage listings, view booking requests, and test landlord features."),
            ("bullet", "Juan Dela Cruz (Tenant): Search properties, set rental requirements, save favorites, and schedule viewings."),
            ("bullet", "Atty. Roberto Gomez (Licensed Broker): PRC-accredited broker (#0028192) managing premium client listings."),
            ("bullet", "Elena Reyes (Authorized Caretaker): On-site caretaker coordinating physical viewings with owner authorization.")
        ]
    },
    {
        "title": "3. Tenant Experience & Rental Discovery",
        "content": [
            ("heading2", "3.1 Personalized Home Feed"),
            ("p", "Upon signing in as a tenant, the Home screen displays personalized rental recommendations based on your preferences, recently viewed properties, active viewing appointments, and saved search alerts."),
            ("heading2", "3.2 Search & Multi-Criteria Filtering (Explore Screen)"),
            ("p", "The Explore Screen features a streamlined search bar and comprehensive filter sheet:"),
            ("bullet", "Compact Top Search Bar: Search by keyword, landmark (e.g. 'Ayala Center', 'Alona Beach'), neighborhood, or city name. Sized to 40dp for a clean, balanced layout."),
            ("bullet", "List View vs. Interactive Map View: Toggle instantly between scrolling property cards and full interactive map with the 40dp map toggle button."),
            ("bullet", "Geographic Cascading Selector: Filter precisely by Country (Philippines) -> Province (e.g. Bohol, Cebu) -> City/Municipality (e.g. Tagbilaran, Panglao, Cebu City) -> Barangay."),
            ("bullet", "Property Type Filter: Filter by Studio, Condominium, Apartment, House, Room, or Townhouse."),
            ("bullet", "Listed By Filter: Filter properties by who posted them: Direct Owner, Licensed Broker, Property Manager, Caretaker, Family Member, or Authorized Representative."),
            ("bullet", "Authorized Posters Only Toggle: Restrict search results strictly to listings with verified legal authority or direct ownership."),
            ("bullet", "Monthly Rent Budget Slider: Set your minimum and maximum monthly rent range (₱0 to ₱100,000+)."),
            ("bullet", "Specifications & Amenities: Toggles for dedicated parking required, pet-friendly only, and verified listings only (Level 3+)."),
            ("bullet", "Sort Order: Sort listings by Newest, Price (Low to High), or Price (High to Low)."),
            ("bullet", "Active Filter Badges: Quick-removal chips at the top of the screen allow you to see and clear individual active filters with one tap, or tap 'Reset All'.")
        ]
    },
    {
        "title": "4. Interactive Map Discovery",
        "content": [
            ("heading2", "4.1 Map Features & Gestures"),
            ("p", "The Interactive Map provides visual spatial discovery across the Philippine archipelago:"),
            ("bullet", "Fluid Navigation: Pan across islands and cities with single-finger drag. Pinch with two fingers to zoom in and out smoothly."),
            ("bullet", "Location Presets (Fly-To): Quick buttons allow instant jump to Cebu City, Bohol, or Panglao Island."),
            ("bullet", "Search Visible Map Area: Tap 'Search this map area' to filter listings exclusively within the bounds of your currently visible screen."),
            ("bullet", "Radius Distance Search: Search properties within 1 km, 5 km, 10 km, or 25 km of any target location."),
            ("bullet", "Interactive Property Markers: Tap any price pin or cluster to view a pop-up preview card with rent, title, verification badge, and direct link to property details.")
        ]
    },
    {
        "title": "5. Property Details & Inquiries",
        "content": [
            ("heading2", "5.1 Complete Cost Transparency"),
            ("p", "Every listing provides an itemized cost schedule to eliminate hidden move-in surprises:"),
            ("bullet", "Monthly Rent: Base recurring monthly rental fee."),
            ("bullet", "Association & Condo Dues: Explicitly listed as included or separate monthly amount."),
            ("bullet", "Advance Rent: Number of months required upfront (typically 1 month)."),
            ("bullet", "Security Deposit: Refundable deposit months (typically 2 months)."),
            ("bullet", "Estimated First Month Out-of-Pocket Cost: Automatically calculated total to prepare tenant budget accurately."),
            ("heading2", "5.2 Verification Badges & Poster Identity"),
            ("p", "Tenants can review the poster's relationship to the property (e.g. 'Direct Owner', 'Family Member (Authorized)', 'Licensed Broker (PRC #0028192)') along with their verification level badge."),
            ("heading2", "5.3 Viewing Requests & In-App Messaging"),
            ("bullet", "Schedule In-Person Viewing: Select desired viewing date, time slot, and add notes. Host receives instant in-app and email notifications."),
            ("bullet", "Direct In-App Chat: Send questions directly to the landlord or broker within the secure chat thread. Property details are attached to the conversation header.")
        ]
    },
    {
        "title": "6. Landlord & Host Features (Posting & Management)",
        "content": [
            ("heading2", "6.1 6-Step Property Posting Wizard"),
            ("p", "Property owners and authorized representatives can publish listings via the Post Property flow:"),
            ("bullet", "Step 1: Poster Relationship & Authority: Specify whether you are the direct owner, family member, caretaker, or broker. State if you possess legal authorization."),
            ("bullet", "Step 2: Basic Details: Title, detailed description, property type, monthly rent, and association dues."),
            ("bullet", "Step 3: Geographic Location: Select Province, City/Municipality, Barangay, street address, and privacy level (Exact, Approximate, or Hidden)."),
            ("bullet", "Step 4: Amenities & Policies: Specify bedrooms, bathrooms, floor area (sqm), furnishing status, parking availability, and pet policy."),
            ("bullet", "Step 5: Photos: Add high-resolution property image URLs."),
            ("bullet", "Step 6: Review & Publish: Confirm listing details and publish immediately to Room local database and Supabase cloud."),
            ("heading2", "6.2 Viewing Requests Management"),
            ("p", "Hosts can review pending tenant viewing requests, confirm appointment dates, suggest alternative times, or mark viewings as completed.")
        ]
    },
    {
        "title": "7. Verification Center & Trust Levels",
        "content": [
            ("heading2", "7.1 The 8-Level Verification Framework"),
            ("p", "Balay-Inn implements a comprehensive multi-tier trust ladder:"),
            ("bullet", "Level 1: Phone Verified (SMS OTP confirmation)."),
            ("bullet", "Level 2: Email Verified (Cryptographic email link validation)."),
            ("bullet", "Level 3: Government ID Verified (PhilSys National ID, Philippine Passport, Driver's License, or UMID)."),
            ("bullet", "Level 4: Biometric Liveness Check (Facial biometric matching)."),
            ("bullet", "Level 5: Proof of Ownership / Authority (Transfer Certificate of Title, Tax Declaration, or Special Power of Attorney)."),
            ("bullet", "Level 6: Professional Broker Accreditation (PRC Real Estate Broker ID and license validation)."),
            ("bullet", "Level 7: On-Site Physical Inspection (Physical inspection and GPS photo confirmation by Balay-Inn staff)."),
            ("bullet", "Level 8: Certified Trusted Partner (Verified track record of 10+ completed leases with positive tenant reviews).")
        ]
    },
    {
        "title": "8. Dashboard & Account Management",
        "content": [
            ("heading2", "8.1 Dashboard Capabilities"),
            ("p", "The Dashboard serves as the central operational hub:"),
            ("bullet", "Profile Summary: Displays user role, current verification level, and account status."),
            ("bullet", "Quick Analytics: Real-time counts of active listings, saved favorites, viewing inquiries, and conversations."),
            ("bullet", "Appearance Mode: Toggle between dynamic Light Mode and high-contrast Dark Mode."),
            ("bullet", "Supabase Cloud Sync: Real-time cloud synchronization status indicator with manual 'Sync Now' action."),
            ("bullet", "Notification Center: Read, review, and clear system notifications and booking alerts."),
            ("bullet", "Account Session & Log Out: Located at the lower section of the dashboard page. Tapping 'Log Out' triggers a confirmation dialog that clears active sessions and tokens, safely returning to the Login screen.")
        ]
    },
    {
        "title": "9. Technical Architecture & Support",
        "content": [
            ("heading2", "9.1 Tech Stack"),
            ("bullet", "UI Framework: Jetpack Compose with Material Design 3 (M3)."),
            ("bullet", "Language: 100% Kotlin with Coroutines and StateFlow."),
            ("bullet", "Local Persistence: Android Room SQLite database with destructive migration fallback and automated seeding."),
            ("bullet", "Cloud Backend: Supabase PostgreSQL (PostgREST API, Row Level Security, Supabase Auth)."),
            ("bullet", "Networking: Retrofit 2 with Moshi JSON serializer and ISO-8601 timestamp adapters."),
            ("bullet", "Image Loading: Coil Compose for asynchronous image rendering and caching."),
            ("heading2", "9.2 Frequently Asked Questions (FAQ)"),
            ("p", "Q: Can I use the app without internet connectivity?"),
            ("p", "A: Yes. Balay-Inn is built with an offline-first architecture using Room database. All cached listings, favorites, and geographic data remain fully accessible without internet. Once connectivity is restored, data synchronizes automatically with Supabase."),
            ("p", "Q: How do I know if a listing is posted by the real owner?"),
            ("p", "A: Check the 'Listed By' label and verification badge on the property card or detail page. Listings tagged 'Direct Owner' with Level 5+ verification have uploaded official Title deeds or Tax declarations."),
            ("p", "Q: Where is the log out button located?"),
            ("p", "A: Navigate to the Dashboard tab (bottom navigation bar) and scroll to the lower dashboard section to find the 'Account Session' card and 'Log Out' button.")
        ]
    }
]

def generate_markdown(output_path):
    """Generates a complete markdown user guide."""
    lines = [
        f"# {GUIDE_TITLE}",
        f"**{GUIDE_SUBTITLE}**  ",
        f"*{VERSION}* | *{PLATFORM}*\n",
        "---\n",
        "## Table of Contents"
    ]
    for section in SECTIONS:
        lines.append(f"- [{section['title']}](#{section['title'].lower().replace(' ', '-').replace('.', '').replace('&', '').replace('(', '').replace(')', '')})")
    lines.append("\n---\n")

    for section in SECTIONS:
        lines.append(f"## {section['title']}\n")
        for item_type, text in section["content"]:
            if item_type == "heading2":
                lines.append(f"### {text}\n")
            elif item_type == "p":
                lines.append(f"{text}\n")
            elif item_type == "bullet":
                lines.append(f"- {text}")
        lines.append("\n")

    content = "\n".join(lines)
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Generated Markdown guide: {output_path} ({len(content)} bytes)")


def generate_docx(output_path):
    """Generates a valid Microsoft Word (.docx) document using OpenXML."""
    # Build document.xml
    doc_body = []
    
    # Title
    doc_body.append("""
    <w:p>
      <w:pPr>
        <w:jc w:val="center"/>
        <w:spacing w:before="240" w:after="120"/>
      </w:pPr>
      <w:r>
        <w:rPr>
          <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
          <w:b/>
          <w:sz w:val="52"/>
          <w:color w:val="1E3A8A"/>
        </w:rPr>
        <w:t>""" + escape(GUIDE_TITLE) + """</w:t>
      </w:r>
    </w:p>
    """)

    # Subtitle
    doc_body.append("""
    <w:p>
      <w:pPr>
        <w:jc w:val="center"/>
        <w:spacing w:before="0" w:after="240"/>
      </w:pPr>
      <w:r>
        <w:rPr>
          <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
          <w:i/>
          <w:sz w:val="26"/>
          <w:color w:val="4B5563"/>
        </w:rPr>
        <w:t>""" + escape(GUIDE_SUBTITLE) + """</w:t>
      </w:r>
    </w:p>
    """)

    # Metadata callout
    doc_body.append("""
    <w:p>
      <w:pPr>
        <w:jc w:val="center"/>
        <w:spacing w:before="0" w:after="480"/>
      </w:pPr>
      <w:r>
        <w:rPr>
          <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
          <w:b/>
          <w:sz w:val="22"/>
          <w:color w:val="059669"/>
        </w:rPr>
        <w:t>""" + escape(f"{VERSION} • {PLATFORM}") + """</w:t>
      </w:r>
    </w:p>
    """)

    for section in SECTIONS:
        # Heading 1
        doc_body.append("""
        <w:p>
          <w:pPr>
            <w:spacing w:before="360" w:after="120"/>
          </w:pPr>
          <w:r>
            <w:rPr>
              <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
              <w:b/>
              <w:sz w:val="34"/>
              <w:color w:val="1E3A8A"/>
            </w:rPr>
            <w:t>""" + escape(section["title"]) + """</w:t>
          </w:r>
        </w:p>
        """)

        for item_type, text in section["content"]:
            if item_type == "heading2":
                doc_body.append("""
                <w:p>
                  <w:pPr>
                    <w:spacing w:before="240" w:after="80"/>
                  </w:pPr>
                  <w:r>
                    <w:rPr>
                      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
                      <w:b/>
                      <w:sz w:val="26"/>
                      <w:color w:val="2563EB"/>
                    </w:rPr>
                    <w:t>""" + escape(text) + """</w:t>
                  </w:r>
                </w:p>
                """)
            elif item_type == "p":
                doc_body.append("""
                <w:p>
                  <w:pPr>
                    <w:spacing w:before="0" w:after="120"/>
                    <w:line w:line="280" w:lineRule="exact"/>
                  </w:pPr>
                  <w:r>
                    <w:rPr>
                      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
                      <w:sz w:val="22"/>
                      <w:color w:val="1F2937"/>
                    </w:rPr>
                    <w:t>""" + escape(text) + """</w:t>
                  </w:r>
                </w:p>
                """)
            elif item_type == "bullet":
                doc_body.append("""
                <w:p>
                  <w:pPr>
                    <w:ind w:left="480" w:hanging="240"/>
                    <w:spacing w:before="0" w:after="80"/>
                  </w:pPr>
                  <w:r>
                    <w:rPr>
                      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
                      <w:b/>
                      <w:color w:val="2563EB"/>
                    </w:rPr>
                    <w:t>• </w:t>
                  </w:r>
                  <w:r>
                    <w:rPr>
                      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
                      <w:sz w:val="22"/>
                      <w:color w:val="374151"/>
                    </w:rPr>
                    <w:t>""" + escape(text) + """</w:t>
                  </w:r>
                </w:p>
                """)

    document_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
      <w:body>
        {''.join(doc_body)}
        <w:sectPr>
          <w:pgSz w:w="12240" w:h="15840"/>
          <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440"/>
        </w:sectPr>
      </w:body>
    </w:document>
    """

    content_types_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
      <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
      <Default Extension="xml" ContentType="application/xml"/>
      <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
      <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
    </Types>
    """

    rels_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
      <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
    </Relationships>
    """

    document_rels_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
      <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
    </Relationships>
    """

    styles_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
      <w:docDefaults>
        <w:rPrDefault>
          <w:rPr>
            <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
            <w:sz w:val="22"/>
          </w:rPr>
        </w:rPrDefault>
      </w:docDefaults>
    </w:styles>
    """

    with zipfile.ZipFile(output_path, "w", zipfile.ZIP_DEFLATED) as docx:
        docx.writestr("[Content_Types].xml", content_types_xml)
        docx.writestr("_rels/.rels", rels_xml)
        docx.writestr("word/_rels/document.xml.rels", document_rels_xml)
        docx.writestr("word/document.xml", document_xml)
        docx.writestr("word/styles.xml", styles_xml)

    print(f"Generated Word document: {output_path} ({os.path.getsize(output_path)} bytes)")


def generate_ps_and_pdf(output_ps_path, output_pdf_path):
    """Generates standard PostScript DSC document and converts to PDF via ps2pdf."""
    PAGE_WIDTH = 612
    PAGE_HEIGHT = 792
    LEFT_MARGIN = 54
    RIGHT_MARGIN = 558
    TOP_MARGIN = 720
    BOTTOM_MARGIN = 54
    USABLE_WIDTH = RIGHT_MARGIN - LEFT_MARGIN

    pages = []
    current_page_commands = []
    y = TOP_MARGIN

    def new_page():
        nonlocal current_page_commands, y
        if current_page_commands:
            pages.append(current_page_commands)
        current_page_commands = []
        y = TOP_MARGIN

    def draw_header_footer(page_num, total_pages_str=""):
        cmds = []
        # Header rule
        cmds.append(f"""
        0.85 0.85 0.85 setrgbcolor
        1 setlinewidth
        {LEFT_MARGIN} 745 moveto {RIGHT_MARGIN} 745 lineto stroke
        /DejaVuSans findfont 8 scalefont setfont
        0.4 0.4 0.4 setrgbcolor
        {LEFT_MARGIN} 750 moveto (Balay-Inn Rental Marketplace • User Operational Guide) show
        """)
        # Footer rule & page number
        cmds.append(f"""
        0.85 0.85 0.85 setrgbcolor
        1 setlinewidth
        {LEFT_MARGIN} 45 moveto {RIGHT_MARGIN} 45 lineto stroke
        /DejaVuSans findfont 8 scalefont setfont
        0.4 0.4 0.4 setrgbcolor
        {LEFT_MARGIN} 35 moveto (Confidential & Proprietary • Balay-Inn Documentation) show
        {RIGHT_MARGIN - 60} 35 moveto (Page {page_num}) show
        """)
        return "".join(cmds)

    def wrap_text(text, max_chars):
        words = text.split()
        lines = []
        cur = []
        cur_len = 0
        for w in words:
            if cur_len + len(w) + 1 <= max_chars:
                cur.append(w)
                cur_len += len(w) + 1
            else:
                if cur:
                    lines.append(" ".join(cur))
                cur = [w]
                cur_len = len(w)
        if cur:
            lines.append(" ".join(cur))
        return lines

    def ps_escape(s):
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")

    # Title Banner on first page
    current_page_commands.append(f"""
    0.1 0.25 0.6 setrgbcolor
    {LEFT_MARGIN} 650 {USABLE_WIDTH} 70 rectfill
    0.95 0.95 0.95 setrgbcolor
    /DejaVuSans-Bold findfont 16 scalefont setfont
    {LEFT_MARGIN + 16} 695 moveto (BALAY-INN USER GUIDE & MANUAL) show
    /DejaVuSans findfont 9.5 scalefont setfont
    0.85 0.9 1 setrgbcolor
    {LEFT_MARGIN + 16} 675 moveto (Rental Housing Marketplace & Transparency Platform • Philippines) show
    {LEFT_MARGIN + 16} 660 moveto ({ps_escape(VERSION)}) show
    """)
    y = 620

    for section in SECTIONS:
        # Check space for Section Heading
        if y < BOTTOM_MARGIN + 100:
            new_page()

        # Section Heading
        current_page_commands.append(f"""
        0.12 0.25 0.55 setrgbcolor
        /DejaVuSans-Bold findfont 13 scalefont setfont
        {LEFT_MARGIN} {y} moveto ({ps_escape(section['title'])}) show
        0.2 0.4 0.8 setrgbcolor
        1.5 setlinewidth
        {LEFT_MARGIN} {y - 4} moveto {RIGHT_MARGIN} {y - 4} lineto stroke
        """)
        y -= 22

        for item_type, text in section["content"]:
            if item_type == "heading2":
                if y < BOTTOM_MARGIN + 60:
                    new_page()
                y -= 6
                current_page_commands.append(f"""
                0.15 0.35 0.75 setrgbcolor
                /DejaVuSans-Bold findfont 10.5 scalefont setfont
                {LEFT_MARGIN} {y} moveto ({ps_escape(text)}) show
                """)
                y -= 15
            elif item_type == "p":
                lines = wrap_text(text, 92)
                for line in lines:
                    if y < BOTTOM_MARGIN + 30:
                        new_page()
                    current_page_commands.append(f"""
                    0.15 0.15 0.15 setrgbcolor
                    /DejaVuSans findfont 9.5 scalefont setfont
                    {LEFT_MARGIN} {y} moveto ({ps_escape(line)}) show
                    """)
                    y -= 13
                y -= 4
            elif item_type == "bullet":
                lines = wrap_text(text, 86)
                if y < BOTTOM_MARGIN + 30:
                    new_page()
                # Bullet dot
                current_page_commands.append(f"""
                0.2 0.4 0.85 setrgbcolor
                {LEFT_MARGIN + 8} {y + 3} 2.5 0 360 arc fill
                """)
                first_line = True
                for line in lines:
                    if y < BOTTOM_MARGIN + 30:
                        new_page()
                    indent = LEFT_MARGIN + 18
                    current_page_commands.append(f"""
                    0.2 0.2 0.2 setrgbcolor
                    /DejaVuSans findfont 9.5 scalefont setfont
                    {indent} {y} moveto ({ps_escape(line)}) show
                    """)
                    y -= 13
                y -= 2
        y -= 10

    if current_page_commands:
        pages.append(current_page_commands)

    # Now write the full PostScript file
    total_pages = len(pages)
    ps_lines = [
        "%!PS-Adobe-3.0",
        f"%%Title: {GUIDE_TITLE}",
        f"%%Creator: Balay-Inn Documentation Engine",
        f"%%Pages: {total_pages}",
        "%%BoundingBox: 0 0 612 792",
        "%%EndComments\n"
    ]

    for p_idx, p_cmds in enumerate(pages, 1):
        ps_lines.append(f"%%Page: {p_idx} {p_idx}")
        ps_lines.append("gsave")
        ps_lines.append(draw_header_footer(p_idx, str(total_pages)))
        ps_lines.extend(p_cmds)
        ps_lines.append("grestore")
        ps_lines.append("showpage\n")

    ps_lines.append("%%EOF")
    ps_content = "\n".join(ps_lines)
    with open(output_ps_path, "w", encoding="utf-8") as f:
        f.write(ps_content)

    print(f"Generated PostScript: {output_ps_path} ({len(ps_content)} bytes)")

    # Run ps2pdf to generate high quality PDF
    cmd = ["ps2pdf", "-dPDFSETTINGS=/prepress", "-sPAPERSIZE=letter", output_ps_path, output_pdf_path]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode == 0 and os.path.exists(output_pdf_path):
        print(f"Generated PDF: {output_pdf_path} ({os.path.getsize(output_pdf_path)} bytes)")
    else:
        print(f"Error running ps2pdf: {res.stderr}")


if __name__ == "__main__":
    workspace_root = sys.argv[1] if len(sys.argv) > 1 else "."
    
    md_path = os.path.join(workspace_root, "USER_GUIDE.md")
    docx_path = os.path.join(workspace_root, "Balay_Inn_User_Guide.docx")
    ps_path = os.path.join(workspace_root, "Balay_Inn_User_Guide.ps")
    pdf_path = os.path.join(workspace_root, "Balay_Inn_User_Guide.pdf")

    # Also place in app/src/main/assets for in-app viewing if desired
    assets_dir = os.path.join(workspace_root, "app/src/main/assets")
    os.makedirs(assets_dir, exist_ok=True)
    assets_md = os.path.join(assets_dir, "USER_GUIDE.md")

    generate_markdown(md_path)
    generate_markdown(assets_md)
    generate_docx(docx_path)
    generate_ps_and_pdf(ps_path, pdf_path)
    print("All documents generated successfully!")
