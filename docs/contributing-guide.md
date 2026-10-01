# Contribution Guide

## Ways to Contribute

You can help Azure Communication UI Library with any of the following:

- Reporting and fixing issues
- Suggesting new features
- Increasing unit test coverage
- Answering any open issues
- Improving documentation
- Reviewing pull requests

We enthusiastically welcome contributions and feedback. You can fork the repo and start contributing now.  
Here are the steps to start and develop inside iOS Mobile UI Library repo.

1. [Setup & Run Samples](#1-setup-and-run-samples)
2. [Submitting a PR](#2-submitting-a-pr)
3. [Having your changes published](#3-having-your-changes-published)

## 1. Setup and Run Samples

Begin by cloning the Repo: [https://github.com/Azure/communication-ui-library-android](https://github.com/Azure/communication-ui-library-android)

### Running a Sample application

For details on development guidelines and instructions on how to build and run the samples, visit the [Demo App](../azure-communication-ui/demo-app)

## 2. Submitting a PR

You can send pull requests to fix the open issues. For any pull request, it's recommended to open an issue and reach an agreement on an implementation design/plan with other contributors first.

We recommend making small and simple pull requests. Avoid making the implementation complicated when there is a simple, small alternative.

Please fork the repository and submit pull requests to `develop` branch. For details on how to set up a fork of this repository and keep it up-to-date see [Fork a Repo - GitHub Help](https://help.github.com/en/github/getting-started-with-github/fork-a-repo).

### Writing unit tests

When submitting a pull request, please add relevant tests and ensure your changes don't break any existing tests. Pull requests should be thoroughly tested and CI checks passed.


### Running unit tests

Unit tests are located in the `/azure-communication-ui/{calling/chat/call-with-chat}/src/test` directory. 

### Style Guidelines

Azure Mobile UI Library employs a few practices to ensure the clean code and project standards. Please follow these practices to make your Pull Request consistent with the MobileUILibrary

1. [ktling](https://ktlint.github.io/) is added to enforce coding style and conventions


### Internal CI package sources

The internal CI and release pipelines use the project-scoped
`skype/SCC/SCC_PublicPackages` Azure Artifacts feed. Its upstreams include Maven
Central, Google Maven, and Gradle Plugin Portal. The effective build identity needs
the Feed and Upstream Reader (Collaborator) role; package-publishing permissions
are not required for dependency restoration.

`eng/pipelines/templates/cfs.yml` installs a job-local Gradle init script and
configures Maven's `central` mirror before `MavenAuthenticate@0` supplies the job
credentials. Reference this template as `/eng/pipelines/templates/cfs.yml@self`;
the enclosing IC3 template is in another repository. Gradle reads the same
credential entry for dependency, buildscript,
and plugin repositories. `GRADLE_USER_HOME` applies to subsequent scanner-launched
Gradle processes as well as the explicit Gradle tasks. Existing private feeds and
publishing credentials are preserved. No credentials belong in this repository.
Private Maven deployment also requires the preceding pipeline steps to succeed;
a failed CFS setup or build must not publish a package.

For hosted release validation, set the `validationOnly` pipeline parameter to
`true`. This omits private-feed authentication and Maven deployment at template
expansion time, regardless of `isPrivateRelease`. Build, test, package generation,
security scanners, and internal ADO artifacts/reporting remain enabled. The
parameter defaults to `false` to preserve normal release behavior.

This setup is internal-CI-only: public consumer/developer builds retain their
existing repository configuration. Fork PR builds must remain disabled for these
credentialed internal pipelines; the setup rejects fork jobs rather than making
internal feed credentials available to them.

Run the configuration tests without an Android SDK:

```sh
GRADLE_USER_HOME="$PWD/.gradle/cfs-tests" python3 eng/scripts/tests/test_configure_cfs.py
```

CI enforces `CFSClean`, `CFSClean2`, and the already-required `CFSClean3`. The
release pipeline additionally uses `DefaultDeny` instead of `Permissive`. Keep
Javadoc generation and all required package artifacts enabled: AGP 8.8.0's
generated documentation task attempts external Kotlin/Android documentation
lookups, which must tolerate blocking under Default Deny. Blocked package-list
lookups can emit warnings; verify that both Javadoc JARs still contain the generated
HTML. Blocked connections are not Network Isolation policy violations.

Before rollout, validate both Windows CI and Linux packaging (without publishing),
including Component Governance and the generated Javadoc JARs. Confirm the policy
mix in **Start Network Isolation** and zero relevant violations in **Stop Network
Isolation**. Hosted validation creates normal build records and internal
security/inventory reports; clean runs count toward automatic policy lock-in.
Preview the expanded plan before queuing the exact feature-branch commit.
Apply the fix to every active branch used by these definitions,
including `develop` and active release branches; do not move published release
tags. S360's observation window is seven violation-free days and at least three
clean runs for CFS, and fourteen days and at least eight clean runs for Default
Deny. These counts alone do not replace functional build validation.

## 3. Having your changes published

Once your PR is merged, your changes are ready to be published in a new version! We do manual publishes of new package versions semi-regularly.
