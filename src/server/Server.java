package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/***
 * Разработать приложение для определения суммы подоходного налога.
 * На клиентской части вводятся заработные платы сотрудников предприятия и передаются северу,
 * а тот в свою очередь возвращает суммы налога.
 * Причем для з/п меньше 100 000 руб. применяется ставка налога 5 %,
 * для з/п от 100 000 до 500 000 – ставка 10 %,
 * для з/п больше 500 000 – ставка 15 %.
 */
public class Server {

    /***
     * Метод вычисляет, какую сумму налога необходимо применить к зарплане
     * @param salary - зарплата сотрудника
     * @return - процент налога
     */
    private static Integer getTaxPercentage(final int salary) {
        if (salary <= 1000) {
            return 5;
        } else if (salary <= 500000) {
            return 10;
        } else return 15;
    }

    /***
     * Метод высчитывает сумму подоходного налога сотрудника
     * @param salary - зарплата сотрудника
     * @return - сумма подохдного налога
     */
    private static Integer calculateEmployeeSalaryTax(final Integer salary) {
        return (salary * getTaxPercentage(salary)) / 100;
    }

    public static void main(String[] args) {
        //объявление объекта класса ServerSocket
        ServerSocket serverSocket = null;
        //объявление объекта класса Socket
        Socket clientAccepted = null;
        //объявление байтового потока ввода
        ObjectInputStream sois = null;
        //объявление байтового потока вывода
        ObjectOutputStream soos = null;

        try {
            System.out.println("Сервер запущен....");
            //создание сокета сервера для //заданного порта
            serverSocket = new ServerSocket(2525);

            //выполнение метода, который //обеспечивает реальное подключение сервера к клиенту
            clientAccepted = serverSocket.accept();
            System.out.println("Соединение с клиентом установлено....");

            //создание потока ввода
            sois = new ObjectInputStream(clientAccepted.getInputStream());
            //создание потока вывода
            soos = new ObjectOutputStream(clientAccepted.getOutputStream());

            //объявление строки и присваивание ей данных потока ввода, представленных
            //в виде строки (передано клиентом)
            String clientMessageRecieved = (String) sois.readObject();

            //выполнениецикла: пока строка не будет равна «quite»
            while (!clientMessageRecieved.equals("quite")) {
                System.out.printf("Сообщение от клиента: '%s'%n", clientMessageRecieved);

                String messageToClient;
                try {
                    //преобразовываем строку, полученную с клинета в тип Ingeger
                    Integer employeeSalary = Integer.parseInt(clientMessageRecieved);
                    //Вычисляем сумму подоходного налога
                    messageToClient = String.valueOf(calculateEmployeeSalaryTax(employeeSalary));
                } catch (NumberFormatException e) {
                    messageToClient = "Вы ввели не правильную зарплату. Пожалуйста ввведите натуральное число";
                }

                //потокувывода присваивается значение строковой переменной (передается клиенту)
                soos.writeObject(messageToClient);
                //строке присваиваются данные потока ввода, представленные в виде строки (передано клиентом)
                clientMessageRecieved = (String) sois.readObject();
            }

            System.out.println("Соедиенение с клиентом разорвано....");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //закрытие потока ввода
                if (sois != null) {
                    sois.close();
                }

                //закрытие потока вывода
                if (soos != null) {
                    soos.close();
                }

                //закрытие сокета, выделенного для клиента
                if (clientAccepted != null) {
                    clientAccepted.close();
                }

                //закрытие сокета сервера
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
