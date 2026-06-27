# AurBatao — Implementation Plan
**Version:** 2.0 | **Platform:** Native Android (Kotlin + Jetpack Compose)
**Team:** Antigravity Dev
**Estimated Timeline:** 10 Weeks

---

## Phase 0 — Project Setup (Week 1)

### 0.1 Android Project Bootstrap
- [ ] Create new Android project (Kotlin, min SDK 26, target SDK 34)
- [ ] Configure Jetpack Compose with Material3
- [ ] Set up Hilt for dependency injection
- [ ] Configure ProGuard/R8 for release
- [ ] Set up `google-services.json` from Firebase
- [ ] Add `local.properties` secrets pattern for API keys
- [ ] Configure `BuildConfig` fields for env (dev/staging/prod)
- [ ] Set up GitHub repo with branch protection + PR template

### 0.2 Firebase Setup
- [ ] Create Firebase project (`aurbatao-prod`)
- [ ] Enable Authentication → Phone provider
- [ ] Enable Firestore (create collections: users, avatars, conversations, transactions, recharge_packs)
- [ ] Enable Remote Config (configure AI model registry JSON)
- [ ] Enable Firebase Performance Monitoring
- [ ] Enable Crashlytics
- [ ] Configure Firestore security rules (see Architecture doc)
- [ ] Seed initial avatar data (minimum 5 avatars with webp images)

### 0.3 Backend Bootstrap (FastAPI)
- [ ] Create FastAPI project structure
- [ ] Set up Render.com deployment (free tier)
- [ ] Implement `/health` endpoint
- [ ] Set up LiveKit Cloud account + credentials
- [ ] Implement `/api/v1/livekit/token` endpoint
- [ ] Configure env vars in Render dashboard

### 0.4 CI/CD
- [ ] GitHub Actions workflow: lint + unit tests on PR
- [ ] GitHub Actions workflow: APK build on merge to main
- [ ] Firebase App Distribution for internal testing

---

## Phase 1 — Auth & Onboarding (Week 2)

### Screens to Build
1. `WelcomeScreen` — Logo, tagline, "Get Started" button
2. `LoginScreen` — Phone number input (+91 prefix, Indian keyboard)
3. `OTPScreen` — 6-box OTP input, resend timer
4. `SetNameScreen` — Username entry ("Last Step")

### Implementation Tasks
- [ ] `FirebaseAuthRepository` — `sendOTP()`, `verifyOTP()`, `signOut()`
- [ ] `AuthViewModel` with UiState machine (Idle → SendingOTP → AwaitingOTP → Verifying → Success/Error)
- [ ] `WelcomeScreen` composable with logo + animated gradient background
- [ ] `LoginScreen` — Indian phone input (validate +91 + 10 digits)
- [ ] `OTPScreen` — 6 custom OTP boxes, auto-advance focus, paste support
- [ ] Resend OTP with 60s countdown timer
- [ ] `SetNameScreen` — username input + avatar preview placeholder
- [ ] Navigation: Welcome → Login → OTP → SetName → Core
- [ ] Persist auth state via DataStore (auto-login on reopen)
- [ ] Handle FirebaseAuthException codes gracefully (wrong OTP, expired, etc.)

### UI Components (new)
- `OTPInputRow` — 6-box composable with auto-focus and cursor
- `PhoneInputField` — custom field with +91 prefix locked
- `GradientBackground` — dark green gradient for auth screens
- `PrimaryButton` — gold CTA button (reusable)

---

## Phase 2 — Avatar Discovery (Week 3)

### Screens to Build
1. `AvatarListScreen` — grid of avatar cards (calls tab)
2. `AvatarDetailBottomSheet` — expanded avatar detail + call CTA

