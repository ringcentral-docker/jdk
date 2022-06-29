FROM alpine:3.16.0
LABEL maintainer="john.lin@ringcentral.com"

ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8 \
    JAVA_VERSION=8 \
    JAVA_DETAIL_VERSION=332 \
    JAVA_UPDATE_SMALL_VERSION=09 \
    JAVA_BASE_URL=https://github.com/adoptium/temurin8-binaries/releases/download \
    GCC_LIBS_VERSION=12.1.0-2 \
    ZLIB_VERSION=1.2.12-2 \
    ARCHLINUX_BASE_URL=https://archive.archlinux.org/packages \
    GLIBC_BASE_URL=https://github.com/sgerrand/alpine-pkg-glibc/releases/download \
    GLIBC_PACKAGE_VERSION=2.35-r0 \
    JAVA_HOME=/opt/java \
    PATH=$PATH:${JAVA_HOME}/bin \
    JAVA_TOOL_OPTIONS="-XX:+IgnoreUnrecognizedVMOptions -XX:+UseContainerSupport -XX:+IdleTuningCompactOnIdle -XX:+IdleTuningGcOnIdle" \
    USER_HOME_DIR="/root"

RUN apk update \
    && apk add --no-cache ca-certificates bash git openssh openssl zip subversion sshpass curl

#====================
# Install GNU Libc
#====================

RUN apk add --no-cache --virtual=build-dependencies wget \
    && ALPINE_GLIBC_BASE_URL="${GLIBC_BASE_URL}/${GLIBC_PACKAGE_VERSION}" \
    && ALPINE_GLIBC_BASE_PACKAGE_FILENAME="glibc-${GLIBC_PACKAGE_VERSION}.apk" \
    && ALPINE_GLIBC_BIN_PACKAGE_FILENAME="glibc-bin-${GLIBC_PACKAGE_VERSION}.apk" \
    && ALPINE_GLIBC_I18N_PACKAGE_FILENAME="glibc-i18n-${GLIBC_PACKAGE_VERSION}.apk" \
    && cd /tmp \
    && wget -q -O "/etc/apk/keys/sgerrand.rsa.pub" "https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub" \
    && wget "${ALPINE_GLIBC_BASE_URL}/${ALPINE_GLIBC_BASE_PACKAGE_FILENAME}" \
        "${ALPINE_GLIBC_BASE_URL}/${ALPINE_GLIBC_BIN_PACKAGE_FILENAME}" \
        "${ALPINE_GLIBC_BASE_URL}/${ALPINE_GLIBC_I18N_PACKAGE_FILENAME}" \
    && apk add --no-cache \
        "${ALPINE_GLIBC_BASE_PACKAGE_FILENAME}" \
        "${ALPINE_GLIBC_BIN_PACKAGE_FILENAME}" \
        "${ALPINE_GLIBC_I18N_PACKAGE_FILENAME}" \
    && /usr/glibc-compat/bin/localedef -i en_US -f UTF-8 en_US.UTF-8 \
    && apk del build-dependencies \
    && rm -rf /etc/apk/keys/sgerrand.rsa.pub \
    && rm -rf /tmp/*

#==============
# Install gcc-libs & zlib
# refer https://github.com/AdoptOpenJDK/openjdk-docker/blob/master/11/jdk/alpine/Dockerfile.openj9.releases.full
#==============

RUN apk add --no-cache --virtual=build-dependencies binutils xz zstd \
    && cd /tmp \
    && mkdir /tmp/gcc \
    && wget "${ARCHLINUX_BASE_URL}/g/gcc-libs/gcc-libs-${GCC_LIBS_VERSION}-x86_64.pkg.tar.zst" -O gcc-libs.tar.zst \
    && zstd -d /tmp/gcc-libs.tar.zst --output-dir-flat /tmp \
    && tar -xf /tmp/gcc-libs.tar -C /tmp/gcc \
    && mv /tmp/gcc/usr/lib/libgcc* /tmp/gcc/usr/lib/libstdc++* /usr/glibc-compat/lib \
    && strip /usr/glibc-compat/lib/libgcc_s.so.* /usr/glibc-compat/lib/libstdc++.so* \
    && mkdir /tmp/zlib \
    && wget "${ARCHLINUX_BASE_URL}/z/zlib/zlib-1%3A${ZLIB_VERSION}-x86_64.pkg.tar.zst" -O zlib.pkg.tar.zst\
    && zstd -d /tmp/zlib.pkg.tar.zst --output-dir-flat /tmp \
    && tar -xf /tmp/zlib.pkg.tar -C /tmp/zlib \
    && mv /tmp/zlib/usr/lib/libz.so* /usr/glibc-compat/lib \
    && apk del build-dependencies \
    && rm -rf /tmp/*

#==============
# Install AdoptOpenJDK
#==============
RUN apk add --no-cache --virtual=build-dependencies wget unzip \
    && mkdir -p ${JAVA_HOME} \
    && cd /tmp \
    && wget --no-check-certificate "${JAVA_BASE_URL}/jdk${JAVA_VERSION}u${JAVA_DETAIL_VERSION}-b${JAVA_UPDATE_SMALL_VERSION}/OpenJDK${JAVA_VERSION}U-jdk_x64_linux_hotspot_${JAVA_VERSION}u${JAVA_DETAIL_VERSION}b${JAVA_UPDATE_SMALL_VERSION}.tar.gz" \
    && tar -xzf "OpenJDK${JAVA_VERSION}U-jdk_x64_linux_hotspot_${JAVA_VERSION}u${JAVA_DETAIL_VERSION}b${JAVA_UPDATE_SMALL_VERSION}.tar.gz" -C "${JAVA_HOME}" --strip-components=1 \
    && ln -s ${JAVA_HOME}/bin/* /usr/bin/ \
    && apk del build-dependencies \
    && rm -rf /tmp/*

#==============
# Show version
#==============
RUN java -version \
    && javac -version