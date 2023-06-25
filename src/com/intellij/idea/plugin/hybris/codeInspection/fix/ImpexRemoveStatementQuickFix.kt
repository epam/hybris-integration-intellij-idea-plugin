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

package com.intellij.idea.plugin.hybris.codeInspection.fix

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.idea.plugin.hybris.impex.psi.ImpexHeaderLine
import com.intellij.idea.plugin.hybris.impex.psi.ImpexHeaderTypeName
import com.intellij.idea.plugin.hybris.impex.psi.ImpexTypes
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType

class ImpexRemoveStatementQuickFix(
    parameter: ImpexHeaderTypeName,
    private val elementName: String,
    private val message: String = "Remove statement"//message("hybris.inspections.fix.impex.ChangeHeaderMode.text", headerMode.firstChild, headerModeReplacement, elementName)
) : LocalQuickFixOnPsiElement(parameter) {

    private val elementsListToStopSearching = mutableSetOf<IElementType>(
        ImpexTypes.USER_RIGHTS,
        ImpexTypes.HEADER_LINE
    )

    override fun getFamilyName() = "remove statement"//message("hybris.inspections.fix.impex.ChangeHeaderMode")

    override fun getText() = message

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val headerLine = PsiTreeUtil.getParentOfType(startElement, ImpexHeaderLine::class.java) ?: return
        val valueLines = headerLine.valueLines

        if (valueLines.isEmpty()) {
            val emptyCrlfs = mutableListOf<PsiElement>()
            var nextSibling = headerLine.nextSibling

            while (nextSibling.elementType == ImpexTypes.CRLF && nextSibling != null) {
                emptyCrlfs.add(nextSibling)
                nextSibling = nextSibling.nextSibling
            }
            emptyCrlfs.forEach { it.delete() }
            headerLine.delete()
            return
        }

        val elementsToDelete = mutableListOf<PsiElement>()
        for (valueLine in valueLines) {
            var nextSibling = valueLine.nextSibling
            while (nextSibling.elementType == ImpexTypes.CRLF && nextSibling != null) {
                val currentSibling = nextSibling
                nextSibling = currentSibling.nextSibling
                currentSibling.delete()
                valueLine.delete()
            }
        }

        var nextHeaderSibling = headerLine.nextSibling
        while (nextHeaderSibling.elementType == ImpexTypes.CRLF && nextHeaderSibling != null) {
            elementsToDelete.add(nextHeaderSibling)
            nextHeaderSibling = nextHeaderSibling.nextSibling
        }

        var nextSibling = valueLines
            .last()
            .nextSibling

        while (!elementsListToStopSearching.contains(nextSibling.elementType) && nextSibling != null) {
            if (nextSibling.elementType == ImpexTypes.CRLF || nextSibling.elementType == TokenType.WHITE_SPACE) {
                elementsToDelete.add(nextSibling)
            }
            nextSibling = nextSibling.nextSibling
        }

        elementsToDelete.forEach { it.delete() }
        headerLine.delete()
    }
}