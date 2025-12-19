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
    unzip \
    curl \
    wget \
    && rm -rf /var/lib/apt/lists/*
