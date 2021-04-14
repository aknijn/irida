package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

import com.google.common.collect.ImmutableList;

/**
 * Responsible for parsing the webpack manifest file and passing along the chunks for
 * js, css, and html resources.  During development the manifest file is checked to ensure
 * it has not been updated during a build, this will not happen in production since the
 * manifest file will never change.
 */
public class WebpackerManifestParser {
	private static final Logger logger = LoggerFactory.getLogger(WebpackerManifestParser.class);
	private static Entries entries;
	private static String manifestChecksum = "";
	private static Boolean autoUpdatable = true;

	/**
	 * Allows the UI configuration to determine during runtime if we are in a production or development
	 * environment.
	 *
	 * @param updatable - should the manifest file be checked to see if it has been updated.
	 */
	public static void setAutoUpdatable(Boolean updatable) {
		WebpackerManifestParser.autoUpdatable = updatable;
	}

	/**
	 * Get a list of webpack chunks for a specific file type given an entry.
	 *
	 * @param context - the {@link ServletContext}
	 * @param entry - the current webpack entry to get chunks for.
	 * @param type  - the type of resource files to get.
	 * @return List of chunks
	 */
	public static List<String> getChunksForEntryType(
			ServletContext context, String entry, WebpackerTagType type) {
		Entries entries = getEntries(context);

		/*
		 * Ensure the entry actually exists in webpack.
		 */
		if (!entries.containsKey(entry)) {
			logger.debug(String.format("Webpack manifest does not have an entry for %s", entry));
			return null;
		}

		/*
		 * Ensure that the entry exists and has resources for the type wanted.
		 */
		if (!entries.containsKey(entry) && !entry.equals("vendor")) {
			logger.debug(String.format("For the entry %s, Webpack manifest does a %s file type", type, entry));
			return null;
		}

		switch (type) {
		case JS:
			return entries.get(entry)
					.getJavascript();
		case CSS:
			return entries.get(entry)
					.getCss();
		case HTML:
			return entries.get(entry)
					.getHtml();
		default:
			return ImmutableList.of();
		}
	}

	/**
	 * Get the current manifest content.  If this is running in development, check to ensure that the
	 * manifest file has not changed, if it has, parse the new manifest file.
	 *
	 * @return {@link Map} of all entries and their corresponding chunks.
	 */
	private static Entries getEntries(ServletContext context) {
		try {
			if (WebpackerManifestParser.entries == null || autoUpdatable) {
				String path = context.getResource("/dist/assets-manifest.json")
						.getPath();
				File manifestFile = ResourceUtils.getFile(path);
				try (InputStream is = Files.newInputStream(manifestFile.toPath())) {
					String checksum = org.apache.commons.codec.digest.DigestUtils.sha256Hex(is);
					if (!checksum.equals(WebpackerManifestParser.manifestChecksum)) {
						WebpackerManifestParser.entries = parseWebpackManifestFile(manifestFile);
						WebpackerManifestParser.manifestChecksum = checksum;
					}
				} catch (IOException e) {
					throw new FileProcessorException("could not calculate checksum", e);
				}
			}
		} catch (FileNotFoundException | MalformedURLException e) {
			logger.error("Cannot find webpack manifest file.");
		}
		return WebpackerManifestParser.entries;
	}

	/**
	 * Parse the webpack manifest file
	 *
	 * @param file - the webpack manifest file.
	 * @return {@link Map} of all entries and their corresponding chunks.
	 */
	@SuppressWarnings("unchecked")
	private static Entries parseWebpackManifestFile(File file) {
		Entries entries = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> manifest = objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {
			});

			Map<String, Map<String, Map<String, List<String>>>> entrypoints = (Map<String, Map<String, Map<String, List<String>>>>) manifest.get(
					"entrypoints");
			entries = new Entries(entrypoints);
		} catch (IOException e) {
			logger.error("Error reading webpack manifest file.");
		}
		return entries;
	}

	static class EntryPoint {
		private final List<String> javascript;
		private final List<String> css;
		private final List<String> html;

		public EntryPoint(List<String> javascript, List<String> css, List<String> html) {
			this.javascript = javascript;
			this.css = css;
			this.html = html;
		}

		public List<String> getJavascript() {
			return javascript;
		}

		public List<String> getCss() {
			return css;
		}

		public List<String> getHtml() {
			return html;
		}
	}

	static class Entries extends HashMap<String, EntryPoint> {
		public Entries(Map<String, Map<String, Map<String, List<String>>>> entrypoints) {
			Set<String> entries = entrypoints.keySet();
			entries.forEach(entry -> {
				Map<String, List<String>> assets = entrypoints.get(entry)
						.get("assets");
				this.put(entry, new EntryPoint(assets.get("js"), assets.get("css"), assets.get("html")));
			});
		}
	}
}
