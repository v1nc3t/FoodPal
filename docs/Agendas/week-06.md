# General information
**Date** - 18/12/2025  
**Time** - 16:45-17:30  
**Location** - online - link provided by TA on Mattermost
**Chair** - @emilieska
**Minute Taker** - @kmartirosyan
**Expected attendees** - @lzajac @hiwabuchi @teodormusat @gciobanu @kmartirosyan @emilieska


# Meeting plan:
- Introduction of today's agenda, naming the chair and minute taker. (2 min)
- Check of approval of the agenda (does everyone accept the agenda) (1 min)
- TA Announcements (10 min)
- Compare expected work and work done (1.5 min x 6 = 9 min)
- TA questions (5 min)
- Show UI demo (3 min)
- Discussion of the received feedback (3 min)
  - Code contribution and reviews
    - Focused commits (Good) - not have commented code in
    - Isolation (Excellent)
    - Reviewability (Poor) - MRs more than 8 commits behind main, make sure to update branch before creating MR
    - Code reviews (Excellent)
    - Build server (Good) - main is OK, but feature branches fail more often than ideal
    - Testing (Good) - pretty good
  - Tasks and planning
    - Issue creation (Good) - indicate user stories
    - Issue description (Poor) - too much variation, informalities, include more user stories
    - Planning (Poor) - assign more issues to team members
    - Time tracking (Poor) - add estimates for ALL issues, keep track of time
- Issues/improvements to work on (3 min)
  - Make text copy-able?
  - QOL and accessibility: keyboard navigation breaks when entering a preparation step (both tab and enter only edit text)
  - Input validation: negative numbers in fields
  - Ingredient and recipe calories should be inferred
  - Client-only recipe scaling
  - Hard coded translation string for recipe name instead of StringProperty
  - Cancel on edit recipe doesn't work
- Division of tasks before the next private meeting (10 min)
- Feedback/question round (3 min)
- Summary of meeting (2 min)
- End the meeting with a 20-second countdown, if questions arise, the countdown will be reset (1 min)

# TA questions:
- What method of websockets can/should we use? 'Raw' websockets or STOMP over websockets?

# Team discussion questions:
- How do we want to handle the UI for the shopping list?