package ch.leica.sdk.commands;

import android.util.Base64;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.WrongDataException;

public class Image {
	private byte[] a;
	private short b = 55537;
	private short c = 55537;

	public Image() {
	}

	public byte[] getImageBytes() throws IllegalArgumentCheckedException {
		if (a != null) {
			return a;
		}
		throw new IllegalArgumentCheckedException(
				"Error: Imagebytes were never assigned. ImageBytes = NULL");
	}

	public void setImageBytes(byte[] paramArrayOfByte) {
		a = paramArrayOfByte;
	}

	public void setImageBytes(String paramString) {
		a = Base64.decode(paramString, 0);
	}

	public void setxCoordinateCrosshair(short paramShort) {
		b = paramShort;
	}

	public short getyCoordinateCrosshair() throws WrongDataException {
		if (c != 55537) {
			return c;
		}
		throw new WrongDataException("Y-Coordinate crosshair has not been set");
	}

	public void setyCoordinateCrosshair(short paramShort) {
		c = paramShort;
	}

	public short getxCoordinateCrosshair() throws WrongDataException {
		if (b != 55537) {
			return b;
		}
		throw new WrongDataException("X-Coordinate crosshair has not been set");
	}
}
