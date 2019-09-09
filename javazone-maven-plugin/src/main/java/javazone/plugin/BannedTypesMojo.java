package javazone.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

@Mojo(name = "banned", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class BannedTypesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute() {
        Set<Method> methods = resolveClasses().stream()
                .filter(this::isApi)
                .map(this::resolveMethods)
                .flatMap(Arrays::stream)
                .filter(this::consumesBannedType)
                .collect(toSet());

        getLog().warn(methods.toString());
    }

    private Collection<Class> resolveClasses() {
        // TODO actually resolve classes
        return Collections.emptyList();
    }

    private boolean consumesBannedType(final Method method) {
        return concat(
                of(method.getReturnType().getCanonicalName()),
                stream(method.getParameterTypes()).map(Class::getCanonicalName)
        ).filter(Objects::nonNull)
                // TODO can we pass this to the Mojo at build time ?
                .anyMatch("com.google.common.base.Predicate"::equals);
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
        // TODO can we pass this to the Mojo at build time ?
        return type.getPackage().getName().contains("org.viqueen.api");
    }
}
