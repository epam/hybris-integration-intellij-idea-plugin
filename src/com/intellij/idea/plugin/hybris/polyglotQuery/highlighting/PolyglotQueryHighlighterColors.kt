/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
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
package com.intellij.idea.plugin.hybris.polyglotQuery.highlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import org.jetbrains.annotations.NonNls

object PolyglotQueryHighlighterColors {

    val PGQ_KEYWORD = key("PGQ_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
    val PGQ_STRING = key("PGQ_STRING", DefaultLanguageHighlighterColors.STRING)
    val PGQ_BRACES = key("PGQ_BRACES", DefaultLanguageHighlighterColors.BRACES)
    val PGQ_PARENS = key("PGQ_PARENS", DefaultLanguageHighlighterColors.BRACES)
    val PGQ_BRACKETS = key("PGQ_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
    val PGQ_PARENTHESES = key("PGQ_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
    val PGQ_NUMBER = key("PGQ_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
    val PGQ_COMMENT = key("PGQ_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    val PGQ_LOCALIZED = key("PGQ_LOCALIZED", DefaultLanguageHighlighterColors.INLINE_PARAMETER_HINT)
    val PGQ_PARAMETER = key("PGQ_PARAMETER", DefaultLanguageHighlighterColors.PARAMETER)
    val PGQ_OPERAND = key("PGQ_OPERAND", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL)

    private fun key(
        externalName: @NonNls String,
        fallbackAttributeKey: TextAttributesKey
    ) = TextAttributesKey.createTextAttributesKey(externalName, fallbackAttributeKey)
}