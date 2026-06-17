package com.compressor;

import java.io.*;
import java.util.zip.*;

/**
 * Handles ZIP file decompression logic.
 */
public class Decompressor {

    /**
     * Decompresses a ZIP file to a target directory.
     * @param zipFile The source ZIP file.
     * @param destDir The destination directory.
     * @throws IOException If an I/O error occurs.
     */
    public static void decompress(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        String canonicalDestDirPath = destDir.getCanonicalPath();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                File filePath = new File(destDir, entry.getName());
                String canonicalDestFile = filePath.getCanonicalPath();

                // Prevent Zip Slip vulnerability
                if (!canonicalDestFile.startsWith(canonicalDestDirPath + File.separator) && !canonicalDestFile.equals(canonicalDestDirPath)) {
                    throw new SecurityException("Blocked Zip Slip vulnerability for entry: " + entry.getName());
                }

                if (!entry.isDirectory()) {
                    // Create parent directories if they don't exist
                    File parent = filePath.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }
                    extractFile(zis, filePath);
                } else {
                    filePath.mkdirs();
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        }
    }

    private static void extractFile(ZipInputStream zis, File filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = zis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        }
    }
}
