package com.google.assistant.actions.converters

import com.google.assistant.actions.ACTIONS_BUILT_IN_INTENT_RESERVED_NAMESPACE
import com.google.assistant.actions.ANDROID_ACTION_VIEW_DEFAULT_INTENT
import com.google.assistant.actions.model.actions.Action
import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.actions.Entity
import com.google.assistant.actions.model.actions.EntitySet
import com.google.assistant.actions.model.shortcuts.*

private var shortcutIdCount = 0

fun convertActionsToShortcuts(actionsRoot: ActionsRoot): List<Shortcut> {
    // Creates a map of entity set IDs to actions for use when creating the <shortcut> tags
    val entitySetIdToAction = mutableMapOf<String, MutableList<Action>>()
    actionsRoot.actions.forEach { action ->
        val entitySetIds = getEntitySetIdsForAction(action)
        entitySetIds.forEach { entitySetId ->
            val actions = entitySetIdToAction[entitySetId] ?: mutableListOf()
            actions.add(action)
            entitySetIdToAction[entitySetId] = actions
        }
    }

    val shortcuts = mutableListOf<Shortcut>()
    actionsRoot.entitySets.forEach { entitySet ->
        addShortcutsFromEntitySets(entitySet, entitySetIdToAction, shortcuts)
    }
    actionsRoot.actions.forEach { action ->
        action.entitySets.forEach { entitySet ->
            addShortcutsFromEntitySets(entitySet, entitySetIdToAction, shortcuts)
        }
    }
    return shortcuts
}

private fun addShortcutsFromEntitySets(
    entitySet: EntitySet,
    entitySetIdToActions: Map<String, List<Action>>,
    shortcuts: MutableList<Shortcut>
) {
    val matchingActions = entitySetIdToActions[entitySet.entitySetId] ?: return
    val shortcutIdToShortcutMap = mutableMapOf<String, Shortcut>()

    entitySet.entities.forEach { entity ->
        val capabilityBindings = createCapabilityBindingsFromEntity(matchingActions, entity)

        val intent: ShortcutIntent = createIntentFromEntity(entity)

        val id = entity.identifier ?: ""
        val extra = createExtraFromEntity(entity)
        val extras = if (extra != null) mutableListOf(extra) else mutableListOf()
        if (shortcutIdToShortcutMap.containsKey(id)) {
            if (extra != null) {
                shortcutIdToShortcutMap[id]!!.extras += extra //TODO: clean up !! usage
            }
        } else {
            // No shortcut created for this identifier, proceed with adding new shortcut.
            val shortcut = Shortcut(
                shortcutId = id.ifEmpty { "YOUR_SHORTCUT_ID_" + shortcutIdCount++ },
                shortcutShortLabel = "YOUR_SHORT_LABEL",
                shortcutLongLabel = entity.name ?: entity.alternateName ?: "",
                enabled = "false",
                intents = mutableListOf(intent),
                capabilityBindings = capabilityBindings,
                extras = extras
            )
            shortcuts.add(shortcut)
            shortcutIdToShortcutMap[id] = shortcut
        }
    }
}

private fun createCapabilityBindingsFromEntity(
    matchingActions: List<Action>,
    entity: Entity
): List<CapabilityBinding> {
    return matchingActions.mapNotNull { matchingAction ->
        createCapabilityBinding(matchingAction, entity).takeIf {
            val intentName = matchingAction.intentName
            intentName != null && intentName.startsWith(
                ACTIONS_BUILT_IN_INTENT_RESERVED_NAMESPACE
            )
        }
    }
}

private fun createIntentFromEntity(entity: Entity?): ShortcutIntent {
    // TODO(paullucas): Handle custom vs BII cases better here and update based on if intentName
    //  starts with actions.
    return ShortcutIntent(
        // Defaulting to android.intent.action.VIEW and allowing user to override
        action = ANDROID_ACTION_VIEW_DEFAULT_INTENT,
        data = entity?.url
    )
}

private fun createExtraFromEntity(entity: Entity): Extra? {
    if (entity.sameAs.isNullOrEmpty()) {
        return null
    }
    return Extra(
        key = "sameAs",
        value = entity.sameAs
    )
}

private fun createCapabilityBinding(
    action: Action,
    entity: Entity?
): CapabilityBinding {
    return CapabilityBinding(
        key = action.intentName,
        parameterBinding = mutableListOf(createParameterBinding(action, entity))
    )
}

private fun createParameterBinding(
    action: Action,
    matchingEntity: Entity?
): ParameterBinding {
    action.parameters.forEach { parameter ->
        return ParameterBinding(key = parameter.name, value = matchingEntity?.name)
    }
    return ParameterBinding(
        key = "YOUR_PARAMETER_BINDING_KEY",
        value = "YOUR_PARAMETER_BINDING_VALUE"
    )
}

private fun getEntitySetIdsForAction(action: Action): List<String> {
    return action.parameters.mapNotNull { parameter ->
        parameter.entitySetReference?.entitySetId
    }
}
