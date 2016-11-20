package net.okyunnura.qrrandomgenerator.web;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.UUID;

@Controller
public class QrController {

	private static final Logger logger = LoggerFactory.getLogger(QrController.class);

	@RequestMapping(path = "/qr", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> qr() throws IOException, WriterException {
		BarcodeFormat format = BarcodeFormat.QR_CODE;
		int width = 300;
		int height = 300;

		Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		hints.put(EncodeHintType.QR_VERSION, 10);

		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix bitMatrix = writer.encode(UUID.randomUUID().toString(), format, width, height, hints);
		BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
		File file = File.createTempFile("barcode", ".png");
		ImageIO.write(image, "png", file);

		logger.info("file path:{}", file.getAbsolutePath());

		return ResponseEntity.ok()
				.contentLength(file.length())
				.contentType(MediaType.IMAGE_PNG)
				.body(new InputStreamResource(new FileInputStream(file)));
	}
}
