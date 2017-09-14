import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;

import java.io.*;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sumanth on 12/9/2016.
 */
public class Iterating  implements Iterator<Document> {
    protected BufferedReader readr;
    protected boolean at_eof = false;

    public Iterating(File f) throws FileNotFoundException {
        readr = new BufferedReader(new FileReader(f));
        System.out.println("Reading the file " + f.toString());
    }

    @Override
    public boolean hasNext() {
        return !at_eof;
    }

    @Override
    public Document next() {
        Document doc = new Document();
        StringBuffer buff = new StringBuffer();
        try {
            String l;


            Pattern text_tag = Pattern.compile("<text>(.+?)</text>");
            Pattern docno_tag = Pattern.compile("<DOCNO>(.+?)</DOCNO>");
            Pattern id_tag = Pattern.compile("<id>(.+?)</id>");
            Pattern Time_tag = Pattern.compile("<created_at>(.+?)</created_at>");
            boolean in_doc = false;
            while (true) {
                l = readr.readLine();
                if (l == null) {
                    at_eof = true;
                    break;
                }
                if (!in_doc) {
                    if (l.startsWith("<DOC>"))
                        in_doc = true;
                    else
                        continue;
                }
                if (l.startsWith("</DOC>")) {
                    in_doc = false;
                    buff.append(l);
                    break;
                }

                Matcher mat = docno_tag.matcher(l);
                if (mat.find()) {
                    String docno = mat.group(1);
                    Long docNo = Long.parseLong(docno);
                    doc.add(new NumericField("docno", Field.Store.YES,true).setLongValue(docNo));

                }
                Matcher mat1 = text_tag.matcher(l);
                if (mat1.find())
                {
                    String text = mat1.group(1);

                    doc.add(new Field("text", text, Field.Store.YES,Field.Index.ANALYZED));

                }
                Matcher mat2 = id_tag.matcher(l);
                if (mat2.find())
                {
                    String id = mat2.group(1);
                    Long Id = Long.parseLong(id);
                    doc.add(new NumericField("id",Field.Store.YES,true).setLongValue(Id));

                }


                Matcher mat3 = Time_tag.matcher(l);
                if (mat3.find())
                {
                    String tweetTimeTag = mat3.group(0);
                    int indexEndTag = tweetTimeTag.lastIndexOf('<');
                    String tweetTime = tweetTimeTag.substring(11,indexEndTag);

                    doc.add(new Field("tweetTime", tweetTime, Field.Store.YES,Field.Index.NOT_ANALYZED));
                }

                buff.append(l);
            }
            if (buff.length() > 0)
            {

            }
        } catch (IOException e) {
            doc = null;
        }
        return doc;
    }

    @Override
    public void remove() {

    }
}


