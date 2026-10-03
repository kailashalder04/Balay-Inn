#!/usr/bin/env python3
"""
Balay-Inn User Manual & Support Document Generator
Generates:
1. BALAY_INN_USER_MANUAL.docx (Valid OpenXML Microsoft Word format)
2. BALAY_INN_USER_MANUAL.pdf (via ps2pdf or direct PDF)
3. USER_GUIDE_AND_SUPPORT_DOCUMENT.md (Markdown format)
"""

import os
import zipfile
import xml.sax.saxutils as saxutils

def escape_xml(text):
    return saxutils.escape(text)

def generate_word_document(docx_path):
    # XML strings for Word OpenXML
    content_types = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>"""

    root_rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""

    doc_rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""

    styles_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:docDefaults>
    <w:rPrDefault>
      <w:rPr>
        <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri" w:cs="Calibri"/>
        <w:sz w:val="22"/>
        <w:szCs w:val="22"/>
        <w:lang w:val="en-US"/>
      </w:rPr>
    </w:rPrDefault>
    <w:pPrDefault>
      <w:pPr>
        <w:spacing w:after="160" w:line="240" w:lineRule="auto"/>
      </w:pPr>
    </w:pPrDefault>
  </w:docDefaults>
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal">
    <w:name w:val="Normal"/>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Title">
    <w:name w:val="Title"/>
    <w:pPr>
      <w:spacing w:before="240" w:after="240"/>
      <w:jc w:val="center"/>
    </w:pPr>
    <w:rPr>
      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
      <w:b/>
      <w:color w:val="0D9488"/>
      <w:sz w:val="56"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Subtitle">
    <w:name w:val="Subtitle"/>
    <w:pPr>
      <w:spacing w:after="300"/>
      <w:jc w:val="center"/>
    </w:pPr>
    <w:rPr>
      <w:color w:val="4B5563"/>
      <w:sz w:val="28"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading1">
    <w:name w:val="heading 1"/>
    <w:pPr>
      <w:spacing w:before="360" w:after="160"/>
    </w:pPr>
    <w:rPr>
      <w:b/>
      <w:color w:val="0F766E"/>
      <w:sz w:val="36"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading2">
    <w:name w:val="heading 2"/>
    <w:pPr>
      <w:spacing w:before="240" w:after="120"/>
    </w:pPr>
    <w:rPr>
      <w:b/>
      <w:color w:val="111827"/>
      <w:sz w:val="28"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading3">
    <w:name w:val="heading 3"/>
    <w:pPr>
      <w:spacing w:before="180" w:after="80"/>
    </w:pPr>
    <w:rPr>
      <w:b/>
      <w:color w:val="374151"/>
      <w:sz w:val="24"/>
    </w:rPr>
  </w:style>
</w:styles>"""

    sections = [
        ("title", "Balay-Inn Rental Marketplace"),
        ("subtitle", "Official App User Manual & Comprehensive Support Documentation"),
        ("meta", "Version 2.4 • Platform: Android (Jetpack Compose & Kotlin) • Backend: Offline SQLite & Supabase PostgreSQL"),
        
        ("h1", "1. Executive Summary & Application Overview"),
        ("p", "Balay-Inn is a next-generation real estate rental marketplace designed specifically for the Philippine rental housing ecosystem. The application connects verified tenants, direct property owners, licensed real estate brokers, property managers, caretakers, and authorized family representatives."),
        ("p", "Key architectural highlights include:"),
        ("bullet", "Cascading Philippine Geographic Hierarchy: Filter rentals strictly across Country -> Region -> Province -> City/Municipality -> Barangay with PSGC-standard data."),
        ("bullet", "Interactive Pinch-to-Zoom Map: Explore rentals visually with interactive clusters, custom price pins, and 'Search this area' map bounds detection."),
        ("bullet", "Role-Based Authority & 'Listed By' Filtering: Distinguish direct owners from licensed brokers and authorized representatives with mandatory proof-of-authority verification."),
        ("bullet", "End-to-End Rental Journey: Schedule in-person property viewings, communicate via built-in real-time chat, dispatch automated Gmail notifications, and manage tenancy contracts."),
        ("bullet", "Dual Offline-First & Cloud Sync Architecture: Powered by Android Room SQLite locally and Supabase PostgreSQL with Row Level Security in the cloud."),

        ("h1", "2. Getting Started & User Roles"),
        ("p", "Balay-Inn features a flexible, role-based experience tailored to each participant:"),
        ("bullet", "Tenant: Browse rentals, apply granular filters, bookmark favorites, request physical viewings, chat with hosts, and submit requirements."),
        ("bullet", "Direct Property Owner: List rental homes, condos, and apartments, upload proof of ownership (Transfer Certificate of Title / Tax Declaration), manage viewing requests, and update property availability."),
        ("bullet", "Licensed Real Estate Broker / Agent: Post verified inventory on behalf of clients with PRC Broker License ID and professional credentials."),
        ("bullet", "Property Manager & Caretaker: Administer multi-unit buildings, coordinate tenant maintenance requests, and schedule viewings."),
        ("bullet", "Authorized Family Member / Representative: Post properties with notarized Special Power of Attorney (SPA) documentation."),
        ("bullet", "Platform Administrator: Review submitted verification documents, inspect audit logs, approve poster credentials, and manage system health."),

        ("h1", "3. Exploring & Searching Rental Properties"),
        ("h2", "3.1 Compact Search Bar & Quick Search"),
        ("p", "Located at the top of the Explore Screen is a streamlined 40dp search bar designed for rapid keyword queries. As you type, listings are filtered in real-time by title, location name, landmark, or description. A clear ('X') button allows instant query resetting."),
        
        ("h2", "3.2 View Mode Toggle (Map vs. List)"),
        ("p", "Directly beside the search bar is the View Mode Toggle button (40dp x 40dp). Tapping this toggles between the full-featured Card List View and the Interactive Vector Map View:"),
        ("bullet", "List View: Displays comprehensive property cards with high-resolution image galleries, monthly rent (₱), bedroom/bathroom specs, verification badges, and instant bookmarking."),
        ("bullet", "Map View: Shows real-time property markers on the Philippine archipelago. Tap any pin to preview property details or tap 'Search this area' to query listings within the visible viewport."),

        ("h2", "3.3 Comprehensive Filter Modal Bottom Sheet"),
        ("p", "Tap the Filter button beside the search box to open the full filter panel. The filter suite includes:"),
        ("bullet", "Geographic Cascading Hierarchy: Select Province (e.g., Cebu, Metro Manila, Bohol), City/Municipality (e.g., Cebu City, Mandaue, Tagbilaran), and Barangay for pinpoint accuracy."),
        ("bullet", "Property Type: Choose from Studio, Apartment, Condominium, House, Room, or Townhouse."),
        ("bullet", "Listed By Filter (NEW): Filter listings specifically by poster relationship: All, Direct Owner, Licensed Broker, Property Manager, Caretaker, Family Member, or Authorized Representative."),
        ("bullet", "Authorized Posters Only (NEW): A dedicated toggle switch that isolates listings uploaded exclusively by verified direct owners or authorized agents with verified credentials."),
        ("bullet", "Monthly Rent Budget Slider: Set minimum and maximum monthly rental thresholds from ₱0 up to ₱100,000+ with instant price recalculation."),
        ("bullet", "Specifications & Amenities: Filter by number of bedrooms, furnished status (Furnished, Semi-Furnished, Unfurnished), dedicated parking spaces, and pet-friendly policies."),
        ("bullet", "Verification Level: Restrict results to high-trust verified properties (Level 3+ with verified proof of ownership)."),

        ("h1", "4. Posting a Rental Property (Host Flow)"),
        ("p", "To publish a new rental listing, navigate to the 'Post' tab in the bottom navigation bar. The wizard guides you through 5 clear steps:"),
        ("bullet", "Step 1: Listing Basics & Authority: Provide the property title, rental category, monthly rate, security deposit, and select your relationship to the property (Direct Owner, Broker, Caretaker, etc.)."),
        ("bullet", "Step 2: Geographic Hierarchy: Choose the Country, Province, City, Barangay, and street address. The app automatically assigns accurate latitude/longitude coordinates for map rendering."),
        ("bullet", "Step 3: Specifications & Amenities: Specify floor area (sqm), bedrooms, bathrooms, furnishing, parking slots, utility provisions (WiFi, generator, water tank), and pet policy."),
        ("bullet", "Step 4: Photo Gallery: Select and upload property photos using the system photo picker or camera. High-resolution imagery increases tenant inquiry rates by over 300%."),
        ("bullet", "Step 5: Verification & Proof of Authority: Upload a scanned copy or photo of the Transfer Certificate of Title (TCT), Tax Declaration, or notarized Special Power of Attorney (SPA). This elevates your listing to Verified Status (Level 3 or 4)."),

        ("h1", "5. Scheduling Property Viewings & Notifications"),
        ("h2", "5.1 Booking a Viewing (Tenant)"),
        ("p", "From any Property Detail screen, tap 'Schedule Viewing'. Select your desired date and preferred time slot (e.g., Morning 9:00 AM, Afternoon 2:00 PM). Add an optional note to the landlord regarding any specific questions. Tap 'Confirm Request'."),
        
        ("h2", "5.2 Host Action & Automated Alerts (Landlord)"),
        ("p", "The host receives an instant in-app notification and an automated email alert sent via Gmail / Supabase Edge Functions. The host can Accept, Reschedule, or Decline the viewing request directly from their Viewings tab or Dashboard."),
        
        ("h2", "5.3 Viewing Status Management"),
        ("p", "Viewing requests transition through standardized lifecycle states: Requested -> Confirmed -> Completed / Cancelled. Both parties can view updated schedules in their respective Viewings tabs."),

        ("h1", "6. In-App Messaging & Real-Time Chat"),
        ("p", "Balay-Inn includes a dedicated peer-to-peer messaging system:"),
        ("bullet", "Initiate Conversation: Tap 'Chat with Host' from any listing to start an instant thread."),
        ("bullet", "Contextual Property Card: The listing title, photo, and rent are pinned at the top of the chat for immediate context."),
        ("bullet", "Real-Time Updates: Messages are saved locally in SQLite and synchronized across devices when connected to Supabase."),

        ("h1", "7. Lower Dashboard, Account Session & Logging Out"),
        ("h2", "7.1 Dashboard Hub Navigation"),
        ("p", "Tap the 'Dashboard' tab (Account icon) in the bottom navigation bar to access the user control center. The dashboard contains:"),
        ("bullet", "User Profile Card: Displays your name, email, verified role, and verification badge status."),
        ("bullet", "Property Management & Stats: View total views, total saves, active listings, and viewing requests."),
        ("bullet", "Notification Center: Review incoming booking alerts, system notifications, and mark alerts as read."),
        ("bullet", "Cloud Sync & Diagnostic Tools: Run real-time cloud synchronizations with Supabase PostgreSQL and test Gmail dispatcher alerts."),

        ("h2", "7.2 Lower Dashboard: Account Session & Log Out Button"),
        ("p", "At the very bottom of the Dashboard page is the dedicated 'Account Session' section:"),
        ("bullet", "Session Identity: Clearly displays 'Signed in as [Your Name] ([Your Email])'."),
        ("bullet", "Prominent Log Out Button: Highlighted in prominent red with an logout icon. Tapping 'Log Out' displays a security confirmation dialog asking 'Log Out of Balay-Inn?' to prevent accidental sign-outs."),
        ("bullet", "Secure Session Termination: Confirming logout safely terminates active tokens, clears cached credentials, resets role states, and redirects the user back to the Login & Welcome screen."),

        ("h1", "8. Frequently Asked Questions (FAQ) & Troubleshooting"),
        ("h2", "Q: How do I filter properties posted only by direct owners?"),
        ("p", "A: Open the Explore tab, tap the Filter button beside the search box, scroll down to the 'Listed By' section, and tap the 'Direct Owner' chip. You can also turn on the 'Authorized Posters Only' toggle switch. Then tap 'Apply Filters'."),
        
        ("h2", "Q: Why is my listing marked as 'Pending' or 'Unverified'?"),
        ("p", "A: Newly created listings remain pending verification until proof-of-ownership documents (TCT or SPA) are uploaded and verified. Upload documents via the Verification Center to unlock Level 3/4 Verified Badges."),

        ("h2", "Q: Can I use the application when offline?"),
        ("p", "A: Yes. Balay-Inn is architected offline-first. All cached properties, favorites, and pending viewings are stored safely in local SQLite Room storage and will automatically synchronize when internet access is restored."),

        ("h2", "Q: Where is the Log Out button located?"),
        ("p", "A: Tap the 'Dashboard' tab on the bottom navigation bar, scroll to the lower bottom of the page to find the 'Account Session' card, and tap the red 'Log Out' button."),

        ("h1", "9. Support & Contact Information"),
        ("p", "For technical support, account inquiries, or bug reports:"),
        ("bullet", "Email Support: support@balay-inn.ph / kailashalder04@gmail.com"),
        ("bullet", "Verification Helpdesk: verification@balay-inn.ph"),
        ("bullet", "Official Repository: Google AI Studio Balay-Inn Project Build"),
        ("p", "Thank you for choosing Balay-Inn — The Premier Rental Marketplace Platform!")
    ]

    # Generate document.xml
    body_xml_parts = []
    for item_type, text in sections:
        safe_text = escape_xml(text)
        if item_type == "title":
            body_xml_parts.append(f"""<w:p><w:pPr><w:pStyle w:val="Title"/></w:pPr><w:r><w:t>{safe_text}</w:t></w:r></w:p>""")
        elif item_type == "subtitle":
            body_xml_parts.append(f"""<w:p><w:pPr><w:pStyle w:val="Subtitle"/></w:pPr><w:r><w:t>{safe_text}</w:t></w:r></w:p>""")
        elif item_type == "meta":
            body_xml_parts.append(f"""<w:p><w:pPr><w:jc w:val="center"/><w:spacing w:after="300"/></w:pPr><w:r><w:rPr><w:i/><w:color w:val="6B7280"/><w:sz w:val="20"/></w:rPr><w:t>{safe_text}</w:t></w:r></w:p>""")
        elif item_type == "h1":
            body_xml_parts.append(f"""<w:p><w:pPr><w:pStyle w:val="Heading1"/></w:pPr><w:r><w:t>{safe_text}</w:t></w:r></w:p>""")
        elif item_type == "h2":
            body_xml_parts.append(f"""<w:p><w:pPr><w:pStyle w:val="Heading2"/></w:pPr><w:r><w:t>{safe_text}</w:t></w:r></w:p>""")
        elif item_type == "h3":
            body_xml_parts.append(f"""<w:p><w:pPr><w:pStyle w:val="Heading3"/></w:pPr><w:r><w:t>{safe_text}</w:t></w:r></w:p>""")
        elif item_type == "bullet":
            body_xml_parts.append(f"""<w:p><w:pPr><w:ind w:left="480" w:hanging="240"/><w:spacing w:after="100"/></w:pPr><w:r><w:rPr><w:color w:val="0D9488"/><w:b/></w:rPr><w:t>• </w:t></w:r><w:r><w:t>{safe_text}</w:t></w:r></w:p>""")
        elif item_type == "p":
            body_xml_parts.append(f"""<w:p><w:pPr><w:spacing w:after="140"/></w:pPr><w:r><w:t>{safe_text}</w:t></w:r></w:p>""")

    document_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    {"".join(body_xml_parts)}
    <w:sectPr>
      <w:pgSz w:w="12240" w:h="15840"/>
      <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="720" w:footer="720" w:gutter="0"/>
    </w:sectPr>
  </w:body>
