#!/bin/bash
set -euo pipefail

scriptpath="$(readlink -f "$0")"
scriptdir="$(dirname "$scriptpath")"
cd "$scriptdir"

git submodule update --force --recursive --init janino
git -C janino clean -fdx
base_version="$(git -C janino rev-parse HEAD | cut -c 1-7)"
patch_version="$(cat janino.patch.d/*.patch | sha1sum | cut -c 1-5)"
version="0.0.0-$base_version.$patch_version"

for patch in janino.patch.d/*.patch; do
	patch="$(readlink -f "$patch")"
	git -C janino am "$patch"
done

(cd janino/janino-parent \
	&& mvn versions:set -DnewVersion="$version" \
	&& mvn clean install)

git submodule update --force --recursive --init janino
git -C janino clean -fdx
