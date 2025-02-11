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

For details on development guidelines and instructions on how to build and run the samples, visit the [Demo App](../azure-communication-ui/azure-communication-ui/demo-app)

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


## 3. Having your changes published

Once your PR is merged, your changes are ready to be published in a new version! We do manual publishes of new package versions semi-regularly.