### Implementation Tasks
- [ ] `AvatarRepository` — Firestore `avatars` collection listener
- [ ] `GetAvatarsUseCase` — filters by language, gender, isActive
- [ ] `AvatarListViewModel` — handles loading/filtering state
- [ ] `AvatarCard` composable — image, name, language badges, call button
- [ ] Language filter chips (Hindi / English / Hinglish / All)
- [ ] `AvatarDetailBottomSheet` — full bio, voice sample play, stats
- [ ] Coil image loading with placeholder + crossfade
- [ ] Handle empty state (no avatars available)
- [ ] Shimmer loading skeleton while fetching

### UI Components (new)
- `AvatarCard` — rounded card with image, name, badge
- `LanguageBadge` — colored pill chip
- `ShimmerCard` — loading placeholder
- `FilterChipRow` — horizontally scrollable filter bar

---

## Phase 3 — Voice Pipeline (Weeks 4–5)

### 3.1 LiveKit Integration
- [ ] Add LiveKit SDK dependency
- [ ] `LiveKitManager` — connect, publish audio, subscribe, disconnect
- [ ] Microphone permission handling (runtime + rationale UI)
- [ ] Audio session configuration (earpiece/speaker routing)
- [ ] Handle room events (participant joined/left, connection quality)

### 3.2 STT Plugin System
- [ ] Define `STTProvider` interface
- [ ] `SarvamSTTProvider` — WebSocket connection, audio streaming, transcript parsing
- [ ] `DeepgramSTTProvider` — streaming transcription, Nova-2 Hindi model
- [ ] Language detection from transcript (auto-switch TTS voice)
- [ ] `STTProviderRegistry` — priority list, health check, fallback logic
- [ ] VAD (Voice Activity Detection) — detect speech start/end
- [ ] Streaming partial transcripts display on call screen

### 3.3 LLM Plugin System
- [ ] Define `LLMProvider` interface with streaming support
- [ ] `GroqLLMProvider` — llama-3.3-70b, low-latency streaming
- [ ] `OpenAILLMProvider` — gpt-4o-mini fallback
- [ ] `LLMProviderRegistry` with fallback
- [ ] System prompt builder from `AvatarPersona`
- [ ] Conversation history management (sliding window, max tokens)
- [ ] Hindi/Hinglish instruction in system prompt

### 3.4 TTS Plugin System
- [ ] Define `TTSProvider` interface with audio streaming
- [ ] `SarvamTTSProvider` — Bulbul model, 10+ Indian language voices
- [ ] `ElevenLabsTTSProvider` — Flash v2 for low latency fallback
- [ ] `TTSProviderRegistry` with fallback
- [ ] Audio buffering and playback queue
- [ ] Barge-in detection (user speaking while AI speaks → interrupt)

### 3.5 VoicePipelineManager
- [ ] `VoicePipelineManager` — orchestrates STT → LLM → TTS
- [ ] Full turn processing with event emission
- [ ] Error recovery without dropping call
- [ ] Remote Config integration for model switching without app update
- [ ] Latency logging per pipeline stage

### 3.6 Active Call Screen
- [ ] `ActiveCallScreen` composable
- [ ] Avatar image with pulsing animation (speaking state)
- [ ] Animated waveform during AI speech
- [ ] Call timer (MM:SS)
- [ ] Mute/unmute button with state
- [ ] Speaker/earpiece toggle
- [ ] Live transcript overlay (optional, toggleable)
- [ ] End call button
- [ ] Low balance warning overlay (< ₹10 remaining)
- [ ] Prevent screen sleep during active call

---

## Phase 4 — Text Chat (Week 6)

### Screens to Build
1. `ChatListScreen` — list of avatar conversations (Chats tab)
2. `ChatScreen` — individual chat with avatar

### Implementation Tasks
- [ ] `ChatRepository` — Firestore conversations + messages real-time listener
- [ ] `SendMessageUseCase` — send user message, trigger LLM, stream response
- [ ] `GetConversationHistoryUseCase`
- [ ] `ChatViewModel` with streaming state
- [ ] `ChatListScreen` — recent conversations with last message preview
- [ ] `ChatScreen` — message bubbles, scroll to bottom, input field
- [ ] Streaming AI response (token-by-token display like typing indicator)
- [ ] Message timestamp display
- [ ] Voice-to-text input button in chat (uses STT provider)
- [ ] Copy message long-press action
- [ ] Empty state for new conversations ("Say hi to [Avatar]!")

