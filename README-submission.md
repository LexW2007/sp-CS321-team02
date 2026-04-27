# Team 02

| Last Name | First Name | GitHub User Name |
|-----------|------------|------------------|
| Watts     | Lennox     | LexW2007         |
| Skeen     | Damian     | TBD              |
| Dunkin    | Maclean    | Maclean-D        |

### Test Results

How many of the dumpfiles matched (using the check-dump-files.sh script)?

9 of 9 dumpfiles matched using the provided `create-btrees.sh` and `check-dump-files.sh` scripts.

How many of the btree query files results matched (using the check-btree-search.sh script)?

9 of 9 full query files matched, and all 9 top-frequency checks passed using the provided
`search-btrees.sh` and `check-btree-search.sh` scripts.

How many of the database query files results matched (using the check-db-search.sh script)?

9 of 9 database query files matched using the provided `search-db.sh` and `check-db-search.sh`
scripts.

### AWS Notes

Pending team input from the required AWS run and screenshots.

### AI Usage

Cursor was used during checkpoint 2 to inspect the rubric and repository state, identify the
missing `SSHCreateBTree` deliverable, implement the create-side parsing and wrangled-log extraction
flow, add targeted checkpoint tests, and update repo-visible checkpoint documentation. The generated
changes were reviewed against the project README and sample result files before being kept.

### Reflection (Team member name: Lex Watts)

Learned how to create a fully functional B-Tree and how to use
SQL databases in tandem with my written B-Tree.
I also learned proper ways to dump and retrieve file information.
Later in the project I became more familiar with using AI assistants to
debug and test code funtionality.

### Reflection (Team member name: Damian Skeen)

Pending personal reflection from Damian Skeen.

### Reflection (Team member name: Maclean Dunkin)

I learned more about B-Trees and testing.
Finding bugs was the hard part.
I fixed them one step at a time.

## Additional Notes

- Repo work completed:
  - Added `SSHCreateBTree` and supporting create-side log parsing.
  - Added `degree=0` support via optimal B-Tree degree resolution.
  - Added persisted B-Tree metadata so B-Tree files can be reopened for search.
  - Added `SSHSearchBTree` and completed top-frequency B-Tree query output.
  - Completed `SSHSearchDatabase` top-frequency database search.
  - Added the optional `SSHDataWrangler` raw-log conversion program and tests.
  - Completed `BTree-Database-Analysis.md`.
  - Added focused unit tests for the checkpoint-2 create path.
  - Verified dump, B-Tree search, and database search outputs against the provided reference
    results.
- External/team inputs still required:
  - GitHub username for Damian Skeen
  - Weekly surveys / teammate evaluations
  - AWS notes and screenshots
  - Personal reflections for Damian Skeen
- Java is now available in the shell via Homebrew OpenJDK 21 and persisted through `~/.zprofile`.
