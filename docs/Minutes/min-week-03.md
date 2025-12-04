# General information
**Date** - 28/11/2025  
**Time** - 16:45-17:30  
**Location** - Drebbelweg, PC Hall 2  
**Chair** - @gciobanu  
**Minute Taker** - @emilieska  
**Expected attendees** - @lzajac @hiwabuchi @teodormusat @gciobanu @kmartirosyan @emilieska

# Notes in order of the meeting plan:
- Introduction of today's agenda, naming the chair and minute taker. (1 min -> 1 min)
  - Notes: introduction carried out.
- Check of approval of the agenda (does everyone accept the agenda) (1 min -> 1 min)
  - Notes: no additions - everybody approves.
- TA Announcements (10 min -> 4 min)
  - Notes:
    - Are we all passing the weekly requirements (100 lines of code, merge request review, i.e. 100+ character meaningful comment, and 1 merge request)? -> Everyone is.
    - 'Buddycheck' news - covered later as one of the TA questions
    - **GitLab down: Week 5 wednesday - Week 6 wednesday**; this leaves only 2 days to merge until deadline. Tip: pull/update as late as possible, and push after it's back up.
    - No weekly requirements during midterms week!
    - **Dependencies (of our own) are NOT allowed**, so we cannot use them, as they might make certain tasks (too) easy.
- Compare expected work and work done (2 mins x 6 = 12 mins -> 10 min)
  - Notes:
    - Hiroki: refactored Informal- and FormalAmount to a single Amount, so it is easier to use in the database, additionally making use of Optional attributes.
    - Teo: remove button, recipe list view, tests for remove mode and actual removing of items.
    - Karen: recipe viewer, editing of recipes, search field and refresh button (not entirely done/polished, but the majority is there).
    - Emilis: basis for internationalization: resource bundles that contain translation keys for English, Dutch (both mandatory), and German as the third language, dynamic updates/instant propagation of language should be supported, once live language switching via, e.g. buttons, is implemented. 
    - George: adders for recipes and ingredients. Everything works well except propagating new ingredients to added recipes. Known issue, yet to be addressed: translation makes some buttons appear out of place.
    - Lukas: the amount of work was smaller than expected: changed/added comments, updated data structures to be either Entities or Embeddable, so they can be persisted on the database, and not only in-memory. Saving and loading of recipes to/from server.
    - Overall: the tasks that we set out to do this week have been successfully accomplished!
- TA questions (5 min -> 8 min)
  - Notes: questions and answers provided below
- Use of GitLab issues, overview, milestones (4 min -> 3 min)
  - Notes: 
    - General remark - the best way to deal with bugs: create an issue in GitLab, notify the relevant person, check issues frequently.
    - Should we make separate issues for everything we are implementing? Depends on the situation and scale, e.g. UI is very big, so definitely separate.
    - Lukas' tip: you can create **blocks**, i.e. bigger issues that have to be solved first, so the work-flow is clearer.
- Talk about MRs (2 min -> 2 min)
  - Notes: 
    - George: started off with one dev branch for UI, but too big of a task and only 1 MR, so meeting the requirements becomes hard.
    - **Takeaway: make smaller MRs**, make issues in case something is buggy or not complete, so we can all meet the quota.
- Show UI demo (1 min -> 5 min)
  - Notes: 
    - Showcasing the current UI: adding recipes, preparation steps, change title, removing recipes, refresh button.
    - Ingredients don't quite work yet, tweaking is necessary.
    - Languages showcase.
    - Back-end stuff is (basically) finished.
- Division of tasks before the next private meeting (10-15 min -> 15 min)
  - Notes: 
    - Checking goal to finish non-functional and basic requirements by week 3 of work (week 4 of quarter) - **so far pretty well.**
    - Agreed that switching up the roles for more variety, getting familiar, learning something new is beneficial.
    - Config file for users for saving settings, e.g. favorites, languages, (themes?), server URL: **Hiroki** (note: to research config files, e.g. where to store, etc.)
    - Recipe list viewer 'order-by' button - repurposing the multichoice box for sorting, e.g. alphabetical, most recent, etc.: **Emilis**
    - Favorites: **unassigned** (maybe Karen depending on work-load)
    - Printing recipes: **Karen** would be curious to see how that works; current goal - making it a nice format.
    - Editing preparation steps: **Lukas**
    - Adding and editing the ingredients (buggy as of now): **Lukas**
    - Cloning recipes: Karen has sort of done that accidentally, but **Teo** will do it thoroughly. Adding a new recipe at the end of the list, or one after another - doesn't matter where it appears.
    - Further internationalization improvements: **George**
    - Once/in case we finish the basic requirements, then we can do extra assignments, e.g. server side.
    - Server-side: only websockets are left, so for now until then, everyone works on mostly the front-end.
    - These tasks currently appear pretty small, so a second meeting will most likely be needed.
    - Note: no separate settings page (at least for languages, because it needs to be observable at first glance)
    - Testing UI is still up for question, as it might prove very difficult.
    - Separate buttons instead of right-clicking recipes might be a better choice.
- Summary of meeting (5 min -> 3 min)
  - Notes: double-checking the task distribution, double-checking progress - it is above expectations so far, so we are doing good!
- End meeting with a 20-second countdown, if questions arise countdown will be reset (1 min -> 2 min)
  - Notes: Git assignment repair
    - Manual grading of repairs not published entirely yet (announcement on Brightspace)
    - No further questions

# TA questions (and answers):
- Do we make custom milestones or use weeks as milestones?
  - Don't push it too much, using weeks is the most natural choice, but depends on what we need to do. Right now we have large milestones (not time-based but functional) but **restructuring to use time-based milestones instead would be better.**
- Do we have to use (story) points for issues?
  - Dividing them up a bit further could be beneficial, but not too much.
- Does the person who creates the MR have to merge it or can the person who accepts the MR merge?
  - Based on the feeling of satisfaction, the original merge request poster can/should be the one to merge, but **ultimately the course staff don't care.**
- What/when are the buddy checks?
  - Giving feedback about each other professionally: giving notes, positives, areas for improvement, distributing points, open-ended questions.
  - The **first buddy check won't be summative**, but formative (pass or fail), but will give feedback for the second and **final buddy check, which is then summative**.
  - Everyone fills the buddy check in individually, ranking each team member separately.
- AI features?
  - **Not recommended**, if it (negatively) affects other parts of the code; especially since they don't award extra points (maybe on a random branch just for fun)
- Print format?
  - **Anything we want.**
- Test lines?
  - All test lines contribute to total lines of code (**no 0.5x multiplier or anything**). Highest points are awarded for testing when publishing tests together with MRs (or at least relatively soon after).
- Personal criteria lines of code:
  - **Later-on deleted lines count as well**
- 100 lines for back-end and front-end:
  - **Only assessed at the end, not weekly**
- Issue and MR times:
  - **Issues should probably include some time management**, MRs not necessary (probably)
- What if you fail weekly criteria?
  - **You will get notified via email, TA can show the statistics later**