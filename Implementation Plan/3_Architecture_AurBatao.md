# AurBatao — Architecture Design
**Version:** 2.0 | **Platform:** Native Android (Kotlin)
**Prepared for:** Antigravity Dev Team

---

## 1. High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     AurBatao Android App                        │
│                   (Kotlin + Jetpack Compose)                    │
│                                                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │  Auth    │  │  Calls   │  │  Chats   │  │  Funds   │       │
│  │  Module  │  │  Module  │  │  Module  │  │  Module  │       │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘       │
│       │              │              │              │             │
│  ┌────▼──────────────▼──────────────▼──────────────▼─────┐     │
│  │              Domain Layer (UseCases + Entities)        │     │
│  └────────────────────────┬───────────────────────────────┘     │
│                           │                                     │
│  ┌────────────────────────▼───────────────────────────────┐     │
│  │              Data Layer (Repositories)                 │     │
│  └──────┬──────────┬───────────┬────────────┬────────────┘     │
│         │          │           │            │                   │
│      Firebase   LiveKit    AI Pipeline   Razorpay              │
│      (Auth/DB)  (Voice)    (STT/LLM/TTS) (Payments)           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Module Structure

```
app/
├── src/main/java/com/blackm/aurbatao/
│   ├── AurBataoApplication.kt
│   ├── MainActivity.kt
│   │
│   ├── core/
│   │   ├── di/                    # Hilt DI modules
│   │   ├── network/               # OkHttp, Retrofit setup
│   │   ├── storage/               # DataStore, SecureStore
│   │   ├── theme/                 # Compose theme, colors, typography
│   │   └── utils/                 # Extensions, helpers
│   │
│   ├── features/
│   │   ├── auth/
│   │   │   ├── data/              # FirebaseAuthRepository
│   │   │   ├── domain/            # LoginUseCase, VerifyOTPUseCase
│   │   │   └── presentation/      # WelcomeScreen, LoginScreen, OTPScreen
│   │   │
│   │   ├── avatars/
│   │   │   ├── data/              # AvatarRepository (Firestore)
│   │   │   ├── domain/            # GetAvatarsUseCase, Avatar entity
│   │   │   └── presentation/      # AvatarListScreen, AvatarDetailSheet
│   │   │
│   │   ├── calls/
│   │   │   ├── data/              # CallRepository, LiveKitManager
│   │   │   ├── domain/            # StartCallUseCase, EndCallUseCase
│   │   │   ├── pipeline/          # AI Voice Pipeline (core)
│   │   │   │   ├── stt/           # STT plugin system
│   │   │   │   ├── llm/           # LLM plugin system
│   │   │   │   ├── tts/           # TTS plugin system
│   │   │   │   └── VoicePipelineManager.kt
│   │   │   └── presentation/      # CallsListScreen, ActiveCallScreen
│   │   │
│   │   ├── chats/
│   │   │   ├── data/              # ChatRepository (Firestore)
│   │   │   ├── domain/            # SendMessageUseCase, GetHistoryUseCase
│   │   │   └── presentation/      # ChatListScreen, ChatScreen
│   │   │
│   │   └── funds/
│   │       ├── data/              # WalletRepository, PaymentRepository
│   │       ├── domain/            # RechargeUseCase, GetBalanceUseCase
│   │       └── presentation/      # FundsScreen, RechargeScreen
│   │
│   └── navigation/
│       ├── AurBataoNavHost.kt
│       ├── AuthNavGraph.kt
│       └── CoreNavGraph.kt
│
├── build.gradle.kts
└── AndroidManifest.xml
```

---

## 3. Navigation Architecture

