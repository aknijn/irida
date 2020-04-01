package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

public class IridaFileStorageAwsServiceImpl implements IridaFileStorageService{
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAzureServiceImpl.class);

	//Aws Specific Variables


	@Autowired
	public IridaFileStorageAwsServiceImpl(){

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getTemporaryFile(Path file) {
		File fileToProcess = null;

		// Implement AWS code to get file

		return fileToProcess;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileSize(Path file) {
		Long fileSize = 0L;
		// Implement AWS code to get file size
		return fileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(Path source, Path target) {
		// Implement AWS code to upload file to bucket
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteFile() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void downloadFile() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void downloadFiles() {
	}

	@Override
	public boolean storageTypeIsLocal(){
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFileName(Path file) {
		String fileName = "";
		// Implement AWS code to get file name
		return fileName;
	}

	@Override
	public boolean fileExists(Path file) {
		return false;
	}

	@Override
	public InputStream getFileInputStream(SequenceFile file) {
		InputStream inputstream = null;
		try {
			inputstream = new FileInputStream(file.getFile().toString());

		} catch(IOException e) {

		}
		return inputstream;
	}

}
