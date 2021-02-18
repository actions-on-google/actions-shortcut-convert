package com.google.assistant.actions.marshall

import org.junit.Assert.*
import org.junit.Test
import javax.xml.bind.UnmarshalException

/**
 * Tests for ensuring that the JAXB bindings work for incoming actions.xml content handled by [readActions].
 */
class ActionsInputTest {

    @Test(expected = UnmarshalException::class)
    fun testEmptyXml() {
        val xml = ""

        readActions(xml.byteInputStream())
    }

    @Test(expected = UnmarshalException::class)
    fun testRootTagIsIncorrect() {
        val xml = "<not-actions><not-actions>"

        readActions(xml.byteInputStream())
    }

    @Test
    fun testEmptyActions() {
        val xml = "<actions></actions>"

        val actionsRoot = readActions(xml.byteInputStream())
        val actions = actionsRoot.actions

        assertEquals(0, actions.size)
    }

    @Test
    fun testOneAction() {
        val intentName = "TEST_INTENT_NAME"
        val urlTemplate = "TEST_URL_TEMPLATE"
        val xml = """
<actions>
    <action intentName="$intentName">
        <fulfillment urlTemplate="$urlTemplate" />
    </action>
</actions>
        """.trimMargin()

        val actionsRoot = readActions(xml.byteInputStream())
        val actions = actionsRoot.actions

        assertEquals(1, actions.size)
        assertEquals(intentName, actions[0].intentName)

        val action = actions[0]
        assertNotNull(action.fulfillment)
        val fulfillment = action.fulfillment ?: return
        assertEquals(urlTemplate, fulfillment.urlTemplate)
    }

    @Test
    fun testTwoActions() {
        val intentName0 = "TEST_INTENT_NAME_0"
        val urlTemplate0 = "TEST_URL_TEMPLATE_0"
        val intentName1 = "TEST_INTENT_NAME_1"
        val urlTemplate1 = "TEST_URL_TEMPLATE_1"
        val xml = """
<actions>
    <action intentName="$intentName0">
        <fulfillment urlTemplate="$urlTemplate0" />
    </action>
    <action intentName="$intentName1">
        <fulfillment urlTemplate="$urlTemplate1" />
    </action>
</actions>
        """.trimMargin()

        val actionsRoot = readActions(xml.byteInputStream())
        val actions = actionsRoot.actions

        assertEquals(2, actions.size)
        assertEquals(intentName0, actions[0].intentName)

        val action0 = actions[0]
        assertNotNull(action0.fulfillment)
        val fulfillment0 = action0.fulfillment ?: return
        assertEquals(urlTemplate0, fulfillment0.urlTemplate)

        val action1 = actions[1]
        assertNotNull(action1.fulfillment)
        val fulfillment1 = action1.fulfillment ?: return
        assertEquals(urlTemplate1, fulfillment1.urlTemplate)
    }

    @Test
    fun testActionWithoutFulfillment() {
        val intentName = "TEST_INTENT_NAME"
        val xml = """
<actions>
    <action intentName="$intentName">
    </action>
</actions>
        """.trimMargin()

        val actionsRoot = readActions(xml.byteInputStream())
        val actions = actionsRoot.actions

        assertEquals(1, actions.size)
        assertEquals(intentName, actions[0].intentName)

        val action = actions[0]
        assertNull(action.fulfillment)
    }

