package xml_analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Main {
    static XML x;
	public static void main(String[] args) {
		x = new XML();
		StringBuilder q = new StringBuilder();
		String string = null;
		String line;
		Scanner ins = new Scanner(System.in);
        System.out.print("������� ���� �� �����: ");
        String name = ins.nextLine();
		File file = new File(name);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
		} catch (FileNotFoundException e) {
			System.out.println("������� ��������� ����!");
		}
		try {
			while ((line = in.readLine()) != null){
				string = string + line;
			}
			in.close();
			q = XML.xmlAnalysisMethod(string);
		} catch (IOException e) {
			System.out.println("������ ����� ������!");
		} catch (NullPointerException e){
			System.out.println("������ ������ �����");
		}
		name = name.replaceAll(".xml", "_result_analysis.txt");
		File fileOut = new File(name);
		try {
			fileOut.createNewFile();
		} catch (IOException e1) {
			System.out.println("�� ������� ������� ����");
		}
		try {
			OutputStreamWriter myfile = 
					new OutputStreamWriter( new FileOutputStream(fileOut.getAbsoluteFile()));
			
			myfile.append(q);
			myfile.close();
		} catch (IOException e1) {
			System.out.println("�� ������� �������� � ����");
		}
		try {
			System.out.println("��������� �������: " + fileOut);
			System.out.println("��� ����������� ������� Enter");
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
