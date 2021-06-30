package com.google.assistant.actions

const val ACTIONS_XML_CONVERTED_SUCCESS_MESSAGE = "Your actions.xml was converted to shortcuts.xml."
// TODO: directly output a list of tags that require post-processing rather than directing to README only.
const val CHECK_README_MESSAGE =
    "Be sure to review the README 'Post-processing and Manual Inspection' for instructions on " +
            "to determine whether the shortcuts.xml output requires further modification."
const val COMMENTS_NOT_TRANSFERRED_MESSAGE =
    "Comments from your actions.xml are not transferred to your shortcuts.xml."

private const val ANSI_YELLOW = "\u001B[33m"
private const val ANSI_GREEN = "\u001B[32m"
private const val ANSI_RESET = "\u001B[0m"

fun createInfoMessage(infoMessage: String): String {
    return ANSI_GREEN + "INFO: " + ANSI_RESET + infoMessage
}

fun createWarningMessage(warningMessage: String): String {
    return ANSI_YELLOW + "WARNING: " + ANSI_RESET + warningMessage
}