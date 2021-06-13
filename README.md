# Sample project to highlight @DynamicPropertySource issue with Spring-Cloud-Vault during testing

## Issue

When using `TestContainers` to start a `Vault` instance, the port is randomly selected during container startup. Using
a `@DynamicPropertySource`
to override properties:

```java
@DynamicPropertySource
static void addProperties(DynamicPropertyRegistry registry) {
   registry.add("spring.cloud.vault.host",()->vaultContainer.getHost());
   registry.add("spring.cloud.vault.port",()->vaultContainer.getFirstMappedPort());
   registry.add("spring.cloud.vault.uri",()->"http://"+vaultContainer.getHost()+":"+vaultContainer.getFirstMappedPort());
   registry.add("spring.cloud.vault.token",()->TOKEN);
}
```

does not work - `Spring Cloud Vault` does not use the added properties.

## Testing

### W/o TestContainers

Start a `Vault` instance locally by running in the project's main directory
`docker-compose up -d`
followed by
`./init-vault.sh`
This will start the container and initialize `Vault` with a single secret.

Now either run `./mvnw clean install` or (from within you IDE) launch `DemoApplicationTests`. In the output there will be a line like:

```java
2021-06-13 14:03:56.923INFO 48059---[main]com.example.demo.DemoApplication:Got vault-supplied value:foo
```

This shows that the value was correctly retrieved from `Vault`.

### W/ TestContainers

[If `Vault` is still running in a container:] Stop the `Vault` container by running
`docker compose down`
in the project's main directory.

`DemoApplicationTests` will launch a `Vault` container and use a `@DynamicPropertySource` to override some properties from `application.yml`.


Running `./mvnw clean install` now will yield a failing test and a `Connection Refused` when trying to resolve the secret, which shows that
the properties added by `@DynamicPropertySource` are not used.

## Note
Setting the `Vault` container's port via the `TestContainer` API is not possible (anymore). 
