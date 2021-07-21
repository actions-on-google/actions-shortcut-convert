# App Actions actions.xml to shortcuts.xml conversion utility

This project is a conversion from actions.xml to shortcuts.xml. The shortcuts.xml output will migrate most (but not all)
content in your actions.xml.

Check out the
[shortcuts.xml documentation](https://developers.google.com/assistant/app/action-schema)
for more details on the new XML format.

## Setup (Recommended)

* IntelliJ IDE (Or any Java/Kotlin IDE)
* Configure IDE settings for Gradle to use JVM 1.8 or greater
  (Settings/Preferences -> Build/Execution/Deployment -> Build Tools -> Gradle)
* Select JDK corresponding to JVM 1.8 (or greater) in Project Structure dialog
  (File -> Project Structure -> Platform Settings -> SDKs)

## Usage

### Command

Once your environment is set up, run this command from the root directory of the project to convert your actions.xml:

`./gradlew run -Pactions=<path_to_actions.xml> -Pshortcuts=<path_to_shortcuts.xml>`

### Verifying

Manually you'll need to review your new shortcuts.xml as well as test the output in the
[App Actions Test Tool](https://developers.google.com/assistant/app/test-tool).

### Post-processing and Manual Inspection

#### XML Character Entity References

The converter escapes any literal `&` by converting it to `&amp;` and assumes any sequence of 10 characters surrounded
by `&` and `;` is a XML entity
reference [(list of XML character entity references)](https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references)
.

It is *highly* recommended that input actions.xml is well-formed XML with properly escaped characters. To ensure the
converter escaped characters as intended, it is also recommended that you verify that the generated shortcuts.xml
contains properly escaped character entity references.

#### Defaults

Post-processing may be needed for defaults based on the use case and/or if you want to take advantage of the new
shortcuts.xml features (e.g. `targetClass` fulfillment).

Field that require manual post-processing in the generated shortcuts.xml will be in the format of `YOUR_<FIELD_NAME>`.
For example, for `shortcutShortLabel`, you will see `YOUR_SHORTCUT_SHORT_LABEL`. To quickly find the fields that require
post-processing, you can do a search for`"YOUR_".`

Please check out the
[shortcuts.xml documentation](https://developers.google.com/assistant/app/)
to get a better idea on new features.

#### List of XML elements to post-process

See shortcuts.xml [schema](https://developers.google.com/assistant/app/action-schema#schema) for details on the elements
listed below.

##### Mandatory post-processing

* `shortcutShortLabel` attribute of `<shortcut>`

##### Optional post-processing

* `android:action` attribute of `<intent>` tag within `<capability>` and `<shortcut>`. This currently defaults
  to `android.intent.action.VIEW`

## Tests

#### Unit Tests

[`ConvertActionsToShortcutsTest`](test/kotlin/com/google/assistant/actions/ConvertActionsToShortcutsTest.kt) tests
proper conversion of actions.xml to shortcuts.xml using either programmatically built actions/shortcuts or sample
actions.xml and shortcuts.xml.

To add a unit test for a conversion case using sample test data, add the corresponding sample actions.xml and
shortcuts.xml to [the test data resources directory](/src/test/resources/testData) and reference the resources in the
corresponding test case. See `test standard actions using sample files`
in [`ConvertActionsToShortcutsTest`](test/kotlin/com/google/assistant/actions/ConvertActionsToShortcutsTest.kt) for an
example test case.

**Notes**:

* Ensure that the actions.xml test data contains proper XML. Special characters should be properly escaped.
