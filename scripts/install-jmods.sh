#!/bin/bash
set -euo pipefail

# JEP 493 Temurin images omit JMODs. Restore matching official modules so jlink
# keeps its normal integrity checks after the installed blocklist is updated.
if [[ "${JAVA_VERSION}" == "8" || -f "${JAVA_HOME}/jmods/java.base.jmod" ]]; then
    exit 0
fi

case "$(dpkg --print-architecture)" in
    amd64) JMODS_ARCH=x64; JMODS_SHA256="${JMODS_SHA256_AMD64:-}" ;;
    arm64) JMODS_ARCH=aarch64; JMODS_SHA256="${JMODS_SHA256_ARM64:-}" ;;
    *) echo "Unsupported JMODS architecture" >&2; exit 1 ;;
esac
if [[ -z "${JMODS_RELEASE_TAG:-}" || -z "${JMODS_ARCHIVE_VERSION:-}" || ! "${JMODS_SHA256}" =~ ^[0-9a-f]{64}$ ]]; then
    echo "Missing pinned JMODS release or checksum for this JDK" >&2
    exit 1
fi

JMODS_TMP_DIR=$(mktemp -d /tmp/java-jmods.XXXXXX)
trap 'rm -rf "${JMODS_TMP_DIR}"' EXIT
JMODS_ARCHIVE="OpenJDK${JAVA_VERSION}U-jmods_${JMODS_ARCH}_linux_hotspot_${JMODS_ARCHIVE_VERSION}.tar.gz"
JMODS_RELEASE_PATH="${JMODS_RELEASE_TAG//+/%2B}"
curl --fail --location --retry 3 \
    "https://github.com/adoptium/temurin${JAVA_VERSION}-binaries/releases/download/${JMODS_RELEASE_PATH}/${JMODS_ARCHIVE}" \
    --output "${JMODS_TMP_DIR}/jmods.tar.gz"
printf '%s  %s\n' "${JMODS_SHA256}" "${JMODS_TMP_DIR}/jmods.tar.gz" | sha256sum --check -
mkdir -p "${JAVA_HOME}/jmods"
tar --extract --gzip --file "${JMODS_TMP_DIR}/jmods.tar.gz" \
    --strip-components=1 --directory "${JAVA_HOME}/jmods"
test -f "${JAVA_HOME}/jmods/java.base.jmod"
