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
package com.intellij.idea.plugin.hybris.system.cockpitng.psi.contributor

import com.intellij.idea.plugin.hybris.system.cockpitng.psi.CngPatterns
import com.intellij.idea.plugin.hybris.system.cockpitng.psi.provider.*
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar

class CngReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            CngPatterns.CONTEXT_TYPE,
            CngTSItemReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.FLOW_STEP_CONTENT_PROPERTY_TYPE,
            CngTSItemReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.FLOW_STEP_CONTENT_PROPERTY_QUALIFIER,
            CngFlowTSItemAttributeReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.FLOW_INITIALIZE_TYPE,
            CngFlowTSItemReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.TREE_NODE_TYPE_CODE,
            CngTSItemReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.CONTEXT_PARENT,
            CngTSItemReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.LIST_VIEW_COLUMN_QUALIFIER,
            CngTSItemAttributeReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.EDITOR_AREA_ATTRIBUTE,
            CngTSItemAttributeReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.EDITOR_AREA_EDITOR,
            CngEditorDefinitionReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.ADVANCED_SEARCH_FIELD_NAME,
            CngTSItemAttributeReferenceProvider.instance
        )
        registrar.registerReferenceProvider(
            CngPatterns.SIMPLE_SEARCH_FIELD_NAME,
            CngTSItemAttributeReferenceProvider.instance
        )
    }
}