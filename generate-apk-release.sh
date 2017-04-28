#!/usr/bin/env bash

# config
RELEASE_REPO=AllanWang/MyMartlet
USER_AUTH=AllanWang
APK_NAME=app-debug
MODULE_NAME=app
VERSION_KEY=MM
# Make version key different from module name

# create a new directory that will contain our generated apk
mkdir $HOME/$VERSION_KEY/
# copy generated apk from build folder to the folder just created
cp -R $MODULE_NAME/build/outputs/apk/$APK_NAME.apk $HOME/$VERSION_KEY/

echo "Create New Release"
API_JSON="$(printf '{"tag_name": "v%s","target_commitish": "master","name": "v%s","body": "Automatic Release v%s","draft": false,"prerelease": false}' $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER)"
newRelease="$(curl --data "$API_JSON" https://api.github.com/repos/$RELEASE_REPO/releases?access_token=$GITHUB_API_KEY)"
rID="$(echo "$newRelease" | jq ".id")"

cd $HOME
echo "Push apk to $rID"
curl "https://uploads.github.com/repos/${RELEASE_REPO}/releases/${rID}/assets?access_token=${GITHUB_API_KEY}&name=${APK_NAME}-v${TRAVIS_BUILD_NUMBER}.apk" --header 'Content-Type: application/zip' --upload-file $VERSION_KEY/$APK_NAME.apk -X POST


echo -e "Done\n"