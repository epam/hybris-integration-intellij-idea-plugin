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

package com.intellij.idea.plugin.hybris.project.configurators.impl;

import com.intellij.idea.plugin.hybris.project.configurators.*;
import com.intellij.idea.plugin.hybris.project.descriptors.ModuleDescriptor;
import com.intellij.idea.plugin.hybris.project.descriptors.HybrisProjectDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DefaultConfiguratorFactory implements ConfiguratorFactory {

    @NotNull
    @Override
    public List<FacetConfigurator> getFacetConfigurators() {
        final FacetConfigurator springFacetConfigurator = ApplicationManager.getApplication().getService(SpringFacetConfigurator.class);
        final FacetConfigurator kotlinFacetConfigurator = ApplicationManager.getApplication().getService(KotlinFacetConfigurator.class);
        final FacetConfigurator webFacetConfigurator = ApplicationManager.getApplication().getService(WebFacetConfigurator.class);

        final List<FacetConfigurator> facetConfigurators = new ArrayList<FacetConfigurator>(3);

        if (null != springFacetConfigurator) {
            facetConfigurators.add(springFacetConfigurator);
        }

        if (null != kotlinFacetConfigurator) {
            facetConfigurators.add(kotlinFacetConfigurator);
        }

        if (null != webFacetConfigurator) {
            facetConfigurators.add(webFacetConfigurator);
        }

        return facetConfigurators;
    }

    @NotNull
    @Override
    public SpringConfigurator getSpringConfigurator() {
        final SpringConfigurator springConfigurator = ApplicationManager.getApplication().getService(DefaultSpringConfigurator.class);

        return (null == springConfigurator) ? new DummySpringConfigurator() : springConfigurator;
    }

    @NotNull
    @Override
    public ModulesDependenciesConfigurator getModulesDependenciesConfigurator() {
        return ApplicationManager.getApplication().getService(ModulesDependenciesConfigurator.class);
    }

    @NotNull
    @Override
    public CompilerOutputPathsConfigurator getCompilerOutputPathsConfigurator() {
        return ApplicationManager.getApplication().getService(CompilerOutputPathsConfigurator.class);
    }

    @NotNull
    @Override
    public ContentRootConfigurator getRegularContentRootConfigurator() {
        return ApplicationManager.getApplication().getService(RegularContentRootConfigurator.class);
    }

    @NotNull
    @Override
    public ContentRootConfigurator getReadOnlyContentRootConfigurator() {
        return ApplicationManager.getApplication().getService(ReadOnlyContentRootConfigurator.class);
    }

    @NotNull
    @Override
    public LibRootsConfigurator getLibRootsConfigurator() {
        return ApplicationManager.getApplication().getService(LibRootsConfigurator.class);
    }

    @NotNull
    @Override
    public GroupModuleConfigurator getGroupModuleConfigurator() {
        return ApplicationManager.getApplication().getService(GroupModuleConfigurator.class);
    }

    @NotNull
    @Override
    public JavadocModuleConfigurator getJavadocModuleConfigurator() {
        return ApplicationManager.getApplication().getService(JavadocModuleConfigurator.class);
    }

    @NotNull
    @Override
    public ModuleSettingsConfigurator getModuleSettingsConfigurator() {
        return ModuleSettingsConfigurator.Companion.getInstance();
    }

    @NotNull
    @Override
    public VersionControlSystemConfigurator getVersionControlSystemConfigurator() {
        return VersionControlSystemConfigurator.Companion.getInstance();
    }

    @NotNull
    @Override
    public RunConfigurationConfigurator getDebugRunConfigurationConfigurator() {
        return RunConfigurationConfigurator.Companion.getDebugInstance();
    }

    @Nullable
    @Override
    public RunConfigurationConfigurator getTestRunConfigurationConfigurator() {
        return RunConfigurationConfigurator.Companion.getTestInstance();
    }

    @Nullable
    @Override
    public AntConfigurator getAntConfigurator() {
        return ApplicationManager.getApplication().getService(AntConfigurator.class);
    }

    @Nullable
    @Override
    public MavenConfigurator getMavenConfigurator() {
        return ApplicationManager.getApplication().getService(MavenConfigurator.class);
    }

    @Nullable
    @Override
    public EclipseConfigurator getEclipseConfigurator() {
        return ApplicationManager.getApplication().getService(EclipseConfigurator.class);
    }

    @Nullable
    @Override
    public GradleConfigurator getGradleConfigurator() {
        return ApplicationManager.getApplication().getService(GradleConfigurator.class);
    }

    @NotNull
    @Override
    public SearchScopeConfigurator getSearchScopeConfigurator() {
        return ApplicationManager.getApplication().getService(SearchScopeConfigurator.class);
    }

    @Nullable
    @Override
    public DataSourcesConfigurator getDataSourcesConfigurator() {
        return ApplicationManager.getApplication().getService(DataSourcesConfigurator.class);
    }

    @Nullable
    @Override
    public JavaCompilerConfigurator getJavaCompilerConfigurator() {
        return ApplicationManager.getApplication().getService(JavaCompilerConfigurator.class);
    }

    @Override
    public @Nullable KotlinCompilerConfigurator getKotlinCompilerConfigurator() {
        return ApplicationManager.getApplication().getService(KotlinCompilerConfigurator.class);
    }

    @NotNull
    @Override
    public LoadedConfigurator getLoadedConfigurator() {
        return ApplicationManager.getApplication().getService(LoadedConfigurator.class);
    }

    protected static class DummySpringConfigurator implements SpringConfigurator {

        @Override
        public void findSpringConfiguration(@NotNull final List<ModuleDescriptor> modulesChosenForImport) {

        }

        @Override
        public void configureDependencies(
            @NotNull final HybrisProjectDescriptor hybrisProjectDescriptor,
            @NotNull final IdeModifiableModelsProvider modifiableModelsProvider
        ) {

        }
    }
}
