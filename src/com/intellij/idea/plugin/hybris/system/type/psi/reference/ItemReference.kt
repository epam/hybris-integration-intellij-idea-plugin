/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019-2024 EPAM Systems <hybrisideaplugin@epam.com> and contributors
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

package com.intellij.idea.plugin.hybris.system.type.psi.reference

import com.intellij.codeInsight.highlighting.HighlightedReference
import com.intellij.idea.plugin.hybris.common.HybrisConstants
import com.intellij.idea.plugin.hybris.psi.reference.TSReferenceBase
import com.intellij.idea.plugin.hybris.psi.util.PsiUtils
import com.intellij.idea.plugin.hybris.system.type.meta.TSMetaModelAccess
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSGlobalMetaItem
import com.intellij.idea.plugin.hybris.system.type.psi.reference.result.ItemResolveResult
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.util.*

class ItemReference(element: PsiElement) : TSReferenceBase<PsiElement>(element), PsiPolyVariantReference, HighlightedReference {

    override fun calculateDefaultRangeInElement(): TextRange =
        if (element.textLength == 0) super.calculateDefaultRangeInElement()
        else TextRange.from(1, element.textLength - HybrisConstants.QUOTE_LENGTH)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> = CachedValuesManager.getManager(project)
        .getParameterizedCachedValue(element, CACHE_KEY, provider, false, this)
        .let { PsiUtils.getValidResults(it) }

    companion object {
        val CACHE_KEY = Key.create<ParameterizedCachedValue<Array<ResolveResult>, ItemReference>>("HYBRIS_TS_CACHED_REFERENCE")

        private val provider = ParameterizedCachedValueProvider<Array<ResolveResult>, ItemReference> { ref ->
            val metaModelAccess = TSMetaModelAccess.getInstance(ref.project)

            val name = ref.value
            val result = metaModelAccess.findMetaItemByName(name)
                ?.let { resolve(it) }
                ?: emptyArray()

            CachedValueProvider.Result.create(
                result,
                metaModelAccess.getMetaModel(), PsiModificationTracker.MODIFICATION_COUNT
            )
        }

        private fun resolve(meta: TSGlobalMetaItem): Array<ResolveResult> = meta.declarations
            .map { ItemResolveResult(it) }
            .toTypedArray()

    }

}
