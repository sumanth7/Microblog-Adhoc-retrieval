import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by sumanth on 12/8/2016.
 */

public class lucene_index {
    public static void main(String[] args)
    {
        FSDirectory direct;
        IndexWriter writer = null;
        try {

            File directory = new File("C:\\project\\input");
            direct = FSDirectory.open(new File("C:\\project\\index"));

            File input_files[]=directory.listFiles();

            Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_36);

            IndexWriterConfig configuration = new IndexWriterConfig(Version.LUCENE_36,analyzer);
            configuration.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            try {
                writer = new IndexWriter(direct,configuration);
                for(File F : input_files){

                    Document Doc;
                    try {
                        Iterating docs = new Iterating(F);

                        while (docs.hasNext()) {
                            Doc = docs.next();
                            if (Doc != null && Doc.get("text") != null)
                            {

                                writer.addDocument(Doc);

                            }
                        }

                    }
                    catch (FileNotFoundException x) {
                        x.printStackTrace();
                    }


                }
            }

            catch (CorruptIndexException x1) {
                x1.printStackTrace();
            } catch (LockObtainFailedException x1) {
                x1.printStackTrace();
            } catch (IOException x1) {
                x1.printStackTrace();
            }
        }
        catch (IOException x2) {
            x2.printStackTrace();
        }
        finally
        {
            System.out.println("IndexWriter is closed");


            try {
                writer.close();
            } catch (CorruptIndexException x) {
                x.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


