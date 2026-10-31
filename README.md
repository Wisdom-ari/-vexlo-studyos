# Vexlo StudyOS — Android starter project

A native Android Kotlin + Jetpack Compose starter for the Vexlo StudyOS study planner.

## Included in this first build
- Home dashboard and exam date (27 November 2026)
- Daily study timetable
- Six course checklist with local persistence
- Completed-session counter
- Study notes saved locally
- Daily 5:00 AM reminder toggle
- Notification channel and Android reboot receiver

## Build
1. Open this folder in Android Studio (Hedgehog or newer).
2. Allow Gradle sync to complete. Android Studio may download Gradle/Android dependencies.
3. Connect your Android phone with USB debugging enabled, or use an emulator.
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Install the generated debug APK from `app/build/outputs/apk/debug/`.

## Important alarm notes
- On Android 13+, allow notifications when prompted.
- On Android 12+, exact alarm access may need to be enabled in Android Settings.
- Battery management can affect reminders on some Tecno/HiOS devices; allow background activity/autostart for Vexlo StudyOS if reminders are delayed.
- This is an initial starter, not a fully tested release. Test alarms and reboot behavior on the target phone before relying on it.

## Next development steps
- Add editable timetable and multiple reminders per day.
- Add real study timer/Pomodoro and per-topic progress.
- Add charts, mock exam scores, backup/export and reminder management.
- Add tests and verify exact alarm behavior on the target Android version.
