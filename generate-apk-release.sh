#!/usr/bin/env bash

# config
RELEASE_REPO=jguerinet/MyMartlet-APK
USER_AUTH=AllanWang
EMAIL=me@allanwang.ca
APK_NAME=MyMartlet-debug
MODULE_NAME=app
VERSION_KEY=MyMartlet
# Make version key different from module name

# create a new directory that will contain our generated apk
mkdir $HOME/$VERSION_KEY/
# copy generated apk from build folder to the folder just created
cp -R $MODULE_NAME/build/outputs/apk/$APK_NAME.apk $HOME/$VERSION_KEY/

# go to home and setup git
echo "Clone Git"
cd $HOME
git config --global fullUser.email "$EMAIL"
git config --global fullUser.name "$USER_AUTH CI"
# clone the repository in the buildApk folder
git clone --quiet --branch=master  https://AllanWang:$GITHUB_API_KEY@github.com/$RELEASE_REPO.git  master > /dev/null
# create version file
echo "Create Version File"
cd master
echo "$VERSION_KEY v$TRAVIS_BUILD_NUMBER" > "$VERSION_KEY.txt"

echo "Push Version File"
git remote rm origin
git remote add origin https://$USER_AUTH:$GITHUB_API_KEY@github.com/$RELEASE_REPO.git
git add -f .
git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed [skip ci]"
git push -fq origin master > /dev/null

echo "Create New Release"
API_JSON="$(printf '{"tag_name": "v%s","target_commitish": "master","name": "v%s","body": "Automatic Release v%s","draft": false,"prerelease": false}' $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER)"
newRelease="$(curl --data "$API_JSON" https://api.github.com/repos/$RELEASE_REPO/releases?access_token=$GITHUB_API_KEY)"
rID="$(echo "$newRelease" | jq ".id")"

cd $HOME
echo "Push apk to $rID"
curl "https://uploads.github.com/repos/${RELEASE_REPO}/releases/${rID}/assets?access_token=${GITHUB_API_KEY}&name=${APK_NAME}-v${TRAVIS_BUILD_NUMBER}.apk" --header 'Content-Type: application/zip' --upload-file $VERSION_KEY/$APK_NAME.apk -X POST


echo -e "Done\n"