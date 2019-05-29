package converter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import converter.errors.OJB2JPAPathNotFoundException;

public class OJB2JPA {

	private DirectoryStream<Path> sourceFiles;
	private List<ConvertedJPAFile> convertedJPAFiles;
	
	public OJB2JPA(String sourceFolderPath) throws OJB2JPAPathNotFoundException {
		
		try {
			sourceFiles = Files.newDirectoryStream(Paths.get(sourceFolderPath), path -> path.toString().endsWith(".java"));
			
		} catch (IOException e) {
			throw new OJB2JPAPathNotFoundException("Path not found. Please, pass the absolute path to source folder.");
		}
		
	}

	public List<Path> listSourceFiles() {
		
		return StreamSupport
				.stream(this.sourceFiles.spliterator(), false)
				.collect(Collectors.toList());
	}
	
	public void convert(String pathToSave) throws Exception {
		
		convertedJPAFiles = new ArrayList<>();
		
		for (Path srcPath : sourceFiles) {
			
			ConvertedJPAFile jpaFile = new ConvertedJPAFile(srcPath, pathToSave);
			convertedJPAFiles.add(jpaFile);
			
		}
		
	}
	
	public boolean isReadyToSave() {
		return false;
	}
}
