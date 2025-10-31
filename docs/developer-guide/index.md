# Developer Guide

Welcome to the Stego developer guide! We're thrilled that you're interested in contributing. This guide is for anyone who wants to help improve Stego, whether you're a first-time open-source contributor or an experienced developer.

If you haven't already, we recommend reading the [Home](../index.md) and [About](../about.md) pages to get a feel for the project's philosophy and core concepts.

## Getting Started

Stego is a Kotlin Multiplatform project built with Gradle. To get started with development, you'll need:

1.  A recent version of the JDK (we use JDK 21 for publishing).
2.  IntelliJ IDEA is highly recommended for the best development experience.
3.  Clone the repository: `git clone https://github.com/development-and-dinosaurs/stego.git`
4.  Open the project in IntelliJ IDEA and let it sync with Gradle.

## How to Contribute

We welcome contributions of all kinds! Here are a few ways you can help:

- **Reporting Bugs**: If you find a bug, please open an issue on our GitHub repository. Provide as much detail as possible to help us reproduce and fix it.
- **Suggesting Enhancements**: Have an idea for a new feature or an improvement? We'd love to hear it! Open an issue to start the discussion.
- **Writing Documentation**: Clear documentation is crucial. If you see an area that could be improved, feel free to open a pull request.
- **Submitting Code**: Fork the repository, create a new branch for your feature or bugfix, and open a pull request when you're ready.

## Project Structure

The project is organized into several key modules:

### :material-cube-outline: `domain`
Contains the core, platform-agnostic logic for the state machine (`domain:core`) and UI models (`domain:ui-core`). This is where the fundamental building blocks like `State`, `Transition`, `Event`, `Guard`, and the `Orchestrator` live.

### :material-code-json: `data`
Contains the translation layer between JSON representations and the core domain models.

### :material-palette-outline: `presentation`
Contains the UI implementations that can be used to present Stego state machines as a user interface.

### :material-lan-connect: `di`
Contains the dependency injection implementations that hook everything together for consumers.

### :material-application-braces-outline: `examples`
Contains examples of using Stego across various platforms.

### :material-book-open-page-variant-outline: `docs`
The source for this documentation site!

## Further Reading
Inside this developer guide you'll find specific pages describing how to achieve common tasks when developing Stego.
