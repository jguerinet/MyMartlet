# Get the latest build tools
export BUILD_TOOLS=$($ANDROID_HOME/tools/bin/sdkmanager --list | grep "build-tools/" | awk '{ print $3 }' | tail -1)
# Grab the version name from the generated APK
export VERSION=$($ANDROID_HOME/build-tools/$BUILD_TOOLS/aapt dump badging app/build/outputs/apk/$title/app-debug.apk | grep versionName | awk '{print $4}' | grep -o [0-9].*[0-9])
echo VERSION $VERSION