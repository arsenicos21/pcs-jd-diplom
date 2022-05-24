import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    HashMap<String, List<PageEntry>> pageEntryHashMap = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException, NullPointerException {
        for (File pdf : Objects.requireNonNull(pdfsDir.listFiles(), "Указан неверный путь к файлам")) {
            PdfDocument doc = new PdfDocument(new PdfReader(pdf));
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                int currentPage = i + 1;
                String text = PdfTextExtractor.getTextFromPage(doc.getPage(currentPage));
                String[] words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> wordFrequency = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    wordFrequency.put(word.toLowerCase(), wordFrequency.getOrDefault(word, 0) + 1);
                }

                for (var entry : wordFrequency.entrySet()) {
                    List<PageEntry> wordSearchingResult;
                    if (pageEntryHashMap.containsKey(entry.getKey())) {
                        wordSearchingResult = pageEntryHashMap.get(entry.getKey());
                    } else {
                        wordSearchingResult = new ArrayList<>();
                    }
                    wordSearchingResult.add(new PageEntry(pdf.getName(), currentPage, entry.getValue()));
                    Collections.sort(wordSearchingResult, Collections.reverseOrder());
                    pageEntryHashMap.put(entry.getKey(), wordSearchingResult);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        if ((pageEntryHashMap.get(word)) != null) {
            return pageEntryHashMap.get(word);
        } else {
            System.out.println("Такое слово не найдено");
            return null;
        }
    }
}