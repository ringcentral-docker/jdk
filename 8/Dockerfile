FROM alpine:3.8
LABEL maintainer="john.lin@ringcentral.com"

ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8 \
    JAVA_VERSION=8 \
    JAVA_UPDATE=191 \
    JAVA_HASH=2787e4a523244c269598db4e85c51e0c \
    JAVA_BASE_URL=https://download.oracle.com/otn-pub/java \
    ALPINE_GLIBC_BASE_URL=https://github.com/sgerrand/alpine-pkg-glibc/releases/download \
    ALPINE_GLIBC_PACKAGE_VERSION=2.28-r0 \
    JAVA_HOME=/opt/java \
    PATH=$PATH:${JAVA_HOME}/bin \
    USER_HOME_DIR="/root"

#========================
# Replace repositories to China Mirror
# https://mirrors.ustc.edu.cn/help/alpine.html
#========================
#RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories

RUN apk update \
    && apk add --no-cache ca-certificates bash

#====================
# Install GNU Libc
#====================

RUN apk add --no-cache --virtual=build-dependencies wget \
    && ALPINE_GLIBC_BASE_URL="${GLIBC_BASE_URL}/${ALPINE_GLIBC_PACKAGE_VERSION}" \
    && ALPINE_GLIBC_BASE_PACKAGE_FILENAME="glibc-${ALPINE_GLIBC_PACKAGE_VERSION}.apk" \
    && ALPINE_GLIBC_BIN_PACKAGE_FILENAME="glibc-bin-${ALPINE_GLIBC_PACKAGE_VERSION}.apk" \
    && ALPINE_GLIBC_I18N_PACKAGE_FILENAME="glibc-i18n-${ALPINE_GLIBC_PACKAGE_VERSION}.apk" \
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
    && wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie;" "${JAVA_BASE_URL}/jdk/${JAVA_VERSION}u${JAVA_UPDATE}-b12/${JAVA_HASH}/jdk-${JAVA_VERSION}u${JAVA_UPDATE}-linux-x64.tar.gz" \
    && tar -xzf "jdk-${JAVA_VERSION}u${JAVA_UPDATE}-linux-x64.tar.gz" -C "${JAVA_HOME}" --strip-components=1 \
    && ln -s ${JAVA_HOME}/bin/* /usr/bin/ \
    && rm -rf "${JAVA_HOME}/"*src.zip \
    && rm -rf "${JAVA_HOME}/lib/missioncontrol" \
           "${JAVA_HOME}/lib/visualvm" \
           "${JAVA_HOME}/lib/"*javafx* \
           "${JAVA_HOME}/jre/lib/plugin.jar" \
           "${JAVA_HOME}/jre/lib/ext/jfxrt.jar" \
           "${JAVA_HOME}/jre/bin/javaws" \
           "${JAVA_HOME}/jre/lib/javaws.jar" \
           "${JAVA_HOME}/jre/lib/desktop" \
           "${JAVA_HOME}/jre/plugin" \
           "${JAVA_HOME}/jre/lib/"deploy* \
           "${JAVA_HOME}/jre/lib/"*javafx* \
           "${JAVA_HOME}/jre/lib/"*jfx* \
           "${JAVA_HOME}/jre/lib/amd64/libdecora_sse.so" \
           "${JAVA_HOME}/jre/lib/amd64/"libprism_*.so \
           "${JAVA_HOME}/jre/lib/amd64/libfxplugins.so" \
           "${JAVA_HOME}/jre/lib/amd64/libglass.so" \
           "${JAVA_HOME}/jre/lib/amd64/libgstreamer-lite.so" \
           "${JAVA_HOME}/jre/lib/amd64/"libjavafx*.so \
           "${JAVA_HOME}/jre/lib/amd64/"libjfx*.so \
    && rm -rf "${JAVA_HOME}/jre/bin/jjs" \
           "${JAVA_HOME}/jre/bin/keytool" \
           "${JAVA_HOME}/jre/bin/orbd" \
           "${JAVA_HOME}/jre/bin/pack200" \
           "${JAVA_HOME}/jre/bin/policytool" \
           "${JAVA_HOME}/jre/bin/rmid" \
           "${JAVA_HOME}/jre/bin/rmiregistry" \
           "${JAVA_HOME}/jre/bin/servertool" \
           "${JAVA_HOME}/jre/bin/tnameserv" \
           "${JAVA_HOME}/jre/bin/unpack200" \
           "${JAVA_HOME}/jre/lib/ext/nashorn.jar" \
           "${JAVA_HOME}/jre/lib/jfr.jar" \
           "${JAVA_HOME}/jre/lib/jfr" \
           "${JAVA_HOME}/jre/lib/oblique-fonts" \
    && wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" "${JAVA_BASE_URL}/jce/${JAVA_VERSION}/jce_policy-${JAVA_VERSION}.zip" \
    && unzip -jo -d "${JAVA_HOME}/jre/lib/security" "jce_policy-${JAVA_VERSION}.zip" \
    && rm "${JAVA_HOME}/jre/lib/security/README.txt" \
    && apk del build-dependencies \
    && rm -rf /tmp/*

#==============
# Show version
#==============
RUN java -version \
    && javac -version