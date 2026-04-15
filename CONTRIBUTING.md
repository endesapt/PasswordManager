# Contributing

## Branching
- Protect `main` and merge only through pull requests.
- Create short-lived feature branches such as `feature/db-schema`, `feature/master-password`, `feature/ui-vault-list`, `feature/docs-pages`.

## Commits
- Keep commits small, buildable, and focused on one logical change.
- Recommended style:
  - `feat: add Room entities and DAO for vault items`
  - `feat: implement master password verification`
  - `test: add unit tests for vault repository`
  - `ci: add Android build workflow`
  - `docs: add GitHub Pages structure`

## Pull Requests
- One pull request should cover one issue or one vertical slice.
- Every PR should include:
  - short summary
  - linked issue
  - testing notes
  - screenshots for UI changes when relevant
- Prefer `Squash and merge` to keep `main` readable.

## Review checklist
- The app still builds.
- Unit tests pass.
- New UI is covered by at least one UI test when feasible.
- Documentation is updated if the feature changes user behavior or architecture.
