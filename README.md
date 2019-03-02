# spring-reactive-chat

Chat application built with Spring WebFlux

[Frontend built with Vue.js](https://github.com/nireinhard/VueChat)

## Requirements

- Java 8+
- MongoDB 3.6+

## Getting Started

The project makes use of [MongoDB Change Streams](https://docs.mongodb.com/manual/changeStreams/) to stream new chat messages via a Server-Sent Events endpoint.

> Change Stream support is only possible for replica sets or for a sharded cluster.
>
> &mdash; [Spring Data MongoDB Reference Documentation](https://docs.spring.io/spring-data/mongodb/docs/2.1.5.RELEASE/reference/html/#change-streams)

To convert your standalone MongoDB instance into a replica set, refer to this [guide](https://docs.mongodb.com/manual/tutorial/convert-standalone-to-replica-set/). Don't worry, it's easy!

Other than that, no manual configuration is necessary 😄

## Building from Source

The project uses [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and the [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html).

To build the project use

```Shell
./gradlew clean build
```

To run the project use

```Shell
./gradlew bootRun
```

To run the project with an specific profile, e.g. `prod`, use

```Shell
./gradlew bootRun -Pargs=--spring.profiles.active=prod
```

Note that the actual Spring arguments starting with `--` are comma-separated:

```Shell
./gradlew bootRun -Pargs=--spring.profiles.active=prod,--spring.main.banner-mode=off
```
