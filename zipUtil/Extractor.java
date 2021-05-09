package zipUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Extractor {

    // Extract a single zip file
    // source: Zip file to extract
    // target: Directory where the file should be extracted
    public void extract(Path source, Path target) throws IOException {
        if (source == null || target == null)
            throw new IllegalArgumentException("source or target is null");

        System.out.print("Extracting " + source.getFileName() + "... ");
        unzipFolder(source, Paths.get(target.toString() + File.separator + source.getFileName()));
        System.out.println("Successfully extracted");
    }

    // Extract all the zip files in a directory
    // source: Directory having all the zip files
    // target: Directory where files should be extracted
    public void extractBulk(Path source, Path target) {
        if (source == null || target == null)
            throw new IllegalArgumentException("source or target is null");

        File f = new File(source.toString());

        FilenameFilter filter = (f1, name) -> name.endsWith(".zip");

        File[] files = f.listFiles(filter);

        for (int i = 0; i < files.length; i++) {            
            Path currentSource = Paths.get(files[i].getPath());
            Path currentTarget = Paths.get(target.toString() + File.separator + files[i].getName());
            try {
                System.out.print("Extracting " + files[i].getPath() + "... ");
                unzipFolder(currentSource, currentTarget);
                System.out.println("Successfully extracted");
            } catch (IOException ex) {
                System.out.println("Error occurred:");
                ex.printStackTrace();
            }
        }
    }

    private void unzipFolder(Path source, Path target) throws IOException {

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = false;
                // some zip stored files and folders separately
                // e.g data/
                // data/folder/
                // data/folder/file.txt
                if (zipEntry.getName().endsWith("/") || zipEntry.getName().endsWith("\\")) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    // some zip stored file path only, need create parent directories
                    // e.g data/folder/file.txt
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }

                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    // protect zip slip attack
    private Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) throws IOException {
        // test zip slip vulnerability
        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }
}