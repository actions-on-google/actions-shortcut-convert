package com.google.assistant.actions

import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.actions.Action
import com.google.assistant.actions.model.actions.Entity
import com.google.assistant.actions.model.actions.EntitySet

import com.google.assistant.actions.model.shortcuts.*
import kotlin.random.Random

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
        actions.entitySets.forEach { entitySet ->
            createShortcutsFromEntitySets(entitySet, actionToEntitySetId, shortcuts)
        }
        actions.actions.forEach { action ->
            action.entitySets.forEach { entitySet ->
                createShortcutsFromEntitySets(entitySet, actionToEntitySetId, shortcuts)
            }
        }
        return shortcuts
    }

    private fun createShortcutsFromEntitySets(
        entitySet: EntitySet,
        actionToEntitySetId: Map<String, Action>,
        shortcuts: MutableList<Shortcut>
    ) {
        val matchingAction: Action = actionToEntitySetId[entitySet.entitySetId]!!
        // TODO(paullucas): Handle duplicate cases where there may be more intents/action

        val shortcutIdToShortcutMap = HashMap<String, Shortcut>()

        entitySet.entities.forEach { entity ->
            // TODO(paullucas): Check if capabilityBinding is needed here
            var capabilityBinding: CapabilityBinding? =
                if (matchingAction.intentName!!.startsWith(ACTIONS_BUILT_IN_INTENT_RESERVED_NAMESPACE)) {
                    createCapabilityBinding(matchingAction, entity)
                } else {
                    null
                }

            val intent: ShortcutIntent = createIntentFromEntity(entity)

            val id = entity.identifier ?: ""
            val extra = createExtraFromEntity(entity);
            val extras = if (extra != null) mutableListOf(extra) else mutableListOf()
            if (shortcutIdToShortcutMap.containsKey(id)) {
                if (extra != null) {
                    shortcutIdToShortcutMap[id]!!.extras += extra
                }
            } else {
                // No shortcut created for this identifier, proceed with adding new shortcut.
                val shortcut = Shortcut(
                    shortcutId = if(!id.isEmpty()) id else "YOUR_SHORTCUT_ID_" + Random.nextInt(0, 100),
                    shortcutShortLabel = "YOUR_SHORT_LABEL",
                    shortcutLongLabel = entity.name ?: entity.alternateName ?: "",
                    enabled = "false",
                    intents = mutableListOf(intent),
                    capabilityBindings = if (capabilityBinding != null) mutableListOf(
                        capabilityBinding
                    ) else mutableListOf(),
                    extras = extras
                );
                shortcuts.add(shortcut)
                shortcutIdToShortcutMap[id] = shortcut
            }
        }
    }

    private fun createIntentFromEntity(entity: Entity?): ShortcutIntent {
        // TODO(paullucas): Handle custom vs BII cases better here and update based on if intentName
        //  starts with actions.
        return ShortcutIntent(
            // Defaulting to android.intent.action.VIEW and allowing user to override
            action = ANDROID_ACTION_VIEW_DEFEAULT_INTENT,
            // TODO(paullucas): Add targetClass details
            data = entity?.url
        );
    }

    private fun createExtraFromEntity(entity: Entity?): Extra? {
        if (entity?.sameAs.isNullOrEmpty()) {
            return null
        }
        return Extra(
            key = "sameAs", value = entity?.sameAs
        );
    }

    private fun createCapabilityBinding(
        action: Action,
        entity: Entity?
    ): CapabilityBinding {
        // TODO(paullucas): Only add capability binding if needed, need to string parse
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
        // TODO(paullucas): remove this default and only send back parameter binding when we need
        //  the field
        return ParameterBinding(
            key = "YOUR_PARAMETER_BINDING_KEY",
            value = "YOUR_PARAMETER_BINDING_VALUE"
        )
    }

    private fun getEntitySetIdForAction(action: Action): String? {
        action.parameters.forEach { parameter ->
            if (parameter.entitySetReference != null &&
                parameter.entitySetReference!!.entitySetId != null
            ) {
                return parameter.entitySetReference!!.entitySetId
            }
            return parameter.entitySetReference?.entitySetId ?: "";
        }
        return null;
    }
}
