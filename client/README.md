# Getting started with running the client
To run the FoodPal client from the command line, you either need to have [Maven](https://maven.apache.org/install.html)
installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then run the project 
out-of-the-box from your project root (not from within the `client` folder!) from your terminal via

	mvn -pl client -am javafx:run

Optionally, since the client uses a local config file to persist its configuration and client state,
e.g., server URL, list of favorite recipes, etc., you are able to define a custom path to the config file.
This is done by passing it as a parameter to the client via a command-line argument in the following format:

    mvn -pl client -am javafx:run -Djavafx.args=--cfg=C:\Users\x\.foodpal\config.json

Where `C:\Users\x\.foodpal\config.json` is the custom path to the config file of a Windows user named `x`.
Moreover, if no path is specified via the command line, this is the default path to the config.
>Keep in mind that the path must be valid: it must be a path to a **file** on the user's system
that exists and is writable, as opposed to a directory.

## Importing the project into your IDE
Starting the client within your IDE (Eclipse/IntelliJ) requires setting up OpenJFX.

First, download (and unzip) an [OpenJFX SDK](https://openjfx.io).
Make sure that the download *matches your Java JDK version*.

Then create a *run configuration* for the `Main` class and add the following *VM* commands 
(which in IntelliJ are hidden by default):

	--module-path="/path/to/javafx-sdk/lib"
	--add-modules=javafx.controls,javafx.fxml,javafx.web

Adjust the module path to *your* local download location. Make sure you adapt the path
to the `lib`(!) directory (not just the directory that you unzipped).

*Tip:* Windows paths are different, they use `\` as the path separator and start with a drive letter like `C:`.

*Tip:* Make sure not to forget the `/lib` at the end of the path.

*Tip:* Double-check that the path is correct. If you receive abstract errors, like `Module javafx.web not found`
or a segmentation fault, you are likely not pointing to the right folder. Try opening the folder and check that
it contains several .jar files, such as `javafx.controls.jar`.
