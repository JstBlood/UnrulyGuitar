## Meeting 3

Chair: Mikolaj

# Opening
There was no feedback for the backlog, so it cannot be discussed.
Implementing the user entity in the database schema is allowed and will be easier.
The schema has been pushed to the GitLab repo.

# Questions for the TA.
A board has a tag set, and every LIST or ENTRY you can add them?
Resolved: tags are for the ENTRIES. 

Dispersed some confusion for what a LIST and a CARD = ENTRY = TASK is. ENTRIES go vertical, LISTS go horizontal.

**From now on: one branch per person.**

Should we implement websockets?
Unresovled, deferred until later during implementation.

Should we implement attachments?
Resolved, NO

# READ/WRITE permissions
The password is just for writing, anyone with the key can read the board, even when the board is set to private.
This has to be be changed in the database schema. - DEFERRED, for now just remove the User entity, work out details later.

# Networking
Websockets and long polling are both options. In the video of Sebastian Proksch, long polling was discussed, so this has preference.

# Dividing the tasks
There are 32 issues, and they will be divided among the team members. The official backlog is used for this.

Tasks assigned as of current:
- Creating a DB branch where there is a USER entity. USERS are not password protected. USERNAMES are unique and used as primary key => Vlad
- Making the board joining system and joining screen. The boards can be created and are stored in the database, and people can join them. => Mikolaj
- Creating the board overview, with the lists and the entries and the UI. It does not need to be fully functional yet. => Matiss
- Adding entries __for the client__ and communicating this to the server => Madeline
- Adding lists __for the client__ and communcating this to the server => Bogdan
- Adding / removing / moving LISTS around __on the server__ and creating the API for the client => Michael
- Adding / removing / moving CARDS around __on the server__ and creating the API for the client => somebody who finishes early.

# Repository practices
If your commit does not build, do not push it. If you push it anyway, do not commit again to fix it, instead amend your commit.

# Targets for the next week
Sprints for milestones, demos, and contributions.
