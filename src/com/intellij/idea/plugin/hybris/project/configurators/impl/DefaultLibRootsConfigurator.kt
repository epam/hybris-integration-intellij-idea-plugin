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
package com.intellij.idea.plugin.hybris.project.configurators.impl

import com.intellij.idea.plugin.hybris.common.HybrisConstants
import com.intellij.idea.plugin.hybris.common.LibraryDescriptorType
import com.intellij.idea.plugin.hybris.project.configurators.LibRootsConfigurator
import com.intellij.idea.plugin.hybris.project.descriptors.JavaLibraryDescriptor
import com.intellij.idea.plugin.hybris.project.descriptors.ModuleDescriptor
import com.intellij.idea.plugin.hybris.project.descriptors.YModuleDescriptorUtil
import com.intellij.idea.plugin.hybris.project.descriptors.YModuleLibDescriptorUtil
import com.intellij.idea.plugin.hybris.project.descriptors.impl.YCoreExtModuleDescriptor
import com.intellij.idea.plugin.hybris.project.descriptors.impl.YOotbRegularModuleDescriptor
import com.intellij.idea.plugin.hybris.project.descriptors.impl.YPlatformModuleDescriptor
import com.intellij.idea.plugin.hybris.settings.HybrisApplicationSettingsComponent
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.roots.DependencyScope
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class DefaultLibRootsConfigurator : LibRootsConfigurator {

    override fun configure(
        modifiableRootModel: ModifiableRootModel,
        moduleDescriptor: ModuleDescriptor,
        modifiableModelsProvider: IdeModifiableModelsProvider,
        indicator: ProgressIndicator
    ) {
        val sourceCodeRoot = getSourceCodeRoot(moduleDescriptor)
        for (javaLibraryDescriptor in YModuleLibDescriptorUtil.getLibraryDescriptors(moduleDescriptor)) {
            if (!javaLibraryDescriptor.libraryFile.exists() && javaLibraryDescriptor.scope == DependencyScope.COMPILE) {
                continue
            }
            if (javaLibraryDescriptor.directoryWithClasses) {
                addClassesToModuleLibs(modifiableRootModel, modifiableModelsProvider, sourceCodeRoot, javaLibraryDescriptor)
            } else {
                addJarFolderToModuleLibs(modifiableRootModel, modifiableModelsProvider, javaLibraryDescriptor, moduleDescriptor, indicator)
            }
        }
        if (moduleDescriptor is YPlatformModuleDescriptor) {
            YModuleLibDescriptorUtil.createBootstrapLib(moduleDescriptor, sourceCodeRoot, modifiableModelsProvider)
        }
        if (moduleDescriptor is YCoreExtModuleDescriptor) {
            addLibsToModule(modifiableRootModel, modifiableModelsProvider, HybrisConstants.PLATFORM_LIBRARY_GROUP, true)
        }
        if (moduleDescriptor is YOotbRegularModuleDescriptor) {
            if (YModuleDescriptorUtil.hasBackofficeModule(moduleDescriptor)) {
                val backofficeJarDirectory = File(moduleDescriptor.rootDirectory, HybrisConstants.BACKOFFICE_JAR_DIRECTORY)
                if (backofficeJarDirectory.exists()) {
                    YModuleLibDescriptorUtil.createGlobalLibrary(modifiableModelsProvider, backofficeJarDirectory, HybrisConstants.BACKOFFICE_LIBRARY_GROUP)
                }
            }
            if (moduleDescriptor.name == HybrisConstants.EXTENSION_NAME_BACK_OFFICE) {
                addLibsToModule(modifiableRootModel, modifiableModelsProvider, HybrisConstants.BACKOFFICE_LIBRARY_GROUP, true)
            }
        }
    }

    private fun getSourceCodeRoot(moduleDescriptor: ModuleDescriptor) = moduleDescriptor.rootProjectDescriptor.sourceCodeFile
        ?.let { VfsUtil.findFileByIoFile(it, true) }
        ?.let {
            if (it.isDirectory) {
                it
            } else {
                JarFileSystem.getInstance().getJarRootForLocalFile(it)
            }
        }

    private fun addClassesToModuleLibs(
        modifiableRootModel: ModifiableRootModel,
        modifiableModelsProvider: IdeModifiableModelsProvider,
        sourceCodeRoot: VirtualFile?,
        javaLibraryDescriptor: JavaLibraryDescriptor
    ) {
        val library = modifiableRootModel.moduleLibraryTable.createLibrary()
        val libraryModifiableModel = modifiableModelsProvider.getModifiableLibraryModel(library)
        libraryModifiableModel.addRoot(VfsUtil.getUrlForLibraryRoot(javaLibraryDescriptor.libraryFile), OrderRootType.CLASSES)

        val vfsSourceFiles = javaLibraryDescriptor.sourceFiles
            .mapNotNull { VfsUtil.findFileByIoFile(it, true) }
        val sourceDirAttached = vfsSourceFiles.isNotEmpty()
        vfsSourceFiles.forEach { libraryModifiableModel.addRoot(it, OrderRootType.SOURCES) }

        if (sourceCodeRoot != null
            && !sourceDirAttached
            && javaLibraryDescriptor.libraryFile.name.endsWith(HybrisConstants.HYBRIS_PLATFORM_CODE_SERVER_JAR_SUFFIX)
        ) {
            libraryModifiableModel.addRoot(sourceCodeRoot, OrderRootType.SOURCES)
        }

        if (javaLibraryDescriptor.exported) {
            setLibraryEntryExported(modifiableRootModel, library)
        }

        setLibraryEntryScope(modifiableRootModel, library, javaLibraryDescriptor.scope)
    }

    private fun addJarFolderToModuleLibs(
        modifiableRootModel: ModifiableRootModel,
        modifiableModelsProvider: IdeModifiableModelsProvider,
        javaLibraryDescriptor: JavaLibraryDescriptor,
        moduleDescriptor: ModuleDescriptor,
        progressIndicator: ProgressIndicator
    ) {
        val projectLibraryTable = modifiableRootModel.moduleLibraryTable
        val library = projectLibraryTable.createLibrary()
        val libraryModifiableModel = modifiableModelsProvider.getModifiableLibraryModel(library)
        libraryModifiableModel.addJarDirectory(VfsUtil.getUrlForLibraryRoot(javaLibraryDescriptor.libraryFile), true)

        javaLibraryDescriptor.sourceFiles
            .mapNotNull { VfsUtil.findFileByIoFile(it, true) }
            .forEach { libraryModifiableModel.addRoot(it, OrderRootType.SOURCES) }

        if (javaLibraryDescriptor.exported) {
            setLibraryEntryExported(modifiableRootModel, library)
        }

        setLibraryEntryScope(modifiableRootModel, library, javaLibraryDescriptor.scope)

        val mavenSources = resolveMavenSources(modifiableRootModel, javaLibraryDescriptor, moduleDescriptor, progressIndicator)
        val standardSources = resolveStandardProvidedSources(javaLibraryDescriptor, moduleDescriptor)
        val resultLibs = mavenSources + standardSources

        for (resultLib in resultLibs) {
            libraryModifiableModel.addRoot("jar://$resultLib!/", OrderRootType.SOURCES)
        }
    }

    private fun addLibsToModule(
        modifiableRootModel: ModifiableRootModel,
        modifiableModelsProvider: IdeModifiableModelsProvider,
        libraryName: String,
        export: Boolean
    ) {
        val libraryTableModifiableModel = modifiableModelsProvider.modifiableProjectLibrariesModel
        val library = libraryTableModifiableModel.getLibraryByName(libraryName)
            ?: libraryTableModifiableModel.createLibrary(libraryName)
        modifiableRootModel.addLibraryEntry(library)

        if (export) {
            setLibraryEntryExported(modifiableRootModel, library)
        }
    }

    private fun setLibraryEntryExported(
        modifiableRootModel: ModifiableRootModel,
        library: Library
    ) {
        findOrderEntryForLibrary(modifiableRootModel, library)
            .let { it.isExported = true }
    }

    private fun setLibraryEntryScope(
        modifiableRootModel: ModifiableRootModel,
        library: Library,
        scope: DependencyScope
    ) {
        findOrderEntryForLibrary(modifiableRootModel, library)
            .let { it.scope = scope }
    }

    private fun resolveMavenSources(
        modifiableRootModel: ModifiableRootModel,
        javaLibraryDescriptor: JavaLibraryDescriptor,
        moduleDescriptor: ModuleDescriptor,
        progressIndicator: ProgressIndicator
    ) = if (LibraryDescriptorType.LIB == javaLibraryDescriptor.descriptorType) {
        MavenUtils.resolveMavenSources(modifiableRootModel, moduleDescriptor, progressIndicator)
    } else emptyList()

    private fun resolveStandardProvidedSources(
        javaLibraryDescriptor: JavaLibraryDescriptor,
        moduleDescriptor: ModuleDescriptor
    ): List<String> {
        if (!HybrisApplicationSettingsComponent.getInstance().state.withStandardProvidedSources) return emptyList()
        if (LibraryDescriptorType.WEB_INF_LIB != javaLibraryDescriptor.descriptorType) return emptyList()

        val sourcesDirectory = File(moduleDescriptor.rootDirectory, HybrisConstants.DOC_SOURCES_JAR_DIRECTORY)
        return sourcesDirectory
            .list { _, name -> name.endsWith("-sources.jar") }
            ?.map { File(sourcesDirectory, it) }
            ?.map { it.absolutePath }
            ?: emptyList()
    }

    // Workaround of using Library.equals in findLibraryOrderEntry, which doesn't work here, because all empty libs are equal. Use == instead.
    private fun findOrderEntryForLibrary(
        modifiableRootModel: ModifiableRootModel,
        library: Library
    ) = modifiableRootModel.orderEntries
        .mapNotNull { it as? LibraryOrderEntry }
        .find { it.library == library }
        ?: (modifiableRootModel.findLibraryOrderEntry(library) as LibraryOrderEntry)
}