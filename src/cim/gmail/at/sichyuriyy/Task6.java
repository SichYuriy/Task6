package cim.gmail.at.sichyuriyy;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Task6 {

	public static void main(String[] args) throws IOException {

		String inputFile;
		String inputText;
		String outputFile;
		int countOfThreads;
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the input file");
		inputFile = in.next();
		System.out.println("Enter the output file");
		outputFile = in.next();
		PrintWriter out = new PrintWriter(outputFile);
		System.out.println("Enter the count of threads");
		countOfThreads = in.nextInt();
		in.close();
		Scanner inputStream = new Scanner(new FileReader(inputFile));
		inputStream.useDelimiter("\\A");
		inputText = inputStream.next();
		inputStream.close();
		inputText = inputText.toLowerCase();
		Frequency word = new Frequency(inputText, countOfThreads);
		word.countFrequency();
		out.println(word);
		out.close();

	}

}

class Frequency {
	String text;
	String[] separetedText;
	HashSet<String> words = new HashSet<String>();
	ArrayList<Future<Integer>> wordF = new ArrayList<Future<Integer>>();
	Vector<String> strWords = new Vector<String>();

	int countOfThreads;

	Frequency(String inputText, int countOfThreads) {
		text = inputText;
		this.countOfThreads = countOfThreads;
		separetedText = text.split("[ \\.,;:!\\?\n\t\r]{1,}");
		for (int i = 0; i < separetedText.length; i++) {
			if (separetedText[i] == "")
				continue;
			if (!words.contains(separetedText[i])) {
				strWords.addElement(separetedText[i]);
				words.add(separetedText[i]);
			}
		}
	}

	public void countFrequency() {
		ExecutorService exec = Executors.newFixedThreadPool(countOfThreads);
		for (int i = 0; i < strWords.size(); i++) {
			wordF.add(exec.submit(new FindWord(strWords.get(i), separetedText)));
		}
		exec.shutdown();
		try {
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	public void getFrequency() {
		for (int i = 0; i < strWords.size(); i++) {
			try {
				System.out.println(strWords.get(i) + " " + wordF.get(i).get());
			} catch (InterruptedException e) {
				System.out.println(e);
			} catch (ExecutionException e) {
				System.out.println(e);
			}
		}
	}

	public String toString() {
		String answer = new String("");
		for (int i = 0; i < strWords.size(); i++) {
			try {
				answer += strWords.get(i) + " " + wordF.get(i).get() + "\n";
			} catch (InterruptedException e) {
				System.out.println(e);
			} catch (ExecutionException e) {
				System.out.println(e);
			}
		}
		return answer;
	}
}

class FindWord implements Callable<Integer> {
	String[] text;
	String word;

	public FindWord(String word, String[] inputText) {
		this.word = word;
		text = inputText;
	}

	public Integer call() {
		Integer frequency = 0;
		for (int i = 0; i < text.length; i++) {
			if (word.equals(text[i]))
				frequency++;
		}

		return frequency;
	}

}