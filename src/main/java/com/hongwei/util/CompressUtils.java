package com.hongwei.util;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.hibernate.boot.archive.spi.ArchiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class CompressUtils {
    static final Logger LOG = LoggerFactory.getLogger(LogWrapper.class);

    /**
     * Untar an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.tar' extension.
     *
     * @param inputFile the input .tar file
     * @param outputDir the output directory file.
     * @return The {@link List} of {@link File}s with the untared content.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ArchiveException
     */
    public static List<File> unTar(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {

        LOG.info(String.format("Untaring %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final List<File> untaredFiles = new LinkedList<File>();
        try {
            final InputStream is = new FileInputStream(inputFile);
            final TarArchiveInputStream debInputStream;

            debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
            TarArchiveEntry entry = null;
            while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {
                final File outputFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    LOG.info(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
                    if (!outputFile.exists()) {
                        LOG.info(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
                        if (!outputFile.mkdirs()) {
                            throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                        }
                    }
                } else {
                    LOG.info(String.format("Creating output file %s.", outputFile.getAbsolutePath()));
                    final OutputStream outputFileStream = new FileOutputStream(outputFile);
                    IOUtils.copy(debInputStream, outputFileStream);
                    outputFileStream.close();
                }
                untaredFiles.add(outputFile);
            }
            debInputStream.close();
        } catch (org.apache.commons.compress.archivers.ArchiveException e) {
            e.printStackTrace();
        }

        return untaredFiles;
    }

    /**
     * Ungzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.gz' extension.
     *
     * @param inputFile the input .gz file
     * @param outputDir the output directory file.
     * @return The {@File} with the ungzipped content.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static File unGzip(final File inputFile, final File outputDir) throws
            FileNotFoundException, IOException {

        LOG.info(String.format("Ungzipping %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(in, out);

        in.close();
        out.close();

        return outputFile;
    }
}
