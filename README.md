Experimental home automation project to try tools, libraries, patterns and architectures

## Basic functionality

1) Arduino device sends BLE event to a Raspberry Pi
2) Client Jvm app running in the Raspberry Pi receives the event and sends it to the server
3) The Ktor Jvm server receives the event and stores it in a redis/postgres database for the specific user
4) The client app (Android/iOS/Browser) retrieves the devices and events from the server for the specific user

## Pieces

- Server (Ktor Jvm servers)
    - App service
    - Mock service (a mock of the app service)
- Client
    - User app
        - Android target
        - iOS target
        - Browser target (Wasm)
    - Home app
        - Raspberry Pi target (Jvm)

## Features

Server:

- Ktor Jvm
- Postgres
- Redis
- JWT for fast authentication but its contents are encrypted
- Secure sessions for browser authentication
- Bearer token for native devices authentication
- Locally runnable with in-memory data sources
- Simple Java Email sender
- Password authentication
- User pre-authentication
- JWT blacklisting
- Wasm application served by Ktor automatically with security best practices
- Multi-client authentication with the same flow
- Safe-validated models from creation to use from server to client and vice versa
- Code reused between all targets as much as possible

User client

- Android/iOS/Browser targets
- Kotlin inject
- Session scoped dependencies
- Locally testable with locally running servers (mock/production)
- Encrypted shared preferences

Home client

- Raspberry Pi target (Jvm)
- User authentication from the command line
- Bluetooth communication through Kable
- Manual setup of Jib for docker image creation
- Able to run locally with real/mock bluetooth devices

Gradle plugins

- Standardized plugins for libraries and apps
- Docker image configuration
- Docker compose configuration
- Postgres configuration
- Redis configuration
- Fake server for the browser client
- Easy Wasm distribution serving for server
- Wasm configuration with compression, HTML generation, deliverables with hashes for caching

CI

- Creation of environments
- Creation and publishing of docker images
- Creation and uploading of APK
- Creation and uploading of IPA

## How to run?

The project is thought to be able to be run locally and mock certain pieces if required

To select which environment to run, it uses [Chamaleon](https://github.com/gerardorodriguezdev/chamaleon) so select the
environment and use the IntelliJ run configurations available

### Important run configurations

Server

- runAll: Runs the server using docker compose (and redis/postgres if needed automatically)
- runJvm: Runs the server with the JVM (requires using the Chamaleon in-memory environment)

User Client

- runAndroid: Runs the android client
- runIos: Runs the iOS client
- runWasm: Runs the wasm client

Home Client

- runAll: Runs the client using docker
- runJvm: Runs the client with the Jvm

### Environments

Server

- localMemoryDataSources: Uses in-memory data sources
- localRealDataSources: Uses real data sources (postgres and redis)
- localRemoteTemplate: Template with almost the same configuration as production except still using non-secure http

User Client

- local: Connects to a local non-secure http server so you can use the mock server if required
- debugRemoteTemplate: Template to test with a remote server (can be staging/production)

Home Client

- local: Connects to a local non-secure http server so you can use the mock server if required
- localFakeDevicesController: Uses a fake devices controller that returns events from a device as if you were connected
  to a bluetooth device so you can test without a real device
- debugRemoteTemplate: Template to test with a remote server (can be staging/production)

### Deployment considerations

Your reverse proxy should:

- Apply rate limiting through all your server instances
- Handle SSL certificates

Databases:

- It is considered that postgres and redis are already set up and running

### Findings of the experimentation

- Projects should only do the bare minimum. This is because anything else makes development slower, introduces security
  risks and is harder to integrate cohesively
- When using multiple Gradle modules, you need to make custom plugins to avoid code duplication
- It is tough to keep the same code format and architecture in every single line in the project. Only enforce the most
  important pieces
- Trying to reuse code is not always the best as it can make the implementation harder, and you may not even reuse it
- Gradle was the hardest part to keep and integrate in a KMP project
- Sharing the Wasm application with the server introduces slow compilation time for the server, so it is better to keep
  aside. The only downside is not being able to easily add CSP headers or try locally exactly as a user would receive it
  before deployment
- iOS KMP integration is still not as good as Android/JVM, using KMP into an already existing iOS project is not trivial
- OpenApi for the Ktor client and the server wasn't working. The CRUD code is predictable so should be generated
- Having strict typed models is the only way to avoid duplicating validation code, but writing that code is harder as
  well versioning them if they are serialized and shared between different client versions
- Tests can introduce friction to update a project, especially when the architecture changes a lot
- To separate the UI, the best way is to be able to separate screens. If anything is shared in multiple screens, a
  provider or shared controller should be used (snackbar, navigation, notifications, bottom nav bar)
- Exceptions are for non-recoverable errors, everything else should be safe to call (and main thread safe)
- Separating modules by server shared or globally shared is not always the best idea as there can be 'shared by feature'
  as well, but separating by client/server was great to find the deliverables
- For projects with little people it is best to use monoliths instead of microservices because it's easier to test
  locally, reason about, and you even use the monolith as the gateway. If you use microservices, you need to add events
  to communicate between them even in things like clearing user data from multiple databases handled by multiple
  microservices
- A lot of configuration is needed when using Ktor. This can introduce security vulnerabilities if avoided
- Jib was selected instead of docker files because of compilation speed on development
- Jib was selected without Proguard even if it is good for security reasons as it would remove unused code because using
  Proguard would increase compilation time with Jib
- Jib was selected without GraalVM because if you are not developing in the target platform, it won't work properly and
  compilation time was huge
- Integrating docker, docker compose, webpack config files was painful, and the only reason it was done was to have a
  reusable Gradle Plugin for other projects that handle it. The reality is that there are so many options that is not
  practical to implement a Gradle Plugin for that
- Ideally, all authentication should be done with a third party service as it is very complex and sensitive
- Everything that is not core to the core features should be done with third party services as it consumes a lot of
  time, and it might be done better by services that only do that
- Server itself can be skipped, and using something like Supabase would be a better option in this case
- The JVM was selected for the Home client because it was easy to set up and works the same in both development and
  actual deployment platforms