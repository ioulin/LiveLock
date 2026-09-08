# LiveLock 개발 계획서 (v6 · 확정 · Plan A 전환)
작성: 2026-09-08 · 작성자: Cline · 상태: Act 진행 중 (소스 생성 단계)

## ⚠️ 실행 환경 진단 결과 (Phase 0)
- 환경: DroidDesk (테옥스 호환 레이어), aarch64
- aapt2 정적 바이너리: 동작
- java/gradle/dash: 공유라이브러리(libc.so) 부재로 실행 불가
- debian proot: 테옥스 프리픽스 접근 불가
- 결론: 온디바이스 Gradle 불가 → Plan A 전환 (소스 생성 → PC Android Studio 빌드)

## 1. 프로젝트 개요
- 목표: 갤럭시(One UI)에서 3D 캐릭터+모션, 사진/모션포토/동영상, 음성을
  잠금화면·홈화면 배경으로 적용 + 알림 반응 안드로이드 앱
- 산출물: make4 프로젝트 전체 소스 (PC Android Studio로 빌드)
- 현재 자료 없음 → 전부 무료 CC0 샘플, 본인 자료 교체 기능 유지

## 2. 기술 제약 (사실관계)
- 정적 잠금화면: WallpaperManager.setBitmap(FLAG_LOCK), API 24+ → 직접 적용
- 라이브 월페이퍼: 홈화면 전용 (잠금 라이브는 시스템 권한 → 불가)
- 잠금화면 동영상: 원UI 내장 기능 유도 (~15초 트림, 무음 루프)
- 잠금화면 음성: 원UI 동영상 배경 무음 강제 → 알림음/알람음 등록으로 우회
- 홈 월페이퍼 음성: WallpaperService 내 ExoPlayer (오디오 포커스 필수)
- 모션포토: JPEG 후행 MP4 → 자체 파서 추출
- 알림 반응: NotificationListenerService (홈 월페이퍼 클립 전환/말풍선/효과음)

## 3. 소스 전략 (무료 샘플 확정)
| 용도 | 샘플 | 라이선스 |
|---|---|---|
| 3D 캐릭터 + 모션 | Fox.glb (Survey/Walk/Run) | CC0+CC-BY 4.0 |
| 효과음 | Kenney Interface Sounds | CC0 |
| BGM | Kenney Music Jingles | CC0 |
- 앱에 "내 음원/내 모델 불러오기" 기능 유지 → 본인 파일 교체 가능
- 3D 렌더: 앱 내 Filament/SceneView 실시간(GPU)
- 립싱크: 1차 제외 (타이밍 매칭만)

## 4. 마일스톤 (Plan A)
- A0: PLAN.md 저장 완료
- A1: 샘플 다운로드 (Fox.glb + Kenney 오디오) → app/src/main/assets
- M1: 프로젝트 스캐폴딩 (Gradle, Kotlin 2.x, Compose M3, minSdk 26, target 34)
- M2: Photo Picker + MotionPhotoExtractor
- M3: 편집 + 3D 화면 + 음성 선택·클립 매칭
- M4: 홈 3D 라이브 월페이퍼 (음성+알림 반응) + 잠금 정적 적용
- M5: 잠금용 MP4 굽기 + 원UI 유도 + 알림음 등록 + 알림시 잠금배경 교체(실험)
- M6: 마무리 (오류처리, 배터리, 다크모드, README, 빌드 가이드)

## 5. 권한
- SET_WALLPAPER + BIND_NOTIFICATION_LISTENER_SERVICE + 알림 접근 허용(사용자)
- 저장소 권한 불필요 (Photo Picker/SAF)

## 7. 진행 기록
- 2026-09-08 A0: PLAN.md v6 저장 완료
- 2026-09-08 Phase 0: 온디바이스 빌드 불가 확인 → Plan A 전환
- 2026-09-08 A1: 샘플 다운로드 (Fox.glb 162KB + bgm_loop.ogg) 완료
- 2026-09-08 M1: 프로젝트 스캐폴딩 완료 (18개 파일, 전체 구조 생성)
- 다음 단계: M2 (Photo Picker + MotionPhotoExtractor 연동)

  잠금화면·홈화면 배경으로 적용하는 안드로이드 앱
- 산출물: make4 프로젝트 전체 소스 + app-debug.apk (온디바이스 빌드)
- 개발 환경: 기기 내 Termux (aarch64) — 온디바이스 Gradle 빌드
- 현재 자료 없음 → 전부 무료 CC0 샘플로 처리, 본인 자료 교체 기능 유지

## 2. 기술 제약 (사실관계)
- 정적 잠금화면: WallpaperManager.setBitmap(FLAG_LOCK), API 24+ → 직접 적용 ✅
- 라이브 월페이퍼: 홈화면 전용 (잠금화면 라이브는 시스템 권한 필요 → 불가 ❌)
- 잠금화면 동영상: 원UI 내장 기능 유도 (갤러리 동영상, ~15초 트림, 무음 루프)
- 잠금화면 음성: 원UI 동영상 배경은 무음 강제 → 시스템 알림음/알람음 등록으로 우회 ✅
- 홈 월페이퍼 음성: WallpaperService 내 ExoPlayer 재생 가능 (오디오 포커스 필수)
- 모션포토: JPEG 후행 MP4 (삼성 SEFT/SEMH · 구글 XMP MicroVideo) → 자체 파서 추출
- 기기 현황: ffmpeg 8.1.2 ✅ / Blender 3.6.23 설치되었으나 libpython3.13.so 누락으로
  실행 불가 (python 3.14 충돌) → 블렌더는 옵션 트랙, 기본은 앱 내 GPU 렌더(트랙 B)