### UI Components (new)
- `MessageBubble` — sent/received with tail, timestamp
- `TypingIndicator` — animated 3-dot bubble
- `ChatInputBar` — text field + send + mic button
- `ConversationRow` — list item with avatar image + last message

---

## Phase 5 — Wallet & Payments (Week 7)

### Screens to Build
1. `FundsScreen` — balance card + transaction history + recharge packs
2. `RechargeScreen` — pack selection + payment

### Implementation Tasks
- [ ] `WalletRepository` — Firestore user balance real-time listener
- [ ] `TransactionRepository` — transaction history paginated list
- [ ] `RechargePackRepository` — active packs from Firestore
- [ ] `GetBalanceUseCase`, `RechargeUseCase`, `GetTransactionHistoryUseCase`
- [ ] Razorpay Android SDK integration
- [ ] `/api/v1/payments/create-order` backend call
- [ ] `/api/v1/payments/verify` backend verification (server-side signature check)
- [ ] Firebase function (or FastAPI) to credit balance after payment
- [ ] `FundsScreen` — balance display, recharge button, transaction list
- [ ] `RechargeScreen` — pack cards, payment flow
- [ ] Payment success/failure bottom sheets
- [ ] Balance deduction during calls (real-time, server-side)
- [ ] Call end → show charges summary screen

### UI Components (new)
- `BalanceCard` — dark green gradient card with neon balance
- `RechargePackCard` — price, bonus badge, select state
- `TransactionRow` — type icon, amount, timestamp
- `PaymentResultSheet` — success (green) / failure (red) modal

---

## Phase 6 — Profile & Settings (Week 8)

### Screens to Build
1. `ProfileScreen` — user details, avatar display
2. `SettingsScreen` — preferences, legal links

### Implementation Tasks
- [ ] `UserRepository` — Firestore user document CRUD
- [ ] `ProfileViewModel`
- [ ] `ProfileScreen` — name, phone (masked), avatar, edit name
- [ ] `SettingsScreen` — language preference, notification toggles
- [ ] "Share app" intent
- [ ] "Rate app" → Play Store link
- [ ] "Community guidelines" → WebView or link
- [ ] Privacy policy + Terms of Service links
- [ ] Sign out with confirmation dialog
- [ ] Delete account flow (Firebase Auth + Firestore data)

---

## Phase 7 — Polish & Edge Cases (Week 9)

### 7.1 Call Quality & Reliability
- [ ] Network reconnection handling (LiveKit auto-reconnect)
- [ ] Pipeline failure UI (graceful "something went wrong, trying again")
- [ ] Call interrupted by incoming native call (AudioFocus handling)
- [ ] Background mode: keep call alive when app backgrounded
- [ ] Notification for ongoing call (foreground service)

