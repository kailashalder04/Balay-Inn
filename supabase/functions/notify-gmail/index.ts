// ============================================================================
// Balay-Inn Rental Marketplace - Supabase Edge Function: notify-gmail
// ============================================================================
// Automated Gmail notification dispatcher triggered by Supabase Database Hooks
// Handles:
// 1. Viewing Requests (INSERT -> Host Alert; UPDATE -> Tenant Status Alert)
// 2. Property Inquiries (INSERT on messages -> Host Alert)
// 3. Verification Updates (UPDATE on verification_documents -> User Alert)
// ============================================================================

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.39.0";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
};

interface WebhookPayload<T = any> {
  type: "INSERT" | "UPDATE" | "DELETE";
  table: string;
  schema: string;
  record: T;
  old_record: T | null;
}

interface DirectRequestPayload {
  action?: string;
  type?: "VIEWING_REQUEST" | "VIEWING_STATUS" | "INQUIRY" | "VERIFICATION_STATUS" | "TEST";
  recipientEmail?: string;
  propertyTitle?: string;
  tenantName?: string;
  date?: string;
  time?: string;
  message?: string;
  status?: string;
  notes?: string;
}

// ----------------------------------------------------------------------------
// Email Generation Utilities (RFC 2822 MIME & Base64URL)
// ----------------------------------------------------------------------------

function encodeBase64Url(str: string): string {
  const encoder = new TextEncoder();
  const bytes = encoder.encode(str);
  let binary = "";
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return btoa(binary)
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/, "");
}

function buildMimeMessage(options: {
  to: string;
  from?: string;
  subject: string;
  htmlBody: string;
  textBody?: string;
}): string {
  const from = options.from || "Balay-Inn Notifications <me>";
  const textBody =
    options.textBody ||
    options.htmlBody.replace(/<[^>]*>?/gm, "").replace(/\s+/g, " ").trim();

  const boundary = `boundary_${Date.now()}_${Math.random().toString(36).substring(2)}`;

  const raw = [
    `To: ${options.to}`,
    `From: ${from}`,
    `Subject: =?UTF-8?B?${btoa(unescape(encodeURIComponent(options.subject)))}?=`,
    `MIME-Version: 1.0`,
    `Content-Type: multipart/alternative; boundary="${boundary}"`,
    "",
    `--${boundary}`,
    `Content-Type: text/plain; charset=UTF-8`,
    `Content-Transfer-Encoding: 7bit`,
    "",
    textBody,
    "",
    `--${boundary}`,
    `Content-Type: text/html; charset=UTF-8`,
    `Content-Transfer-Encoding: 7bit`,
    "",
    options.htmlBody,
    "",
    `--${boundary}--`,
  ].join("\r\n");

  return encodeBase64Url(raw);
}

// ----------------------------------------------------------------------------
// Responsive HTML Email Templates
// ----------------------------------------------------------------------------