</w:document>"""

    with zipfile.ZipFile(docx_path, 'w', zipfile.ZIP_DEFLATED) as docx:
        docx.writestr('[Content_Types].xml', content_types)
        docx.writestr('_rels/.rels', root_rels)
        docx.writestr('word/_rels/document.xml.rels', doc_rels)
        docx.writestr('word/styles.xml', styles_xml)
        docx.writestr('word/document.xml', document_xml)

    print(f"Word Document created successfully at: {docx_path}")

def generate_markdown(md_path):
    content = """# Balay-Inn Rental Marketplace
## Official App User Manual & Comprehensive Support Documentation
*Version 2.4 • Platform: Android (Jetpack Compose & Kotlin) • Backend: Room SQLite & Supabase PostgreSQL*

---

## 1. Executive Summary & Application Overview
Balay-Inn is a next-generation real estate rental marketplace designed specifically for the Philippine rental housing ecosystem. The application connects verified tenants, direct property owners, licensed real estate brokers, property managers, caretakers, and authorized family representatives.

### Key Architectural Highlights:
- **Cascading Philippine Geographic Hierarchy**: Filter rentals strictly across Country -> Region -> Province -> City/Municipality -> Barangay using PSGC standard data.
- **Interactive Pinch-to-Zoom Map**: Explore rentals visually with interactive clusters, custom price pins, and 'Search this area' map bounds detection.
- **Role-Based Authority & 'Listed By' Filtering**: Distinguish direct owners from licensed brokers and authorized representatives with mandatory proof-of-authority verification.
- **End-to-End Rental Journey**: Schedule in-person property viewings, communicate via built-in real-time chat, dispatch automated Gmail notifications, and manage tenancy contracts.
- **Dual Offline-First & Cloud Sync Architecture**: Powered by Android Room SQLite locally and Supabase PostgreSQL with Row Level Security in the cloud.

