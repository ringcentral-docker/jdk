#!/bin/bash
# Docker CLI installation script for jdk17-docker variant
set -e

# Install additional dependencies
apt-get update && apt-get install -y --no-install-recommends \
    jq \
    python3 \
    python3-pip \
    gnupg \
    lsb-release \
    && rm -rf /var/lib/apt/lists/*

# Install coverage tools
pip3 install --no-cache-dir --break-system-packages coverage-badge jacoco-badge-generator

# Install Docker CLI
mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update && apt-get install -y --no-install-recommends \
    docker-ce \
    docker-ce-cli \
    containerd.io \
    docker-buildx-plugin \
    docker-compose-plugin \
    && rm -rf /var/lib/apt/lists/*
