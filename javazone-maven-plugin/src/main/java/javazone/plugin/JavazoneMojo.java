package javazone.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Scm;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase validate
 */
@Mojo(name = "jz", defaultPhase = LifecyclePhase.VALIDATE)
public class JavazoneMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;


    public void execute() throws MojoFailureException {
        try {
            List<Dependency> dependencies = project.getDependencies();
            getLog().warn(dependencies.toString());

            List<String> compileClasspathElements = project.getCompileClasspathElements();
            List<String> testClasspathElements = project.getTestClasspathElements();
            getLog().warn(compileClasspathElements.toString());
            getLog().warn(testClasspathElements.toString());

            Scm scm = project.getScm();
            getLog().warn(scm.getUrl());
            getLog().warn(scm.getConnection());
            getLog().warn(scm.getTag());

        } catch (DependencyResolutionRequiredException exception) {
            throw new MojoFailureException(exception.getMessage(), exception);
        }
    }

}
