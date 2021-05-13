package com.google.assistant.actions.model.shortcuts

import com.sun.xml.bind.marshaller.NamespacePrefixMapper
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

private const val ANDROID_NAMESPACE_URI = "http://schemas.android.com/apk/res/android"

@XmlRootElement(name = "shortcuts")
data class ShortcutsRoot(

    @set:XmlElement(name = "capability")
    var capabilities: List<Capability> = mutableListOf(),

    @set:XmlElement(name = "shortcut")
    var shortcuts: List<Shortcut> = mutableListOf(),
)

// Children of ShortcutsRoot

data class Capability(

    @set:XmlAttribute(required = true, namespace = ANDROID_NAMESPACE_URI)
    var name: String? = null,

    @set:XmlAttribute
    var queryPatterns: String? = null,

    @set:XmlElement
    var intent: CapabilityIntent? = null,

    @set:XmlElement
    var slice: Slice? = null,
)

data class Shortcut(

    @set:XmlAttribute(required = true, namespace = ANDROID_NAMESPACE_URI)
    var shortcutId: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var enabled: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var icon: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var shortcutShortLabel: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var shortcutLongLabel: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var disabledMessage: String? = null,

    @set:XmlElement
    var intent: List<ShortcutIntent> = mutableListOf(),

    @set:XmlElement(name = "capability-reference")
    var capabilityReference: List<CapabilityReference> = mutableListOf(),

    @set:XmlElement
    var extra: List<Extra> = mutableListOf(),
)

//
// Children of Capability
//

data class CapabilityIntent(

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var action: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var targetClass: String? = null,

    @set:XmlElement(name = "url-template")
    var urlTemplate: UrlTemplate? = null,

    @set:XmlElement
    var parameter: List<Parameter> = mutableListOf(),

    @set:XmlElement
    var extra: List<Extra> = mutableListOf(),
)

data class Slice(

    @set:XmlElement(name = "url-template")
    var urlTemplate: UrlTemplate? = null,

    @set:XmlElement
    var parameter: List<Parameter> = mutableListOf(),
)

//
// Children of Shortcut
//

data class ShortcutIntent(

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var action: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var targetPackage: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var targetClass: String? = null,

    @set:XmlElement(name = "url-template")
    var urlTemplate: UrlTemplate? = null,
)

//
// Children of CapabilityIntent
//

data class UrlTemplate(

    @set:XmlAttribute(required = true, namespace = ANDROID_NAMESPACE_URI)
    var value: String? = null
)

data class Parameter(

    @set:XmlAttribute(required = true, namespace = ANDROID_NAMESPACE_URI)
    var name: String? = null,

    @set:XmlAttribute(required = true, namespace = ANDROID_NAMESPACE_URI)
    var key: String? = null,

    @set:XmlAttribute(name = "mime-type", required = true, namespace = ANDROID_NAMESPACE_URI)
    var mimeType: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var required: String? = null,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var shortcutMatchRequired: String? = null,

    @set:XmlElement
    var data: Data? = null,
)

data class Extra(

    @set:XmlAttribute(required = true, namespace = ANDROID_NAMESPACE_URI)
    var name: String? = null,

    @set:XmlAttribute(required = true, namespace = ANDROID_NAMESPACE_URI)
    var value: String? = null,
)

//
// Children of ShortcutIntent
//

data class CapabilityReference(

    @set:XmlAttribute(required = true, namespace = ANDROID_NAMESPACE_URI)
    var capability: String?,

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var parameter: String?,
)

//
// Children of Parameter
//

data class Data(

    @set:XmlAttribute(namespace = ANDROID_NAMESPACE_URI)
    var pathPattern: String?,
)

class AndroidNamespaceMapper : NamespacePrefixMapper() {

    override fun getPreferredPrefix(namespaceUri: String?, suggestion: String?, requirePrefix: Boolean): String? {
        if (ANDROID_NAMESPACE_URI == namespaceUri) {
            return "android"
        }
        return suggestion
    }

    override fun getPreDeclaredNamespaceUris(): Array<String> {
        return arrayOf(ANDROID_NAMESPACE_URI)
    }
}
