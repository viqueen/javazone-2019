package javazone.plugin;

import com.google.common.reflect.ClassPath;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

@Mojo(
        name = "banned",
        defaultPhase = LifecyclePhase.VERIFY,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class BannedTypesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "packages", readonly = true, required = true)
    private Set<String> packages;

    @Parameter(property = "types", readonly = true, required = true)
    private Set<String> bannedTypes;

    public void execute() throws MojoFailureException {
        try {
            Set<Method> methods = resolveClasses().stream()
                    .filter(this::isApi)
                    .map(this::resolveMethods)
                    .flatMap(Arrays::stream)
                    .filter(this::consumesBannedType)
                    .collect(toSet());

            getLog().warn(methods.toString());
        } catch (IOException | DependencyResolutionRequiredException exception) {
            throw new MojoFailureException(exception.getMessage(), exception);
        }
    }

    private Collection<Class> resolveClasses() throws DependencyResolutionRequiredException, IOException {
        final Set<URL> urls = new LinkedHashSet<>();

        // get the project's compile classpath elements
        // and turn them into URLs
        for (String element : project.getCompileClasspathElements()) {
            urls.add(new File(element).toURI().toURL());
        }

        // create a URLClassLoader using the previously
        // resolved urls
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{
                new File(project.getBuild().getOutputDirectory()).toURI().toURL()
        }, new URLClassLoader(urls.toArray(new URL[]{})));

        // feed the classloader to ClassPath and resolve allClasses
        return ClassPath.from(urlClassLoader)
                .getTopLevelClasses()
                .stream()
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toList());
    }

    private boolean consumesBannedType(final Method method) {
        return concat(
                of(method.getReturnType().getCanonicalName()),
                stream(method.getParameterTypes()).map(Class::getCanonicalName)
        ).filter(Objects::nonNull)
                .anyMatch(item -> bannedTypes.contains(item));
    }

    private Method[] resolveMethods(Class<?> type) {
        try {
            return type.getDeclaredMethods();
        } catch (Throwable exception) {
            // as we are building out our plugin, it is preferable to let it do its magic and report
            // issues as warnings, this is just one of my dev preferences really
            getLog().warn(exception);
            return new Method[]{};
        }
    }

    private boolean isApi(Class<?> type) {
        return packages.stream()
                .anyMatch(item -> type.getPackage().getName().contains(item));
    }
}
