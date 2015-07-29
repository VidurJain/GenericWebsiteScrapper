package com.gettify.files.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BufferedFileSplitter implements FileSplitter
{
	private File file;

	private BufferedReader bufferedReader;

	public BufferedFileSplitter()
	{
	}

	/**
	 * Constructor.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 *             If the file is null or cannot be found.
	 */
	public BufferedFileSplitter(File file) throws FileNotFoundException
	{
		if (file == null)
		{
			throw new FileNotFoundException("The file cannot be null.");
		}
		this.file = file;
		this.bufferedReader = new BufferedReader(new FileReader(file));
	}

	/**
	 * Returns the next record from the file.
	 * 
	 * @return A String containing the contents of the next record, not including any line-termination characters, or null if the end of file has been reached.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public String getNextRecord() throws IOException
	{
		if (file == null)
		{
			throw new IOException("The file is null. The file must be set before it can be read.");
		}
		if (bufferedReader == null)
		{
			throw new IOException("The buffered reader is null.");
		}
		return bufferedReader.readLine();
	}

	/**
	 * Returns maximum n next record from the file.
	 * 
	 * @return A String containing the contents of the next record, not including any line-termination characters, or null if the end of file has been reached.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public ArrayList<String> getNextRecords(int n) throws IOException
	{
		ArrayList<String> toRet = new ArrayList<String>();
		if (file == null)
		{
			throw new IOException("The file is null. The file must be set before it can be read.");
		}
		if (bufferedReader == null)
		{
			throw new IOException("The buffered reader is null.");
		}
		String ret = null;
		ret = bufferedReader.readLine();
		int i = 0;
		while (ret != null && i < n)
		{
			toRet.add(ret);
			ret = bufferedReader.readLine();
			i++;
		}
		return toRet;
	}

	/**
	 * Closes the BufferedFileSplitter.
	 */
	public void close()
	{
		try
		{
			bufferedReader.close();
		}
		catch (IOException e)
		{
			System.out.println("Unable to close buffered reader." + e);
		}
	}

	/**
	 * Gets the file being split.
	 * 
	 * @return The File.
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Sets the file to split.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 *             If the file is null or cannot be found.
	 */
	public void setFile(File file) throws FileNotFoundException
	{
		if (file == null)
		{
			throw new FileNotFoundException("The File cannot be null.");
		}
		this.file = file;
		this.bufferedReader = new BufferedReader(new FileReader(file));
	}
}