---

## 2. Getting Started & User Roles
Balay-Inn features a flexible, role-based experience tailored to each participant:
- **Tenant**: Browse rentals, apply granular filters, bookmark favorites, request physical viewings, chat with hosts, and submit requirements.
- **Direct Property Owner**: List rental homes, condos, and apartments, upload proof of ownership (Transfer Certificate of Title / Tax Declaration), manage viewing requests, and update property availability.
- **Licensed Real Estate Broker / Agent**: Post verified inventory on behalf of clients with PRC Broker License ID and professional credentials.
- **Property Manager & Caretaker**: Administer multi-unit buildings, coordinate tenant maintenance requests, and schedule viewings.
- **Authorized Family Member / Representative**: Post properties with notarized Special Power of Attorney (SPA) documentation.
- **Platform Administrator**: Review submitted verification documents, inspect audit logs, approve poster credentials, and manage system health.

---

## 3. Exploring & Searching Rental Properties

### 3.1 Compact Search Bar & Quick Search
Located at the top of the Explore Screen is a streamlined 40dp search bar designed for rapid keyword queries. As you type, listings are filtered in real-time by title, location name, landmark, or description. A clear ('X') button allows instant query resetting.

### 3.2 View Mode Toggle (Map vs. List)
Directly beside the search bar is the View Mode Toggle button (40dp x 40dp). Tapping this toggles between the full-featured Card List View and the Interactive Vector Map View:
- **List View**: Displays comprehensive property cards with high-resolution image galleries, monthly rent (₱), bedroom/bathroom specs, verification badges, and instant bookmarking.
- **Map View**: Shows real-time property markers on the Philippine archipelago. Tap any pin to preview property details or tap 'Search this area' to query listings within the visible viewport.

