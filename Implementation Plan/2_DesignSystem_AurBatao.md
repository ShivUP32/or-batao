# AurBatao — Design System
**Version:** 2.0 | **Platform:** Native Android (Jetpack Compose)
**Extracted from:** APK `app-release.apk` + bundle analysis

---

## 1. Brand Identity

**App Name:** AurBatao
**Scheme:** `aurBatao://`
**Logo:** `assets/images/logo.png`
**Splash BG:** `#161615`
**Orientation:** Portrait only
**Theme:** Dark-first, warm neon accents on dark backgrounds

---

## 2. Color Palette

All colors extracted directly from the compiled bundle.

### 2.1 Primary Backgrounds (Dark Theme)
```
Background Deep:     #161615   // Splash, primary screen bg
Background Surface:  #1D2025   // Cards, sheets
Background Card:     #212436   // Elevated cards
Background Subtle:   #272726   // Dividers, subtle surfaces
Background Input:    #32322F   // Input fields
Background Muted:    #3A3838   // Disabled states
```

### 2.2 Brand Accent Colors
```
Neon Green Primary:  #0AFF96   // Primary CTA, active states
Neon Green Dark:     #00FFA3   // Hover/pressed green
Teal Accent:         #3ACBAC   // Secondary accent
Lime Accent:         #6FE173   // Success, positive states
Yellow-Green:        #96D941   // Balance, earnings
Lime Bright:         #98DF3D   // Highlighted values
```

### 2.3 Warm Accents
```
Gold Primary:        #FEC424   // Primary CTA buttons (non-neon)
Gold Bright:         #FFC122   // Button hover
Amber:               #FFA81A   // Warnings, highlights
Yellow Soft:         #FCDC29   // Stars, ratings
Yellow Pale:         #F6EB85   // Text highlight
Cream:               #E9DC6E   // Warm text accent
```

### 2.4 System Colors
```
Error/Danger:        #BF2B2B   // Error states
Pink Alert:          #F92C6F   // Critical alerts
Purple (Clerk):      #6c47ff   // Auth provider accent
Blue (System):       #007AFF   // iOS compat / links
Blue Material:       #2196F3   // Material info
Blue Bright:         #4096FA   // Interactive elements
```

### 2.5 Text Colors
```
Text Primary:        #FCFCFB   // Main readable text
Text Secondary:      #D9D9D9   // Subtitles, captions
Text Muted:          #A0A0A0   // Disabled, hints
Text Dimmed:         #7C7D82   // Placeholders
Text Dark Hint:      #828986   // Very muted
```

### 2.6 Dark Green Tones (Avatar/Brand)
```
Deep Forest:         #082720   // Avatar card overlay
Dark Green:          #0A483B   // Deep accent bg
```

---

## 3. Typography

**Font Family:** Urbanist (Google Fonts)

Weights found in bundle:
- `Urbanist_100Thin`
- `Urbanist_200ExtraLight`
- `Urbanist_300Light`
- `Urbanist_400Regular`
- `Urbanist_500Medium`
- `Urbanist_600SemiBold`
- `Urbanist_700Bold`
- `Urbanist_800ExtraBold`
- `Urbanist_900Black`

### Type Scale (Compose `sp`)
```kotlin
// Compose TextStyle definitions
val AurBataoTypography = Typography(
    displayLarge  = TextStyle(fontFamily = Urbanist, fontWeight = W900, fontSize = 32.sp),
    displayMedium = TextStyle(fontFamily = Urbanist, fontWeight = W800, fontSize = 28.sp),
    headlineLarge = TextStyle(fontFamily = Urbanist, fontWeight = W700, fontSize = 24.sp),
    headlineMedium= TextStyle(fontFamily = Urbanist, fontWeight = W700, fontSize = 20.sp),
    titleLarge    = TextStyle(fontFamily = Urbanist, fontWeight = W600, fontSize = 18.sp),
    titleMedium   = TextStyle(fontFamily = Urbanist, fontWeight = W600, fontSize = 16.sp),
    bodyLarge     = TextStyle(fontFamily = Urbanist, fontWeight = W400, fontSize = 16.sp),
    bodyMedium    = TextStyle(fontFamily = Urbanist, fontWeight = W400, fontSize = 14.sp),
    bodySmall     = TextStyle(fontFamily = Urbanist, fontWeight = W400, fontSize = 12.sp),
    labelLarge    = TextStyle(fontFamily = Urbanist, fontWeight = W500, fontSize = 14.sp),
    labelMedium   = TextStyle(fontFamily = Urbanist, fontWeight = W500, fontSize = 12.sp),
    labelSmall    = TextStyle(fontFamily = Urbanist, fontWeight = W500, fontSize = 10.sp),
)
```

---

## 4. Spacing & Layout

```
Base unit: 4dp

XS:   4dp
SM:   8dp
MD:   12dp
LG:   16dp
XL:   24dp
2XL:  32dp
3XL:  48dp
4XL:  64dp

Screen horizontal padding: 20dp
Card padding: 16dp
Bottom nav height: 56dp
Status bar: edge-to-edge (transparent)
```

---

## 5. Component Specifications

### 5.1 Bottom Navigation Bar
```kotlin
// 4 tabs: Calls, Chats, Funds, Settings
backgroundColor = #161615
selectedTintColor = #0AFF96     // Neon green
unselectedTintColor = #828986   // Muted gray
indicatorColor = #0A483B        // Dark green pill under icon
height = 56dp
iconSize = 24dp
labelStyle = labelMedium
```

