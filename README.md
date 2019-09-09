## JavaZone 2019 - Building a Maven plugin to explore your code-base

### Assumptions

You are using Maven as your build tool, but I am pretty sure the skills/ideas are transferable to other build tools.

### Requirements

* java
* maven

I trust the linked resources are enough to get you started on your platform/environment of choice

### Create a new maven plugin project

Mojo archetype to the rescue

```bash
    mvn archetype:generate \
      -DgroupId=javazone.plugin \
      -DartifactId=javazone-maven-plugin \
      -DarchetypeGroupId=org.apache.maven.archetypes \
      -DarchetypeArtifactId=maven-archetype-mojo
```


