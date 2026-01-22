# CSE Project - FoodPal

## What is it?
**FoodPal is an all-in-one novel, distributed cooking organizer app** 
that runs in a client/server setting. It allows users to manage their recipes, ingredients,
and shopping list in one unified application.

The repository contains the source code for FoodPal, which is the result of 
the Collaborative Software Engineering Project at TU Delft, 2025–2026, 
BSc Computer Science and Engineering, Y1 Q2.

## Getting started
The application consists of two parts: the server and the client.
>**Please note that the server needs to be running before you can start the client.**

To run FoodPal from the command line, you either need to have [Maven](https://maven.apache.org/install.html) 
installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

    mvn -pl server -am spring-boot:run

to run the server and

	mvn -pl client -am javafx:run

to run the client. 

Optionally, since the client uses a local config file to persist its configuration and client state,
e.g., server URL, list of favorite recipes, etc., you are able to define a custom path to the config file. 
This is done by passing it as a parameter to the client via a command-line argument in the following format:

    mvn -pl client -am javafx:run -Djavafx.args=--cfg=C:\Users\x\.foodpal\config.json

Where `C:\Users\x\.foodpal\config.json` is the custom path to the config file of a Windows user named `x`. 
Moreover, if no path is specified via the command line, this is the default path to the config.
>Keep in mind that the path must be valid: it must be a path to a **file** on the user's system 
that exists and is writable, as opposed to a directory.

## Importing the project into your IDE
If you want to contribute to the project, you can try importing the project into your favorite IDE. 
The client may be a bit more tricky to set up in an IDE due to the dependency on a JavaFX SDK.
However, to help you get started, you can find additional instructions in the corresponding 
[README](client/README.md) of the client project.

## Features and user-guide

>When you start the application, you'll be greeted with many buttons and labels. If everything seems confusing at first, and
you are unsure what to do to get started with FoodPal,
or if you simply want to learn more about all the subtleties of the app, the following sections will help you.

### Basic recipe and ingredient management

- By pressing the `+` button at the bottom left, you can add a new recipe to your list at any time. You can select:
  - the title
  - the language
  - the ingredients:
    - if there are any previously used ingredients, you can select them from the drop-down to the right.
    - if what you want to add is not there, you can click on the '+' to add a new ingredient. You will be prompted to enter
the name, amount, unit, and nutritional values (per 100 grams) for the ingredient. Once you save, it will be added to the ingredient
list of the recipe. Make sure to use positive numbers for the values. For the units you can either choose one from the drop-down to the
right of the input field (considered format units) or enter your own custom unit, which will be considered informal and won't be considered in
nutritional calculations.
    - you can edit individual ingredients by clicking 'Edit' to the far right.
  - the preparation steps:
    - you can edit individual preparation steps by clicking 'Edit' to the far right. You can then save changes by clicking 'Save'.
    - you can delete individual preparation steps by clicking '-' to the far right.
    - you can change the step order by using the up and down arrows to the far right of the preparation step.
  - the portions. Make sure to enter a positive whole number.
  - click 'Done' to save the recipe, or 'Cancel' to discard the changes.
- By pressing the '-' button at the bottom left, you enter 'remove mode' and clicking on a recipe will remove it from your list, after
confirmation. If you change your mind, you can cancel the removal by clicking on a recipe and pressing cancel. If you want to delete an
ingredient, do the same but on the ingredients tab. Do keep in mind there will be no confirmation by default! 
However, in case an ingredient is used in recipes, you will have to confirm you want to delete it by considering how many recipes it's used in 
to avoid accidental changes to recipes.
- By pressing the 'Clone' button at the bottom left, you enter 'clone mode' and clicking on a recipe will clone it;
you can change the name of the cloned recipe in the confirmation screen, or, by default, (Copy) will be appended to the name.
- By pressing either the 'Recipes' or 'Ingredients' tab at the top of the list on the left, you can switch between 
viewing either recipes or ingredients.
  - Viewing the recipe list enables all the functionality described below.
  - Viewing the ingredient list disables filtering by language and favourites, cloning, and entering favourite mode, as these are 
intended for recipes only.
- You can view the recipe details by double-clicking on a recipe in the list. Similarly, you can view ingredient details 
by double-clicking on an ingredient.
- Once you view a recipe, details about the recipe are shown in the right section of the screen. Here you will see:
  - the recipe title
  - total nutritional values (calories, protein, carbohydrates, fat)
  - the serving size (calories / portion)
  - the inferred calories per 100 grams
  - the ingredient list
  - the preparation steps
  - the recipe language
  - the recipe portions
- Once you view an ingredient, details about the ingredient are shown in the right section of the screen. 
Here you will see:
  - The ingredient name
  - The nutritional values (protein, carbohydrates, fat) per 100 grams
  - Estimated calories per 100 grams
  - How many recipes the ingredient is used in
- By clicking 'Edit' at the middle bottom of the ingredient details, you will be able to edit the ingredient name,
protein, carbs, and fat values for 100 grams. Make sure you use a positive number for each value.
- By clicking 'Edit' at the bottom right of the recipe details, you will be able to edit a recipe. This will enter
a screen familiar to adding a recipe, so look for the instructions above.
- By clicking 'Print' at the bottom right of the recipe details, you can print the recipe to bring to the kitchen.
This will save it to a .txt file in your desired location that you can print out.

### Automated change synchronization
- Modernizes the application with auto-updates, eliminating the need for manual refreshes.
- Changes are auto-propagated across all clients.
- Recipes, ingredients, and their details are updated in real-time.
- Uses web sockets to subscribe for changes.

### Nutritional values

- By clicking the '+,' '-' or 'Reset' buttons at the bottom to the right of the 'Portions' count, you can scale the recipe.
  - The '+' button will increase the portions by 1.
  - The '-' button will decrease the portions by 1, if possible.
  - The 'Reset' button will reset the portions to what the author of the recipe specified. You can at a glance tell
when a recipe is scaled by either:
    - Looking at the portions count - a scaled recipe will feature a ~ symbol next to it.
    - The reset button is only enabled when the recipe is scaled; otherwise, it will be greyed out.
  - Keep in mind this does not change the actual number of portions in the recipe. This applies a scale to the recipe
to be able to adapt recipes to individual needs, e.g., making a recipe for twice the people the author designed the recipe
for. Scaling results in the following:
    - Scales the total nutritional values (calories, protein, carbs, fat) of the recipe, as more ingredients are used.
    - Scales the respective quantity of ingredients needed. These will also carry over to the shopping list if 
they're added when scaled. Additionally, the units will be normalized, i.e., either in kilograms or liters to make reading
large values easier.
    - The serving size (calories / portion) and inferred calories per 100 grams will NOT change because the ratio
of calories / portion or calories / 100 grams remains the same.
- Many of the features described in [Basic recipe and ingredient management](#basic-recipe-and-ingredient-management)


### Recipe organization
- By changing the sort order of the list (to the right of the search bar), you can sort the item list (both ingredients
and recipes) by their name either in alphabetical or reverse alphabetical order.
- By pressing the 'Favourite/Unfavourite' button at the bottom left, you enter 'favourite mode' and clicking on a recipe 
will toggle its favourite status. You are easily able to see which recipes are favourite by looking at the star icon 
next to their name.
- By toggling the 'Only show favourite recipes' checkbox, you will only see your favourite recipes in the list.
  - Keep in mind this option only affects recipes and not ingredients, as ingredients cannot be favourite. That's why
you won't be able to toggle the favourite checkbox for ingredients.
- Favourite recipes are saved in the config file and persisted through a restart of the application.
- In case a favourite recipe gets deleted by someone else, you will be notified (either upon client initialization 
or while using the application) that the recipe has been removed.
- By entering a query into the search bar at the top left, you can filter the recipe list by their title, included ingredients,
and featured preparation steps. If you are viewing the ingredients, the search bar will also filter the ingredient list, based 
on the names.
  - You can combine your search terms with spaces, which will then show all results that feature all the terms.
  - You can cancel your search at any time by pressing 'Escape' on your keyboard.
  - Bonus functionality: searching returns a list sorted by relevance of your search, e.g., a matching query in the title or name
  will result in an item appearing higher in the list than if an ingredient or preparation step matches.
  - Bonus functionality: you can append a '-' in front of terms in your search query to show all items that exclude the term.

### Shopping list

- By pressing the shopping cart icon at the bottom left, you can view your shopping list at any time. Inside, you will see
the list of ingredients added to the shopping list (if there are any).
  - By pressing the 'Edit' button to the right of an ingredient in the shopping list, you can edit the name or amount 
of the ingredient you want to buy. Make sure not to forget the units for the amount as well.
  - You can remove items from the shopping list by clicking '-' to the far right of the ingredient name.
  - By pressing the 'Add Item' button at the bottom middle, you can add an ingredient to your shopping list by hand. Adding 
an item will prompt you to enter the name of the ingredient and amount of the ingredient. Make sure not to forget the 
units for the amount as well.
  - By pressing the 'Print' button at the bottom middle, you can print your shopping list to bring to the store. This will
save a .txt file in your desired location that you can print out.
  - You can also remove all items from the shopping list at once by clicking 'Clear List.' This will ask for a confirmation,
just to make sure you don't accidentally clear your entire shopping list.
- By clicking the 'Add to Shopping List' button above the ingredient list while viewing a recipe, you can add all ingredients in the recipe 
to your shopping list.
  - Once you click this button, you will be prompted with an overview of the ingredients you are about to add to your shopping list.
  - You can make changes to the list by clicking 'Add Item,' 'Remove Item,' or make changes to the existing ingredients
by double-clicking on their name or amount.
  - You will see that ingredients filled from the recipe have a source recipe name next to them (which you cannot
edit), so you don't forget which ingredient you needed for what.
  - Adding an item will prompt you to enter the name of the ingredient and amount of the ingredient. Make sure not to forget
the units for the amount as well.
  - If you change your mind, you can click 'Cancel' to cancel the addition of ingredients to your shopping list.
  - If you are happy with the ingredient overview, you can click 'Add to Shopping List' to 
add all ingredients to your shopping list.

### Live language switch

- By clicking the flag icon next to the search bar at the top left, you can choose what language you want to see
the client in. FoodPal supports English, Dutch, and German. Keep in mind that you can change the language anytime and
can tell at a glance which language you are currently using by looking at the flag icon.
- By toggling the language checkboxes shown next to 'Show recipes in,' you can filter which recipes you see in the list based on their
language. A selected language means that recipes will be shown in that language, so if you select all languages,
all results will be shown, if you unselect all, there will be no results.
  - Keep in mind this option only affects recipes and not ingredients, as ingredients don't have a language associated with them.
That's why you won't be able to toggle the language checkboxes for ingredients.
- The language selection and filters are saved in the config file and persisted through a restart of the application.

### Miscellaneous
- By clicking the sun/moon icon at the top left, to the right of the sort options, you can toggle between light and dark mode.


## Licensing
The project and application are licensed under the Apache License, Version 2.0.
```
Copyright 2026 Team 45 at TU Delft
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
*For more information see the [LICENSE](LICENSE.txt) file.*