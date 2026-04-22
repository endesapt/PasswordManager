# Team Workflow

## GitHub labels
- `backend`
- `ui`
- `docs`
- `tests`
- `ci`
- `security`
- `good first slice`
- `blocked`

## GitHub Project columns
- `Backlog`
- `Ready`
- `In Progress`
- `Review`
- `Done`

## Issues to create
1. Create GitHub organization and repositories
2. Set up GitHub Project board and labels
3. Prepare app repository structure
4. Prepare docs repository and connect app as submodule
5. Define app layers and folder structure
6. Add Room entities, DAO and database configuration
7. Implement vault repository for CRUD operations
8. Implement master password setup and verification
9. Add encryption service for password fields
10. Add search and filtering for vault items
11. Integrate HIBP password breach check
12. Show compromised password status in domain layer
13. Set up app navigation and screen skeletons
14. Implement onboarding and unlock screens
15. Implement vault list screen
16. Implement add/edit entry screen
17. Implement entry details screen
18. Implement settings/about screen
19. Connect UI to ViewModels and repositories
20. Add unit tests for crypto service
21. Add unit tests for repository and use cases
22. Add unit tests for ViewModels
23. Add Compose UI tests for onboarding and unlock
24. Add Compose UI tests for vault CRUD flows
25. Configure Firebase Test Lab UI test execution
26. Configure GitHub Actions for Android build and unit tests
27. Publish APK artifact from CI
28. Add GitHub Actions workflow for Firebase Test Lab
29. Configure GitHub Pages deployment
30. Write README according to PMVS requirements
31. Create GitHub Pages main page
32. Document functional requirements and use cases
33. Add file diagram and architecture description
34. Add database schema page with image and schema.sql
35. Add project presentation link and team contribution page

## Pull request slices
- PR 1: Project bootstrap
- PR 2: Data layer foundation
- PR 3: Master password and crypto
- PR 4: Vault repository and CRUD use cases
- PR 5: Navigation and auth UI
- PR 6: Vault list UI
- PR 7: Entry form and details UI
- PR 8: Connect UI to real logic
- PR 9: Breach check integration
- PR 10: Unit tests
- PR 11: UI tests
- PR 12: Android CI
- PR 13: Firebase Test Lab CI
- PR 14: Docs and GitHub Pages
