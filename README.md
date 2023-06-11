# Neat
Functional minimalistic Unit Frames for the modern Minecrafter

## Release Process
Neat's release process is mostly automated. Here's the steps:

1. Pull master so you're up to date, make sure everything is committed
2. Run `git tag -a release-<mc_version>-<build_number>`. If you don't know or remember what those are, look at `gradle.properties`
3. In the editor that pops up, write the changelog
4. In `gradle.properties`, increment the build_number by one for the next version. Commit this.
5. Push master and the release tag: `git push origin master release-<mc_version>-<build_number>`
6. Shortly after, the mod should be automatically uploaded to GitHub's release tab, Modrinth, and CurseForge.

## Signing
Releases are signed with the Violet Moon signing key, see [this
page](https://github.com/VazkiiMods/.github/blob/main/security/README.md) for information
about how to verify the artifacts.
