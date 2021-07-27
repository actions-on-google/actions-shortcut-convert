package com.google.assistant.actions.processors

import com.google.assistant.actions.COMMENTS_NOT_TRANSFERRED_MESSAGE
import com.google.assistant.actions.createWarningMessage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

// Literal '&' should be escaped, but not the '&'s that are already part of entity references.
// To allow numerical and named entity references, we assume substrings up to 10 characters long
// are entity references. Users of the tool should pre-process/post-process their
// actions/shortcuts.xml to ensure correct encoding.
private const val LITERAL_AMPERSAND_MATCHER_REGEX : String  = "&(?!(.{1,10});)"
private const val ESCAPED_AMPERSAND : String = "&amp;"
private const val COMMENT_MATCHER_REGEX : String = "<!--.*-->"

/** Pre-process actions.xml for unmarshalling. */
fun processActionsXml(actionsFile: File) : InputStream {
    val actionsFileAsString = actionsFile.readText()
    if (actionsFileAsString.contains(COMMENT_MATCHER_REGEX.toRegex())){
        System.err.println(createWarningMessage(COMMENTS_NOT_TRANSFERRED_MESSAGE))
    }
    val escapedActions = actionsFileAsString.replace(LITERAL_AMPERSAND_MATCHER_REGEX.toRegex(), ESCAPED_AMPERSAND)
    return ByteArrayInputStream(escapedActions.toByteArray(Charsets.UTF_8))
}
