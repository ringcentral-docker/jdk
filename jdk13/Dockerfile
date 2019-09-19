FROM alpine:3.10
LABEL maintainer="john.lin@ringcentral.com"

ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8 \
    JAVA_VERSION=13 \
    JAVA_UPDATE=33 \
    GCC_LIBS_VERSION=9.1.0-2 \
    ZLIB_VERSION=1.2.11-3 \
    JAVA_BASE_URL=https://download.oracle.com/otn-pub/java \
    JAVA_BASE_MIRRORS_URL=https://repo.huaweicloud.com/java \
    ARCHLINUX_BASE_URL=https://archive.archlinux.org/packages \
    GLIBC_BASE_URL=https://github.com/sgerrand/alpine-pkg-glibc/releases/download \
    GLIBC_PACKAGE_VERSION=2.30-r0 \
    JAVA_HOME=/opt/java \
    PATH=$PATH:${JAVA_HOME}/bin \
    USER_HOME_DIR="/root"

RUN apk update \
    && apk add --no-cache ca-certificates bash git openssh openssl zip subversion sshpass curl

#==============
# Install gcc-libs & zlib
# refer https://github.com/io-sgr/docker-java-oracle/blob/master/alpine/jdk/Dockerfile
#==============

RUN apk add --no-cache --virtual=build-dependencies binutils xz \
    && mkdir /tmp/gcc-libs \
    && curl -Ls "${ARCHLINUX_BASE_URL}"/g/gcc-libs/gcc-libs-"${GCC_LIBS_VERSION}"-x86_64.pkg.tar.xz \
        | tar -xJ -C /tmp/gcc-libs \
    && mv /tmp/gcc-libs/usr/lib/libgcc* /tmp/gcc-libs/usr/lib/libstdc++* /usr/glibc-compat/lib \
    && strip /usr/glibc-compat/lib/libgcc_s.so.* /usr/glibc-compat/lib/libstdc++.so* \
    && mkdir /tmp/zlib \
    && curl -Ls "${ARCHLINUX_BASE_URL}"/z/zlib/zlib-1%3A"${ZLIB_VERSION}"-x86_64.pkg.tar.xz \
        | tar -xJ -C /tmp/zlib \
    && mv /tmp/zlib/usr/lib/libz.so* /usr/glibc-compat/lib \
    && apk del build-dependencies \
    && rm -rf /tmp/*

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
# Install Oracle JDK
#==============
RUN apk add --no-cache --virtual=build-dependencies wget unzip \
    && mkdir -p /opt/java \
    && cd /tmp \
    && wget --no-check-certificate "${JAVA_BASE_MIRRORS_URL}/jdk/${JAVA_VERSION}+${JAVA_UPDATE}/jdk-${JAVA_VERSION}_linux-x64_bin.tar.gz" \
    && tar -xzf "jdk-${JAVA_VERSION}_linux-x64_bin.tar.gz" -C "${JAVA_HOME}" --strip-components=1 \
    && ln -s ${JAVA_HOME}/bin/* /usr/bin/ \
    && apk del build-dependencies \
    && rm -rf /tmp/*

#==============
# Show version
#==============
RUN java -version \
    && javac -version