    @Test
    fun testAllElements() {
        val intentName = "TEST_INTENT_NAME"
        val queryPatterns = "TEST_QUERY_PATTERNS"
        val urlTemplate = "TEST_URL_TEMPLATE"
        val fulfillmentMode = "TEST_FULFILLMENT_MODE"
        val rfa = "TEST_RFA"
        val intentParameter0 = "TEST_IPARAM_0"
        val intentParameter1 = "TEST_IPARAM_1"
        val urlParameter0 = "TEST_UPARAM_0"
        val urlParameter1 = "TEST_UPARAM_1"
        val parameterName0 = "TEST_PARAM_NAME_0"
        val parameterName1 = "TEST_PARAM_NAME_1"
        val urlFilter = "TEST_URL_FILTER"
        val entitySetId0 = "TEST_ENTITY_SET_0"
        val entitySetId1 = "TEST_ENTITY_SET_1"
        val entityName = "TEST_ENTITY_NAME"
        val entityAltName = "TEST_ENTITY_ALTNAME"
        val entitySameAs = "TEST_ENTITY_SAMEAS"
        val entityIdentifier = "TEST_ENTITY_ID"
        val entityUrl = "TEST_ENTITY_URL"

        // This is not a sensical actions config, but it exercises every element and attribute
        val xml = """
<actions>
    <action intentName="$intentName" queryPatterns="$queryPatterns">
        <fulfillment urlTemplate="$urlTemplate" fulfillmentMode="$fulfillmentMode" requiredForegroundActivity="$rfa">
            <parameter-mapping intentParameter="$intentParameter0" urlParameter="$urlParameter0"/>
            <parameter-mapping intentParameter="$intentParameter1" urlParameter="$urlParameter1"/>
        </fulfillment>
        <parameter name="$parameterName0">
            <entity-set-reference entitySetId="$entitySetId0" urlFilter="$urlFilter"/>
        </parameter>
        <parameter name="$parameterName1"/>
    </action>
    <entity-set entitySetId="$entitySetId0">
        <entity
            name="$entityName"
            alternateName="$entityAltName"
            sameAs="$entitySameAs"
            identifier="$entityIdentifier"
            url="$entityUrl"
        />
        <entity
            name="IGNORE"
            alternateName="IGNORE"
            sameAs="IGNORE"
            identifier="IGNORE"
            url="IGNORE"
        />
    </entity-set>
    <entity-set entitySetId="$entitySetId1"/>
</actions>
        """.trimMargin()

        val actionsRoot = readActions(xml.byteInputStream())
        val actions = actionsRoot.actions
        val entitySets = actionsRoot.entitySets

        assertEquals(1, actions.size)
        assertEquals(2, entitySets.size)

        val action = actions[0]
        assertEquals(intentName, action.intentName)
        assertEquals(queryPatterns, action.queryPatterns)
        assertEquals(2, action.parameters.size)

        assertNotNull(action.fulfillment)
        val fulfillment = action.fulfillment ?: return
        assertEquals(urlTemplate, fulfillment.urlTemplate)
        assertEquals(fulfillmentMode, fulfillment.fulfillmentMode)
        assertEquals(rfa, fulfillment.requiredForegroundActivity)
        assertEquals(2, fulfillment.parameterMappings.size)

        val parameterMapping0 = fulfillment.parameterMappings[0]
        assertEquals(intentParameter0, parameterMapping0.intentParameter)
        assertEquals(urlParameter0, parameterMapping0.urlParameter)

        val parameterMapping1 = fulfillment.parameterMappings[1]
        assertEquals(intentParameter1, parameterMapping1.intentParameter)
        assertEquals(urlParameter1, parameterMapping1.urlParameter)

        val parameter0 = action.parameters[0]
        assertEquals(parameterName0, parameter0.name)
        assertNotNull(parameter0.entitySetReference)

        val entitySetReference = parameter0.entitySetReference ?: return
        assertEquals(entitySetId0, entitySetReference.entitySetId)
        assertEquals(urlFilter, entitySetReference.urlFilter)

        val parameter1 = action.parameters[1]
        assertEquals(parameterName1, parameter1.name)
        assertNull(parameter1.entitySetReference)

        val entitySet0 = entitySets[0]
        assertEquals(entitySetId0, entitySet0.entitySetId)

        assertEquals(2, entitySet0.entities.size)
        val entity0 = entitySet0.entities[0]
        assertEquals(entityName, entity0.name)
        assertEquals(entityAltName, entity0.alternateName)
        assertEquals(entitySameAs, entity0.sameAs)
        assertEquals(entityIdentifier, entity0.identifier)
        assertEquals(entityUrl, entity0.url)

        val entitySet1 = entitySets[1]
        assertEquals(0, entitySet1.entities.size)
    }
}
