# Trading Simulation Development Rules

Before modifying code:

1. Audit all relevant files.
2. Explain the root cause.
3. Present a detailed implementation plan.
4. Wait for approval.
5. Make only minimal changes.
6. Preserve all existing functionality.
7. Do not introduce unnecessary dependencies.
8. Compile using:

javac -d bin -cp "lib/*;libs/*;src" src/Main.java src/auth/*.java src/db/*.java src/model/*.java src/engine/*.java src/display/*.java src/ds/*.java src/io/*.java src/admin/*.java

9. Report:
- Files changed
- Why they changed
- Verification performed