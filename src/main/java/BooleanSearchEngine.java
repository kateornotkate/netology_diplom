import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    // мапа-хранилище для слов;
    private Map<String, List<PageEntry>> wordStorage = new HashMap(); // k - слово, v - искомый список;

    // конструктор должен содержать готовый "список-ответ";
    public BooleanSearchEngine(File pdfsDir) throws IOException {

        File[] pdfs = pdfsDir.listFiles();  // сохраняем все пдф-файлы из папки в массив;
        for (File pdf : pdfs) {
            PdfDocument doc = new PdfDocument(new PdfReader(pdf)); // создаем объект каждого пдф-документа;

            for (int i = 0; i < doc.getNumberOfPages(); i++) { // перебираем его страницы;
                String text = PdfTextExtractor.getTextFromPage(doc.getPage(i + 1)); // получаем текст каждой страницы (нумерация начинается с 1);
                String[] words = text.split("\\P{IsAlphabetic}+"); // и разбиваем текст на слова, сохраняя их в массив;

                // для подсчёта частоты слов на каждой странице используем мапу,
                Map<String, Integer> wordFrequency = new HashMap<>(); // k - слово, v - частота;
                for (String word : words) {
//                    if (word.isEmpty()) {
//                        continue;
//                    }
                    word = word.toLowerCase();
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1); // getOrDefault() значение увеличивается по ключу;
                }

                // заполняем основую мапу-хранилище wordStorage ключами и значениями, через обход мапы wordFrequency;
                for (Map.Entry<String, Integer> searchResult : wordFrequency.entrySet()) { // entrySet() множество пар ключ-значений;
                    wordStorage.computeIfAbsent(searchResult.getKey(), value -> new ArrayList<>()) // computeIfAbsent(Key, mappingFunction) если ключ ещё не связан со значением либо отсутвует вовсе, и его нужно передать через функцию;
                            .add(new PageEntry(pdf.getName(), i + 1, searchResult.getValue()));
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> result = wordStorage.get(word); // получаем значение по ключу;
        if (result != null) { // если слово есть,
            Collections.sort(result); // то отсортируем результат поиска;
        }
        return result;
    }
}