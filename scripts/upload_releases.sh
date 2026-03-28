#!/usr/bin/env bash
set -euo pipefail

# Remove 'refs/tags/' from front
TAGNAME="${GIT_REF/#refs\/tags\/}"

# Remove 'release-' from front
VERSION="${TAGNAME/#release-}"
MC_VERSION=$(echo "${VERSION}" | cut -d '-' -f 1)

function release_github() {
	echo >&2 'Creating GitHub Release'
	local GH_RELEASE_RESPONSE
	GH_RELEASE_RESPONSE="$(gh api \
	   --method POST \
	   -H "Accept: application/vnd.github+json" \
	   -H "X-GitHub-Api-Version: 2022-11-28" \
	   /repos/VazkiiMods/Neat/releases \
	   -f tag_name="${TAGNAME}")"
	GH_RELEASE_PAGE=$(echo "$GH_RELEASE_RESPONSE" | jq -r .html_url)

	echo >&2 'Uploading Fabric Jar and Signature to GitHub'
	gh release upload "${TAGNAME}" "${FABRIC_JAR}#Fabric Jar"
	gh release upload "${TAGNAME}" "${FABRIC_JAR}.asc#Fabric Signature"
	echo >&2 'Uploading NeoForge Jar and Signature to GitHub'
	gh release upload "${TAGNAME}" "${NEOFORGE_JAR}#NeoForge Jar"
	gh release upload "${TAGNAME}" "${NEOFORGE_JAR}.asc#Forge Signature"
}

function release_modrinth() {
	echo >&2 'Uploading Fabric Jar to Modrinth'
	local MODRINTH_FABRIC_SPEC
	MODRINTH_FABRIC_SPEC=$(cat <<EOF
{
	"dependencies": [
		{
			"project_id": "P7dR8mSH",
			"dependency_type": "required"
		},
		{
		  "project_id": "9s6osm5g",
      "dependency_type": "required"
		}
	],
	"version_type": "release",
	"loaders": ["fabric", "quilt"],
	"featured": false,
	"project_id": "Ins7SzzR",
	"file_parts": [
		"jar"
	],
	"primary_file": "jar"
}
EOF
						)

	MODRINTH_FABRIC_SPEC=$(echo "${MODRINTH_FABRIC_SPEC}" | \
							   jq --arg name "${VERSION}-fabric" \
								  --arg mcver "${MC_VERSION}" \
								  --arg changelog "${GH_RELEASE_PAGE}" \
								  '.name=$ARGS.named.name | .version_number=$ARGS.named.name | .game_versions=[$ARGS.named.mcver] | .changelog=$ARGS.named.changelog')
	curl 'https://api.modrinth.com/v2/version' \
		 -H "Authorization: $MODRINTH_TOKEN" \
		 -F "data=$MODRINTH_FABRIC_SPEC" \
		 -F "jar=@${FABRIC_JAR}" # TODO modrinth doesn't allow asc files. Remember to readd "signature" to the spec when reenabling this. \ -F "signature=@${FABRIC_JAR}.asc"

	echo >&2 'Uploading NeoForge Jar to Modrinth'
	local MODRINTH_NEOFORGE_SPEC
	MODRINTH_NEOFORGE_SPEC=$(cat <<EOF
{
	"dependencies": [],
	"version_type": "release",
	"loaders": ["neoforge"],
	"featured": false,
	"project_id": "Ins7SzzR",
	"file_parts": [
		"jar"
	],
	"primary_file": "jar"
}
EOF
					   )

	MODRINTH_NEOFORGE_SPEC=$(echo "${MODRINTH_NEOFORGE_SPEC}" | \
							  jq --arg name "${VERSION}-neoforge" \
								 --arg mcver "${MC_VERSION}" \
								 --arg changelog "${GH_RELEASE_PAGE}" \
								 '.name=$ARGS.named.name | .version_number=$ARGS.named.name | .game_versions=[$ARGS.named.mcver] | .changelog=$ARGS.named.changelog')
	curl 'https://api.modrinth.com/v2/version' \
		 -H "Authorization: $MODRINTH_TOKEN" \
		 -F "data=$MODRINTH_NEOFORGE_SPEC" \
		 -F "jar=@${NEOFORGE_JAR}" # TODO modrinth doesn't allow asc files. Remember to readd "signature" to the spec when reenabling this. \ -F "signature=@${NEOFORGE_JAR}.asc"
}

