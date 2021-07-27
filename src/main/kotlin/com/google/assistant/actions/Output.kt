// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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