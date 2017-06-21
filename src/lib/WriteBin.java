/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class WriteBin {
    
        private FileOutputStream fos;
	private BufferedOutputStream bos;
	
        private DataOutputStream dos;
        
	public WriteBin(String file) {
		try {
			this.fos = new FileOutputStream("resources/bin/" + file);
		} catch (FileNotFoundException e) {
			throw new UnsupportedOperationException("Bin loading failed! The file '"+file+"' could not be found.");
		}
		this.bos = new BufferedOutputStream(fos);
		this.dos = new DataOutputStream(bos);
	}
	
	/**
	 * Writes a byte to the output stream.
	 * @param b
	 * @throws IOException
	 */
	public void writeByte(int b) throws IOException {
                dos.writeByte(b);
	}
	
	/**
	 * Writes a boolean to the output stream.
	 * @param b
	 * @throws IOException
	 */
	public void writeBool(boolean b) throws IOException {
		dos.writeBoolean(b);
	}

	/**
	 * Writes a short to the output stream.
	 * @param s
	 * @throws IOException
	 */
	public void writeShort(short s) throws IOException {
		dos.writeShort(s);
	}
	/**
	 * Writes an integer to the output stream.
	 * @param i
	 * @throws IOException
	 */
	public void writeInt(int i) throws IOException {
		dos.writeInt(i);
	}
	
	/**
	 * Writes a long to the output stream.
	 * @param l
	 * @throws IOException
	 */
	public void writeLong(long l) throws IOException {
		dos.writeLong(l);
	}
	
	/**
	 * Writes a double to the output stream.
	 * @param d
	 * @throws IOException
	 */
	public void writeDouble(double d) throws IOException {
		dos.writeDouble(d);
	}
	
	/**
	 * Writes a float to the output stream.
	 * @param f
	 * @throws IOException
	 */
	public void writeFloat(float f) throws IOException {
		dos.writeFloat(f);
	}

	/**
	 * Writes a string to the output stream.
	 * @param s
	 * @throws IOException
	 */
	public void writeString(String s) throws IOException {
		dos.writeUTF(s);
	}
	
	/**
	 * Close all the output streams.
	 * @throws IOException
	 */
	public void close() throws IOException {
		dos.close();
		bos.close();
		fos.close();
	}

}
