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
            System.out.println("Server starting....");
            //создание сокета сервера для //заданного порта
            serverSocket = new ServerSocket(2525);

            //выполнение метода, который //обеспечивает реальное подключение сервера к клиенту
            clientAccepted = serverSocket.accept();
            System.out.println("connection established....");

            //создание потока ввода
            sois = new ObjectInputStream(clientAccepted.getInputStream());
            //создание потока вывода
            soos = new ObjectOutputStream(clientAccepted.getOutputStream());

            //объявление строки и присваивание ей данных потока ввода, представленных
            //в виде строки (передано клиентом)
            String clientMessageRecieved = (String) sois.readObject();

            //выполнениецикла: пока строка не будет равна «quite»
            while (!clientMessageRecieved.equals("quite")) {
                System.out.println("message received: '" + clientMessageRecieved + "'");
                //приведение символов строки к верхнему регистру
                clientMessageRecieved = clientMessageRecieved.toUpperCase();
                //потокувывода присваивается значение строковой переменной (передается клиенту)
                soos.writeObject(clientMessageRecieved);
                //строке присваиваются данные потока ввода, представленные в виде строки (передано клиентом)
                clientMessageRecieved = (String) sois.readObject();
            }

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
