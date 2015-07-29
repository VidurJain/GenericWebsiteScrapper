package com.gettify.files.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileSplitter
{
	public void setFile(File file) throws FileNotFoundException;

	public File getFile();

	public String getNextRecord() throws IOException;

	public void close();
}
