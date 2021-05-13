package com.google.assistant.actions

import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.*

class ShortcutConverter {

    fun convertActionsToShortcuts(actions: ActionsRoot): List<Shortcut> {
        val shortcuts: MutableList<Shortcut> = mutableListOf()
        actions.entitySets.map { entitySet ->
            entitySet.entities.map {entity -> {
                shortcuts.add(Shortcut(
                    shortcutId = entity.identifier,
                    shortcutShortLabel= entity.name  ?: entity.alternateName ?: "",
                    shortcutLongLabel = entity.name  ?: entity.alternateName ?: ""))
            } }
        }
        return shortcuts
    }
}
