# CLAUDE.md

Binding guide for any AI assistant or collaborator working in this repo.

## Language rule

- **Everything you write must be in English.** This includes code, identifiers, documentation, READMEs, ADRs, plans, commit messages, PR descriptions, issue comments, and any other written output. No exceptions.

## Design principles

- **SOLID**: respect the five principles (SRP, OCP, LSP, ISP, DIP) in both Java backend and TypeScript frontend.
- **DRY**: do not duplicate logic. Extract to helpers, base classes, hooks, or shared modules whenever something repeats.

## Code rules

- **Do not add comments in code.** Variable, function, class, and file names must be self-explanatory. If you feel like commenting, rename instead.
- **Java**: follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). 2-space indentation, no wildcard imports, ~100 column line limit, camelCase names, UPPER_SNAKE constants. Enforced by `google-java-format` (Maven plugin) and verified in CI.
- **React + TypeScript**: standard best practices. Functional components with hooks. `strict: true` in `tsconfig.json`. No `any`. Typed props. ESLint + Prettier configured.
- **Code language: English.** All identifiers (classes, methods, variables, files, packages), technical string literals, and topic/queue/column names in English. If a comment is unavoidable (very rare), it must also be in English.

## Git rules

- **No Claude co-author in commits.** Do not include the `Co-Authored-By: Claude <...>` trailer or anything similar. Only the local author appears as committer.
- Commit messages in English, short, and focused on the "why".
- One plan phase = a coherent series of commits; not a single giant commit.

## How to run the project

See the `Makefile` at the root. Any project action must be launchable via `make <target>`.

## Execution rules

- **Everything runs in Docker. Nothing runs on the developer's machine.** The only host requirement is **Docker + make**. Do not install Maven, JDK, Node, npm, psql, cqlsh, kcat, etc. locally.
- Any CLI tool you need (Maven, npm, psql, cqlsh, jq...) is invoked through a `make` target that internally:
  - uses `docker compose exec <service>` when the service is already running, or
  - launches an ephemeral container with `docker run --rm` (e.g. `maven:3.9-eclipse-temurin-21`, `node:22-alpine`, `postgres:16-alpine` for psql).
- If a command does not yet exist in the `Makefile`, add it there. Instructions like "install X and run Y" are not documented or accepted in the README.
