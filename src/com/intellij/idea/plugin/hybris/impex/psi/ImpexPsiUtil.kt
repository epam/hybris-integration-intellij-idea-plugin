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

@file:JvmName("ImpexPsiUtil")

package com.intellij.idea.plugin.hybris.impex.psi

import com.intellij.idea.plugin.hybris.common.HybrisConstants
import com.intellij.idea.plugin.hybris.impex.constants.modifier.AttributeModifier
import com.intellij.idea.plugin.hybris.impex.utils.ImpexPsiUtils
import com.intellij.idea.plugin.hybris.impex.utils.ProjectPropertiesUtils
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.siblings

fun getHeaderLine(element: ImpexFullHeaderParameter): ImpexHeaderLine? = PsiTreeUtil
    .getParentOfType(element, ImpexHeaderLine::class.java)

fun getHeaderLine(element: ImpexValueLine): ImpexHeaderLine? = PsiTreeUtil
    .getPrevSiblingOfType(element, ImpexHeaderLine::class.java)

fun getValueGroup(element: ImpexString): ImpexValueGroup? = PsiTreeUtil
    .getParentOfType(element, ImpexValueGroup::class.java)

fun getColumnNumber(element: ImpexValueGroup): Int = ImpexPsiUtils
    .getColumnNumber(element)

fun getColumnNumber(element: ImpexFullHeaderParameter): Int = ImpexPsiUtils
    .getColumnNumber(element)

fun getValueGroup(element: ImpexValueLine, columnNumber: Int): ImpexValueGroup? = element
    .childrenOfType<ImpexValueGroup>()
    .getOrNull(columnNumber)

fun getValueLine(element: PsiElement): ImpexValueLine? = PsiTreeUtil
    .getParentOfType(element, ImpexValueLine::class.java)

fun getFullHeaderParameter(element: ImpexValueGroup): ImpexFullHeaderParameter? = ImpexPsiUtils
    .getHeaderForValueGroup(element) as? ImpexFullHeaderParameter

fun getValueLines(element: ImpexHeaderLine): Collection<ImpexValueLine> {
    val subTypesIterator = element.siblings(withSelf = false).iterator()
    var proceed = true
    val valueLines = mutableListOf<ImpexValueLine>()

    while (proceed && subTypesIterator.hasNext()) {
        when (val psi = subTypesIterator.next()) {
            is ImpexHeaderLine -> proceed = false
            is ImpexValueLine -> valueLines.add(psi)
        }
    }
    return valueLines
}

/**
 * This method will get value of the value group and if it's empty will check for value in default attribute
 */
fun computeValue(element: ImpexValueGroup): String? {
    val computedValue = element
        .value
        ?.text
        ?: element.fullHeaderParameter
            ?.getAttribute(AttributeModifier.DEFAULT)
            ?.anyAttributeValue
            ?.stringList
            ?.firstOrNull()
            ?.text
    return computedValue
        ?.let { StringUtil.unquoteString(it) }
        ?.trim()
}

fun getAttribute(element: ImpexFullHeaderParameter, attributeModifier: AttributeModifier): ImpexAttribute? = element
    .modifiersList
    .flatMap { it.attributeList }
    .find { it.anyAttributeName.textMatches(attributeModifier.modifierName) }

fun getHeaderTypeName(element: ImpexSubTypeName): ImpexHeaderTypeName? = element
    .valueLine
    ?.headerLine
    ?.fullHeaderType
    ?.headerTypeName

fun getFullHeaderParameter(element: ImpexHeaderLine, parameterName: String): ImpexFullHeaderParameter? = element
    .fullHeaderParameterList
    .find { it.anyHeaderParameterName.text.equals(parameterName, true) }

fun getConfigPropertyKey(element: ImpexMacroUsageDec): String? {
    if (!element.text.startsWith(HybrisConstants.IMPEX_CONFIG_COMPLETE_PREFIX)) return null

    val propertyKey = element.text.replace(HybrisConstants.IMPEX_CONFIG_COMPLETE_PREFIX, "")

    if (propertyKey.isBlank()) return null

    return if (DumbService.isDumb(element.project)) {
        element.text.replace(HybrisConstants.IMPEX_CONFIG_COMPLETE_PREFIX, "")
    } else ProjectPropertiesUtils
        .findMacroProperty(element.project, propertyKey)
        ?.key
        ?: element.text.replace(HybrisConstants.IMPEX_CONFIG_COMPLETE_PREFIX, "")
}

// ------------------------------------------
//              User Rights
// ------------------------------------------
fun getValueGroups(element: ImpexUserRights, index: Int): Collection<ImpexUserRightsValueGroup> = element
    .userRightsValueLineList
    .mapNotNull { it.getValueGroup(index) }

fun getValueGroup(element: ImpexUserRightsValueLine, index: Int): ImpexUserRightsValueGroup? = element
    .userRightsValueGroupList
    .getOrNull(index)

fun getHeaderParameter(element: ImpexUserRightsHeaderLine, index: Int): ImpexUserRightsHeaderParameter? = element
    .userRightsHeaderParameterList
    .getOrNull(index)

fun getValueLine(element: ImpexUserRightsValueGroup): ImpexUserRightsValueLine? = element
    .parentOfType<ImpexUserRightsValueLine>()

fun getColumnNumber(element: ImpexUserRightsValueGroup): Int? = element
    .valueLine
    ?.let { valueLine ->
        valueLine.userRightsValueGroupList.indexOf(element)
            .takeIf { it != -1 }
            ?.let {
                // if there is no first column we have to add +1 for column number
                val withFirstGroup = valueLine.userRightsFirstValueGroup
                    ?.let { 0 }
                    ?: 1
                it + withFirstGroup
            }
    }

fun getHeaderParameter(element: ImpexUserRightsValueGroup): ImpexUserRightsHeaderParameter? = element
    .columnNumber
    ?.let {
        element.getUserRights()
            ?.userRightsHeaderLine
            ?.getHeaderParameter(it)
    }

fun getHeaderLine(element: ImpexUserRightsHeaderParameter): ImpexUserRightsHeaderLine? = element
    .parentOfType<ImpexUserRightsHeaderLine>()

fun getColumnNumber(element: ImpexUserRightsHeaderParameter): Int? = element
    .headerLine
    ?.userRightsHeaderParameterList
    ?.indexOf(element)
    ?.takeIf { it != -1 }

fun getValueGroups(element: ImpexUserRightsHeaderParameter): Collection<ImpexUserRightsValueGroup> {
    val columnNumber = element.columnNumber ?: return emptyList()
    val userRights = element.getUserRights() ?: return emptyList()
    return userRights.getValueGroups(columnNumber)
}
