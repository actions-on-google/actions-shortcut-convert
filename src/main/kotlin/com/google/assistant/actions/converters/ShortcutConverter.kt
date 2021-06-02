package com.google.assistant.actions

import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.actions.Action

import com.google.assistant.actions.model.shortcuts.*

class ShortcutConverter {

    fun convertActionsToShortcuts(actions: ActionsRoot): List<Shortcut> {

        // Creates a map of actions to entitySetIds for use when creating the <shortcut> tags
        val actionToEntitySetId = HashMap<String, Action>();
        actions.actions.forEach { action ->
            val entitySetId = getEntitySetIdForAction(action)
            if (entitySetId != null && entitySetId.isNotEmpty()) {
                actionToEntitySetId[entitySetId] = action
            }
        }

        val shortcuts: MutableList<Shortcut> = mutableListOf()
        actions.entitySets.map { entitySet ->
            val matchingAction = actionToEntitySetId[entitySet.entitySetId]
            // TODO(paullucas): Handle duplicate cases where there may be more intents/action
            val intent: ShortcutIntent = createIntentFromAction(matchingAction) ?: ShortcutIntent();
            entitySet.entities.map { entity ->
                {
                    val shortcut = Shortcut(
                        shortcutId = entity.identifier,
                        shortcutLongLabel = entity.name ?: entity.alternateName ?: "",
                        intent = listOf(intent)
                    );
                    shortcuts.add(shortcut)
                }
            }
        }
        return shortcuts
    }

    private fun createIntentFromAction(action: Action?): ShortcutIntent? {
        if (action == null) {
            return null;
        }
        // TODO(paullucas): Add fields targetClass, targetPackage and data
        return ShortcutIntent(action = action.intentName);
    }


    private fun getEntitySetIdForAction(action: Action): String? {
        action.parameters.forEach { parameter ->
            if (parameter.entitySetReference != null &&
                parameter.entitySetReference!!.entitySetId != null) {
                return parameter.entitySetReference!!.entitySetId
            }
            return parameter.entitySetReference?.entitySetId ?: "";
        }
        return null;
    }
}
