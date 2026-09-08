# LiveLock

갤럭시(One UI) 잠금화면·홈화면에 3D 캐릭터+모션, 사진/모션포토/동영상, 음성을 적용하고,
알림에 반응하는 안드로이드 앱입니다.

## 주요 기능
- 3D 캐릭터 라이브 월페이퍼 (Filament 기반 실시간 렌더)
- 사진/모션포토/동영상 → 잠금화면 배경 적용
- 음성/BGM 재생 + 애니메이션 클립 매칭
- 알림 수신 시 캐릭터 반응 (클립 전환/말풍선/효과음)
- 잠금화면 동영상 배경 (원UI 연동, ≤15초)

## PC에서 빌드하기 (Android Studio)

### 사전 요구
- Android Studio Hedgehog (2023.1.1) 이상
- JDK 17
- Android SDK Platform 34
- Android Build Tools 34.0.0

### 빌드 步骤
```bash
# 1. 프로젝트를 PC로 복사 (make4 폴더 전체)
# 2. Android Studio에서 make4 폴더 열기 (Open)
# 3. Gradle 동기화 대기
# 4. 실행: Shift+F10 또는 ▶ 버튼
```

### APK 생성
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
→ app/build/outputs/apk/debug/app-debug.apk
```

## 프로젝트 구조
```
make4/
├── app/
│   ├── build.gradle.kts          # 앱 모듈 빌드
│   └── src/main/
│       ├── AndroidManifest.xml   # 권한 + 서비스 선언
│       ├── assets/
│       │   ├── models/Fox.glb    # 3D 캐릭터 샘플 (CC0)
│       │   └── audio/            # 오디오 샘플 (CC0)
│       ├── java/com/livelock/app/
│       │   ├── MainActivity.kt
│       │   ├── MainScreen.kt
│       │   ├── media/            # 모션포토 추출, 비디오 트림
│       │   ├── notify/           # 알림 반응 서비스
│       │   ├── wallpaper/        # 비디오 월페이퍼 서비스
│       │   └── ui/theme/         # Compose 테마
│       └── res/                  # 리소스
├── build.gradle.kts              # 루트 빌드
├── settings.gradle.kts
├── gradle.properties
└── PLAN.md                       # 개발 계획서
```

## 권한 안내
| 권한 | 용도 |
|---|---|
| SET_WALLPAPER | 배경화면 설정 |
| 미디어 익스세스 | 사진/동영상/오디오 선택 |
| 알림 접근 | 알림 반응 기능 |

## 기술 스택
- Kotlin 2.x + Jetpack Compose (Material 3)
- Filament (3D PBR 렌더)
- Media3 ExoPlayer (비디오/오디오)
- MediaCodec/MediaMuxer (MP4 인코딩)

## 샘플 출처
- Fox.glb: KhronosGroup/glTF-Sample-Models (CC0)
- 오디오: Kenney.nl (CC0)
