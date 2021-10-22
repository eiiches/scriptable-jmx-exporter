#!/bin/bash
set -euo pipefail

scriptpath="$(readlink -f "$0")"
scriptdir="$(dirname "$scriptpath")"
cd "$scriptdir"

git submodule update --force --recursive --init janino
git -C janino clean -fdx
