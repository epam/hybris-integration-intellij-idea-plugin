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
package com.intellij.idea.plugin.hybris.system.cockpitng.codeInsight.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.idea.plugin.hybris.common.utils.HybrisIcons
import com.intellij.idea.plugin.hybris.system.cockpitng.model.config.Context
import com.intellij.idea.plugin.hybris.system.type.meta.TSMetaModelAccess
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext
import com.intellij.util.xml.DomManager

class CngItemAttributeCodeCompletionProvider : CompletionProvider<CompletionParameters>() {

    public override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val project = parameters.editor.project ?: return
        val type = resolveContext(parameters.position)
            ?.type
            ?.stringValue
            ?: return

        val resultCaseInsensitive = result.caseInsensitive()

        TSMetaModelAccess.getInstance(project).findMetaItemByName(type)
            ?.allAttributes
            ?.map {
                LookupElementBuilder.create(it.name)
                    .withStrikeoutness(it.isDeprecated)
                    .withTypeText(it.flattenType, true)
                    .withIcon(HybrisIcons.ATTRIBUTE)
            }
            ?.forEach { resultCaseInsensitive.addElement(it) }
    }

    private fun resolveContext(element: PsiElement): Context? {
        var parent = element.findParentOfType<XmlTag>()

        while (parent != null && parent.name != "context") {
            parent = parent.findParentOfType()
        }
        return DomManager.getDomManager(element.project).getDomElement(parent) as? Context
    }

    companion object {
        val instance: CompletionProvider<CompletionParameters> =
            ApplicationManager.getApplication().getService(CngItemAttributeCodeCompletionProvider::class.java)
    }
}