package edu.wpi.cs3733.D21.teamE;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;

public class QRCode {

	public static String scanQR() {
		Webcam webcam = Webcam.getDefault();
		String result = null;

		while (result == null) {
			WebcamUtils.capture(webcam, "src/main/resources/edu/wpi/TeamE/temp/temp", ImageUtils.FORMAT_PNG);
			try {
				result = QRCode.readQR("src/main/resources/edu/wpi/TeamE/temp/temp.png");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.substring(result.lastIndexOf('/') + 1, result.lastIndexOf('.'));
	}

	public static String readQR(String path) throws IOException {
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(path)))));
		Result result;
		try {
			result = new MultiFormatReader().decode(binaryBitmap);
		} catch (NotFoundException e) {
			//e.printStackTrace();
			return null;
		}
		return result.getText();
	}
}