## 3. 소스 전략 (무료 샘플 확정)
| 용도 | 샘플 | 라이선스 | 상태 |
|---|---|---|---|
| 3D 캐릭터 + 모션 | Fox.glb (Survey/Walk/Run 3클립) | CC0(모델)+CC-BY 4.0(애니) | 검증됨 |
| 효과음 | Kenney Interface Sounds (100종) | CC0 | 검증됨 |
| BGM | Kenney Music Jingles (85종) | CC0 | 검증됨 |
| 보이스 샘플 | Kenney Voiceover Pack | CC0 | 후보 |
- 앱에 "내 음원/내 모델 불러오기" 기능 유지 → 나중에 본인 파일로 교체 가능
- 3D 렌더: 앱 내 Filament/SceneView 실시간(GPU). 홈=실시간 3D 월페이퍼,
  잠금=오프스크린 GL → MediaCodec H.264+AAC → MediaMuxer 굽기(≤15초 루프)
- 립싱크: 1차 버전 제외 (애니메이션 클립↔음성 타이밍 매칭만)

## 4. 알림 반응 (v5.1 추가)
- 홈 3D 월페이퍼: NotificationListenerService(알림 접근 허용)로 알림 수신 →
  앱별·키워드별 반응 규칙 → 캐릭터 클립 전환 + 말풍선(토글) + 효과음 ✅
- 잠금화면: 실시간 반응 불가(시스템 렌더) / 실험적 우회 — 알림 수신 시
  setBitmap(FLAG_LOCK)으로 반응 포즈 이미지 교체 (다음 잠금 렌더부터 반영, 실험 항목)
- 조합 연출: 알림음을 캐릭터 보이스로 등록 시 알림+보이스 자연 연출

## 5. 설치 매니페스트 (소스 검증 완료)
| 항목 | 소스 | 상태 |
|---|---|---|
| openjdk-17, gradle, apksigner, d8 | Termux pkg | 표준 |
| aapt2 ARM64 | Termux pkg (aapt2_16.0.0.4-2_aarch64) | 검증됨 |
| 백업 build-tools 35.0.0 ARM64 | GitHub: lzhiyong/termux-ndk android-sdk 릴리스 | 검증됨 |
| Fox.glb | GitHub: KhronosGroup/glTF-Sample-Models 2.0/Fox | 검증됨 |
| Kenney 오디오 | kenney.nl (Interface Sounds/Music Jingles) | 검증됨 |
| AGP/AndroidX/Filament | Google Maven | gradle 자동 |
| platforms;android-34 | sdkmanager(Java) | Phase 0 설치 |
- gradle.properties: android.aapt2FromMavenOverride=<Termux aapt2 경로>

## 6. 마일스톤
- A0: 본 문서 저장(PLAN.md) ← 현재
- Phase 0: 도구 설치 → 빈 프로젝트 assembleDebug 검증
  (실패 시: GitHub build-tools 35.0.0 교체 → 그래도 실패 시 Android Studio 폴백)
- T0: Fox.glb + Kenney 오디오 다운로드 → assets 배치 → 로딩·재생 검증
- M1: 스캐폴딩 (Kotlin 2.x + Compose M3, minSdk 26 / target 34, 단일 모듈)
- M2: Photo Picker + MotionPhotoExtractor (+JVM 단위테스트)
- M3: 편집(크롭/트림) + 3D 화면(클립·속도·카메라) + 음성 파일 선택·클립 매칭
- M4: 홈 3D 라이브 월페이퍼(음성+알림 반응 포함) + 잠금 정적 적용
- M5: 잠금용 MP4 굽기 + Movies/LiveLock 저장 + 원UI 유도 + 알림음/알람음 등록
       + 실험: 알림 시 잠금 배경 이미지 교체
- M6: 마무리 (오류처리·배터리·다크모드·README) · APK 산출

## 7. 권한
- SET_WALLPAPER (normal) + BIND_NOTIFICATION_LISTENER_SERVICE + 알림 접근 허용(사용자)
- 저장소 권한 불필요 (Photo Picker/SAF)

## 8. 리스크 대응
- 온디바이스 빌드 실패 → 단계별 폴백 경로 확정 (위)
- Filament GLB 호환성 → 샘플 2~3개로 사전 검증
- 원UI 버전별 동영상 배경 지원 차이 → 기능 감지 후 가이드 분기
- 보유 음원 저작권 → 개인 사용 기준 사용자 책임
- 알림 접근 권한 미허용 시 → 알림 반응 기능만 비활성화, 나머지 정상 동작

## 9. 성공 기준
1. assembleDebug 성공 → 설치 가능한 APK 생성
2. Fox 캐릭터가 홈에서 클립 전환 재생 + 음성 재생
3. 알림 수신 시 캐릭터 반응(클립 전환/말풍선) 동작
4. 잠금화면: 정적 프레임 직접 적용 + 15초 MP4 원UI 유도 완료
5. 알림 수신 시 캐릭터 보이스 재생
