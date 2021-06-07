package com.google.assistant.actions

import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.actions.Action
import com.google.assistant.actions.model.actions.Entity

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
        actions.entitySets.forEach { entitySet ->
            val matchingAction = actionToEntitySetId[entitySet.entitySetId]
            // TODO(paullucas): Handle duplicate cases where there may be more intents/action

            val shortcutIdToShortcutMap = HashMap<String, Shortcut>();

            entitySet.entities.forEach { entity ->
                // TODO(paullucas): Check if capabilityBinding is needed here
                val capabilityBinding: CapabilityBinding =
                    createCapabilityBindingFromAction(matchingAction, entity)

                val intent: ShortcutIntent = createIntentFromAction(matchingAction, entity)

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
                        shortcutId = id,
                        shortcutLongLabel = entity.name ?: entity.alternateName ?: "",
                        enabled = "false",
                        intents = mutableListOf(intent),
                        capabilityBindings = mutableListOf(capabilityBinding),
                        extras = extras
                    );
                    shortcuts.add(shortcut)
                    shortcutIdToShortcutMap[id] = shortcut
                }
            }
        }
        return shortcuts
    }

    private fun createIntentFromAction(action: Action?, entity: Entity?): ShortcutIntent {
        if (action == null) {
            return ShortcutIntent()
        }
        // TODO(paullucas): Handle custom vs BII cases better here and update based on if intentName
        //  starts with actions.
        return ShortcutIntent(
            action = action.intentName, // Use android.intent.action.VIEW as default
            targetClass = action.intentName,
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

    private fun createCapabilityBindingFromAction(
        action: Action?,
        entity: Entity?
    ): CapabilityBinding {
        if (action == null) {
            // TODO(paullucas): remove this text when we confirm action is not null
            return CapabilityBinding("<TODO DELETE ME>")
        }
        // TODO(paullucas): Only add capability binding if needed, need to string parse
        return CapabilityBinding(
            key = action.intentName,
            parameterBinding = mutableListOf(createParameterBindingFromAction(action, entity))
        )
    }

    private fun createParameterBindingFromAction(
        action: Action,
        matchingEntity: Entity?
    ): ParameterBinding {
        // TODO(paullucas): Key = action.fulfillment.parameter.name from action or
        //  shortcut-fulfillment.parameter.name
        action.parameters.forEach { parameter ->
            return ParameterBinding(key = parameter.name, value = matchingEntity?.alternateName)
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
