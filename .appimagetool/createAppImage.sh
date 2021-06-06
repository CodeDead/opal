#!/bin/sh
BASEDIR=$(dirname "$0")
PARENT_DIR="$(dirname "$BASEDIR")"
cd $BASEDIR
VAR="./appimagetool-x86_64.AppImage ${PARENT_DIR}/build/AppImage/Opal.AppDir ${PARENT_DIR}/build/AppImage/Opal-x86_64.AppImage"
eval $VAR
