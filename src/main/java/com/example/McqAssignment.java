package com.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class McqAssignment {
    public static List<Question> readQuestionsFromXML(String filePath) {
        List<Question> questions = new ArrayList<>();
        List<Option> options = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        try {
            File file = new File(filePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList questionNodes = doc.getElementsByTagName("Question");
            for (int i = 0; i < questionNodes.getLength(); i++) {
                Node node = questionNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String value = element.getElementsByTagName("Value").item(0).getTextContent();
                    questions.add(new Question(id, value, null, null));
                }
            }

            NodeList optionNodes = doc.getElementsByTagName("Option");
            for (int i = 0; i < optionNodes.getLength(); i++) {
                Node node = optionNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String valueOne = element.getElementsByTagName("ValueOne").item(0).getTextContent();
                    String valueTwo = element.getElementsByTagName("ValueTwo").item(0).getTextContent();
                    String valueThree = element.getElementsByTagName("ValueThree").item(0).getTextContent();
                    String valueFour = element.getElementsByTagName("ValueFour").item(0).getTextContent();
                    options.add(new Option(id, valueOne, valueTwo, valueThree, valueFour));
                }
            }

            NodeList answerNodes = doc.getElementsByTagName("Answer");
            for (int i = 0; i < answerNodes.getLength(); i++) {
                Node node = answerNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String value = element.getTextContent().split("\\|")[1];
                    answers.add(value);
                }
            }

            for (Question question : questions) {
                for (Option option : options) {
                    if (question.id.equals(option.id)) {
                        question.options = option;
                        break;
                    }
                }
                for (String answer : answers) {
                    if (question.id.equals(answer)) {
                        question.answer = answer;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static void main(String[] args) {
        String filePath = "/Users/paramjotsingh/Desktop/JavaQuestions.xml";  // Path to your XML file
        List<Question> questions = readQuestionsFromXML(filePath);

        // Step 2: Console Input for Name, Mobile Number, and Random Number Generation
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your mobile number: ");
        String mobileNumber = scanner.nextLine();

        Random random = new Random();
        int randomId = 100000 + random.nextInt(900000);
        System.out.println("Your assessment ID is: " + randomId);

        // Step 3: Prompt to Start Assessment
        System.out.print("To start the assessment enter 'yes': ");
        String startAssessment = scanner.nextLine();
        if (!startAssessment.equalsIgnoreCase("yes")) {
            System.out.println("Exiting the assessment.");
            return;
        }

        // Step 4: Display Questions and Get User Input
        List<String> userAnswers = new ArrayList<>();
        for (Question question : questions) {
            System.out.println(question.value);
            System.out.println(question.options.valueOne);
            System.out.println(question.options.valueTwo);
            System.out.println(question.options.valueThree);
            System.out.println(question.options.valueFour);
            System.out.print("Your answer: ");
            String userAnswer = scanner.nextLine();
            userAnswers.add(userAnswer);
        }

        // Step 5: Calculate Marks
        int marks = 0;
        int wrongAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers.get(i).equalsIgnoreCase(questions.get(i).answer)) {
                marks += 2;
            } else {
                wrongAnswers++;
            }
        }

        // Step 6: Apply Negative Marking
        int negativeMarks = 0;
        if (wrongAnswers >= 3 && wrongAnswers <= 5) {
            negativeMarks = -1;
        } else if (wrongAnswers >= 6 && wrongAnswers <= 8) {
            negativeMarks = -2;
        } else if (wrongAnswers >= 9 && wrongAnswers <= 10) {
            negativeMarks = -3;
        }

        int totalMarks = marks + negativeMarks;
        System.out.println("Marks: " + marks);
        System.out.println("Negative Marks: " + negativeMarks);
        System.out.println("Total Marks: " + totalMarks);

        // Step 7: Write Data to Excel Sheet
        writeDataToExcel(randomId, name, mobileNumber, marks, negativeMarks, totalMarks);
    }

    public static void writeDataToExcel(int randomId, String name, String mobileNumber, int marks, int negativeMarks, int totalMarks) {
        String excelFilePath = "/Users/paramjotsingh/Desktop/MCQResult.xlsx";
        try {
            File file = new File(excelFilePath);
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(excelFilePath);
                workbook = new XSSFWorkbook(fileInputStream);
                sheet = workbook.getSheetAt(0);
                fileInputStream.close();
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("MCQ Results");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("RandomId");
                header.createCell(1).setCellValue("Name");
                header.createCell(2).setCellValue("MobileNumber");
                header.createCell(3).setCellValue("Marks");
                header.createCell(4).setCellValue("Negative Marks");
                header.createCell(5).setCellValue("Total Marks");
            }

            int rowCount = sheet.getLastRowNum();

            Row row = sheet.createRow(++rowCount);
            row.createCell(0).setCellValue(String.valueOf(randomId));
            row.createCell(1).setCellValue(String.valueOf(name));
            row.createCell(2).setCellValue(String.valueOf(mobileNumber));
            row.createCell(3).setCellValue(String.valueOf(marks));
            row.createCell(4).setCellValue(String.valueOf(negativeMarks));
            row.createCell(5).setCellValue(String.valueOf(totalMarks));

            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Step 8: Sort Data in Excel Sheet
        sortExcelData(excelFilePath);
    }

    public static void sortExcelData(String excelFilePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(excelFilePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            List<Row> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                rows.add(sheet.getRow(i));
            }

            Collections.sort(rows, (r1, r2) -> {
                int marks1 = Integer.parseInt(r1.getCell(5).getStringCellValue());
                int marks2 = Integer.parseInt(r2.getCell(5).getStringCellValue());
                return Integer.compare(marks2, marks1);
            });

            for (int i = 0; i < rows.size(); i++) {
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
            }

            for (int i = 0; i < rows.size(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < rows.get(i).getLastCellNum(); j++) {
                    row.createCell(j).setCellValue(rows.get(i).getCell(j).getStringCellValue());
                }
            }

            fileInputStream.close();

            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