### 3.3 Comprehensive Filter Modal Bottom Sheet
Tap the Filter button beside the search box to open the full filter panel:
- **Geographic Cascading Hierarchy**: Select Province (e.g., Cebu, Metro Manila, Bohol), City/Municipality (e.g., Cebu City, Mandaue, Tagbilaran), and Barangay for pinpoint accuracy.
- **Property Type**: Choose from Studio, Apartment, Condominium, House, Room, or Townhouse.
- **Listed By Filter (NEW)**: Filter listings specifically by poster relationship:
  - `All`
  - `Direct Owner`
  - `Licensed Broker`
  - `Property Manager`
  - `Caretaker`
  - `Family Member`
  - `Authorized Representative`
- **Authorized Posters Only (NEW)**: A dedicated toggle switch that isolates listings uploaded exclusively by verified direct owners or authorized agents with verified credentials.
- **Monthly Rent Budget Slider**: Set minimum and maximum monthly rental thresholds from ₱0 up to ₱100,000+ with instant price recalculation.
- **Specifications & Amenities**: Filter by number of bedrooms, furnished status (Furnished, Semi-Furnished, Unfurnished), dedicated parking spaces, and pet-friendly policies.
- **Verification Level**: Restrict results to high-trust verified properties (Level 3+ with verified proof of ownership).

