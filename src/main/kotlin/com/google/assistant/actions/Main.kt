package com.google.assistant.actions

import com.google.assistant.actions.marshall.readActions
import com.google.assistant.actions.marshall.writeShortcuts
import com.google.assistant.actions.processors.processActionsXml
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2) {
        throw IllegalArgumentException("main requires two arguments")
    }
    val actionsArg = args[0]
    val shortcutsArg = args[1]

    val actionsFile = File(actionsArg).absoluteFile
    if (!actionsFile.isFile || !actionsFile.canRead()) {
        throw Exception("Actions argument is not a readable file (was: $actionsFile)")
    }

    val shortcutsFile = File(shortcutsArg).absoluteFile
    if (!shortcutsFile.parentFile.canWrite()) {
        throw Exception("Shortcuts argument parent directory is not writable (was: $shortcutsFile)")
    }

    val actionsRoot = processActionsXml(actionsFile).use { readActions(it) }
    val shortcutsRoot = convertActionsToShortcuts(actionsRoot)
    shortcutsFile.outputStream().use { writeShortcuts(shortcutsRoot, it) }
}