function release_curseforge() {
	# Java versions, Loaders, and Environment tags are actually "game versions" (lmfao), as are real game versions.

	# Hardcoded from https://minecraft.curseforge.com/api/game/versions
	# I'm not betting on these changing any time soon, so hardcoding is ok
	local CURSEFORGE_JAVA_VERSION=11135 # Java 21
	local CURSEFORGE_FABRIC_VERSION=7499
	local CURSEFORGE_NEOFORGE_VERSION=10150
	local CURSEFORGE_CLIENT_VERSION=9638
	# For the Minecraft one, don't hardcode so we don't have to remember to come change this every time.
	# Each game version seems to be duplicated three times:
	# Once with type ID 1 (unused?), once with its major-version-specific type ID, and once with the type ID for "Addons" 615
	# We want the second one. Just dirtily pluck it out based on this.
	local CURSEFORGE_GAME_VERSION
	CURSEFORGE_GAME_VERSION=$(curl https://minecraft.curseforge.com/api/game/versions \
								   -H 'Accept: application/json' \
								   -H "X-Api-Token: ${CURSEFORGE_TOKEN}" | \
								  jq --arg mcver "${MC_VERSION}" \
									 'map(select(.name == $ARGS.named.mcver and .gameVersionTypeID != 1 and .gameVersionTypeID != 615)) | first | .id')

	echo >&2 'Uploading Fabric Jar to CurseForge'
	local CURSEFORGE_FABRIC_SPEC
	CURSEFORGE_FABRIC_SPEC=$(cat <<EOF
{
	"changelogType": "text",
	"releaseType": "release",
	"relations": {
		"projects": [
			{
				"slug": "fabric-api",
				"type": "requiredDependency"
			},
      {
        "slug": "cloth-config",
        "type": "requiredDependency"
      }
		]
	}
}
EOF
						  )

	local CURSEFORGE_FABRIC_GAMEVERS="[\
$CURSEFORGE_JAVA_VERSION,\
$CURSEFORGE_CLIENT_VERSION,\
$CURSEFORGE_FABRIC_VERSION,\
$CURSEFORGE_GAME_VERSION]"

	CURSEFORGE_FABRIC_SPEC=$(echo "$CURSEFORGE_FABRIC_SPEC" | \
								 jq --arg changelog "$GH_RELEASE_PAGE" \
									--argjson gamevers "$CURSEFORGE_FABRIC_GAMEVERS" \
									'.gameVersions=$ARGS.named.gamevers | .changelog=$ARGS.named.changelog')
	curl 'https://minecraft.curseforge.com/api/projects/639987/upload-file' \
		 -H "X-Api-Token: $CURSEFORGE_TOKEN" \
		 -F "metadata=$CURSEFORGE_FABRIC_SPEC" \
		 -F "file=@$FABRIC_JAR"
	# TODO: Upload the asc as an 'Additional file'

	echo >&2 'Uploading Forge Jar to CurseForge'
	local CURSEFORGE_NEOFORGE_SPEC
	CURSEFORGE_NEOFORGE_SPEC=$(cat <<EOF
{
    "changelogType": "text",
    "releaseType": "release"
}
EOF
						 )

	local CURSEFORGE_NEOFORGE_GAMEVERS="[\
$CURSEFORGE_JAVA_VERSION,\
$CURSEFORGE_CLIENT_VERSION,\
$CURSEFORGE_NEOFORGE_VERSION,\
$CURSEFORGE_GAME_VERSION]"

	CURSEFORGE_NEOFORGE_SPEC=$(echo "$CURSEFORGE_NEOFORGE_SPEC" | \
								jq --arg changelog "$GH_RELEASE_PAGE" \
								   --argjson gamevers "$CURSEFORGE_NEOFORGE_GAMEVERS" \
								   '.gameVersions=$ARGS.named.gamevers | .changelog=$ARGS.named.changelog')
	curl 'https://minecraft.curseforge.com/api/projects/238372/upload-file' \
		 -H "X-Api-Token: $CURSEFORGE_TOKEN" \
		 -F "metadata=$CURSEFORGE_NEOFORGE_SPEC" \
		 -F "file=@$NEOFORGE_JAR"
	# TODO: Upload the asc as an 'Additional file'
}

release_github
release_modrinth
release_curseforge
