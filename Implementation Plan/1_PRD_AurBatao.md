# AurBatao — Product Requirements Document (PRD)
**Version:** 2.0 (Native Android)
**Prepared for:** Antigravity Dev Team
**App Name:** AurBatao (`aurBatao`)
**Package:** `com.blackm.aurBatao`
**Platform:** Native Android (Kotlin + Jetpack Compose)

---

## 1. Product Overview

AurBatao ("Aur Batao" = "Tell me more" in Hindi) is an AI Avatar companion app for Indian users. Users can talk to AI-powered personas via **voice calls** or **text chat** in Hindi, English, and Hinglish. The app supports real-time voice conversations using a LiveKit-powered pipeline with multi-model STT/TTS/LLM fallback, and text-based chat with streaming AI responses.

### 1.1 Core Value Proposition
- Talk to an AI avatar that understands Indian languages natively
- Voice-first, phone-call-like UX familiar to Indian users
- Pay-as-you-go calling model (balance-based, like a prepaid SIM)
- Safe, private, anonymous conversations

### 1.2 Monetisation Model
- Users recharge their balance (like prepaid mobile top-up)
- Calls deduct balance per minute
- Text chat may be free or low-cost tier
- In-app packs: Recharge Packs with tiered pricing

---

## 2. User Flows (Extracted from APK)

### 2.1 Auth Flow (`/(public)/`)
**Screens identified:** `welcome.tsx`, `login.tsx`, `_layout.tsx`

| Step | Screen | Action |
|------|--------|--------|
| 1 | Welcome | Splash/onboarding — CTA to get started |
| 2 | Login | Enter mobile number (Indian format) |
| 3 | OTP | 4/6-digit OTP ("One-Time Password") |
| 4 | Resend OTP | "Resend OTP sent to your mobile number" |
| 5 | Last Step | Enter username/avatar name |

**Copy extracted from bundle:**
- "Walk Safe & in Private"
- "We don't share your mobile number to anyone"
- "Resend OTP sent to your mobile number"
- "Last Step! Enter the entry"
- "People will be able to see your avatar"

### 2.2 Core App Flow (`/(core)/`)
**Screens identified:** `calls.tsx`, `chats.tsx`, `funds.tsx`, `phoneCall.tsx`, `_layout.tsx`

#### Bottom Tab Navigation (4 tabs)
1. **Calls** — Browse/start avatar voice calls
2. **Chats** — Text chat with avatars
3. **Funds** — Wallet, recharge, transaction history
4. *(Settings/Profile)*

#### Voice Call Flow (`phoneCall.tsx`)
| State | UI Copy |
|-------|---------|
| Dialling | "Connecting you to…" |
| Connected | "Call in Progress" |
| Active | "You can talk to [Avatar]" |
| Muted | Mic mute toggle |
| Rating | "Rate the call with ⭐" |
| End | "Hope you loved the call!" |
| Post-call | "Congratulations! You can now start calling people!" |

**Extracted call-related strings:**
- "Call in Progress"
- "Call Charges"
- "Call Duration"
- "Connecting you to"
- "Or batao!" (during call prompt)
- "Apne baare…" (Tell me about yourself)
- "Let us find someone"
- "Make new friends!"
- "Call and make new friends!"

#### Funds Flow (`funds.tsx`)
- "Current Balance"
- "Recharge Packs"
- "Buy Now"
- "buysell" / "adskip" (ad-based free minutes possible)
- Package pricing display
- "headerFundsTextAmount" (balance display)

### 2.3 Avatar Selection Flow
- Avatar list with names (Rahul, Kritika, Shanukia identified)
- Avatar images (webp format — 21 avatar images found)
- Avatar persona displayed before call
- "You can talk to [Name]" confirmation

---

## 3. Feature Requirements

### 3.1 Authentication (P0)
- [ ] Phone number input (Indian +91 format)
- [ ] Firebase Phone Auth (OTP via SMS)
- [ ] OTP screen with resend timer
- [ ] Username/avatar name entry
- [ ] Secure token storage (Android Keystore)
- [ ] Auto-login on return visit

### 3.2 Avatar Discovery (P0)
- [ ] Scrollable grid/list of AI avatars
- [ ] Avatar card: image, name, short bio, language tags
- [ ] Filter by language (Hindi/English/Hinglish)
- [ ] Filter by gender/persona type
- [ ] Avatar detail sheet before initiating call

### 3.3 Voice Call (P0)
- [ ] LiveKit room creation and joining
- [ ] Real-time STT (Sarvam AI primary, Deepgram fallback)
- [ ] Multi-model LLM (Groq primary, OpenAI fallback)
- [ ] Multi-model TTS (Sarvam AI primary, ElevenLabs fallback)
- [ ] Call timer display
- [ ] Balance deduction per minute
- [ ] Mute/unmute microphone
- [ ] End call button
- [ ] Post-call rating (1–5 stars)
- [ ] Call charges summary post-call
- [ ] Background audio support
- [ ] Earpiece/speaker toggle

### 3.4 Text Chat (P0)
- [ ] Chat list screen per avatar
- [ ] Streaming AI text response
- [ ] Message bubbles (sent/received)
- [ ] Timestamp on messages
- [ ] Chat history persisted in Firebase Firestore
- [ ] Hindi/Hinglish keyboard support
- [ ] Voice-to-text input option in chat

### 3.5 Wallet & Recharge (P0)
- [ ] Current balance display
- [ ] Recharge pack selection (multiple tiers)
- [ ] Payment via Razorpay / UPI (Indian payment stack)
- [ ] Transaction history list
- [ ] Low-balance warning during calls
- [ ] Balance insufficient → redirect to recharge

### 3.6 Multi-Model AI Pipeline (P0)
- [ ] STT plugin interface with model registry
- [ ] TTS plugin interface with model registry
- [ ] LLM plugin interface with model registry
- [ ] Automatic fallback on model failure/timeout
- [ ] Health check per model before call
- [ ] Configurable from Firebase Remote Config (no app update needed to swap models)

### 3.7 Profile & Settings (P1)
- [ ] User profile: name, avatar, phone
- [ ] Language preference setting
- [ ] Notification preferences
- [ ] Rate the app
- [ ] Share the app ("Share us With Your Friends")
- [ ] Community guidelines
- [ ] Privacy policy / Terms

---

## 4. Non-Functional Requirements

| Requirement | Target |
|-------------|--------|
| STT first-word latency | < 400ms |
| LLM first-token latency | < 600ms |
| TTS first-audio latency | < 300ms |
| Total turn latency (end-to-end) | < 1.5s |
| Voice call uptime | 99.5% |
| Android min SDK | API 26 (Android 8.0) |
| Target SDK | API 34 |
| App size | < 30MB APK |
| Offline handling | Graceful error + retry on reconnect |
| Data privacy | No call recordings stored; only transcripts if consented |

---

## 5. Out of Scope (v2.0)
- iOS app
- Web app
- Group calls
- Avatar video (audio-only for now)
- User-created avatars
- Marketplace for avatar creators

---

## 6. Success Metrics
- D1 retention > 40%
- Average session length > 5 minutes
- Call completion rate > 70%
- Recharge conversion rate > 15% of active users
- Avg calls per active user per day > 2
