# Balay-Inn Supabase Edge Function: `notify-gmail`

Automated, server-side Gmail notification dispatcher triggered by Supabase Database Webhooks.

## Capabilities

1. **Viewing Requests (`viewing_requests`)**:
   - **`INSERT`**: Automatically emails the landlord/host when a tenant requests an in-person viewing.
   - **`UPDATE`**: Automatically emails the tenant when the host confirms, reschedules, or cancels the viewing.
2. **Property Inquiries (`messages`)**:
   - **`INSERT`**: Automatically emails the listing host or tenant with inquiry text and conversation link.
3. **Verification Status (`verification_documents`)**:
   - **`UPDATE`**: Automatically notifies users when their ID, PRC Broker License, or Property Title has been reviewed/approved/rejected.

---

## Deployment Instructions

### 1. Deploy the Function using the Supabase CLI
```bash
supabase functions deploy notify-gmail --no-verify-jwt
```

### 2. Configure Function Secrets in Supabase
Set your Gmail OAuth / Google API credentials in the Supabase Dashboard under **Project Settings > Edge Functions > Secrets** (or via CLI):

```bash
# Add your Gmail OAuth access token (or Google OAuth service credentials)
supabase secrets set GMAIL_OAUTH_ACCESS_TOKEN="your-oauth-access-token"
```

### 3. Enable Database Webhooks in Supabase Dashboard
Go to **Supabase Dashboard > Database > Webhooks** and add hooks pointing to `https://<project-ref>.functions.supabase.co/notify-gmail`:
1. **Viewing Requests Hook**:
   - Table: `viewing_requests`
   - Events: `INSERT`, `UPDATE`
   - HTTP Method: `POST`
2. **Inquiry Messages Hook**:
   - Table: `messages`
   - Events: `INSERT`
   - HTTP Method: `POST`
3. **Verification Documents Hook**:
   - Table: `verification_documents`
   - Events: `UPDATE`
   - HTTP Method: `POST`

Alternatively, execute the SQL trigger scripts in `supabase_schema.sql` to manage them directly in PostgreSQL!
