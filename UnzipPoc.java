import zipUtil.Extractor;
import java.nio.file.Paths;

class UnzipPoc {
    public static void main(String[] args) {
        extractBulk();
        //extract();
    }

    public static void extract() {
        var extractor = new Extractor();
        var source = Paths.get("files/file1.zip");
        var target = Paths.get("extracted");

        try {
            extractor.extract(source, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void extractBulk() {
        var extractor = new Extractor();
        var source = Paths.get("files");
        var target = Paths.get("extracted");
        try {
            extractor.extractBulk(source, target);            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}