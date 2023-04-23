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

package com.intellij.idea.plugin.hybris.flexibleSearch.codeInsight.lookup

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.idea.plugin.hybris.common.HybrisConstants
import com.intellij.idea.plugin.hybris.common.utils.HybrisIcons
import com.intellij.idea.plugin.hybris.flexibleSearch.psi.FlexibleSearchTableAliasName

object FxSLookupElementFactory {

    fun buildYColumnReference() = LookupElementBuilder.create("{}")
        .withPresentableText("{...}")
        .withInsertHandler { ctx, _ ->
            val cursorOffset = ctx.editor.caretModel.offset
            ctx.editor.caretModel.moveToOffset(cursorOffset - 1)
        }
        .withIcon(HybrisIcons.FXS_Y_COLUMN)
        .withCaseSensitivity(false)

    fun buildExclamationMark() = LookupElementBuilder.create('!')
        .withTailText(" (omit all subtypes)")
        .withIcon(HybrisIcons.FXS_TABLE_SUFFIX)

    fun buildSeparatorDot(aliasPrefix: String) = LookupElementBuilder.create("$aliasPrefix${HybrisConstants.FXS_TABLE_ALIAS_SEPARATOR_DOT}")
        .withPresentableText(HybrisConstants.FXS_TABLE_ALIAS_SEPARATOR_DOT)
        .withTailText(" (column separator)")
        .withIcon(HybrisIcons.FXS_TABLE_ALIAS_SEPARATOR)

    fun buildSeparatorColon(aliasPrefix: String) = LookupElementBuilder.create("$aliasPrefix${HybrisConstants.FXS_TABLE_ALIAS_SEPARATOR_COLON}")
        .withPresentableText(HybrisConstants.FXS_TABLE_ALIAS_SEPARATOR_COLON)
        .withTailText(" (alternative column separator)")
        .withIcon(HybrisIcons.FXS_TABLE_ALIAS_SEPARATOR)

    fun buildStar() = LookupElementBuilder.create('*')
        .withTailText(" (omit type restrictions)")
        .withIcon(HybrisIcons.FXS_TABLE_SUFFIX)

    fun build(tableAlias: FlexibleSearchTableAliasName, separatorPostfix: String = "") = tableAlias.name
        ?.let {
            LookupElementBuilder.create(it + separatorPostfix)
                .withPresentableText(it)
                .withTypeText(tableAlias.table?.text)
                .withIcon(HybrisIcons.FXS_TABLE_ALIAS)
        }

}