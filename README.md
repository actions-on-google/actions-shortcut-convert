# App Actions actions.xml to shortcuts.xml conversion utility

This project is a conversion from actions.xml to shortcuts.xml. The shortcuts.xml 
output will migrate most (but not all) content in your actions.xml.

Check out the
 [shortcuts.xml documentation](https://developers.google.com/assistant/app/action-schema)
 for more details on the new XML format.
 
 ## Setup (Recommended)
* IntelliJ IDE (Or any Java/Kotlin IDE)
* Configure IDE settings for Gradle to use JVM 1.8 
(Settings/Preferences -> Build/Execution/Deployment -> Build Tools -> Gradle)
* Select JDK corresponding to JVM 1.8 in Project Structure dialog 
(File -> Project Structure -> Platform Settings -> SDKs)
 
 ## Usage
 #### Command
Once your environment is set up, run this command from the root directory of the
project to convert your actions.xml:
 
 `./gradlew run -Pactions=<path_to_actions.xml> -Pshortcuts=<path_to_shortcuts.xml>`

#### Verifying
 Manually you'll need to review your new shortcuts.xml as well as test the output in the
 [App Actions Test Tool](https://developers.google.com/assistant/app/test-tool).
 
Post-processing may be needed for defaults based on the use case and/or if you
want to take advantage of the new shortcuts.xml features (e.g. targetClass fulfillment). 

Please check out the
 [shortcuts.xml documentation](https://developers.google.com/assistant/app/)
 to get a better idea on new features.
 

 
 
 ## Tests
 #### Unit Tests
 Can be seen in 
 actions-shortcuts-convert/src/test/kotlin/com/google/assistant/actions/ConvertActionsToShortcutsTest.kt

TODO Add any other details here

#### End to End Tests
TODO Add I/O files
