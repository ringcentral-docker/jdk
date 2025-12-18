#!/bin/bash
# Base dependencies installation script
set -e

apt-get update && apt-get install -y --no-install-recommends \
    libgcc1 \
    fontconfig \
    fonts-dejavu \
    ca-certificates \
    bash \
    git \
    openssh-client \
    openssl \
    zip \
    subversion \
    sshpass \
    curl \
    && rm -rf /var/lib/apt/lists/*
