package converter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import converter.errors.OJB2JPAPathNotFoundException;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class OJB2JPA {

	private static final Logger logger = LoggerFactory.getLogger(OJB2JPA.class);

	private DirectoryStream<Path> sourceFiles;
	private List<ConvertedJPAFile> convertedJPAFiles = new ArrayList<>();

	public OJB2JPA(String sourceFolderPath) throws OJB2JPAPathNotFoundException {

		try {
			sourceFiles = Files.newDirectoryStream(Paths.get(sourceFolderPath),
					path -> path.toString().endsWith(".java"));

		} catch (IOException e) {
			throw new OJB2JPAPathNotFoundException("Path not found. Please, pass the absolute path to source folder.");
		}

	}

	public void convert(String pathToSave) throws Exception {

		for (Path srcPath : sourceFiles) {

			ConvertedJPAFile jpaFile = new ConvertedJPAFile(srcPath, pathToSave);
			convertedJPAFiles.add(jpaFile);

		}
		
		logger.info("The convertion of OJB files to JPA has been finished.");
		
		List<ConvertedJPAFile> skippedFiles = convertedJPAFiles.stream().filter(c -> c.isSkippedClass()).collect(Collectors.toList());
		
		String strOfSkippedFiles = skippedFiles
											.stream()
											.map(f -> f.getClassName())
											.collect(Collectors.joining(", "));
		
		int convertedFiles = convertedJPAFiles.size() - skippedFiles.size();
		
		logger.info("Total of source files converted: " + convertedFiles);
		logger.info("Total of skipped classes: " + skippedFiles.size());
		logger.info("These classes were skipped: [" + strOfSkippedFiles + "]");

	}

	public void write() {

		convertedJPAFiles.parallelStream().forEach(jpaFile -> {

			if (!jpaFile.isSkippedClass()) {
				Path file = Paths.get(jpaFile.getPathToSave(), jpaFile.getClassName() + ".java");

				file.getParent().toFile().mkdirs();

				byte[] content = jpaFile.printConvertedClass().getBytes();

				try {
					logger.info("Saving file to " + file);
					Files.write(file, content);
				} catch (IOException e) {

					logger.error("Unable to save the file " + file, e);
				}
			}
		});
	}

	public List<ConvertedJPAFile> getJPAFiles() {

		return convertedJPAFiles;
	}

	protected static String findDoclet(String surround, String content) {

		final String regex = String.format(
				"(?:\\/\\*(?:[^\\*]|(?:\\*+[^\\*\\/]))*\\*+\\/(?=\\s.*))(?=\\s*(?:@\\w.+\\s*)*.+\\b%s\\b)", surround);
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(0);
		}

		return "";
	}
}
