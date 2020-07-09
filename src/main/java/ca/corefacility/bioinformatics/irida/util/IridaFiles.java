package ca.corefacility.bioinformatics.irida.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

/**
 * Static class which has file object operations that require
 * access to the iridaFileStorageUtility but in a static context
 */

public final class IridaFiles {

	private static IridaFileStorageUtility iridaFileStorageUtility;

	public static void setIridaFileStorageUtility(IridaFileStorageUtility iridaFileStorageUtility) {
		IridaFiles.iridaFileStorageUtility = iridaFileStorageUtility;
	}
	private IridaFiles() {
	}

	/**
	 * Gets the file size of the file from the iridaFileStorageUtility
	 *
	 * @param file The path to the file
	 * @return file size as a human readable string
	 */
	public static String getFileSize(Path file) {
		return iridaFileStorageUtility.getFileSize(file);
	}

	/**
	 * Checks if the file is gzipped in iridaFileStorageUtility
	 *
	 * @param file The path to the file
	 * @return if file is gzipped or not
	 * @throws IOException if file cannot be read
	 */
	public static boolean isGzipped(Path file) throws IOException {
		return iridaFileStorageUtility.isGzipped(file);
	}

	/**
	 * Gets the file input stream from iridaFileStorageUtility
	 *
	 * @param file The path to the file
	 * @return the file input stream
	 */
	public static InputStream getFileInputStream(Path file) {
		return iridaFileStorageUtility.getFileInputStream(file);
	}

	/**
	 * Gets the file extension from iridaFileStorageUtility
	 *
	 * @param toConcatenate List of sequencingObjects to get file extensions for
	 * @return the common extension of the files
	 */
	public static String getFileExtension(List<? extends SequencingObject> toConcatenate) throws IOException{
		return iridaFileStorageUtility.getFileExtension(toConcatenate);
	}

	/**
	 * Appends a sequence file to another file
	 *
	 * @param target The path to the file into which to append
	 * @param file The sequence file to append
	 */
	public static void appendToFile(Path target, SequenceFile file) throws IOException {
		iridaFileStorageUtility.appendToFile(target, file);
	}
}