```kotlin
// Root navigation — auth state decides entry point
sealed class Screen(val route: String) {
    // Public (unauthenticated)
    object Welcome  : Screen("welcome")
    object Login    : Screen("login")
    object OTP      : Screen("otp/{phone}")
    object SetName  : Screen("set_name")

    // Core (authenticated) — Bottom Nav
    object Calls    : Screen("calls")
    object Chats    : Screen("chats")
    object Funds    : Screen("funds")
    object Settings : Screen("settings")

    // Detail screens
    object ActiveCall    : Screen("active_call/{avatarId}")
    object ChatDetail    : Screen("chat/{avatarId}")
    object AvatarDetail  : Screen("avatar/{avatarId}")
    object Recharge      : Screen("recharge")
}
```

---

## 4. Voice Pipeline Architecture (Multi-Model)

### 4.1 Plugin Interface Pattern

```kotlin
// ── STT Plugin ──────────────────────────────────────
interface STTProvider {
    val name: String
    val priority: Int
    suspend fun isHealthy(): Boolean
    fun transcribe(audioStream: Flow<ByteArray>): Flow<STTResult>
}

data class STTResult(
    val text: String,
    val isFinal: Boolean,
    val confidence: Float,
    val language: Language
)

// Implementations
class SarvamSTTProvider  : STTProvider { /* primary */ }
class DeepgramSTTProvider: STTProvider { /* fallback 1 */ }
class WhisperSTTProvider : STTProvider { /* fallback 2 */ }

// ── LLM Plugin ──────────────────────────────────────
interface LLMProvider {
    val name: String
    val priority: Int
    suspend fun isHealthy(): Boolean
    fun generate(
        messages: List<ChatMessage>,
        systemPrompt: String,
        persona: AvatarPersona
    ): Flow<String>  // streaming tokens
}

class GroqLLMProvider    : LLMProvider { /* primary — fastest */ }
class OpenAILLMProvider  : LLMProvider { /* fallback 1 */ }
class GeminiLLMProvider  : LLMProvider { /* fallback 2 */ }

// ── TTS Plugin ──────────────────────────────────────
interface TTSProvider {
    val name: String
    val priority: Int
    suspend fun isHealthy(): Boolean
    fun synthesize(text: String, voice: VoiceConfig): Flow<ByteArray>
}

class SarvamTTSProvider    : TTSProvider { /* primary — Indian voices */ }
class ElevenLabsTTSProvider: TTSProvider { /* fallback 1 */ }
class CartesiaTTSProvider  : TTSProvider { /* fallback 2 */ }
```

### 4.2 Pipeline Manager (Fallback Logic)

```kotlin
class VoicePipelineManager @Inject constructor(
    private val sttProviders: List<STTProvider>,     // ordered by priority
    private val llmProviders: List<LLMProvider>,
    private val ttsProviders: List<TTSProvider>,
    private val liveKitRoom: Room,
    private val remoteConfig: FirebaseRemoteConfig
) {
    // Auto-select healthy provider with fallback
    suspend fun <T> withFallback(
        providers: List<T>,
        healthCheck: suspend (T) -> Boolean,
        action: suspend (T) -> Flow<*>
    ): Flow<*> {
        for (provider in providers.sortedBy { /* priority */ }) {
            if (healthCheck(provider)) {
                return try {
                    action(provider)
                } catch (e: Exception) {
                    Log.w("Pipeline", "Provider failed, trying next: $e")
                    continue
                }
            }
        }
        throw NoPipelineProviderException("All providers failed")
    }

    // Full turn: audio → text → LLM → audio
    suspend fun processTurn(
        audioInput: Flow<ByteArray>,
        conversationHistory: List<ChatMessage>,
        persona: AvatarPersona
    ): Flow<PipelineEvent>

    sealed class PipelineEvent {
        data class Transcribing(val partial: String) : PipelineEvent()
        data class Thinking(val tokens: String) : PipelineEvent()
        data class Speaking(val audio: ByteArray) : PipelineEvent()
        object TurnComplete : PipelineEvent()
        data class Error(val cause: Throwable, val recoverable: Boolean) : PipelineEvent()
    }
}
```

