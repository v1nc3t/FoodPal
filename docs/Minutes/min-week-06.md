# General information
**Date** - 18/12/2025  
**Time** - 16:45-17:30  
**Location** - online - link provided by TA on Mattermost
**Chair** - @emilieska
**Expected attendees** - @lzajac @hiwabuchi @teodormusat @gciobanu @kmartirosyan @emilieska

# Meeting plan:
- Introduction of today's agenda, naming the chair and minute taker. (1 min -> 1 min)
    - Notes: Agenda was introduced verbally.

- Check of approval of the agenda (does everyone accept the agenda) (1 min -> 1 min)
    - Notes: No objections; agenda approved.


- TA Announcements (10 min -> 5 mins)
    - **Notes:** published feedback on gitlab.
    - Don't make one line buddy check, be more precise with feedback.
    - During winter break we can make contributions but won't count for the final grade/ contributions.
    - Check the pipelines for any faulty test's.


- Compare expected work and work done (2 mins x 6 = 12 mins -> 10 mins)
    - **George:**  language  switch has a interface manager that changes all language scene's.
    - **Lukas:** Add list of ingredients and refactored RecipeList doesn't depend on recipe anymore, RecipeManager is not a singleton anymore.
    - **Teo:** favourites/ unfavorite button made a new uuid for fav recipe's.
    - **Emilis:** Recipe language , filter language.
    - **Hiroki:** UI refactored so now it resizable , Has been put in vbox and hbox, Started work on the ShoppingList
    - **Karen:** Completed the basics of websockets.
        - **Notes:** New scene should use vbox and hboxes. When implementing a button internationalize it


- Show UI demo (1 mins -> 2 mins)
    - **Notes:**
    - Hiroki showed the resizing - preparation is now a textfield.
    - Hiroki showed the ingredients list which Lukas added.
    - Teodor showed the favourites/unfavorite button.


- Discussion of the received feedback (3 min -> 5 min)
    - Code contribution and reviews:
        - Focused commits (Good) - not have commented code in
        - Isolation (Excellent)
        - Reviewability (Poor) - MRs more than 8 commits behind main, make sure to update branch before creating MR
        - Code reviews (Excellent)
        - Build server (Good) - main is OK, but feature branches fail more often than ideal
        - Testing (Good) - pretty good
    - Tasks and planning:
        - Issue creation (Good) - indicate user stories
        - Issue description (Poor) - too much variation, informalities, include more user stories
        - Planning (Poor) - assign more issues to team members
        - Time tracking (Poor) - add estimates for ALL issues, keep track of time
    - Notes:
        - Make sure update the branch to the latest so you are not behind a lot of commits
        - When creating a issue open the rubric to check .
        - Descriptions should have more descriptive.


- Issues/improvements to work on (3 min -> 3 min)
    - Make text copy-able?
    - QOL and accessibility: keyboard navigation breaks when entering a preparation step (both tab and enter only edit text)
    - Input validation: negative numbers in fields
    - Ingredient and recipe calories should be inferred
    - Client-only recipe scaling
    - Hard coded translation string for recipe name instead of StringProperty
    - Cancel on edit recipe doesn't work


- Task Division Before Next Meeting (10-15 mins -> 15 mins)
    - **George:** Finishing Websocket.
    - **Lukas:** Increase use of dependency injection , Ingredient list and editing.
    - **Teo:**  Finish Server util.
    - **Emilis:** Fixing issues , Finishing Websocket.
    - **Hiroki:** Shopping list potentially (Websocket).
    - **Karen:** Finishing Websocket.
        - **Notes:** Discuss in private meeting how we are going to split Websocket workload after we know what method to use.


- Summary of meeting (5 mins -> 2 mins)
    - **Notes:**
    - Discussed agenda
    - heard TA announcement
    - Got the feedback
    - Discussed feedback
    - Showed ui
    - current issues

- End meeting with a 20 second countdown, if questions arise countdown will be reset (1 min -> 20s)
    - **Notes:** no extra question, tradition of countdown + awkward silence was held.

# TA question:
- Raw Websocket Vs. STOMP over Websocket? -> Ta has to check with the course staff because it isn't specified
- Where should we ask the questions about Websockets? -> on the private group of MatterMost

# Team discussion questions:
- How do we want to handle the UI for the shopping list? -> Will have its own separate scene where you can edit and add ingredients for the shopping list.
- What method of Websocket to use? -> Will be discussed  after we know from TA

# To-Do List
- Finish websocket
- Finish server utils
- Fix issues
- Work on Shopping list
- Finish ingredient list & editing