function renderEmailTemplate(options: {
  title: string;
  subtitle: string;
  badgeText: string;
  badgeBg: string;
  badgeColor: string;
  contentHtml: string;
  ctaText?: string;
  ctaUrl?: string;
}): string {
  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${options.title}</title>
</head>
<body style="margin: 0; padding: 0; background-color: #F8FAFC; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1E293B;">
  <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background-color: #F8FAFC; padding: 24px 12px;">
    <tr>
      <td align="center">
        <table role="presentation" width="100%" style="max-width: 580px; background-color: #FFFFFF; border-radius: 16px; overflow: hidden; border: 1px solid #E2E8F0; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);" cellspacing="0" cellpadding="0">
          <!-- Header Banner -->
          <tr>
            <td style="background: linear-gradient(135deg, #059669 0%, #047857 100%); padding: 24px 28px; text-align: left;">
              <table width="100%" cellspacing="0" cellpadding="0">
                <tr>
                  <td>
                    <span style="font-size: 20px; font-weight: 800; color: #FFFFFF; letter-spacing: -0.5px;">Balay-Inn</span>
                    <span style="font-size: 11px; background-color: rgba(255,255,255,0.2); color: #FFFFFF; padding: 3px 8px; border-radius: 12px; margin-left: 8px; font-weight: 600; text-transform: uppercase;">Philippines</span>
                    <div style="font-size: 13px; color: #D1FAE5; margin-top: 4px;">Verified Rental Marketplace</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Main Content -->
          <tr>
            <td style="padding: 28px 28px 20px 28px;">
              <div style="display: inline-block; background-color: ${options.badgeBg}; color: ${options.badgeColor}; font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 6px; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 12px;">
                ${options.badgeText}
              </div>
              <h1 style="font-size: 20px; font-weight: 700; color: #0F172A; margin: 0 0 6px 0; line-height: 1.3;">
                ${options.title}
              </h1>
              <p style="font-size: 13px; color: #64748B; margin: 0 0 20px 0; line-height: 1.5;">
                ${options.subtitle}
              </p>

              <!-- Dynamic Content Block -->
              <div style="background-color: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 12px; padding: 18px; margin-bottom: 24px;">
                ${options.contentHtml}
              </div>

              ${
                options.ctaText
                  ? `<div style="text-align: center; margin-bottom: 24px;">
                      <a href="${options.ctaUrl || "#"}" style="display: inline-block; background-color: #059669; color: #FFFFFF; text-decoration: none; font-size: 14px; font-weight: 600; padding: 12px 28px; border-radius: 10px; box-shadow: 0 2px 6px rgba(5, 150, 105, 0.3);">
                        ${options.ctaText}
                      </a>
                    </div>`
                  : ""
              }

              <p style="font-size: 12px; color: #94A3B8; margin: 0; line-height: 1.6; border-top: 1px solid #F1F5F9; padding-top: 16px;">
                Need help or suspect fraudulent activity? Tap "Report Listing" inside the app or reply directly to this alert.
              </p>
            </td>
          </tr>

          <!-- Footer -->
          <tr>
            <td style="background-color: #F1F5F9; padding: 16px 28px; text-align: center; font-size: 11px; color: #64748B;">
              © ${new Date().getFullYear()} Balay-Inn Rental Marketplace • Cebu & Bohol, Philippines<br>
              Transparent, verified, direct rental platform.
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>`;
}

// ----------------------------------------------------------------------------
// Gmail REST API Dispatcher
// ----------------------------------------------------------------------------

async function sendGmailEmail(options: {
  to: string;
  subject: string;
  htmlBody: string;
  textBody?: string;
}): Promise<{ success: boolean; messageId?: string; error?: string }> {
  const oauthToken = Deno.env.get("GMAIL_OAUTH_ACCESS_TOKEN") || Deno.env.get("GOOGLE_OAUTH_TOKEN");

  const rawEncoded = buildMimeMessage({
    to: options.to,
    subject: options.subject,
    htmlBody: options.htmlBody,
    textBody: options.textBody,
  });

  // If OAuth token is not configured in environment, log and return graceful simulated success
  if (!oauthToken) {
    console.log(
      `[notify-gmail] No GMAIL_OAUTH_ACCESS_TOKEN configured in Supabase secrets. Notification payload prepared successfully for: ${options.to} (${options.subject})`
    );
    return {
      success: true,
      messageId: `simulated_${Date.now()}`,
    };
  }

  try {
    const res = await fetch(
      "https://gmail.googleapis.com/gmail/v1/users/me/messages/send",
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${oauthToken}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ raw: rawEncoded }),
      }
    );

    if (!res.ok) {
      const errText = await res.text();
      console.error(`[notify-gmail] Gmail API Error (${res.status}): ${errText}`);
      return { success: false, error: errText };
    }

    const json = await res.json();
    console.log(`[notify-gmail] Successfully dispatched email via Gmail API! ID: ${json.id}`);
    return { success: true, messageId: json.id };
  } catch (err: any) {
    console.error(`[notify-gmail] Dispatch exception: ${err.message}`);
    return { success: false, error: err.message };
  }
}

// ----------------------------------------------------------------------------
// Main HTTP Handler
// ----------------------------------------------------------------------------

serve(async (req: Request) => {
  // Handle CORS
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const supabaseUrl = Deno.env.get("SUPABASE_URL") || "";
    const supabaseKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") || Deno.env.get("SUPABASE_ANON_KEY") || "";
    const supabase = createClient(supabaseUrl, supabaseKey);

    const body = await req.json();

    // Check if invocation is a Database Webhook or direct API call
    const isWebhook = body && body.table && body.type;

    let emailResult = { success: false, message: "" };

    if (isWebhook) {
      const webhook = body as WebhookPayload;
      console.log(`[notify-gmail] Database Webhook received for table: ${webhook.table} (${webhook.type})`);

      // ----------------------------------------------------------------------
      // 1. Table: viewing_requests
      // ----------------------------------------------------------------------
      if (webhook.table === "viewing_requests") {
        const record = webhook.record;
        const oldRecord = webhook.old_record;

        // Fetch property title & host / tenant emails
        const { data: prop } = await supabase
          .from("properties")
          .select("title, poster_id, poster_name")
          .eq("id", record.property_id)
          .single();

        const propertyTitle = prop?.title || "Balay-Inn Rental Listing";

        const { data: hostUser } = await supabase
          .from("users")
          .select("email, full_name")
          .eq("id", record.poster_id)
          .single();

        const { data: tenantUser } = await supabase
          .from("users")
          .select("email, full_name, phone")
          .eq("id", record.tenant_id)
          .single();

        const hostEmail = hostUser?.email || "host@balayinn.ph";
        const tenantEmail = tenantUser?.email || "tenant@balayinn.ph";
        const tenantName = tenantUser?.full_name || "Prospective Tenant";

        if (webhook.type === "INSERT") {
          // New viewing request -> Alert Host
          const subject = `🏠 New Viewing Request: ${propertyTitle}`;
          const html = renderEmailTemplate({
            title: "New Viewing Appointment Request",
            subtitle: `${tenantName} is requesting to schedule an in-person viewing.`,
            badgeText: "Viewing Requested",
            badgeBg: "#FEF3C7",
            badgeColor: "#D97706",
            contentHtml: `
              <table width="100%" style="font-size: 13px; line-height: 1.8;">
                <tr><td width="35%" style="color: #64748B; font-weight: 600;">Listing:</td><td style="color: #0F172A; font-weight: 700;">${propertyTitle}</td></tr>
                <tr><td style="color: #64748B; font-weight: 600;">Preferred Date:</td><td style="color: #059669; font-weight: 700;">${record.preferred_date}</td></tr>
                <tr><td style="color: #64748B; font-weight: 600;">Time Window:</td><td style="color: #0F172A;">${record.preferred_time}</td></tr>
                <tr><td style="color: #64748B; font-weight: 600;">Visitors:</td><td style="color: #0F172A;">${record.number_of_visitors || 1} Person(s)</td></tr>
                <tr><td style="color: #64748B; font-weight: 600;">Tenant Note:</td><td style="color: #334155; font-style: italic;">"${record.tenant_message || "No special requests."}"</td></tr>
              </table>
            `,
            ctaText: "Open In-App to Confirm or Reschedule",
            ctaUrl: "https://balayinn.ph/dashboard/viewings",
          });

          const sendRes = await sendGmailEmail({ to: hostEmail, subject, htmlBody: html });
          emailResult = { success: sendRes.success, message: `Host viewing alert sent to ${hostEmail}` };
        } else if (webhook.type === "UPDATE" && record.status !== oldRecord?.status) {
          // Viewing status updated -> Alert Tenant
          const newStatus = record.status;
          const isConfirmed = newStatus === "CONFIRMED";
          const subject = `📅 Viewing Schedule ${newStatus}: ${propertyTitle}`;

          const html = renderEmailTemplate({
            title: `Viewing Appointment ${newStatus}`,
            subtitle: `The host has updated your scheduled viewing request for ${propertyTitle}.`,
            badgeText: newStatus,
            badgeBg: isConfirmed ? "#ECFDF5" : "#FEE2E2",
            badgeColor: isConfirmed ? "#059669" : "#DC2626",
            contentHtml: `
              <table width="100%" style="font-size: 13px; line-height: 1.8;">
                <tr><td width="35%" style="color: #64748B; font-weight: 600;">Status:</td><td style="color: ${isConfirmed ? "#059669" : "#DC2626"}; font-weight: 800;">${newStatus}</td></tr>
                <tr><td style="color: #64748B; font-weight: 600;">Confirmed Date:</td><td style="color: #0F172A; font-weight: 700;">${record.preferred_date}</td></tr>
                <tr><td style="color: #64748B; font-weight: 600;">Confirmed Time:</td><td style="color: #0F172A;">${record.preferred_time}</td></tr>
                ${
                  record.poster_response
                    ? `<tr><td style="color: #64748B; font-weight: 600;">Host Remarks:</td><td style="color: #334155;">"${record.poster_response}"</td></tr>`
                    : ""
                }
              </table>
            `,
            ctaText: "View Appointment Details",
            ctaUrl: "https://balayinn.ph/dashboard/viewings",
          });

          const sendRes = await sendGmailEmail({ to: tenantEmail, subject, htmlBody: html });
          emailResult = { success: sendRes.success, message: `Tenant status alert sent to ${tenantEmail}` };
        }
      }

      // ----------------------------------------------------------------------
      // 2. Table: messages (Inquiries)
      // ----------------------------------------------------------------------
      else if (webhook.table === "messages" && webhook.type === "INSERT") {
        const msg = webhook.record;

        // Fetch conversation to identify host / tenant
        const { data: conv } = await supabase
          .from("conversations")
          .select("property_id, property_title, tenant_id, poster_id")
          .eq("id", msg.conversation_id)
          .single();

        const recipientId = msg.sender_id === conv?.tenant_id ? conv?.poster_id : conv?.tenant_id;
        const { data: recipientUser } = await supabase
          .from("users")
          .select("email, full_name")
          .eq("id", recipientId)
          .single();

        const targetEmail = recipientUser?.email || "user@balayinn.ph";
        const subject = `💬 New Message regarding ${conv?.property_title || "Rental Listing"}`;

        const html = renderEmailTemplate({
          title: "New Rental Inquiry Message",
          subtitle: `You have received a new message from ${msg.sender_name}.`,
          badgeText: "New Inquiry",
          badgeBg: "#EFF6FF",
          badgeColor: "#2563EB",
          contentHtml: `
            <div style="font-size: 14px; color: #1E293B; font-style: italic; background-color: #FFFFFF; border-left: 4px solid #2563EB; padding: 12px 14px; border-radius: 4px; margin-bottom: 12px;">
              "${msg.message_text}"
            </div>
            <div style="font-size: 12px; color: #64748B;">
              Property: <strong>${conv?.property_title || "Rental Listing"}</strong>
            </div>
          `,
          ctaText: "Reply in Balay-Inn Chat",
          ctaUrl: `https://balayinn.ph/messages/${msg.conversation_id}`,
        });

        const sendRes = await sendGmailEmail({ to: targetEmail, subject, htmlBody: html });
        emailResult = { success: sendRes.success, message: `Inquiry message alert sent to ${targetEmail}` };
      }

      // ----------------------------------------------------------------------
      // 3. Table: verification_documents (Verification Status Updates)
      // ----------------------------------------------------------------------
      else if (webhook.table === "verification_documents" && webhook.type === "UPDATE") {
        const doc = webhook.record;
        const { data: user } = await supabase
          .from("users")
          .select("email, full_name")
          .eq("id", doc.user_id)
          .single();

        const userEmail = user?.email || "user@balayinn.ph";
        const isVerified = doc.status === "VERIFIED";
        const subject = `🛡️ Balay-Inn Verification Update: ${doc.status}`;

        const html = renderEmailTemplate({
          title: `Verification Status: ${doc.status}`,
          subtitle: `Your verification submission (${doc.document_type.replace(/_/g, " ")}) has been reviewed.`,
          badgeText: doc.status,
          badgeBg: isVerified ? "#ECFDF5" : "#FEF3C7",
          badgeColor: isVerified ? "#059669" : "#D97706",
          contentHtml: `
            <table width="100%" style="font-size: 13px; line-height: 1.8;">
              <tr><td width="35%" style="color: #64748B; font-weight: 600;">Document:</td><td style="color: #0F172A; font-weight: 700;">${doc.document_type.replace(/_/g, " ")}</td></tr>
              <tr><td style="color: #64748B; font-weight: 600;">Status:</td><td style="color: ${isVerified ? "#059669" : "#D97706"}; font-weight: 800;">${doc.status}</td></tr>
              ${
                doc.reviewer_notes
                  ? `<tr><td style="color: #64748B; font-weight: 600;">Reviewer Notes:</td><td style="color: #334155;">${doc.reviewer_notes}</td></tr>`
                  : ""
              }
            </table>
          `,
          ctaText: "Check Verification Profile",
          ctaUrl: "https://balayinn.ph/dashboard/verification",
        });

        const sendRes = await sendGmailEmail({ to: userEmail, subject, htmlBody: html });
        emailResult = { success: sendRes.success, message: `Verification status alert sent to ${userEmail}` };
      }
    } else {
      // ----------------------------------------------------------------------
      // Direct API Trigger (e.g. from Android client or dashboard test)
      // ----------------------------------------------------------------------
      const direct = body as DirectRequestPayload;
      const targetEmail = direct.recipientEmail || "kailashalder04@gmail.com";
      const propTitle = direct.propertyTitle || "Solinea Tower 2 (Demo Listing)";
      const subject = `🏠 Viewing Request Alert: ${propTitle}`;

      const html = renderEmailTemplate({
        title: "Viewing Appointment Notification",
        subtitle: `${direct.tenantName || "Joshua Alcantara"} has requested an in-person viewing.`,
        badgeText: "Viewing Alert",
        badgeBg: "#ECFDF5",
        badgeColor: "#059669",
        contentHtml: `
          <table width="100%" style="font-size: 13px; line-height: 1.8;">
            <tr><td width="35%" style="color: #64748B; font-weight: 600;">Property:</td><td style="color: #0F172A; font-weight: 700;">${propTitle}</td></tr>
            <tr><td style="color: #64748B; font-weight: 600;">Scheduled Date:</td><td style="color: #059669; font-weight: 700;">${direct.date || "Saturday, Oct 5"}</td></tr>
            <tr><td style="color: #64748B; font-weight: 600;">Time Window:</td><td style="color: #0F172A;">${direct.time || "2:00 PM - 3:00 PM"}</td></tr>
            <tr><td style="color: #64748B; font-weight: 600;">Message:</td><td style="color: #334155; font-style: italic;">"${direct.message || "Looking forward to viewing the apartment!"}"</td></tr>
          </table>
        `,
        ctaText: "Open Balay-Inn Dashboard",
        ctaUrl: "https://balayinn.ph/dashboard",
      });

      const sendRes = await sendGmailEmail({ to: targetEmail, subject, htmlBody: html });
      emailResult = {
        success: sendRes.success,
        message: `Direct notification email dispatched to ${targetEmail} (ID: ${sendRes.messageId || "simulated"})`,
      };
    }

    return new Response(JSON.stringify({ ok: true, result: emailResult }), {
      headers: { ...corsHeaders, "Content-Type": "application/json" },
      status: 200,
    });
  } catch (err: any) {
    console.error(`[notify-gmail] Handler error: ${err.message}`);
    return new Response(JSON.stringify({ ok: false, error: err.message }), {
      headers: { ...corsHeaders, "Content-Type": "application/json" },
      status: 500,
    });
  }
});
