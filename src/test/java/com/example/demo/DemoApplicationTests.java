package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.vault.VaultContainer;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAutoConfiguration
@DirtiesContext
@Testcontainers
@TestPropertySource(locations = { "classpath:application.yml" })
class DemoApplicationTests {
	public static final String TOKEN = "my-root-token";
	@Container
	static VaultContainer vaultContainer = new VaultContainer<>("vault:1.7.2")
		.withVaultToken(TOKEN)
		.withInitCommand(
			"secrets enable --path foo kv-v2")
		.withInitCommand(
			"kv put foo/app bar=foo");

	@Autowired
	private DemoBean demoBean;

	@DynamicPropertySource
	static void addProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.cloud.vault.host", () -> vaultContainer.getHost());
		registry.add("spring.cloud.vault.port", () -> vaultContainer.getFirstMappedPort());
		registry.add("spring.cloud.vault.uri", () -> "http://" + vaultContainer.getHost() + ":" + vaultContainer.getFirstMappedPort());
		registry.add("spring.cloud.vault.token", () -> TOKEN);
	}

	@Test
	void contextLoads() {
		String foo = demoBean.getFoo();
		assertThat(foo).isEqualTo("foo");
	}

}
