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

package com.intellij.idea.plugin.hybris.type.system.inspections.rules

import com.intellij.idea.plugin.hybris.type.system.inspections.fix.XmlDeleteAttributeQuickFix
import com.intellij.idea.plugin.hybris.type.system.model.Items
import com.intellij.idea.plugin.hybris.type.system.model.Modifiers
import com.intellij.idea.plugin.hybris.type.system.model.modifiers
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder
import com.intellij.util.xml.highlighting.DomHighlightingHelper

class UseOfUnoptimizedAttributesIsNotRecommended : AbstractTypeSystemInspection() {

    override fun checkItems(
        project: Project,
        items: Items,
        holder: DomElementAnnotationHolder,
        helper: DomHighlightingHelper,
        severity: HighlightSeverity
    ) {
        items.modifiers.forEach { check(it, holder, severity) }
    }

    private fun check(
        dom: Modifiers,
        holder: DomElementAnnotationHolder,
        severity: HighlightSeverity
    ) {
        val doNotOptimizeXmlElement = dom.doNotOptimize.xmlElement
        val doNotOptimize = dom.doNotOptimize.value ?: false

        if (doNotOptimizeXmlElement != null && doNotOptimize) {
            holder.createProblem(
                dom,
                severity,
                displayName,
                TextRange.from(doNotOptimizeXmlElement.startOffsetInParent, doNotOptimizeXmlElement.textLength),
                XmlDeleteAttributeQuickFix(Modifiers.DONT_OPTIMIZE)
            )
        }
    }
}