---

## 4. Posting a Rental Property (Host Flow)
To publish a new rental listing, navigate to the **'Post'** tab in the bottom navigation bar:
1. **Step 1: Listing Basics & Authority**: Provide the property title, rental category, monthly rate, security deposit, and select your relationship to the property (Direct Owner, Broker, Caretaker, etc.).
2. **Step 2: Geographic Hierarchy**: Choose Country, Province, City, Barangay, and street address. The app automatically assigns accurate latitude/longitude coordinates for map rendering.
3. **Step 3: Specifications & Amenities**: Specify floor area (sqm), bedrooms, bathrooms, furnishing, parking slots, utility provisions (WiFi, generator, water tank), and pet policy.
4. **Step 4: Photo Gallery**: Select and upload property photos using the system photo picker or camera.
5. **Step 5: Verification & Proof of Authority**: Upload a scanned copy or photo of the Transfer Certificate of Title (TCT), Tax Declaration, or notarized Special Power of Attorney (SPA). This elevates your listing to Verified Status (Level 3 or 4).

---

## 5. Scheduling Property Viewings & Notifications

### 5.1 Booking a Viewing (Tenant)
From any Property Detail screen, tap **'Schedule Viewing'**. Select your desired date and preferred time slot (e.g., Morning 9:00 AM, Afternoon 2:00 PM). Add an optional note to the landlord regarding any specific questions. Tap 'Confirm Request'.

### 5.2 Host Action & Automated Alerts (Landlord)
The host receives an instant in-app notification and an automated email alert sent via Gmail / Supabase Edge Functions. The host can Accept, Reschedule, or Decline the viewing request directly from their Viewings tab or Dashboard.

### 5.3 Viewing Status Management
Viewing requests transition through standardized lifecycle states: `Requested` -> `Confirmed` -> `Completed` / `Cancelled`. Both parties can view updated schedules in their respective Viewings tabs.

---

## 6. In-App Messaging & Real-Time Chat
Balay-Inn includes a dedicated peer-to-peer messaging system:
- **Initiate Conversation**: Tap 'Chat with Host' from any listing to start an instant thread.
- **Contextual Property Card**: The listing title, photo, and rent are pinned at the top of the chat for immediate context.
- **Real-Time Updates**: Messages are saved locally in SQLite and synchronized across devices when connected to Supabase.

---

## 7. Lower Dashboard, Account Session & Logging Out

### 7.1 Dashboard Hub Navigation
Tap the **'Dashboard'** tab (Account icon) in the bottom navigation bar to access the user control center:
- **User Profile Card**: Displays your name, email, verified role, and verification badge status.
- **Property Management & Stats**: View total views, total saves, active listings, and viewing requests.
- **Notification Center**: Review incoming booking alerts, system notifications, and mark alerts as read.
- **Cloud Sync & Diagnostic Tools**: Run real-time cloud synchronizations with Supabase PostgreSQL and test Gmail dispatcher alerts.

### 7.2 Lower Dashboard: Account Session & Log Out Button
At the very bottom of the Dashboard page is the dedicated **'Account Session'** section:
- **Session Identity**: Clearly displays `Signed in as [Your Name] ([Your Email])`.
- **Prominent Log Out Button**: Highlighted in prominent red with a logout icon. Tapping 'Log Out' displays a security confirmation dialog asking *"Log Out of Balay-Inn?"* to prevent accidental sign-outs.
- **Secure Session Termination**: Confirming logout safely terminates active tokens, clears cached credentials, resets role states, and redirects the user back to the Login & Welcome screen.

