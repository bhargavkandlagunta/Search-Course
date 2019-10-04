/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class indexComparison{
	
	private indexComparison() {
		
	}
	
	/** Index all text files under a directory. 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException */
	public static void main(String[] args) throws IOException {
		ArrayList<HashMap<String, String>> documents = new ArrayList();
		String filesPath = "./corpus/corpus";
		File dirFiles = new File(filesPath);
		File[] directoryListing = dirFiles.listFiles();
		
		int j = 0;
		if (directoryListing != null) {
		    for (File child : directoryListing) {
		    	if(child.getName().charAt(0)=='A') {
		    		System.out.println(child.getName() + " " + j);
	            	String string = FileUtils.readFileToString(child);
	            	String[] docs = StringUtils.substringsBetween(string, "<DOC>", "</DOC>");
			    	for (String doc : docs) {
	        			String TEXT = null;
	        			HashMap<String, String> document = new HashMap<String, String>();
	        			if(doc.contains("<TEXT>")){
	            			String[] TEXTS = StringUtils.substringsBetween(doc, "<TEXT>", "</TEXT>");
	            			for (String text : TEXTS) {
	            				TEXT += " "+text;
	            			}
	            			document.put("TEXT", TEXT);
	        			}	        			
	                    documents.add(document);
	        		}
			    	j++;
		    	}
		    }
		} 	
		
		String indexPath = "./index";

		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			iwc.setOpenMode(OpenMode.CREATE);

			IndexWriter writer = new IndexWriter(dir, iwc);
			int docno = 0;
			for (HashMap<String, String> document : documents) {
				System.out.println(docno);
				indexDoc(writer, document);
				docno++;
			}

			writer.close();
			System.out.println("Done ...");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	/** Indexes a single document 
	 * @throws IOException */
	static void indexDoc(IndexWriter writer, HashMap<String, String> document) throws IOException {
		// make a new, empty document
		Document lDoc = new Document();
		
		if(document.get("DOCNO")!=null) lDoc.add(new StringField("DOCNO", document.get("DOCNO"),Field.Store.YES));
		if(document.get("HEAD")!=null) lDoc.add(new TextField("HEAD", document.get("HEAD"), Field.Store.YES));
		if(document.get("BYLINE")!=null) lDoc.add(new TextField("BYLINE", document.get("BYLINE"), Field.Store.YES));
		if(document.get("DATELINE")!=null) lDoc.add(new TextField("DATELINE", document.get("DATELINE"), Field.Store.YES));
		if(document.get("TEXT")!=null) lDoc.add(new TextField("TEXT", document.get("TEXT"), Field.Store.YES));
		writer.addDocument(lDoc);
	}
	
	
}