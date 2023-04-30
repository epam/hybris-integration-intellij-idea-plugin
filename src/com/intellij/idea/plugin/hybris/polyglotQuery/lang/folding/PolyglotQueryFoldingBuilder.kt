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
package com.intellij.idea.plugin.hybris.polyglotQuery.lang.folding

import ai.grazie.utils.toDistinctTypedArray
import com.intellij.idea.plugin.hybris.polyglotQuery.psi.PolyglotQueryTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.roots.ProjectRootModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

class PolyglotQueryFoldingBuilder : FoldingBuilderEx(), DumbAware {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> =
        CachedValuesManager.getCachedValue(root) {
            val filter = PolyglotQueryFoldingBlocksFilter.instance
            val results = SyntaxTraverser.psiTraverser(root)
                .filter { filter.isAccepted(it) }
                .mapNotNull {
                    if (it is PsiErrorElement || it.textRange.isEmpty) return@mapNotNull null
                    FoldingDescriptor(it.node, it.textRange, FoldingGroup.newGroup(GROUP_NAME))
                }
                .toDistinctTypedArray()

            CachedValueProvider.Result.create(
                results,
                root.containingFile,
                ProjectRootModificationTracker.getInstance(root.project)
            )
        }

    override fun getPlaceholderText(node: ASTNode) = when (node.elementType) {
        PolyglotQueryTypes.COMMENT -> "/*...*/"

        PolyglotQueryTypes.TYPE_KEY -> node.findChildByType(PolyglotQueryTypes.IDENTIFIER)
            ?.text
            ?.trim()

        PolyglotQueryTypes.ORDER_BY -> "ORDER BY ..."
        PolyglotQueryTypes.WHERE_CLAUSE -> "WHERE ..."

        PolyglotQueryTypes.ATTRIBUTE_KEY -> {
            val language = node.findChildByType(PolyglotQueryTypes.LOCALIZED_NAME)
                ?.let {
                    it.findChildByType(PolyglotQueryTypes.IDENTIFIER)
                        ?.text
                        ?.trim()
                        ?: "?"
                }
                ?.let { ":$it" }
                ?: ""

            val attribute = node.findChildByType(PolyglotQueryTypes.IDENTIFIER)
                ?.text
                ?.trim()
                ?: "?"

            attribute + language
        }

        else -> FALLBACK_PLACEHOLDER
    }
        ?: FALLBACK_PLACEHOLDER

    override fun isCollapsedByDefault(node: ASTNode) = true

    companion object {
        private const val GROUP_NAME = "PolyglotQuery"
        private const val FALLBACK_PLACEHOLDER = "..."
    }
}