### 4.3 LiveKit Integration

```kotlin
class LiveKitManager @Inject constructor(
    private val context: Context,
    private val tokenService: LiveKitTokenService  // your backend
) {
    private var room: Room? = null

    suspend fun joinRoom(
        avatarId: String,
        userId: String
    ): Room {
        val token = tokenService.getToken(avatarId, userId)
        val roomOptions = RoomOptions(
            adaptiveStream = true,
            dynacast = true,
            audioCaptureDefaults = AudioCaptureOptions(
                echoCancellation = true,
                noiseSuppression = true,
                autoGainControl = true
            )
        )
        room = LiveKit.connect(
            context,
            url = BuildConfig.LIVEKIT_URL,
            token = token,
            options = roomOptions
        )
        return room!!
    }

    fun publishMicrophone(): LocalAudioTrack
    fun muteLocalAudio(muted: Boolean)
    suspend fun disconnect()
}
```

---

## 5. Firebase Architecture

### 5.1 Firestore Schema

```
/users/{uid}
  - displayName: String
  - phone: String
  - createdAt: Timestamp
  - balance: Number          ← wallet balance in paise (₹)
  - preferredLanguage: String
  - avatarUrl: String?

/avatars/{avatarId}
  - name: String
  - gender: String
  - languages: [String]
  - persona: String          ← system prompt
  - voiceId: String          ← TTS voice identifier
  - imageUrl: String
  - isActive: Boolean
  - callRatePerMin: Number   ← paise per minute
  - tags: [String]

/conversations/{conversationId}
  - userId: String
  - avatarId: String
  - startedAt: Timestamp
  - type: "voice" | "text"
  - durationSeconds: Number?
  - chargesDeducted: Number

/conversations/{conversationId}/messages/{messageId}
  - role: "user" | "assistant"
  - content: String
  - timestamp: Timestamp
  - audioUrl: String?

/transactions/{txId}
  - userId: String
  - type: "recharge" | "call_charge" | "refund"
  - amount: Number           ← paise
  - createdAt: Timestamp
  - metadata: Map<String, Any>

/recharge_packs/{packId}
  - name: String
  - amount: Number           ← paise credited
  - price: Number            ← INR price
  - bonusAmount: Number?
  - isActive: Boolean
```

### 5.2 Firebase Security Rules (Firestore)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    match /avatars/{avatarId} {
      allow read: if request.auth != null;
      allow write: if false; // admin only
    }
    match /conversations/{convId} {
      allow read, write: if request.auth.uid == resource.data.userId;
    }
    match /transactions/{txId} {
      allow read: if request.auth.uid == resource.data.userId;
      allow write: if false; // server-side only
    }
  }
}
```

---

## 6. Backend (FastAPI)

### 6.1 Endpoints

```
POST /api/v1/livekit/token
  → Body: { avatarId, userId }
  → Returns: { token, roomName }

POST /api/v1/payments/create-order
  → Body: { userId, packId }
  → Returns: { orderId, amount, currency }

POST /api/v1/payments/verify
  → Body: { orderId, paymentId, signature }
  → Returns: { success, newBalance }

POST /api/v1/calls/start
  → Body: { userId, avatarId }
  → Returns: { conversationId }

POST /api/v1/calls/end
  → Body: { conversationId, durationSeconds }
  → Deducts balance, returns { chargesDeducted, remainingBalance }

GET /api/v1/pipeline/health
  → Returns: { stt: {}, llm: {}, tts: {} } per provider
