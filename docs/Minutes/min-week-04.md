# General information
**Date** - 05/12/2025  
**Time** - 16:45-17:30  
**Location** - Drebbelweg, PC Hall 2  
**Chair** - @teodormusat
**Minute Taker** - @gciobanu
**Expected attendees** - @lzajac @hiwabuchi  @gciobanu @kmartirosyan @emilieska  @emilieska

# Meeting plan:
- Introduction of today's agenda, naming the chair and minute taker. (1 mins -> 1 min)
    - Notes: Agenda was introduced verbally.
- Check of approval of the agenda (does everyone accept the agenda) (1 min -> 1 min)  
    - Notes: No objections; agenda approved.
- TA Announcements (10 min -> 10 mins)
    - Notes: 
    - Did all students complete the Buddycheck? - everyone did
    - Early mistakes are expected; the last weeks are more important for grading.
    - Formative feedback soon, will be harsh - everyone wants that
    - GitLab will be down (10 - 17 dec); Week 6 has only Thursday and Friday available.
    - Week 6, meeting online on Thursday same time (16:45-17:30), link will be shared by TA on mattermost.
    - Multiple TAs will grade in the end; last weeks matter more than first ones
    - Check your email for requirement-related notifications (if not met).
- Compare expected work and work done (2 mins x 6 = 12 mins -> 10 mins)
    - Notes: 
    - Remember to merge with main every day. This week, a branch fell 50 commits behind, and an incomplete merge reverted changes from an earlier merge request.
    - **George:** Added testFX for one contoller (70% coverage), minimal resizing and optimizing, implemented live language switching locally only. 
    - **Lukas:** Finished add/remove ingredient; small bug remaining. Server utils planned for tonight.
    - **Teo:** Clone button completed and added to UI. Added tests for cloning.
    - **Emilis:** Implemented ordering/sorting, recipe list viewer; date feature pending.
    - **Hiroki:** Config file + class linked to server, favourites support, language, saving locally.
    - **Karen:** Printing feature; merge confict was not resolved properly causing reversal of resizing changes.
- TA questions (and answers) (5 mins -> 5 mins)
    - Notes:
    - Reminder: check backlog for details about implementing features
    - Can you rename a branch? - yes, but dont give bad name in future to be in position to rename
    - Favourites logic: Debate between adding a boolean in Recipe or using a list of UUIDs.
        - Team decided on storing a list of cloned UUIDs + names.
    - Is there the 100 lines requirement for week 5? - no, they are not counted
- Show UI demo (1 mins -> 2 mins)  
    - Notes: Demonstrated UI:
        - Cloning recipes
        - Adding ingredients
        - Fullscreen
        - Printing functionality
        - Live language change (on George's local machine)
- Task Division Before Next Meeting (10-15 mins -> 10 mins)
- **George:** TestFX, finalize live language switching
- **Lukas:** Ingredient view, refactoring
- **Teo:** Favourites system
- **Emilis:** Fix sorting, commons recipe language
- **Hiroki:** Resizing, start shopping list
- **Karen:** WebSockets, print testing
- Summary of meeting (5 mins -> 2 mins)  
    - Notes:
    - Reviewed and accepted the agenda  
    - Heard TA announcements  
    - Compared progress  
    - Light workload this week compared to last 
    - Found bugs 
    - Assigned new tasks
- End meeting with a 20 second countdown, if questions arise countdown will be reset (1 min -> 20s)
    - Notes: no extra question, tradition of countdown + akward silence was held.

# TA question:
- Logic for favourites.Boolean attribute in the recipe class or a list of UUIDs of favourites ?

# To-Do List
- Server utils  
- Finish live language change  
- Fix sorting  
- Improve ingredient addition (amount case)  
- Recipe language attribute  
- Tabs for ingredients/recipes  
- Favourites  
- Resizing improvements