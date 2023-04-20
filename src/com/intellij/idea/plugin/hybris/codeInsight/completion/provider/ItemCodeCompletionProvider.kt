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
package com.intellij.idea.plugin.hybris.codeInsight.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.idea.plugin.hybris.system.type.codeInsight.lookup.TSLookupElementFactory
import com.intellij.idea.plugin.hybris.system.type.meta.TSMetaModelAccess
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSGlobalMetaEnum
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSGlobalMetaItem
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSGlobalMetaRelation
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSMetaType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.ProcessingContext

open class ItemCodeCompletionProvider : CompletionProvider<CompletionParameters>() {

    public override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val project = parameters.editor.project ?: return
        val resultCaseInsensitive = result.caseInsensitive()
        val itemTypes = TSMetaModelAccess.getInstance(project).getAll<TSGlobalMetaItem>(TSMetaType.META_ITEM)
            .mapNotNull { TSLookupElementFactory.build(it) }
        val enumTypes = TSMetaModelAccess.getInstance(project).getAll<TSGlobalMetaEnum>(TSMetaType.META_ENUM)
            .filter { it.name != null }
            .mapNotNull { TSLookupElementFactory.build(it, it.name) }
        val relationTypes = TSMetaModelAccess.getInstance(project).getAll<TSGlobalMetaRelation>(TSMetaType.META_RELATION)
            .mapNotNull { TSLookupElementFactory.build(it) }

        (itemTypes + enumTypes + relationTypes)
            .forEach { resultCaseInsensitive.addElement(it) }
    }

    companion object {
        val instance: CompletionProvider<CompletionParameters> =
            ApplicationManager.getApplication().getService(ItemCodeCompletionProvider::class.java)
    }
}