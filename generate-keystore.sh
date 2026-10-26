#!/usr/bin/env bash
set -euo pipefail

KEYSTORE_PATH="app/keystore/release-keystore.jks"
mkdir -p "$(dirname "$KEYSTORE_PATH")"

if [ -f "$KEYSTORE_PATH" ]; then
  echo "Keystore already exists at $KEYSTORE_PATH"
  exit 0
fi

keytool -genkeypair -v \
  -keystore "$KEYSTORE_PATH" \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias gear_metrology_key \
  -storepass "mscGear2026!" \
  -keypass "mscGear2026!" \
  -dname "CN=Mobarakeh Steel Manufacturing Workshop, OU=Manufacturing Workshop, O=Mobarakeh Steel Company, L=Mobarakeh, ST=Isfahan, C=IR"

echo "Generated release keystore at $KEYSTORE_PATH"
