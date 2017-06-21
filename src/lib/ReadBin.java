package lib;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReadBin {
	
	private FileInputStream fis;
	private BufferedInputStream bis;
	private DataInputStream dis;
	
	public ReadBin(String file) {
		try {
			this.fis = new FileInputStream("resources/bin/" + file);
		} catch (FileNotFoundException e) {
			throw new UnsupportedOperationException("Bin loading failed! The file '"+file+"' could not be found.");
		}
		this.bis = new BufferedInputStream(fis);
		this.dis = new DataInputStream(bis);
	}
	
	/**
	 * Read a byte from the input stream.
	 * @param b
	 * @throws IOException
	 */
	public byte readByte() throws IOException {
		return dis.readByte();
	}
	
	/**
	 * Read a boolean from the input stream.
	 * @return
	 * @throws IOException
	 */
	public boolean readBool() throws IOException {
		return dis.readByte() > 0;
	}

	/**
	 * Read a short from the input stream.
	 * @param s
	 * @throws IOException
	 */
	public short readShort() throws IOException {
		return dis.readShort();
	}
	/**
	 * Read an integer from the input stream.
	 * @param i
	 * @throws IOException
	 */
	public int readInt() throws IOException {
		return dis.readInt();
	}
	
	/**
	 * Read a long from the input stream.
	 * @param l
	 * @throws IOException
	 */
	public long readLong() throws IOException {
		return dis.readLong();
	}
	
	/**
	 * Read a double from the input stream.
	 * @param d
	 * @throws IOException
	 */
	public double readDouble() throws IOException {
		return dis.readDouble();
	}
	
	/**
	 * Read a float from the input stream.
	 * @param f
	 * @throws IOException
	 */
	public float readFloat() throws IOException {
		return dis.readFloat();
	}

	/**
	 * Read a string from the input stream.
	 * @param s
	 * @throws IOException
	 */
	public String readString() throws IOException {
		return dis.readUTF();
	}
	
	/**
	 * Skip a set amount of  bytes.
	 * @param n
	 * @throws IOException
	 */
	public void skip(int n) throws IOException {
		dis.skip(n);
	}
	
	/**
	 * Close all the output streams.
	 * @throws IOException
	 */
	public void close() throws IOException {
		dis.close();
		dis.close();
		fis.close();
	}

}