---

## 8. Frequently Asked Questions (FAQ) & Troubleshooting

**Q: How do I filter properties posted only by direct owners?**  
A: Open the Explore tab, tap the Filter button beside the search box, scroll down to the 'Listed By' section, and tap the 'Direct Owner' chip. You can also turn on the 'Authorized Posters Only' toggle switch. Then tap 'Apply Filters'.

**Q: Why is my listing marked as 'Pending' or 'Unverified'?**  
A: Newly created listings remain pending verification until proof-of-ownership documents (TCT or SPA) are uploaded and verified. Upload documents via the Verification Center to unlock Level 3/4 Verified Badges.

**Q: Can I use the application when offline?**  
A: Yes. Balay-Inn is architected offline-first. All cached properties, favorites, and pending viewings are stored safely in local SQLite Room storage and will automatically synchronize when internet access is restored.

**Q: Where is the Log Out button located?**  
A: Tap the 'Dashboard' tab on the bottom navigation bar, scroll to the lower bottom of the page to find the 'Account Session' card, and tap the red 'Log Out' button.

---

## 9. Support & Contact Information
For technical support, account inquiries, or bug reports:
- **Email Support**: support@balay-inn.ph / kailashalder04@gmail.com
- **Verification Helpdesk**: verification@balay-inn.ph
- **Official Repository**: Google AI Studio Balay-Inn Project Build

*Balay-Inn — The Premier Rental Marketplace Platform*
"""
    with open(md_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Markdown User Manual created successfully at: {md_path}")

def generate_pdf_document(pdf_path):
    # Generates a clean PostScript file and converts to PDF via ps2pdf
    ps_path = pdf_path.replace('.pdf', '.ps')
    
    ps_content = """%!PS-Adobe-3.0
%%Title: Balay-Inn User Manual & Support Document
%%Creator: Balay-Inn Documentation Engine
%%Pages: 4
%%BoundingBox: 0 0 612 792
%%DocumentData: Clean7Bit
%%EndComments

/inch { 72 mul } def

/page_header {
  gsave
  0.05 0.58 0.53 setrgbcolor
  0 750 612 42 rectfill
  1 1 1 setrgbcolor
  /Helvetica-Bold findfont 14 scalefont setfont
  40 765 moveto (BALAY-INN RENTAL MARKETPLACE - USER MANUAL & SUPPORT GUIDE) show
  grestore
} def

/page_footer {
  gsave
  0.4 0.4 0.4 setrgbcolor
  /Helvetica findfont 9 scalefont setfont
  40 30 moveto (Balay-Inn Android App Documentation - Confidential & Proprietary) show
  540 30 moveto (Page ) show
  grestore
} def

%%Page: 1 1
page_header
page_footer

% Title block
0.05 0.58 0.53 setrgbcolor
/Helvetica-Bold findfont 22 scalefont setfont
40 700 moveto (Balay-Inn Rental Marketplace) show

0.3 0.3 0.3 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 680 moveto (Comprehensive App User Manual & Support Documentation) show

0.5 0.5 0.5 setrgbcolor
/Helvetica-Oblique findfont 9 scalefont setfont
40 665 moveto (Version 2.4 - Android Jetpack Compose & Kotlin - Offline-First SQLite & Supabase) show

% Section 1
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 635 moveto (1. Executive Summary & Application Overview) show

0.1 0.1 0.1 setrgbcolor
/Helvetica findfont 10 scalefont setfont
40 615 moveto (Balay-Inn is a next-generation real estate rental marketplace designed specifically) show
40 600 moveto (for the Philippine rental housing ecosystem. The application connects verified tenants,) show
40 585 moveto (direct owners, licensed brokers, property managers, caretakers, and authorized representatives.) show

0.05 0.58 0.53 setrgbcolor
/Helvetica-Bold findfont 10 scalefont setfont
40 560 moveto (Key System Architecture Highlights:) show

0.1 0.1 0.1 setrgbcolor
/Helvetica findfont 9.5 scalefont setfont
55 542 moveto (- Cascading Philippine Geographic Hierarchy: Country -> Province -> City -> Barangay) show
55 527 moveto (- Interactive Map: Visual pins, pinch-to-zoom clusters, and 'Search this area' bounds) show
55 512 moveto (- Role-Based Authority & 'Listed By' Filtering: Direct Owners, Brokers, Representatives) show
55 497 moveto (- Viewing Request Scheduler: In-person scheduling, Gmail notifications, status tracking) show
55 482 moveto (- Dual Database Engine: Offline SQLite Room with Supabase PostgreSQL Cloud Sync) show

