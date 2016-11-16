

# How to release

- Update the CHANGELOG.md with all the latest changes
- We use semantic versioning so in case there are breaking changes, update the version accordingly
- Change the version in the build.sbt file to a fixed version
- Update the README.md file to reflect the new version
- Commit the changes and tag the version: Eg "Release 0.1.3"
- Then, for cross-publish:

    sbt +publish

- Start change the version again to the next snapshot and commit "Start 0.2.0-SNAPSHOT"

Note:
To login to bintray:

    sbt bintrayChangeCredentials
