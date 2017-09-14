import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.knallgrau.utils.textcat.TextCategorizer;

import java.io.*;

/**
 * Created by sumanth on 12/10/2016.
 */
public class searcher {
    public static void main(String[] args){
        try {
            FSDirectory direct = FSDirectory.open(new File("C:\\project\\index"));

            IndexReader reader = IndexReader.open(direct);

            FileInputStream inputs = new FileInputStream("C:\\project\\adhoc\\queries.txt");
            IndexSearcher searcher = new IndexSearcher(reader);
            DataInputStream l = new DataInputStream(inputs);
            BufferedReader l1 = new BufferedReader(new InputStreamReader(l));
            EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_36);
            QueryParser query_parser = new QueryParser(Version.LUCENE_36,"text", analyzer);

            FileWriter f = new FileWriter("C:\\project\\adhoc\\data.txt");
            BufferedWriter out= new BufferedWriter(f);
            String queryid = "";
            String current_query = "";
            String str_Line;
            int TotalDocs = 0;



            while ((str_Line = l1.readLine()) != null) {
                if(str_Line.contains("<num>"))
                {

                    int i1 = str_Line.indexOf("B");
                    int i2 = str_Line.indexOf("</num>");
                    queryid = str_Line.substring(i1+1,i2);
                    queryid=queryid.trim();
                    if (queryid.startsWith("00")==true)
                    {
                        queryid= queryid.substring(2);

                    }
                    if (queryid.startsWith("0")==true)
                    {
                        queryid= queryid.substring(1);

                    }

                }
                if(str_Line.contains("<title>"))
                {
                    int lower = str_Line.indexOf(" ", 7);
                    int upper = str_Line.indexOf("</title>");
                    current_query = str_Line.substring(lower,upper);
                    System.out.println(current_query);
                }
                if (str_Line.contains("<query>"))
                {
                    int lowerq = str_Line.indexOf(" ", 7);
                    int upperq = str_Line.indexOf("</query>");
                    current_query = str_Line.substring(lowerq,upperq);
                    System.out.println(current_query);
                }
                {

                }
                if (str_Line.contains("<querytweettime>"))
                {
                    int start_ind = str_Line.indexOf(" ",16);
                    int end_ind = str_Line.indexOf("</querytweettime>");
                    String tweetTiming = str_Line.substring(start_ind,end_ind);
                    tweetTiming = tweetTiming.trim();
                    Long docLimit = Long.parseLong(tweetTiming);
                    Long lowerLimit = Long.parseLong("1");
                    Filter rangeFilter = NumericRangeFilter.newLongRange("docno", lowerLimit, docLimit,true,true);

                    Query q = query_parser.parse(current_query);

                    TopDocs output = searcher.search(q,rangeFilter,3000);

                    System.out.println(output.totalHits);
                    int count=0;


                    for(int i=0;i<Math.min(3000,output.totalHits);i++)
                    {

                        float tfidf = output.scoreDocs[i].score;

                        Document doc = reader.document(output.scoreDocs[i].doc);

                        String cat = "";
                        TextCategorizer TC = new TextCategorizer();
                        cat = TC.categorize(doc.get("text"));
                        if (cat.equalsIgnoreCase("english"))
                        {

                            NumericField docno = (NumericField)doc.getFieldable("docno");
                            Long DocNo = (Long)docno.getNumericValue();
                            count++;

                            out.write(queryid+" "+"0"+" "+DocNo+" "+count+" "+tfidf+" "+"0"+"\n");

                        }
                    }
                }

            }



            System.out.println(TotalDocs);
            out.close();
            l1.close();

        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

