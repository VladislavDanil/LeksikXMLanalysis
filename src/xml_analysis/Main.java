package xml_analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Main {
    static XML x;
	public static void main(String[] args) {
		x = new XML();
		StringBuilder q = new StringBuilder();
		Scanner ins = new Scanner(System.in);
        System.out.print("Введите путь до файла: ");
        String name = ins.nextLine();
		File file = new File(name);
		InputStream in = null;
		try {
			in = new FileInputStream(file.getAbsoluteFile());
		} catch (FileNotFoundException e) {
			System.out.println("Неверно введенный путь!");
		}
		    q = XML.xmlAnalysisMethod(in);
			try {
				in.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		name = name.replaceAll(".xml", "_result_analysis.txt");
		File fileOut = new File(name);
		try {
			fileOut.createNewFile();
		} catch (IOException e1) {
			System.out.println("Не удалось создать файл");
		}
		try {
			OutputStreamWriter myfile = 
					new OutputStreamWriter( new FileOutputStream(fileOut.getAbsoluteFile()));
			
			myfile.append(q);
			myfile.close();
		} catch (IOException e1) {
			System.out.println("Не удалось записать в файл");
		}
		try {
			System.out.println("Результат анализа: " + fileOut);
			System.out.println("Для продолжения нажмите Enter");
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
