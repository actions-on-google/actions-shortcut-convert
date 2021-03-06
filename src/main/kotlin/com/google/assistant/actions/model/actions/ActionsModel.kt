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

package com.google.assistant.actions.model.actions

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name="actions")
data class ActionsRoot(

    @set:XmlElement(name="action")
    var actions: List<Action> = mutableListOf(),

    @set:XmlElement(name="entity-set")
    var entitySets: List<EntitySet> = mutableListOf(),
)

//
// Children of ActionsRoot
//

data class Action(

    @set:XmlAttribute(required = true)
    var intentName: String? = null,

    @set:XmlAttribute
    var queryPatterns: String? = null,

    @set:XmlElement(name="fulfillment", required = true)
    var fulfillments: List<Fulfillment> = mutableListOf(),

    @set:XmlElement(name="parameter", required = true)
    var parameters: List<Parameter> = mutableListOf(),

    @set:XmlElement(name="entity-set", required = false)
    var entitySets: List<EntitySet> = mutableListOf(),

    )

data class EntitySet(

    @set:XmlAttribute(required = true)
    var entitySetId: String? = null,

    @set:XmlElement(name="entity", required = true)
    var entities: List<Entity> = mutableListOf(),
)

//
// Children of Action
//

data class Fulfillment(

    @set:XmlAttribute(required = true)
    var urlTemplate: String? = null,

    @set:XmlAttribute
    var fulfillmentMode: String? = null,

    @set:XmlAttribute
    var requiredForegroundActivity: String? = null,

    @set:XmlElement(name="parameter-mapping")
    var parameterMappings: List<ParameterMapping> = mutableListOf(),
)

data class Parameter(

    @set:XmlAttribute(required = true)
    var name: String? = null,

    @set:XmlAttribute
    var type: String? = null,

    @set:XmlElement(name = "entity-set-reference")
    var entitySetReference: EntitySetReference? = null,
)

//
// Children of Fulfillment
//

data class ParameterMapping(

    @set:XmlAttribute(required = true)
    var intentParameter: String? = null,

    @set:XmlAttribute(required = true)
    var urlParameter: String? = null,

    @set:XmlAttribute
    var required: String? = null,

    @set:XmlAttribute
    var entityMatchRequired: String? = null,
)

//
// Children of Parameter
//

data class EntitySetReference(

    @set:XmlAttribute
    var entitySetId: String? = null,

    @set:XmlAttribute
    var urlFilter: String? = null,
)

//
// Children of EntitySet
//

data class Entity(

    @set:XmlAttribute
    var name: String? = null,

    @set:XmlAttribute
    var alternateName: String? = null,

    @set:XmlAttribute
    var sameAs: String? = null,

    @set:XmlAttribute
    var identifier: String? = null,

    @set:XmlAttribute
    var url: String? = null,
)
