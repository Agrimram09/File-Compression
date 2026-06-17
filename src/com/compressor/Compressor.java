package com.compressor;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.*;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.sound.sampled.*;

/**
 * Handles all core compression logic for the File Compressor Tool.
 * Provides methods for Image, Audio, and general ZIP compression.
 */
public class Compressor {

    // --- Image Compression Methods ---

    public static BufferedImage loadImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static long getFileSize(File file) {
        return file != null ? file.length() : 0;
    }

    public static BufferedImage compressImage(BufferedImage original, int quality) {
        if (original == null) return null;
        int width = original.getWidth();
        int height = original.getHeight();

        int maxDim = Math.max(width, height);
        double scale = 1.0;
        if (maxDim > 1200) {
            scale = 1200.0 / maxDim;
        }
        
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        BufferedImage compressed = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = compressed.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return compressed;
    }

    public static void saveImage(BufferedImage image, String format, File outputFile, int quality) throws IOException {
        if (image == null || outputFile == null) return;
        if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
            applyJPEGQuality(image, outputFile, (float) quality);
        } else {
            ImageIO.write(image, format.toLowerCase(), outputFile);
        }
    }

    private static void applyJPEGQuality(BufferedImage image, File outputFile, float quality) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality / 100f);
        }
        try (FileImageOutputStream stream = new FileImageOutputStream(outputFile)) {
            writer.setOutput(stream);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    // --- General File Compression (ZIP) ---

    /**
     * Compresses any selected file or directory recursively into a .zip archive.
     * @param quality 0 = maximum compression (smallest), 100 = no compression (fastest)
     */
    public static void compressToZip(File inputFile, File outputFile, int quality) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            // Map quality 0-100 -> deflate level 9-0 (inverted: lower quality = smaller file)
            int level = Math.round((100 - quality) * 9.0f / 100);
            zos.setLevel(level);
            compressToZipRecursive(inputFile, inputFile.getName(), zos);
        }
    }

    /**
     * Compresses multiple files/directories into a single .zip archive.
     * @param files   The files and/or directories to include.
     * @param quality 0 = maximum compression (smallest), 100 = no compression (fastest)
     */
    public static void compressMultipleToZip(File[] files, File outputFile, int quality) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            int level = Math.round((100 - quality) * 9.0f / 100);
            zos.setLevel(level);
            for (File f : files) {
                compressToZipRecursive(f, f.getName(), zos);
            }
        }
    }

    private static void compressToZipRecursive(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            // Append '/' to denote directory entry in ZIP
            String dirName = fileName.endsWith("/") ? fileName : fileName + "/";
            zos.putNextEntry(new ZipEntry(dirName));
            zos.closeEntry();
            
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    compressToZipRecursive(childFile, dirName + childFile.getName(), zos);
                }
            }
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }
    }

    /**
     * Compresses a WAV audio file to a lower-quality format based on the quality level.
     * @param quality 0=8kHz 8-bit mono (smallest), 100=44.1kHz 16-bit stereo (best)
     */
    public static void compressAudio(File inputFile, File outputFile, int quality) throws Exception {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(inputFile)) {
            AudioFormat format = ais.getFormat();

            // Pick target format tier based on quality
            AudioFormat targetFormat;
            if (quality <= 25) {
                // Lowest: 8 kHz, 8-bit, mono
                targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
                        8000f, 8, 1, 1, 8000f, false);
            } else if (quality <= 50) {
                // Low: 11 kHz, 8-bit, mono
                targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
                        11025f, 8, 1, 1, 11025f, false);
            } else if (quality <= 75) {
                // Medium: 22 kHz, 16-bit, mono
                targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        22050f, 16, 1, 2, 22050f, false);
            } else {
                // High: 44.1 kHz, 16-bit, stereo
                targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        44100f, 16, 2, 4, 44100f, false);
            }

            if (AudioSystem.isConversionSupported(targetFormat, format)) {
                try (AudioInputStream convertedAis = AudioSystem.getAudioInputStream(targetFormat, ais)) {
                    AudioSystem.write(convertedAis, AudioFileFormat.Type.WAVE, outputFile);
                }
            } else {
                // Fallback: write as-is (still produces a valid .wav)
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
            }
        }
    }
}
