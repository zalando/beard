

# How to release

- Update the CHANGELOG.md with all the latest changes
- We use semantic versioning so in case there are breaking changes, update the version accordingly
- Change the version in the build.sbt file
- Update the README.md file to reflect the new version

To login to bintray:

    sbt bintrayChangeCredentials

Then, for cross-publish:

    sbt +publish