```

### 6.2 Hosting
- **FastAPI on Render.com** (free tier, spins down after 15min inactivity)
- Keep-alive ping via Firebase Cloud Messaging or cron
- Later: migrate to Railway or DigitalOcean App Platform

---

## 7. AI Model Registry (Remote Config)

```json
// Firebase Remote Config — no app update needed to swap models
{
  "stt_providers": [
    {"name": "sarvam", "enabled": true, "priority": 1, "timeout_ms": 3000},
    {"name": "deepgram", "enabled": true, "priority": 2, "timeout_ms": 5000},
    {"name": "whisper", "enabled": false, "priority": 3, "timeout_ms": 8000}
  ],
  "llm_providers": [
    {"name": "groq", "model": "llama-3.3-70b-versatile", "enabled": true, "priority": 1},
    {"name": "openai", "model": "gpt-4o-mini", "enabled": true, "priority": 2},
    {"name": "gemini", "model": "gemini-1.5-flash", "enabled": false, "priority": 3}
  ],
  "tts_providers": [
    {"name": "sarvam", "enabled": true, "priority": 1},
    {"name": "elevenlabs", "enabled": true, "priority": 2},
    {"name": "cartesia", "enabled": false, "priority": 3}
  ]
}
```

---

## 8. Dependency Injection (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirebaseAuth() = Firebase.auth

    @Provides @Singleton
    fun provideFirestore() = Firebase.firestore

    @Provides @Singleton
    fun provideLiveKitManager(@ApplicationContext ctx: Context) =
        LiveKitManager(ctx, LiveKitTokenService(/* retrofit */))

    @Provides
    fun provideSTTProviders(
        sarvam: SarvamSTTProvider,
        deepgram: DeepgramSTTProvider
    ): List<STTProvider> = listOf(sarvam, deepgram)

    @Provides
    fun provideLLMProviders(
        groq: GroqLLMProvider,
        openai: OpenAILLMProvider
    ): List<LLMProvider> = listOf(groq, openai)

    @Provides
    fun provideTTSProviders(
        sarvam: SarvamTTSProvider,
        elevenlabs: ElevenLabsTTSProvider
    ): List<TTSProvider> = listOf(sarvam, elevenlabs)
}
```

---

## 9. State Management Pattern (ViewModels)

All screens use **MVVM + UiState sealed class pattern**:

```kotlin
// Example: CallViewModel
data class CallUiState(
    val status: CallStatus = CallStatus.IDLE,
    val avatar: Avatar? = null,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val transcript: String = "",
    val balance: Long = 0,
    val error: String? = null
)

enum class CallStatus { IDLE, CONNECTING, CONNECTED, ENDING, ENDED }

@HiltViewModel
class CallViewModel @Inject constructor(
    private val startCallUseCase: StartCallUseCase,
    private val liveKitManager: LiveKitManager,
    private val voicePipeline: VoicePipelineManager
) : ViewModel() {
    val uiState: StateFlow<CallUiState> = MutableStateFlow(CallUiState())
    // ...
}
```

---

## 10. Security

| Concern | Solution |
|---------|----------|
| API keys | Android Keystore / BuildConfig secrets / backend-proxied |
| User auth tokens | Firebase ID tokens, auto-refreshed |
| Payments | Razorpay server-side verification (never trust client) |
| Balance deduction | Server-side only, Firestore transactions |
| Call recordings | Not stored (audio processed in-memory only) |
| Phone numbers | Hashed before storage; never exposed in responses |
| ProGuard | Enabled for release builds |
| Certificate pinning | OkHttp `CertificatePinner` for backend calls |

---

## 11. Gradle Dependencies

```kotlin
// build.gradle.kts (app)
dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    // LiveKit
    implementation("io.livekit:livekit-android:2.1.1")

    // DI
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Payments
    implementation("com.razorpay:checkout:1.6.40")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.0")
}
```

---

## 12. Performance Targets

| Metric | Target | Measurement |
|--------|--------|-------------|
| App cold start | < 2s | Android Vitals |
| Voice turn latency | < 1.5s | Custom trace |
| Chat message send→receive | < 2s | Firebase Perf |
| Avatar list load | < 500ms | Custom trace |
| Memory (active call) | < 150MB | Android Profiler |
| Battery (60min call) | < 8% drain | Manual test |