### 5.2 Primary Button (CTA)
```
Background: #FEC424 (Gold) or #0AFF96 (Neon Green)
Text: #161615 (Dark, always)
Font: Urbanist SemiBold 16sp
Height: 52dp
Corner radius: 14dp
Horizontal padding: 24dp
Pressed state: scale(0.97) + darken 10%
Disabled: background #3A3838, text #7C7D82
```

### 5.3 Avatar Card
```
Background: #212436
Corner radius: 16dp
Image: full-width top, 200dp height, scaleType=centerCrop
Name: headlineMedium, color #FCFCFB
Subtitle: bodySmall, color #A0A0A0
Status badge: #0AFF96 bg, #082720 text, 6dp corner radius
Call button: Gold CTA button, bottom of card
Shadow: elevation 4dp
```

### 5.4 Call Screen
```
Background: full-screen #161615
Avatar image: 120dp circle, center
Avatar name: displayMedium, white
Call status: titleMedium, #3ACBAC
Timer: headlineLarge, monospace, #FCFCFB
Mic button: 64dp FAB, bg #1D2025, icon #FCFCFB
  → Muted state: bg #BF2B2B, icon white
End call button: 72dp FAB, bg #BF2B2B, icon white phone-down
Speaker button: 48dp, bg #272726
Waveform: animated bars, color #0AFF96
```

### 5.5 Chat Bubble
```
Sent (user):
  background: #3ACBAC
  text: #161615
  corner radius: 16dp 16dp 4dp 16dp
  alignment: end

Received (AI):
  background: #212436
  text: #FCFCFB
  corner radius: 4dp 16dp 16dp 16dp
  alignment: start

Timestamp: labelSmall, #7C7D82
Max width: 72% of screen width
```

### 5.6 OTP Input Field
```
Boxes: 4 or 6, each 52dp × 56dp
Border: 1.5dp, color #3A3838 (unfocused), #0AFF96 (focused)
Background: #272726
Text: displayMedium, centered, #FCFCFB
Corner radius: 10dp
Gap between boxes: 12dp
```

### 5.7 Balance / Wallet Card
```
Background gradient: #0A483B → #082720 (vertical)
Corner radius: 20dp
Balance text: displayLarge, #0AFF96 neon green
Label: bodyMedium, #A4D0A4
Recharge button: Gold CTA
Width: full screen - 32dp margins
```

### 5.8 Recharge Pack Card
```
Background: #212436
Selected border: 2dp #0AFF96
Corner radius: 12dp
Price: headlineMedium, #FEC424 gold
Pack details: bodyMedium, #D9D9D9
Bonus badge: #4A2E01 bg, #FED64A text
```

### 5.9 Star Rating
```
Filled star: #FCDC29
Empty star: #3A3838
Size: 32dp
Spacing: 8dp
```

### 5.10 Loading / Waveform Animation
```
3–5 animated vertical bars
Color: #0AFF96
Height animates: 8dp → 32dp (spring animation)
Width per bar: 4dp, corner radius 2dp
Gap: 4dp
Duration: 600ms, staggered 100ms offset
```

---

## 6. Icons

**Primary set:** MaterialCommunityIcons (found in bundle)
**Secondary set:** FontAwesome5, Ionicons, Entypo

### Key Icon Mappings
```
Microphone (active):   microphone
Microphone (muted):    microphone-off
Phone (call):          phone
Phone (end):           phone-hangup
Speaker:               volume-high
Chat bubble:           message-text
Wallet:                wallet
Recharge:              credit-card-plus
Star (rating):         star
Back arrow:            arrow-left
Settings:              cog
Avatar:                account-circle
```

---

## 7. Animations

### 7.1 Screen Transitions
- Enter: `SlideInRight` (300ms, ease-out)
- Exit: `SlideOutLeft` (300ms, ease-in)
- Modal: `SlideInUp` (350ms, spring)

### 7.2 Call Connection
- Pulsing avatar circle (scale 1.0 → 1.08 → 1.0, 1200ms loop)
- Neon green ring opacity pulse

### 7.3 Speaking Indicator
- Waveform bars animated with spring physics
- Reanimated-style spring (stiffness 100, damping 10)

### 7.4 Button Press
- Scale down: `scale(0.97)`, duration 80ms
- Restore: spring, stiffness 300

---

## 8. Dark/Light Mode
- **App is dark-mode first** (splash bg `#161615`)
- `userInterfaceStyle: "automatic"` in original config
- For v2 Native: implement both, but default dark
- All components must have explicit dark + light color tokens

---

## 9. Assets Inventory

| Asset | Type | Usage |
|-------|------|-------|
| logo.png | PNG | App icon, splash |
| favicon.png | PNG | Web (skip for native) |
| splash.png | PNG | Splash screen, resize=contain |
| 21 avatar webp images | WebP | Avatar cards and call screen |
| Urbanist font (9 weights) | TTF | All text |

---

## 10. Accessibility
- Minimum touch target: 48×48dp
- Content descriptions on all icon buttons
- TalkBack support for call controls
- Minimum contrast ratio: 4.5:1 for body text
- Dynamic font size: support up to 130% system font scale
