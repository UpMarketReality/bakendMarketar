#!/bin/sh
set -eu
if [ "${MEDIA_DIR:-/app/media}" != /app/media ]; then
    echo "MEDIA_DIR must be /app/media for persistent Docker storage" >&2
    exit 1
fi
mkdir -p /app/media
if [ -d /app/media-seed ]; then
    cp -R -n /app/media-seed/. /app/media/
fi
exec java -jar /app/app.jar "$@"
