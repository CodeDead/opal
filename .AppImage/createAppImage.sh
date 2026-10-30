#!/bin/sh
set -eu

exec 2>&1

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <version>" >&2
    exit 1
fi

version=$1
script_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
project_dir=$(dirname -- "$script_dir")

case "$(uname -m)" in
    x86_64|amd64)
        appimage_arch="x86_64"
        ;;
    aarch64|arm64)
        appimage_arch="aarch64"
        ;;
    armv7l|armv8l|armhf)
        appimage_arch="armhf"
        ;;
    i386|i486|i586|i686)
        appimage_arch="i686"
        ;;
    *)
        echo "Unsupported architecture: $(uname -m)" >&2
        exit 1
        ;;
esac

export ARCH="$appimage_arch"

temporary_dir=$(mktemp -d "${TMPDIR:-/tmp}/opal-appimage.XXXXXX")
trap 'rm -rf "$temporary_dir"' EXIT HUP INT TERM

appimagetool="$temporary_dir/appimagetool-${appimage_arch}.AppImage"
wget --quiet --show-progress --timeout=60 --tries=3 --output-document="$appimagetool" \
    "https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-${appimage_arch}.AppImage"

download_magic=$(od -An -tx1 -N4 "$appimagetool" | tr -d ' \n')
if [ "$download_magic" != "7f454c46" ]; then
    echo "Downloaded appimagetool is not an executable ELF file" >&2
    exit 1
fi

chmod a+x "$appimagetool"
"$appimagetool" -n \
    "$project_dir/build/AppImage/Opal.AppDir" \
    "$project_dir/build/AppImage/Opal-${appimage_arch}-${version}.AppImage"