### 7.2 Balance Edge Cases
- [ ] Block call start if balance < minimum (₹2)
- [ ] Auto-end call when balance reaches ₹0
- [ ] Lock call screen from exiting (warn user they'll be charged)

### 7.3 Onboarding Polish
- [ ] First-time user tutorial overlay (call screen)
- [ ] Push notification opt-in post-registration
- [ ] "Congratulations! You can now start calling people!" first-call celebration

### 7.4 Performance
- [ ] Optimize Coil image caching for avatar grid
- [ ] LazyColumn itemKeys for chat list
- [ ] Avoid recompositions in chat screen (stable lambdas)
- [ ] Memory leak audit (LeakCanary in debug)

### 7.5 Accessibility
- [ ] Content descriptions on all icon buttons
- [ ] TalkBack support for call controls
- [ ] Dynamic font size testing

---

## Phase 8 — QA & Release Prep (Week 10)

### 8.1 Testing
- [ ] Unit tests: all UseCases, ViewModels (mock repositories)
- [ ] Integration tests: Firebase Auth flow
- [ ] Pipeline unit tests: STT/LLM/TTS mock providers
- [ ] Manual testing on 5+ device models (cheap to flagship)
- [ ] Network condition testing (2G/3G simulation)

### 8.2 Security Audit
- [ ] API key audit (none in APK)
- [ ] Firestore rules review
- [ ] Payment verification end-to-end test
- [ ] ProGuard output review (no sensitive class names leaked)

### 8.3 Play Store Prep
- [ ] App signing key generation + secure storage
- [ ] Store listing: screenshots, description (Hindi + English)
- [ ] Privacy policy hosted URL
- [ ] Content rating questionnaire
- [ ] Target audience declaration (18+)
- [ ] Internal testing → Closed testing → Open testing → Production

### 8.4 Post-Launch Monitoring
- [ ] Firebase Crashlytics baseline
- [ ] Firebase Performance dashboard
- [ ] Pipeline health dashboard (Render logs)
- [ ] Balance deduction accuracy spot checks

---

## Tech Stack Summary

| Layer | Technology | Reason |
|-------|-----------|--------|
| Language | Kotlin | Native Android |
| UI | Jetpack Compose + Material3 | Modern Android UI |
| Navigation | Compose Navigation | Type-safe routes |
| DI | Hilt | Standard Android DI |
| State | ViewModel + StateFlow | Lifecycle-safe |
| Auth | Firebase Auth (Phone OTP) | Indian SMS OTP |
| Database | Firebase Firestore | Real-time, free tier |
| Storage | Firebase Storage | Avatar images |
| Remote Config | Firebase Remote Config | AI model switching |
| Realtime Voice | LiveKit Android SDK | WebRTC backbone |
| STT (primary) | Sarvam AI (saarika model) | Indian languages |
| STT (fallback) | Deepgram Nova-2 Hindi | English/Hindi |
| LLM (primary) | Groq (llama-3.3-70b) | Lowest latency |
| LLM (fallback) | OpenAI GPT-4o-mini | Reliability |
| TTS (primary) | Sarvam AI (bulbul model) | Indian voices |
| TTS (fallback) | ElevenLabs Flash v2 | Low latency |
| Backend | FastAPI on Render.com | Free tier, Python |
| Payments | Razorpay | UPI + Indian cards |
| Images | Coil | Kotlin-first |
| Fonts | Google Fonts (Urbanist) | Matches MVP design |
| Monitoring | Firebase Crashlytics + Perf | Free |
| CI/CD | GitHub Actions + Firebase App Distribution | Standard |

---

## Environment Configuration

```
# local.properties (never commit)
LIVEKIT_URL=wss://your-project.livekit.cloud
LIVEKIT_API_KEY=...
LIVEKIT_API_SECRET=...
SARVAM_API_KEY=...
DEEPGRAM_API_KEY=...
GROQ_API_KEY=...
OPENAI_API_KEY=...
ELEVENLABS_API_KEY=...
RAZORPAY_KEY_ID=...
BACKEND_BASE_URL=https://aurbatao-api.onrender.com
```

```kotlin
// BuildConfig access in app
object Config {
    const val LIVEKIT_URL = BuildConfig.LIVEKIT_URL
    const val BACKEND_URL = BuildConfig.BACKEND_BASE_URL
    // API keys for STT/TTS/LLM are fetched from backend
    // NEVER embed them in the APK
}
```

**Rule:** All third-party AI API keys live on the FastAPI backend. The Android app only holds the backend URL + Razorpay key ID (public).

---

## Risk Register

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Sarvam API latency spike | Medium | High | Deepgram fallback, timeout 3s |
| LiveKit room creation failure | Low | High | Retry 3x with exponential backoff |
| Razorpay payment gateway downtime | Low | High | Show retry option, don't block balance |
| Firebase free tier limits (50K reads/day) | Medium | Medium | Cache aggressively, paginate |
| User balance goes negative | Low | High | Server-side pre-check before call start |
| Play Store rejection (content moderation) | Medium | High | 18+ flag, community guidelines, content filter in LLM |
