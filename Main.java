import functions.*;
import functions.basic.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    
    private static void test1() {
        System.out.println("-".repeat(80));
        System.out.println("Тестирование работы всех написанных классов");
        System.out.println("-".repeat(80));
        
        testBasicFunctionsSinCos();
        testTabulatedAnalogues();
        testSumOfSquares();
        testFileOperationsExponential();
        testFileOperationsLogarithm();
        compareStorageFormats();
    }
    
    private static void test2() {
        System.out.println("=".repeat(80));
        System.out.println("ЗАДАНИЕ 9: ТЕСТИРОВАНИЕ СЕРИАЛИЗАЦИИ");
        System.out.println("=".repeat(80));
        
        testSerializableApproach();
        testExternalizableApproach();
        compareSerializationMethods();
    }
    
    private static void testBasicFunctionsSinCos() {
        System.out.println("\n1. Тестирование базовых функций Sin и Cos");
        System.out.println("-".repeat(50));
        
        Function sin = new Sin();
        Function cos = new Cos();
        
        double from = 0;
        double to = Math.PI;
        double step = 0.1;
        
        System.out.println("Значения sin(x) на [0, π] с шагом 0.1:");
        System.out.printf("%-8s %-10s%n", "x", "sin(x)");
        for (double x = from; x <= to + 1e-10; x += step) {
            System.out.printf("%-8.3f %-10.6f%n", x, sin.getFunctionValue(x));
        }
        
        System.out.println("\nЗначения cos(x) на [0, π] с шагом 0.1:");
        System.out.printf("%-8s %-10s%n", "x", "cos(x)");
        for (double x = from; x <= to + 1e-10; x += step) {
            System.out.printf("%-8.3f %-10.6f%n", x, cos.getFunctionValue(x));
        }
    }
    
    private static void testTabulatedAnalogues() {
        System.out.println("\n\n2. Табулированные аналоги Sin и Cos (10 точек)");
        System.out.println("-".repeat(55));
        
        Function sin = new Sin();
        Function cos = new Cos();
        
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        
        double from = 0;
        double to = Math.PI;
        double step = 0.1;
        
        System.out.println("Сравнение sin(x) и табулированного sin(x) (10 точек):");
        System.out.printf("%-8s %-12s %-12s %-12s%n", "x", "sin(x)", "tab_sin(x)", "погрешность");
        for (double x = from; x <= to + 1e-10; x += step) {
            double exact = sin.getFunctionValue(x);
            double approx = tabulatedSin.getFunctionValue(x);
            double error = Math.abs(exact - approx);
            System.out.printf("%-8.3f %-12.6f %-12.6f %-12.6f%n", x, exact, approx, error);
        }
        
        System.out.println("\nСравнение cos(x) и табулированного cos(x) (10 точек):");
        System.out.printf("%-8s %-12s %-12s %-12s%n", "x", "cos(x)", "tab_cos(x)", "погрешность");
        for (double x = from; x <= to + 1e-10; x += step) {
            double exact = cos.getFunctionValue(x);
            double approx = tabulatedCos.getFunctionValue(x);
            double error = Math.abs(exact - approx);
            System.out.printf("%-8.3f %-12.6f %-12.6f %-12.6f%n", x, exact, approx, error);
        }
    }
    
    private static void testSumOfSquares() {
        System.out.println("\n\n3. Сумма квадратов табулированных функций");
        System.out.println("-".repeat(45));
        
        int[] pointsCounts = {5, 10, 20};
        
        for (int pointsCount : pointsCounts) {
            System.out.println("\nКоличество точек в табулированных функциях: " + pointsCount);
            
            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, pointsCount);
            TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, pointsCount);
            
            Function sumOfSquares = Functions.sum(
                Functions.power(tabulatedSin, 2),
                Functions.power(tabulatedCos, 2)
            );
            
            double from = 0;
            double to = Math.PI;
            double step = 0.1;
            
            System.out.printf("%-8s %-15s%n", "x", "sin²(x)+cos²(x)");
            for (double x = from; x <= to + 1e-10; x += step) {
                double value = sumOfSquares.getFunctionValue(x);
                double deviation = Math.abs(value - 1.0);
                System.out.printf("%-8.3f %-15.8f (отклонение: %.8f)%n", x, value, deviation);
            }
        }
    }
    
    private static void testFileOperationsExponential() {
        System.out.println("\n\n4. Работа с текстовыми файлами (экспонента)");
        System.out.println("-".repeat(50));
        
        String filename = "exponential_function.txt";
        
        try {
            TabulatedFunction expFunction = TabulatedFunctions.tabulate(new Exp(), 0, 10, 11);
            
            try (FileWriter writer = new FileWriter(filename)) {
                TabulatedFunctions.writeTabulatedFunction(expFunction, writer);
            }
            System.out.println("Табулированная экспонента записана в файл: " + filename);
            
            TabulatedFunction readFunction;
            try (FileReader reader = new FileReader(filename)) {
                readFunction = TabulatedFunctions.readTabulatedFunction(reader);
            }
            System.out.println("Функция прочитана из текстового файла");
            
            System.out.println("\nСравнение исходной и прочитанной функции:");
            System.out.printf("%-8s %-15s %-15s %-15s%n", "x", "исходная", "прочитанная", "разница");
            for (int i = 0; i < expFunction.getPointsCount(); i++) {
                double x = expFunction.getPointX(i);
                double original = expFunction.getPointY(i);
                double read = readFunction.getPointY(i);
                double difference = Math.abs(original - read);
                System.out.printf("%-8.1f %-15.8f %-15.8f %-15.8f%n", x, original, read, difference);
            }
            
            System.out.println("\nСодержимое текстового файла:");
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            System.out.println(content);
            
            Files.deleteIfExists(Paths.get(filename));
            System.out.println("Временный файл удален");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void testFileOperationsLogarithm() {
        System.out.println("\n\n5. Работа с бинарными файлами (логарифм)");
        System.out.println("-".repeat(50));
        
        String filename = "logarithm_function.dat";
        
        try {
            TabulatedFunction logFunction = TabulatedFunctions.tabulate(new Log(Math.E), 1, 10, 11);
            
            try (FileOutputStream out = new FileOutputStream(filename)) {
                TabulatedFunctions.outputTabulatedFunction(logFunction, out);
            }
            System.out.println("Табулированный логарифм записан в файл: " + filename);
            
            TabulatedFunction readFunction;
            try (FileInputStream in = new FileInputStream(filename)) {
                readFunction = TabulatedFunctions.inputTabulatedFunction(in);
            }
            System.out.println("Функция прочитана из бинарного файла");
            
            System.out.println("\nСравнение исходной и прочитанной функции:");
            System.out.printf("%-8s %-15s %-15s %-15s%n", "x", "исходная", "прочитанная", "разница");
            for (int i = 0; i < logFunction.getPointsCount(); i++) {
                double x = logFunction.getPointX(i);
                double original = logFunction.getPointY(i);
                double read = readFunction.getPointY(i);
                double difference = Math.abs(original - read);
                System.out.printf("%-8.1f %-15.8f %-15.8f %-15.8f%n", x, original, read, difference);
            }
            
            File file = new File(filename);
            System.out.println("\nРазмер бинарного файла: " + file.length() + " байт");
            
            Files.deleteIfExists(Paths.get(filename));
            System.out.println("Временный файл удален");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void compareStorageFormats() {
        System.out.println("\n\n6. Сравнение орматов хранения");
        System.out.println("-".repeat(35));
        
        String textFile = "comparison_text.txt";
        String binaryFile = "comparison_binary.dat";
        
        try {
            TabulatedFunction testFunction = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 5);
            
            try (FileWriter writer = new FileWriter(textFile)) {
                TabulatedFunctions.writeTabulatedFunction(testFunction, writer);
            }
            
            try (FileOutputStream out = new FileOutputStream(binaryFile)) {
                TabulatedFunctions.outputTabulatedFunction(testFunction, out);
            }
            
            File text = new File(textFile);
            File binary = new File(binaryFile);
            
            System.out.println("Размер текстового файла: " + text.length() + " байт");
            System.out.println("Размер бинарного файла: " + binary.length() + " байт");
            System.out.println("Бинарный файл занимает " + 
                String.format("%.1f", (double)binary.length() / text.length() * 100) + "% от текстового");
            
            System.out.println("\nСодержимое текстового файла:");
            System.out.println(new String(Files.readAllBytes(Paths.get(textFile))));
            
            Files.deleteIfExists(Paths.get(textFile));
            Files.deleteIfExists(Paths.get(binaryFile));
            System.out.println("\nВременные файлы удалены");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void testSerializableApproach() {
    System.out.println("\n1. СЕРИАЛИЗАЦИЯ ЧЕРЕЗ Serializable");
    System.out.println("=".repeat(45));
    
    String filename = "serializable_function.ser";
    
    try {
        // Создаем функцию, которая использует ТОЛЬКО Serializable
        TabulatedFunction originalFunction = createSerializableOnlyFunction();
        
        System.out.println("Функция создана:");
        System.out.println("  Тип: " + originalFunction.getClass().getSimpleName());
        System.out.println("  Количество точек: " + originalFunction.getPointsCount());
        System.out.println("  Область определения: [" + originalFunction.getLeftDomainBorder() + ", " + originalFunction.getRightDomainBorder() + "]");
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(originalFunction);
        }
        System.out.println("✓ Сериализована в файл: " + filename);
        
        TabulatedFunction deserializedFunction;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFunction = (TabulatedFunction) in.readObject();
        }
        System.out.println("✓ Десериализована из файла");
        System.out.println("  Тип после десериализации: " + deserializedFunction.getClass().getSimpleName());
        
        System.out.println("\nСравнение исходной и десериализованной функции:");
        System.out.printf("%-8s %-12s %-12s %-10s%n", "x", "исходная", "десериал.", "разница");
        boolean allMatch = true;
        for (int i = 0; i < originalFunction.getPointsCount(); i++) {
            double x = originalFunction.getPointX(i);
            double original = originalFunction.getPointY(i);
            double deserialized = deserializedFunction.getPointY(i);
            double diff = Math.abs(original - deserialized);
            System.out.printf("%-8.1f %-12.6f %-12.6f %-10.6f", x, original, deserialized, diff);
            if (diff > 1e-10) {
                System.out.print(" ✗");
                allMatch = false;
            } else {
                System.out.print(" ✓");
            }
            System.out.println();
        }
        
        File file = new File(filename);
        System.out.println("\nРазмер файла Serializable: " + file.length() + " байт");
        
        Files.deleteIfExists(Paths.get(filename));
        System.out.println("✓ Временный файл удален");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}

private static void testExternalizableApproach() {
    System.out.println("\n\n2. СЕРИАЛИЗАЦИЯ ЧЕРЕЗ Externalizable");
    System.out.println("=".repeat(50));
    
    String filename = "externalizable_function.ser";
    
    try {
        // Создаем функцию, которая использует Externalizable
        TabulatedFunction originalFunction = createExternalizableFunction();
        
        System.out.println("Функция создана:");
        System.out.println("  Тип: " + originalFunction.getClass().getSimpleName());
        System.out.println("  Количество точек: " + originalFunction.getPointsCount());
        System.out.println("  Область определения: [" + originalFunction.getLeftDomainBorder() + ", " + originalFunction.getRightDomainBorder() + "]");
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(originalFunction);
        }
        System.out.println("✓ Сериализована в файл: " + filename);
        
        TabulatedFunction deserializedFunction;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFunction = (TabulatedFunction) in.readObject();
        }
        System.out.println("✓ Десериализована из файла");
        System.out.println("  Тип после десериализации: " + deserializedFunction.getClass().getSimpleName());
        
        System.out.println("\nСравнение исходной и десериализованной функции:");
        System.out.printf("%-8s %-12s %-12s %-10s%n", "x", "исходная", "десериал.", "разница");
        boolean allMatch = true;
        for (int i = 0; i < originalFunction.getPointsCount(); i++) {
            double x = originalFunction.getPointX(i);
            double original = originalFunction.getPointY(i);
            double deserialized = deserializedFunction.getPointY(i);
            double diff = Math.abs(original - deserialized);
            System.out.printf("%-8.1f %-12.6f %-12.6f %-10.6f", x, original, deserialized, diff);
            if (diff > 1e-10) {
                System.out.print(" ✗");
                allMatch = false;
            } else {
                System.out.print(" ✓");
            }
            System.out.println();
        }
        
        File file = new File(filename);
        System.out.println("\nРазмер файла Externalizable: " + file.length() + " байт");
        
        Files.deleteIfExists(Paths.get(filename));
        System.out.println("✓ Временный файл удален");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}

// Функция, которая использует ТОЛЬКО Serializable (не реализует Externalizable)
private static TabulatedFunction createSerializableOnlyFunction() {
    // Создаем LinkedListTabulatedFunction через массив FunctionPoint
    FunctionPoint[] points = new FunctionPoint[11];
    Function composition = Functions.composition(new Exp(), new Log(Math.E));
    
    for (int i = 0; i < 11; i++) {
        double x = i;
        double y = composition.getFunctionValue(x);
        points[i] = new FunctionPoint(x, y);
    }
    
    return new LinkedListTabulatedFunction(points);
}

// Функция, которая использует Externalizable
private static TabulatedFunction createExternalizableFunction() {
    // Создаем ArrayTabulatedFunction через два массива
    double[] xValues = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    double[] yValues = new double[11];
    Function composition = Functions.composition(new Exp(), new Log(Math.E));
    
    for (int i = 0; i < 11; i++) {
        yValues[i] = composition.getFunctionValue(xValues[i]);
    }
    
    return new ArrayTabulatedFunction(xValues, yValues);
}

private static void compareSerializationMethods() {
    System.out.println("\n\n3. СРАВНЕНИЕ МЕТОДОВ СЕРИАЛИЗАЦИИ");
    System.out.println("=".repeat(45));
    
    String serializableFile = "comparison_serializable.ser";
    String externalizableFile = "comparison_externalizable.ser";
    
    try {
        // Serializable-only функция (LinkedListTabulatedFunction)
        TabulatedFunction serializableFunc = createSerializableOnlyFunction();
        // Externalizable функция (ArrayTabulatedFunction)
        TabulatedFunction externalizableFunc = createExternalizableFunction();
        
        // Сериализация через стандартный Serializable
        try (ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream(serializableFile))) {
            out1.writeObject(serializableFunc);
        }
        
        // Сериализация через Externalizable
        try (ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(externalizableFile))) {
            out2.writeObject(externalizableFunc);
        }
        
        File serializable = new File(serializableFile);
        File externalizable = new File(externalizableFile);
        
        long serializableSize = serializable.length();
        long externalizableSize = externalizable.length();
        long difference = Math.abs(serializableSize - externalizableSize);
        double percent = (1 - (double)externalizableSize / serializableSize) * 100;
        
        System.out.println("Размеры файлов:");
        System.out.println("  Serializable (LinkedList): " + serializableSize + " байт");
        System.out.println("  Externalizable (Array): " + externalizableSize + " байт");
        System.out.println("  Разница: " + difference + " байт");
        System.out.printf("  Экономия: %.1f%%\n", Math.abs(percent));
        
        System.out.println("\nАнализ подходов:");
        System.out.println("  Serializable: автоматическая сериализация всех полей");
        System.out.println("  Externalizable: ручное управление, только нужные данные");
        
        Files.deleteIfExists(Paths.get(serializableFile));
        Files.deleteIfExists(Paths.get(externalizableFile));
        System.out.println("\n✓ Временные файлы удалены");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private static void test3() {
        System.out.println("=".repeat(80));
        System.out.println("ЗАДАНИЕ 5: ТЕСТИРОВАНИЕ toString(), equals(), hashCode(), clone()");
        System.out.println("=".repeat(80));
        
        testToString();
        testEquals();
        testHashCode();
        testClone();
    }
    
    private static void testToString() {
        System.out.println("\n1. ТЕСТИРОВАНИЕ toString()");
        System.out.println("-".repeat(40));
        
        // Создаем тестовые функции
        FunctionPoint[] points = {
            new FunctionPoint(0.0, 1.0),
            new FunctionPoint(1.0, 3.0),
            new FunctionPoint(2.0, 5.0),
            new FunctionPoint(3.0, 7.0)
        };
        
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(points);
        LinkedListTabulatedFunction listFunc = new LinkedListTabulatedFunction(points);
        
        System.out.println("ArrayTabulatedFunction.toString():");
        System.out.println("  " + arrayFunc.toString());
        
        System.out.println("LinkedListTabulatedFunction.toString():");
        System.out.println("  " + listFunc.toString());
        
        // Тестирование с разным количеством точек
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};
        ArrayTabulatedFunction shortArrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        
        System.out.println("Короткая функция (3 точки):");
        System.out.println("  " + shortArrayFunc.toString());
    }
    
    private static void testEquals() {
        System.out.println("\n\n2. ТЕСТИРОВАНИЕ equals()");
        System.out.println("-".repeat(40));
        
        // Создаем идентичные функции
        FunctionPoint[] points1 = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0)
        };
        
        FunctionPoint[] points2 = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0)
        };
        
        FunctionPoint[] differentPoints = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 2.0), // Разное значение Y
            new FunctionPoint(2.0, 4.0)
        };
        
        ArrayTabulatedFunction array1 = new ArrayTabulatedFunction(points1);
        ArrayTabulatedFunction array2 = new ArrayTabulatedFunction(points2);
        ArrayTabulatedFunction arrayDifferent = new ArrayTabulatedFunction(differentPoints);
        
        LinkedListTabulatedFunction list1 = new LinkedListTabulatedFunction(points1);
        LinkedListTabulatedFunction list2 = new LinkedListTabulatedFunction(points2);
        LinkedListTabulatedFunction listDifferent = new LinkedListTabulatedFunction(differentPoints);
        
        System.out.println("Сравнение одинаковых ArrayTabulatedFunction:");
        System.out.println("  array1.equals(array2): " + array1.equals(array2));
        System.out.println("  array2.equals(array1): " + array2.equals(array1));
        
        System.out.println("Сравнение одинаковых LinkedListTabulatedFunction:");
        System.out.println("  list1.equals(list2): " + list1.equals(list2));
        System.out.println("  list2.equals(list1): " + list2.equals(list1));
        
        System.out.println("Сравнение разных ArrayTabulatedFunction:");
        System.out.println("  array1.equals(arrayDifferent): " + array1.equals(arrayDifferent));
        
        System.out.println("Сравнение ArrayTabulatedFunction и LinkedListTabulatedFunction:");
        System.out.println("  array1.equals(list1): " + array1.equals(list1));
        System.out.println("  list1.equals(array1): " + list1.equals(array1));
        
        System.out.println("Сравнение с разным количеством точек:");
        double[] shortX = {0.0, 2.0};
        double[] shortY = {0.0, 4.0};
        ArrayTabulatedFunction shortArray = new ArrayTabulatedFunction(shortX, shortY);
        System.out.println("  array1.equals(shortArray): " + array1.equals(shortArray));
        
        System.out.println("Сравнение с null:");
        System.out.println("  array1.equals(null): " + array1.equals(null));
        
        System.out.println("Сравнение с другим типом объекта:");
        System.out.println("  array1.equals(\"строка\"): " + array1.equals("строка"));
    }
    
    private static void testHashCode() {
        System.out.println("\n\n3. ТЕСТИРОВАНИЕ hashCode()");
        System.out.println("-".repeat(40));
        
        // Создаем идентичные функции
        FunctionPoint[] points = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0),
            new FunctionPoint(3.0, 9.0)
        };
        
        ArrayTabulatedFunction array1 = new ArrayTabulatedFunction(points);
        ArrayTabulatedFunction array2 = new ArrayTabulatedFunction(points);
        LinkedListTabulatedFunction list1 = new LinkedListTabulatedFunction(points);
        LinkedListTabulatedFunction list2 = new LinkedListTabulatedFunction(points);
        
        System.out.println("Хэш-коды одинаковых ArrayTabulatedFunction:");
        int hashArray1 = array1.hashCode();
        int hashArray2 = array2.hashCode();
        System.out.println("  array1.hashCode(): " + hashArray1);
        System.out.println("  array2.hashCode(): " + hashArray2);
        System.out.println("  Совпадают: " + (hashArray1 == hashArray2));
        
        System.out.println("Хэш-коды одинаковых LinkedListTabulatedFunction:");
        int hashList1 = list1.hashCode();
        int hashList2 = list2.hashCode();
        System.out.println("  list1.hashCode(): " + hashList1);
        System.out.println("  list2.hashCode(): " + hashList2);
        System.out.println("  Совпадают: " + (hashList1 == hashList2));
        
        System.out.println("Хэш-коды Array и LinkedList с одинаковыми точками:");
        System.out.println("  array1.hashCode(): " + hashArray1);
        System.out.println("  list1.hashCode(): " + hashList1);
        System.out.println("  Совпадают: " + (hashArray1 == hashList1));
        
        // Тестирование изменения объекта
        System.out.println("\nТестирование изменения объекта:");
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(points);
        int originalHash = original.hashCode();
        System.out.println("  Исходный hashCode: " + originalHash);
        
        // Незначительно изменяем одну координату
        try {
            original.setPointY(1, 1.001); // Изменяем Y на 0.001
            int modifiedHash = original.hashCode();
            System.out.println("  После изменения Y[1] на 0.001: " + modifiedHash);
            System.out.println("  Хэш-код изменился: " + (originalHash != modifiedHash));
            System.out.println("  Разница: " + Math.abs(originalHash - modifiedHash));
        } catch (Exception e) {
            System.out.println("  Ошибка при изменении: " + e.getMessage());
        }
        
        // Тестирование с разным количеством точек
        System.out.println("\nТестирование с разным количеством точек:");
        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func3points = new ArrayTabulatedFunction(xValues1, yValues1);
        
        double[] xValues2 = {0.0, 1.0};
        double[] yValues2 = {0.0, 1.0};
        ArrayTabulatedFunction func2points = new ArrayTabulatedFunction(xValues2, yValues2);
        
        System.out.println("  func3points.hashCode(): " + func3points.hashCode());
        System.out.println("  func2points.hashCode(): " + func2points.hashCode());
        System.out.println("  Совпадают: " + (func3points.hashCode() == func2points.hashCode()));
    }
    
    private static void testClone() {
        System.out.println("\n\n4. ТЕСТИРОВАНИЕ clone()");
        System.out.println("-".repeat(40));
        
        testArrayTabulatedFunctionClone();
        testLinkedListTabulatedFunctionClone();
        testCrossClassClone();
    }
    
    private static void testArrayTabulatedFunctionClone() {
        System.out.println("ArrayTabulatedFunction.clone():");
        
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        
        ArrayTabulatedFunction clone = (ArrayTabulatedFunction) original.clone();
        
        System.out.println("  Исходная функция: " + original.toString());
        System.out.println("  Клонированная функция: " + clone.toString());
        System.out.println("  equals(): " + original.equals(clone));
        System.out.println("  == : " + (original == clone));
        
        // Изменяем исходный объект
        try {
            original.setPointY(1, 999.0); // Меняем Y во второй точке
            original.setPointX(2, 2.5);   // Меняем X в третьей точке
            
            System.out.println("  После изменения исходной функции:");
            System.out.println("    Исходная: " + original.toString());
            System.out.println("    Клон: " + clone.toString());
            System.out.println("    Клон не изменился: " + 
                (clone.getPointY(1) == 1.0 && clone.getPointX(2) == 2.0));
        } catch (Exception e) {
            System.out.println("  Ошибка при изменении: " + e.getMessage());
        }
    }
    
    private static void testLinkedListTabulatedFunctionClone() {
        System.out.println("\nLinkedListTabulatedFunction.clone():");
        
        FunctionPoint[] points = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0),
            new FunctionPoint(3.0, 9.0)
        };
        
        LinkedListTabulatedFunction original = new LinkedListTabulatedFunction(points);
        LinkedListTabulatedFunction clone = (LinkedListTabulatedFunction) original.clone();
        
        System.out.println("  Исходная функция: " + original.toString());
        System.out.println("  Клонированная функция: " + clone.toString());
        System.out.println("  equals(): " + original.equals(clone));
        System.out.println("  == : " + (original == clone));
        
        // Изменяем исходный объект
        try {
            original.setPointY(1, 888.0); // Меняем Y во второй точке
            original.deletePoint(2);       // Удаляем третью точку
            
            System.out.println("  После изменения исходной функции:");
            System.out.println("    Исходная: " + original.toString());
            System.out.println("    Клон: " + clone.toString());
            System.out.println("    Клон не изменился: " + 
                (clone.getPointY(1) == 1.0 && clone.getPointsCount() == 4));
        } catch (Exception e) {
            System.out.println("  Ошибка при изменении: " + e.getMessage());
        }
    }
    
    private static void testCrossClassClone() {
        System.out.println("\nКлонирование через интерфейс TabulatedFunction:");
        
        FunctionPoint[] points = {
            new FunctionPoint(0.0, 10.0),
            new FunctionPoint(1.0, 20.0),
            new FunctionPoint(2.0, 30.0)
        };
        
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(points);
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(points);
        
        TabulatedFunction arrayClone = (TabulatedFunction) arrayFunc.clone();
        TabulatedFunction listClone = (TabulatedFunction) listFunc.clone();
        
        System.out.println("  ArrayTabulatedFunction.clone() тип: " + arrayClone.getClass().getSimpleName());
        System.out.println("  LinkedListTabulatedFunction.clone() тип: " + listClone.getClass().getSimpleName());
        System.out.println("  Array clone equals original: " + arrayFunc.equals(arrayClone));
        System.out.println("  List clone equals original: " + listFunc.equals(listClone));
        
        // Проверка глубокого клонирования через интерфейс
        try {
            arrayFunc.setPointY(0, 100.0);
            listFunc.setPointY(0, 200.0);
            
            System.out.println("  После изменения исходных функций:");
            System.out.println("    Array clone Y[0]: " + arrayClone.getPointY(0) + " (должно быть 10.0)");
            System.out.println("    List clone Y[0]: " + listClone.getPointY(0) + " (должно быть 10.0)");
            System.out.println("    Глубокое клонирование работает: " + 
                (arrayClone.getPointY(0) == 10.0 && listClone.getPointY(0) == 10.0));
        } catch (Exception e) {
            System.out.println("  Ошибка при проверке глубокого клонирования: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        test1();
        test2();
        test3();
    }
}