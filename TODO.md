# Gradle Build Fix - TODO Steps

## Approved Plan Breakdown:
1. [x] Update app/build.gradle: Fix JavaFX plugin version to 0.1.0 (Gradle 9.4 compatible), change mainClass to 'org.example.App', comment out JavaFX config temporarily.
2. [x] Update gradle/libs.versions.toml: Fix junit-jupiter to "5.10.3".
3. [ ] Run `./gradlew.bat clean build` to verify.
4. [ ] [Later] Rename app/src/main/java/com/auction/cilent/ to client/ (if contains files).
5. [ ] [Later] Create com.auction.client.Main with JavaFX auction UI.
6. [ ] [Later] Re-enable full JavaFX, implement server Main, auction logic.

**Progress: Starting step 1**
