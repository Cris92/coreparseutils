package com.logparse.core.property;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@org.springframework.context.annotation.Configuration
@PropertySource("classpath:anagraphicparse.properties")
@ConfigurationProperties(prefix = "anagraphicparse")
public class PropertiesConfiguration {

	@NotBlank
	private static String iso3166_2Folder;
	@NotBlank
	private static String provinceMapping;
	@NotBlank
	private static String countriesMapping;

	public static String getCountriesMapping() {
		return countriesMapping;
	}

	public static void setCountriesMapping(String countriesMapping) {
		PropertiesConfiguration.countriesMapping = countriesMapping;
	}

	public static String getProvinceMapping() {
		return provinceMapping;
	}

	public void setProvinceMapping(String provinceMapping) {
		PropertiesConfiguration.provinceMapping = provinceMapping;
	}

	public static String getIso3166_2Folder() {
		return iso3166_2Folder;
	}

	public void setIso3166_2Folder(String iso3166_2Folder) {
		this.iso3166_2Folder = iso3166_2Folder;
	}

}
