# Project Log

## Team 02

**Members:**

- Lex Watts
- Damian Skeen
- Maclean Dunkin

---

## Sprint 1 (Week of March 31 - April 6, 2026)

### Completed Tasks

#### **Task 1 - Implement B-Tree Constructor**

**Developer:** Lex
**Issue:** <https://github.com/users/LexW2007/projects/1/views/1?pane=issue&itemId=172637041&issue=LexW2007%7Csp26-CS321-team02%7C1>
**Description:** Configured constructors for B-Tree
**Commit Reference:** commit 626c3e1
**Status:** Closed

---

#### **Task 2 - B-Tree Insert Operation**

**Developer:** Lex
**Issue:** <https://github.com/users/LexW2007/projects/1/views/1?pane=issue&itemId=172637042&issue=LexW2007%7Csp26-CS321-team02%7C2>
**Description:** Configured and tested insertion operation for B-Tree class
**Commit Reference:** commit 626c3e1
**Status:** Closed

---

#### **Task 3 - Implement diskRead()**

**Developer:** Lex
**Issue:** <https://github.com/users/LexW2007/projects/1/views/1?pane=issue&itemId=172637043&issue=LexW2007%7Csp26-CS321-team02%7C3>
**Description:** Implemented and tested diskRead() method for searching and insertion
**Commit Reference:** commit 626c3e1
**Status:** Closed

---

#### **Task 4 - Implement diskWrite()**

**Developer:** Lex
**Issue:** <https://github.com/users/LexW2007/projects/1/views/1?pane=issue&itemId=172637044&issue=LexW2007%7Csp26-CS321-team02%7C4>
**Description:** Implemented and tested diskWrite() method so that nodes persist while program runs
**Commit Reference:** commit 626c3e1
**Status:** Closed

---

#### **Task 5 - Create fake SQLite data for testing**

**Developer:** Lex
**Issue:** <https://github.com/users/LexW2007/projects/1/views/1?pane=issue&itemId=172637045&issue=LexW2007%7Csp26-CS321-team02%7C5>
**Description:** Created placeholder SSH keys for SSHSearchDatabase class for testing.
**Commit Reference:** commit 626c3e1
**Status:** Closed

---

#### **Task 6 - Implement SSHSearchDatabase driver**

**Developer:** Lex
**Issue:** <https://github.com/users/LexW2007/projects/1/views/1?pane=issue&itemId=172637046&issue=LexW2007%7Csp26-CS321-team02%7C6>
**Description:** Implement and test search and count functionality for database.
**Commit Reference:** commit 626c3e1
**Status:** Closed

---

#### **Task 7 - Create Scrum Project Board**

**Developer:** Lex
**Issue:** <https://github.com/users/LexW2007/projects/1/views/1?pane=issue&itemId=172637047&issue=LexW2007%7Csp26-CS321-team02%7C7>
**Description:** Created project board for tasks in Github repo.
**Commit Reference:** n/a
**Status:** Closed

---

#### **Task 8 - Instructor Meeting Logged**

**Developer:** Damian
**Issue:** <https://github.com/users/LexW2007/projects/1/views/1?pane=issue&itemId=172637048&issue=LexW2007%7Csp26-CS321-team02%7C8>
**Description:** Schedule and attend short meeting with instructor for clarification on project requirements.
**Commit Reference:** n/a
**Status:** Open

---

## Sprint 2 (Week of April 14 - April 19, 2026)

### Completed Tasks

#### **Task 9 - Implement SSHCreateBTree driver**

**Developer:** Team 02 (AI-assisted checkpoint session)
**Issue:** n/a
**Description:** Added the missing `SSHCreateBTree` entry point, command-line parsing, wrangled SSH
log extraction by tree type, B-Tree creation, dump-file writing, and SQLite table output for the
checkpoint-2 deliverable.
**Commit Reference:** working tree (not yet committed)
**Status:** Review/QA

---

#### **Task 10 - Support optimum degree and repeatable database output**

**Developer:** Team 02 (AI-assisted checkpoint session)
**Issue:** n/a
**Description:** Added support for `--degree=0` by resolving the largest degree that fits a
4096-byte block and updated database output to replace table contents on repeated runs instead of
accumulating duplicates.
**Commit Reference:** working tree (not yet committed)
**Status:** Review/QA

---

#### **Task 11 - Add focused checkpoint 2 tests**

**Developer:** Team 02 (AI-assisted checkpoint session)
**Issue:** n/a
**Description:** Added targeted tests for create-side argument parsing, tree-type key extraction,
`degree=0` resolution, and a small end-to-end create flow covering dump-file and database output.
**Commit Reference:** working tree (not yet committed)
**Status:** Review/QA

---

#### **Task 12 - Verification attempt and checkpoint notes**

**Developer:** Team 02 (AI-assisted checkpoint session)
**Issue:** n/a
**Description:** Installed OpenJDK 21 for the local shell, reran `bash ./gradlew test`, rebuilt the
create jar, and verified that all nine checkpoint-2 dump files match the provided reference output.
**Commit Reference:** working tree (not yet committed)
**Status:** Closed

---

---

## Sprint 3 (Week of April 14 - April 19, 2026)

### Completed Tasks

#### **Task 13 - Implement Cache

**Developer:** Damian Skeen ("Co-Pilot assisted")
**Issue:** <https://github.com/users/LexW2007/projects/2?pane=issue&itemId=173381788&issue=LexW2007%7Csp-CS321-team02%7C11>
**Description:** Implemented the cache from prject 1 into this project to improve performace and reduce disk reads.
**Commit Reference:** Commit 3032a34
**Status:** In progress

---

#### **Task 14 - Pass All Integration Test Scripts

**Developer:** Lex Watts, Maclean Dunkin
**Issue:** <https://github.com/users/LexW2007/projects/2?pane=issue&itemId=173381995&issue=LexW2007%7Csp-CS321-team02%7C14>
**Description:** Debug and testing for BTree creation, search, and database management.
**Commit Reference:** Commit 28ce1ac
**Status:** Closed

---

#### **Task 15 - Write SSHDataWrangler

**Developer:** Maclean Dunkin
**Issue:** n/a
**Description:** Implemented and tested DataWrangler class for database information.
**Commit Reference:** Commit 3b31c37
**Status:** Closed

---
