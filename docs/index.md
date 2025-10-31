# Welcome to Stego

**Stego: State Transition Event Guard Orchestrator**

Stego is a Kotlin-first framework for building state-driven, dynamic applications. At its core, Stego is a state machine orchestrator that can break down complex business problems into manageable flows.

## What is Stego?

Stego is built on the concepts of Harel statecharts, or extended state machines. It provides a structured way to manage application complexity.

- **State**: Represents a specific mode or condition of your application. A state defines the things we are interested in (`events`) and what can happen off the back of them (`transitions`).

- **Transition**: A directive that moves the application from one `State` to another. A transition is triggered by an `Event`.

- **Event**: A message that represents an occurrence in your application, such as a user clicking a button (`"SUBMIT"`) or data arriving from a server (`"FETCH_SUCCESS"`). Events are the sole mechanism for triggering state transitions.

- **Guard**: A condition (a function that returns `true` or `false`) that determines whether a `Transition` should be taken in response to an `Event`. This allows for dynamic, conditional logic.

- **Orchestrator**: The engine that puts it all together. The orchestrator listens for events, checks guards, executes transitions, and manages the overall application state. In Stego, this orchestration can include rendering a UI, making API calls, or performing any other side effect.

We know what you're thinking. "What about `Actions`? `Invokables`? `Extended State`?" Look, we had a choice: a cool, dinosaur-themed acronym, or a technically accurate one. We couldn't have both. We stand by our decision. All of those features and more are supported by Stego - so don't worry, you get all the features *and* the cool name. Win-win. Tell your friends.

## Key Features

### :material-server-network: Server-Driven
**Back in the Jurassic, APIs only sent raw data.** Evolve your architecture and define not just your UI, but your entire application's behavior on the server to be sent to the client.

### :material-test-tube: Testable by Design
**Make bugs extinct.** By modeling logic as a formal state machine, your application's behavior becomes deterministic and easy to test, eliminating entire classes of bugs.

### :material-puzzle-edit-outline: Easily Extensible
**Don't be a fossil.** Stego is built to adapt, allowing you to quickly add your own custom UI components, actions, or invokable services with just a few lines of code.

### :material-shield-check-outline: Guaranteed Predictability
**Chaos theory is great for blockbuster movies, but terrible for application state.** Eliminate race conditions and impossible states with explicit, guarded transitions.

### :material-source-branch: One Source of Truth
**Stop reassembling your business logic for each platform like a paleontologist piecing together a fossil.** Define it once as a single source of truth and share it across Android, iOS, JS, and beyond.