% Section 2
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 450 moveto (2. User Roles & Account Capabilities) show

0.1 0.1 0.1 setrgbcolor
/Helvetica findfont 9.5 scalefont setfont
40 430 moveto (The platform supports multiple dedicated user profiles:) show
55 413 moveto (- Tenant: Search listings, apply filters, bookmark favorites, request viewings, and chat.) show
55 398 moveto (- Direct Owner: Publish listings, upload Land Title/Tax Declaration, accept viewings.) show
55 383 moveto (- Licensed Broker: Manage client properties with PRC Real Estate Broker license ID.) show
55 368 moveto (- Property Manager / Caretaker: Handle building operations and schedule viewing tours.) show
55 353 moveto (- Authorized Representative: Post properties with notarized Special Power of Attorney.) show
55 338 moveto (- Administrator: Inspect audits, verify proof-of-ownership documents, manage users.) show

% Section 3
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 305 moveto (3. Exploring & Searching Rental Properties) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 285 moveto (3.1 Compact Search Bar & View Mode Toggle) show
/Helvetica findfont 9.5 scalefont setfont
40 270 moveto (A streamlined 40dp search bar allows instantaneous filtering by keyword, location, or name.) show
40 255 moveto (Beside the search bar, the 40dp Toggle button instantly switches between List View & Map View.) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 230 moveto (3.2 Comprehensive Filter Suite & 'Listed By' Filter) show
/Helvetica findfont 9.5 scalefont setfont
40 215 moveto (Tap the Filter button beside the search box to open the full filter modal bottom sheet:) show
55 198 moveto (- Geographic Cascading Hierarchy: Filter by Province, City/Municipality, and Barangay.) show
55 183 moveto (- Listed By Filter: Direct Owner, Licensed Broker, Property Manager, Caretaker, Family.) show
55 168 moveto (- Authorized Posters Only: Switch on to show only verified direct owners and agents.) show
55 153 moveto (- Rent Budget Slider: Dual-thumb price slider ranging from PHP 0 to PHP 100,000+.) show
55 138 moveto (- Specifications & Policies: Bedrooms, furnished status, parking, and pet policies.) show

showpage

%%Page: 2 2
page_header
page_footer

% Section 4
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 700 moveto (4. Posting a Rental Property (Host Flow)) show

0.1 0.1 0.1 setrgbcolor
/Helvetica findfont 9.5 scalefont setfont
40 680 moveto (To publish a listing, tap the 'Post' tab on the bottom navigation bar. Complete 5 easy steps:) show
55 662 moveto (Step 1: Listing Basics & Authority - Title, rental rate, deposit, and your poster role.) show
55 647 moveto (Step 2: Geographic Hierarchy - Select Country, Province, City, Barangay, and address.) show
55 632 moveto (Step 3: Specifications & Amenities - Floor area, bedrooms, bathrooms, parking, WiFi.) show
55 617 moveto (Step 4: Photo Gallery - Upload multiple high-resolution photos using Photo Picker.) show
55 602 moveto (Step 5: Verification & Proof of Authority - Upload Land Title (TCT), Tax Dec, or SPA.) show

% Section 5
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 565 moveto (5. Scheduling Property Viewings & Notifications) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 545 moveto (5.1 Booking a Viewing (Tenant)) show
/Helvetica findfont 9.5 scalefont setfont
40 530 moveto (On any property detail page, tap 'Schedule Viewing'. Pick your date and time slot,) show
40 515 moveto (include an inquiry note, and tap 'Confirm Request'.) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 490 moveto (5.2 Landlord Alerts & Automated Gmail Dispatcher) show
/Helvetica findfont 9.5 scalefont setfont
40 475 moveto (Hosts receive instant in-app alerts and automated Gmail notifications triggered via) show
40 460 moveto (Supabase Edge Functions. Hosts can Accept, Reschedule, or Decline in their Viewings tab.) show

% Section 6
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 425 moveto (6. In-App Messaging & Real-Time Chat) show

0.1 0.1 0.1 setrgbcolor
/Helvetica findfont 9.5 scalefont setfont
40 405 moveto (Communicate securely without sharing personal contact numbers outside the platform:) show
55 388 moveto (- Tap 'Chat with Host' to open a dedicated direct messaging conversation.) show
55 373 moveto (- Contextual property header keeps price and details visible during negotiation.) show
55 358 moveto (- Encrypted, real-time message sync between tenant and host devices.) show

