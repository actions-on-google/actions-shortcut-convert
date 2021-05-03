package com.google.assistant.actions.marshall

import com.google.assistant.actions.model.shortcuts.AndroidNamespaceMapper
import com.google.assistant.actions.model.shortcuts.ShortcutsRoot
import java.io.OutputStream
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.MarshalException
import javax.xml.bind.Marshaller

@Throws(JAXBException::class, MarshalException::class)
fun writeShortcuts(shortcutsRoot: ShortcutsRoot, outStream: OutputStream) {
    val jc = JAXBContext.newInstance(ShortcutsRoot::class.java)
    val marshaller = jc.createMarshaller()
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", AndroidNamespaceMapper())
    marshaller.marshal(shortcutsRoot, outStream)
}
