# General information

Date - 21/11/2025

Time - 16:45-17:30

Location - Drebbelweg, PC Hall 2

Chair - @lzajac

Minute Taker - @hiwabuchi

Expected attendees - @lzajac @hiwabuchi @teodormusat @gciobanu @kmartirosyan @emilieska

Attendees - @lzajac @hiwabuchi @teodormusat @gciobanu @kmartirosyan @emilieska

This is the first meeting, so I do not expect a lot to transpire, thus the length of the agenda reflects that.

## Notes

1. Introduction of today's agenda, and naming the chair and minute taker. (2 mins)
    - Today Lucas was the chair and he started with an introduction of the topics.
2. Check of approval of the agenda (does everyone accept the agenda) (1 min)
3. TA Announcements (10 min)
    - The checking of the TA starts from saturday 0:00. She will check for example, if everyone has at least 100 lines of code.
    - The merge requests that aren't merged yet, will still get counted for that week if it's merged before saturday.
    - The lines of code will be counted with the date of the commit.
    - (2.50 min)
4. Git assignment (5 min)
5. Updates about the things done so far (trello board) (5 min)
    - We've done so far:
        - checkstyle is done
        - finished .fxml-files
        - data design is also finished
        - there is a merge request backend
        - we're progressing with the controllers
    - Only internationalization not yet
    - (2 min)
6. TA Questions (7 min)
    - FXML-files not counted as lines of code. (Just to be sure, it's better to read the documents)
    - Can we add other dependencies?
        - Like gluon
        - Added already jackson
        - Yes, you can add them
    - Online partcipation as a minute taker or chair
        - Yes you can but for a chair it is maybe better to do another meeting.
    - (4 min)
7. If the UI has made progress, we can view a quick demo (5 min)
    - Show the mainstage
    - Show the recipe adding stage
    - Good progress (TA)
    - (1 min)
8. Discuss [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) (5 min)
    - Conventional commits are a way to structure commit messages. With this it is easier to understand what happened. 
    - Stuctured as:
        - <type>[optional scope]: <description>
        - [optional body]
        - [optional footer(s)]
        - example: feat(ui); added a recipe window
    - Many different types and standard types
    - It is easier to understand and cleaner on gitlab
    - Look for more information on the website
    - The result of voting: let's use it! (6 out of 6)
    - (4 min)
9. Q&A (\<10 min)
    - Can i start with i18n? (teo)
        - It was very easy so maybe not for 2 people work (emilies)
        - The i18n also has to wait for the UI-skeleton.
10. Division of tasks before the next private meeting (10-15 min)
    - In this weekend or until thursday, UI working
        - For example a working add button
    - At least one class requesting for the server data.
    - Update informalAmout class with also an amount input.
    - In general it's better to not use abstract classes in the data structure, if there is another way to implement the system.

    - tasks:
        - Karen, george: Finish UI
            - merge a bit earlier the UI-skeleton, and start new branch like functionality.
            - If it's hard maybe split a task and give to someone else:
                - Karen tries to make an add button for the recipe.
        - Teo: Will do a button, UI related. (More information will follow in discord)

        - Emilies: internationalization
        
        - Lucas: Server-side
            - Persistence of the recipes to the database
                - Saving the data to disk and not to memory
            - tests

        - Hiroki: Data structure
            - Nutritionvalues
            - Update informalAmount class
            - tests

    - final weapon: testing!
    - (18 min)
11. An ending to the meeting, with a 20 second countdown during which anyone can still ask questions (resetting the countdown). (1 min)
    - Question george:
        - Shall we do a meeting in the week. -> discord and thursday

Total time: 35 minutes

## TA Questions
For answer look above.

- Do fxml files count towards weekly LoC? / What does not count towards LoC?
- Can we add dependencies?
- Can a minute taker participate online? (Week 5 George)

