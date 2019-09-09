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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase validate
 */
@Mojo(name = "jz", defaultPhase = LifecyclePhase.VALIDATE)
public class JavazoneMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}")
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        File f = outputDirectory;

        // create the output directory if it does not exist
        if (!f.exists()) {
            f.mkdirs();
        }

        // create a file touch.txt
        File touch = new File(f, "touch.txt");

        FileWriter w = null;
        try {
            w = new FileWriter(touch);

            // and write to it
            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            // TODO: bleh we can use try with resource for this
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