% Section 7
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 320 moveto (7. Lower Dashboard, Account Session & Logging Out) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 300 moveto (7.1 Accessing the Dashboard) show
/Helvetica findfont 9.5 scalefont setfont
40 285 moveto (Tap 'Dashboard' on the bottom navigation bar to view your activity, saved listings,) show
40 270 moveto (notifications, dark mode settings, and database synchronization controls.) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 245 moveto (7.2 Lower Dashboard: Account Session & Log Out Button) show
/Helvetica findfont 9.5 scalefont setfont
40 230 moveto (Scroll to the lower portion of the Dashboard to find the Account Session card:) show
55 212 moveto (- Displays your active logged-in identity: name, email, and user role.) show
55 197 moveto (- Prominent red 'Log Out' button terminates your session safely.) show
55 182 moveto (- A confirmation dialog ('Log Out of Balay-Inn?') prevents accidental clickouts.) show
55 167 moveto (- Clears authentication tokens and safely redirects to the Login/Welcome screen.) show

showpage

%%Page: 3 3
page_header
page_footer

% Section 8
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 700 moveto (8. Frequently Asked Questions (FAQ) & Troubleshooting) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 675 moveto (Q: How do I filter properties posted only by direct owners?) show
/Helvetica findfont 9.5 scalefont setfont
40 660 moveto (A: Go to Explore, tap the Filter button beside the search box, scroll to 'Listed By',) show
40 645 moveto (tap 'Direct Owner', or toggle 'Authorized Posters Only', and tap 'Apply Filters'.) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 615 moveto (Q: How do I verify my property listing to get Level 3/4 Verified Badges?) show
/Helvetica findfont 9.5 scalefont setfont
40 600 moveto (A: Upload your Transfer Certificate of Title (TCT), Tax Declaration, or notarized) show
40 585 moveto (Special Power of Attorney (SPA) during property posting or in the Verification Center.) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 555 moveto (Q: Does the app work offline without internet connection?) show
/Helvetica findfont 9.5 scalefont setfont
40 540 moveto (A: Yes! Balay-Inn stores listings, favorites, and viewings in local SQLite storage.) show
40 525 moveto (Changes will synchronize automatically with Supabase when your connection is restored.) show

0.2 0.2 0.2 setrgbcolor
/Helvetica-Bold findfont 10.5 scalefont setfont
40 495 moveto (Q: Where is the Log Out button located?) show
/Helvetica findfont 9.5 scalefont setfont
40 480 moveto (A: Tap 'Dashboard' on the bottom navigation bar, scroll to the lower dashboard) show
40 465 moveto (card, and tap the red 'Log Out' button.) show

% Section 9
0.06 0.46 0.43 setrgbcolor
/Helvetica-Bold findfont 13 scalefont setfont
40 420 moveto (9. Technical Support & System Specifications) show

0.1 0.1 0.1 setrgbcolor
/Helvetica findfont 9.5 scalefont setfont
40 400 moveto (Platform Architecture Details:) show
55 382 moveto (- Framework: Android Native (Kotlin 2.0+ & Jetpack Compose Material 3)) show
55 367 moveto (- Local Storage: Android Jetpack Room Database (SQLite)) show
55 352 moveto (- Cloud Database: Supabase PostgreSQL with PostgREST & Row Level Security) show
55 337 moveto (- Edge Functions: Deno / TypeScript automated Gmail alert dispatchers) show
55 322 moveto (- Support Email: support@balay-inn.ph / kailashalder04@gmail.com) show

0.05 0.58 0.53 setrgbcolor
/Helvetica-Bold findfont 12 scalefont setfont
40 260 moveto (Thank you for using Balay-Inn - The Premier Rental Marketplace Platform!) show

showpage
%%EOF
"""
    with open(ps_path, 'w', encoding='utf-8') as f:
        f.write(ps_content)
    
    # Convert PS to PDF using ps2pdf
    cmd = f"ps2pdf '{ps_path}' '{pdf_path}'"
    exit_code = os.system(cmd)
    if exit_code == 0:
        print(f"PDF Document generated successfully at: {pdf_path}")
        if os.path.exists(ps_path):
            os.remove(ps_path)
    else:
        print(f"Failed to generate PDF from PostScript. Exit code: {exit_code}")

if __name__ == '__main__':
    root_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
    
    docx_file = os.path.join(root_dir, 'BALAY_INN_USER_MANUAL_SUPPORT_DOCUMENT.docx')
    pdf_file = os.path.join(root_dir, 'BALAY_INN_USER_MANUAL_SUPPORT_DOCUMENT.pdf')
    md_file = os.path.join(root_dir, 'USER_GUIDE_AND_SUPPORT_DOCUMENT.md')
    
    assets_dir = os.path.join(root_dir, 'app', 'src', 'main', 'assets')
    os.makedirs(assets_dir, exist_ok=True)
    asset_pdf = os.path.join(assets_dir, 'balay_inn_user_manual.pdf')
    asset_md = os.path.join(assets_dir, 'user_guide.md')
    
    generate_word_document(docx_file)
    generate_markdown(md_file)
    generate_markdown(asset_md)
    generate_pdf_document(pdf_file)
    generate_pdf_document(asset_pdf)
    print("All support documents generated successfully!")
