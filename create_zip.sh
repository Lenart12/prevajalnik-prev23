#!/bin/bash
rm -rf prev23/.vscode
rm -rf prev23/src/prev23/phase/lexan/.antlr
rm -f prev23/lib/antlr-4.11.1-complete.jar
make -C prev23 clean

zip -r - prev23 > ${1:-release.zip}
