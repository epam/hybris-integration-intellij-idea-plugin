/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019 EPAM Systems <hybrisideaplugin@epam.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.intellij.idea.plugin.hybris.type.system.meta.impl

import com.intellij.idea.plugin.hybris.type.system.meta.*
import com.intellij.idea.plugin.hybris.type.system.meta.TSMetaRelation.ReferenceEnd
import com.intellij.idea.plugin.hybris.type.system.model.*
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import org.apache.commons.lang3.StringUtils

class TSMetaModelBuilder(
    private val myProject: Project,
    private val myModule: Module,
    private val myPsiFile: PsiFile,
    private val myCustom: Boolean
) {

    private val myMetaModel = TSMetaModel(myModule, myPsiFile, myCustom)

    private fun extractName(dom: ItemType): String? = dom.code.value
    private fun extractName(dom: EnumType): String? = dom.code.value
    private fun extractName(dom: CollectionType): String? = dom.code.value
    private fun extractName(dom: Relation): String? = dom.code.value
    private fun extractName(dom: AtomicType): String? = dom.clazz.value
    private fun extractName(dom: MapType): String? = dom.code.value

    private fun findOrCreate(dom: ItemType): TSMetaItem? {
        val name = extractName(dom) ?: return null
        val typeCode = dom.deployment.typeCode.stringValue
        val items = myMetaModel.getMetaType<TSMetaItem>(MetaType.META_ITEM)
        var impl = items[name]

        if (impl == null) {
            impl = TSMetaItemImpl(myProject, name, typeCode, dom)
            items[name] = impl
        } else {
            impl.merge(TSMetaItemImpl(myProject, name, typeCode, dom))
        }
        return impl
    }

    private fun findOrCreate(dom: EnumType): TSMetaEnum? {
        val name = extractName(dom) ?: return null
        val enums = myMetaModel.getMetaType<TSMetaEnum>(MetaType.META_ENUM)
        var impl = enums[name]

        if (impl == null) {
            impl = TSMetaEnumImpl(myProject, name, dom)
            enums[name] = impl
        }
        return impl
    }

    private fun findOrCreate(dom: AtomicType): TSMetaAtomic? {
        val clazzName = extractName(dom) ?: return null

        return myMetaModel.getMetaType<TSMetaAtomic>(MetaType.META_ATOMIC)
            .computeIfAbsent(clazzName)
            { key: String -> TSMetaAtomicImpl(myProject, key, dom) }
    }

    private fun findOrCreate(dom: CollectionType): TSMetaCollection? {
        val name = extractName(dom) ?: return null

        return myMetaModel.getMetaType<TSMetaCollection>(MetaType.META_COLLECTION)
            .computeIfAbsent(name)
            { key: String? -> TSMetaCollectionImpl(myProject, key, dom) }
    }

    private fun findOrCreate(dom: Relation): TSMetaRelation? {
        val name = extractName(dom)
        val typeCode = dom.deployment.typeCode.stringValue

        if (name == null || typeCode == null) return null

        return myMetaModel.getMetaType<TSMetaRelation>(MetaType.META_RELATION)
            .computeIfAbsent(name) { key: String ->
                val impl: TSMetaRelation = TSMetaRelationImpl(myProject, key, typeCode, dom)
                registerReferenceEnd(impl.source, impl.target)
                registerReferenceEnd(impl.target, impl.source)
                impl
            }
    }

    private fun findOrCreate(dom: MapType): TSMetaMap? {
        val name = extractName(dom) ?: return null

        val maps = myMetaModel.getMetaType<TSMetaMap>(MetaType.META_MAP)
        var map = maps[name]

        if (map == null) {
            map = TSMetaMapImpl(myProject, name, dom)
            maps[name] = map
        } else {
            map.merge(TSMetaMapImpl(myProject, name, dom))
        }

        return map;
    }

    private fun registerReferenceEnd(ownerEnd: ReferenceEnd, targetEnd: ReferenceEnd) {
        if (!targetEnd.isNavigable) return

        val ownerTypeName = ownerEnd.typeName

        if (!StringUtil.isEmpty(ownerTypeName)) {
            myMetaModel.getReferences().putValue(ownerTypeName, targetEnd)
        }
    }

    private fun build(type: ItemType) {
        val meta = findOrCreate(type) ?: return

        type.attributes.attributes
            .map { TSMetaAttributeImpl(myProject, meta, it) }
            .filter { StringUtils.isNotBlank(it.name) }
            .forEach { attr -> meta.addAttribute(attr.name!!.trim { it <= ' ' }, attr) }

        type.customProperties.properties
            .map { TSMetaCustomPropertyImpl(myProject, it) }
            .filter { StringUtils.isNotBlank(it.name) }
            .forEach { prop -> meta.addCustomProperty(prop.name!!.trim { it <= ' ' }, prop) }
    }

    private fun build(type: EnumType) {
        val meta = findOrCreate(type) ?: return
        type.values.forEach { meta.createValue(it) }
    }

    private fun build(type: Relation) = findOrCreate(type)
    private fun build(type: MapType) = findOrCreate(type)
    private fun build(type: AtomicType) = findOrCreate(type)
    private fun build(type: CollectionType) = findOrCreate(type)

    fun withItemTypes(types: List<ItemType>): TSMetaModelBuilder {
        types.forEach { build(it) }

        return this
    }

    fun withEnumTypes(types: List<EnumType>): TSMetaModelBuilder {
        types.forEach { build(it) }
        return this
    }

    fun withCollectionTypes(types: List<CollectionType>): TSMetaModelBuilder {
        types.forEach { build(it) }
        return this
    }

    fun withMapTypes(types: List<MapType>): TSMetaModelBuilder {
        types.forEach { build(it) }
        return this
    }

    fun withRelationTypes(types: List<Relation>): TSMetaModelBuilder {
        types.forEach { build(it) }
        return this
    }

    fun withAtomicTypes(types: List<AtomicType>): TSMetaModelBuilder {
        types.forEach { build(it) }
        return this
    }

    fun build() = myMetaModel

}