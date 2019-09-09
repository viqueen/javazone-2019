## JavaZone 2019 - Building a Maven plugin to explore your code-base

### Assumptions

You are using Maven as your build tool, but I am pretty sure the skills/ideas are transferable to other build tools.

### Requirements

* java
* maven

I trust the linked resources are enough to get you started on your platform/environment of choice

### 1. Create a new maven plugin project

Mojo archetype to the rescue

```bash
    mvn archetype:generate \
      -DgroupId=javazone.plugin \
      -DartifactId=javazone-maven-plugin \
      -DarchetypeGroupId=org.apache.maven.archetypes \
      -DarchetypeArtifactId=maven-archetype-mojo
```

Update your pom file definition with the following properties and dependencies

```xml
<properties>
    <maven.version>3.6.0</maven.version>
</properties>
<dependencies>
    <dependency>
        <groupId>org.apache.maven</groupId>
        <artifactId>maven-plugin-api</artifactId>
        <version>${maven.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.maven.plugin-tools</groupId> 
        <artifactId>maven-plugin-annotations</artifactId>
        <version>${maven.version}</version>
    </dependency>
</dependencies>
```

Update your java class with the following

```java
@Mojo(name = "jz", defaultPhase = LifecyclePhase.VALIDATE)
public class JavazoneMojo extends AbstractMojo {
    // resolves the target directory
    @Parameter(defaultValue = "${project.build.directory}")
    private File outputDirectory;
    
    // ...
}
```

Now build your plugin

```bash
cd javazone-maven-plugin
mvn -e clean install

```

And run it, it should create a file `touch.txt` under your target directory

```bash
mvn javazone.plugin:javazone-maven-plugin:1.0-SNAPSHOT:jz
```

### 2. Consume Maven Core API

Update your pom file definition by adding the following dependency

```xml
<dependency>
    <groupId>org.apache.maven</groupId>
    <artifactId>maven-core</artifactId>
    <version>${maven.version}</version>
</dependency>
```

And discover what you can do with by updating your Mojo class

```java
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
```