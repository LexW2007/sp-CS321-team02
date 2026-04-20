# Team 02

## Lex Watts, Damian Skeen, Maclean Dunkin

| Last Name | First Name | GitHub User Name |
|-----------|------------|------------------|
| Watts     | Lennox     | LexW2007         |
| Skeen     | Damian     | TBD              |
| Dunkin    | Maclean    | TBD              |

### Test Results

How many of the dumpfiles matched (using the check-dump-files.sh script)?

9 of 9 dumpfiles matched after installing OpenJDK 21 locally and rerunning the checkpoint-2 create
workflow.

How many of the btree query files results matched (using the check-btree-search.sh script)?

Not run for checkpoint 2. `SSHSearchBTree` belongs to the later integration/final-deliverable work.

How many of the database query files results matched (using the check-db-search.sh script)?

Not run for checkpoint 2. `SSHSearchDatabase` belongs to the later integration/final-deliverable
work.

### AWS Notes

Brief reflection on your experience with running your code on AWS.

Pending team input from the required AWS run and screenshots.

### Reflection

The first subsection below should include how the team used AI tools to help with the
project,including which tools were used, how they were used, and any benefits or challenges
encountered.

Provide a reflection by each of the team member (in a separate subsection for each team member) on
their experience with the project, including what they learned, what they found challenging, and how
they overcame those challenges.

### AI Usage

Cursor was used during checkpoint 2 to inspect the rubric and repository state, identify the
missing `SSHCreateBTree` deliverable, implement the create-side parsing and wrangled-log extraction
flow, add targeted checkpoint tests, and update repo-visible checkpoint documentation. The generated
changes were reviewed against the project README and sample result files before being kept.

### Reflection (Team member name: Lex Watts)

Pending personal reflection from Lex Watts.

### Reflection (Team member name: Damian Skeen)

Pending personal reflection from Damian Skeen.

### Reflection (Team member name: Maclean Dunkin)

Pending personal reflection from Maclean Dunkin.

## Additional Notes

- Repo work completed for checkpoint 2 in this session:
  - Added `SSHCreateBTree` and supporting create-side log parsing.
  - Added `degree=0` support via optimal B-Tree degree resolution.
  - Added focused unit tests for the checkpoint-2 create path.
  - Installed OpenJDK 21 and verified the checkpoint-2 dumpfile outputs against the provided
    reference results.
- External/team inputs still required:
  - GitHub usernames for Damian Skeen and Maclean Dunkin
  - Weekly surveys / teammate evaluations
  - AWS notes and screenshots
  - Personal reflections for each team member
- Java is now available in the shell via Homebrew OpenJDK 21 and persisted through `~/.zprofile`.
