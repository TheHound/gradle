/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradleinternal.buildinit.plugins.internal.maven;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public class MavenManagedDependencies {
    
    private Set<ManagedDepKey> managed = new HashSet<>();
    
    public boolean isManaged(Dependency dependency) {
        if (dependency instanceof ExternalDependency) {
            ExternalDependency extDep = (ExternalDependency) dependency;
            return managed.contains(new ManagedDepKey(extDep.getGroupId(), extDep.getModule(), extDep.getVersion(),
                    extDep.getClassifier()));
        }
        return false;
    }
    
    public static MavenManagedDependencies createFor(MavenProject mavenProject) {
        MavenManagedDependencies managedDeps = new MavenManagedDependencies();
        if (mavenProject.getDependencyManagement() == null) {
            return managedDeps;
        }
        List<org.apache.maven.model.Dependency> dependencies = mavenProject.getDependencyManagement().getDependencies();
        for (org.apache.maven.model.Dependency dep : dependencies) {
            managedDeps.managed.add(
                    new ManagedDepKey(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getClassifier()));
        }
        return managedDeps;
    }
    
    /**
     * Parts that make up a dependency being managed <br>
     * I.e. effective pom needs to match these for dependency to be managed. if the version is different is dependency override, if classifier is different different dependency
     */
    private static class ManagedDepKey {
        private final String group;
        private final String module;
        private final String version;
        private final String classifier;
        
        public ManagedDepKey(String group, String module, String version, String classifier) {
            this.group = group;
            this.module = module;
            this.version = version;
            this.classifier = classifier;
        }

        @Override
        public int hashCode() {
            return Objects.hash(classifier, group, module, version);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ManagedDepKey)) {
                return false;
            }
            ManagedDepKey other = (ManagedDepKey) obj;
            return Objects.equals(classifier, other.classifier) 
                    && Objects.equals(group, other.group)
                    && Objects.equals(module, other.module) 
                    && Objects.equals(version, other.version);
        }
        
        
    }
}
