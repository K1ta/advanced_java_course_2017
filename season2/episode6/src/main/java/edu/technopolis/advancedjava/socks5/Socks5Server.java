package edu.technopolis.advancedjava.socks5;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class Socks5Server {
    private static int port = 1080;
    private static String host = "127.0.0.1";

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isConnectable()) {
                        connect(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    принять запрос на подключение от клиента
     */
    private static void accept(SelectionKey key) throws IOException {
        //принимаем запрос
        SocketChannel newChannel = ((ServerSocketChannel) key.channel()).accept();
        //делаем неблокирующим
        newChannel.configureBlocking(false);
        //регестрируем в селекторе
        newChannel.register(key.selector(), SelectionKey.OP_READ);
    }

    /*
    соединение с другим сервером завершено
     */
    private static void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        Attachment attachment = (Attachment) key.attachment();
        //завершаем соединение
        channel.finishConnect();

        //набираем сообщение, которое нужно отправить клиенту
        //нам нужен адрес, по которому мы будем связываться с удаленным сервером
        StringBuilder sb = new StringBuilder(channel.getLocalAddress().toString());
        sb = sb.replace(0, 1, "");
        StringTokenizer st = new StringTokenizer(sb.toString(), ":");
        InetAddress ip = InetAddress.getByName(st.nextToken());
        int intPort = Integer.parseInt(st.nextToken());
        byte[] bytesIp = ip.getAddress();
        byte[] bytesPort = new byte[]{(byte) ((intPort & 0xFF00) >> 8), (byte) (intPort & 0xFF)};

        Attachment peerAttachment = (Attachment) attachment.peer.attachment();
        peerAttachment.writeBuffer.clear();
        //сообщаем, что все ок
        peerAttachment.writeBuffer.put(new byte[]{0x05, 0x00, 0x00, 0x01});
        //добавляем в сообщение адрес и порт
        peerAttachment.writeBuffer.put(bytesIp);
        peerAttachment.writeBuffer.put(bytesPort);

        //добавляем интерес на запись клиенту
        attachment.peer.interestOps(SelectionKey.OP_WRITE);
        System.out.println("Connected");
    }

    /*
    прочитать запрос от клиента
     */
    private static void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        Attachment clientAttachment = (Attachment) key.attachment();
        if (clientAttachment == null) {
            //если у клиента нет attachment, то значит, он у нас в первый раз
            //нужно проверить запрос на соответствие шаблону и отправить номер версии и тип аутентификации
            clientAttachment = new Attachment();
            //читаем запрос от клиента
            int code = clientChannel.read(clientAttachment.readBuffer);
            if (code < 1) {
                //если -1 - то разрыв соединения, 0 - нет места в буфере
                close(key);
                return;
            }
            //печатаем прочитанное в консоль
            System.out.println("Read1 from " + clientChannel.getRemoteAddress() + ": " + Arrays.toString(clientAttachment.readBuffer.array()));
            //проверяем сообщение на соответствие приветствию
            if (clientAttachment.readBuffer.get(0) == 0x05 && clientAttachment.readBuffer.get(1) == 0x01) {
                //отвечаем версией протокола и типом аутентификации
                clientAttachment.writeBuffer.put(((byte) 0x05));
                clientAttachment.writeBuffer.put(((byte) 0x00));
                key.interestOps(SelectionKey.OP_WRITE);
            } else {
                throw new IllegalStateException("Bad Request");
            }
        } else if (clientAttachment.peer == null) {
            //если у клиента есть attachment, но нет peer, то это запрос с адресом для подключения
            //нужно прочитать адрес и порт и создать соединение с полученным адресом
            //также нужно зарегестрировать это соединение, создать SelectionKey и настроить peer клиента и нового key
            clientAttachment.readBuffer.clear();
            //читаем запрос от клиента
            int code = clientChannel.read(clientAttachment.readBuffer);
            if (code < 1) {
                //если -1 - то разрыв соединения, 0 - нет места в буфере
                close(key);
                return;
            }
            //печатаем прочитанное в консоль
            System.out.println("Read2 from " + clientChannel.getRemoteAddress() + ": " + Arrays.toString(clientAttachment.readBuffer.array()));
            //проверяем сообщение на соответствие
            byte[] rb = clientAttachment.readBuffer.array();
            if (!(rb[0] == 0x05 && rb[1] == 0x01 && rb[2] == 0x00 && rb[3] == 0x01)) {
                System.out.println("array: " + Arrays.toString(rb));
                throw new IllegalStateException("Bad Request");
            }
            //читаем адрес и порт
            byte[] addr = new byte[]{rb[4], rb[5], rb[6], rb[7]};
            int p = ((rb[8] & 0xFF) << 8) | (rb[9] & 0xFF);
            //создаем новый сокет
            SocketChannel newSocketChannel = SocketChannel.open();
            //делаем его неблокирующим
            newSocketChannel.configureBlocking(false);
            //коннектимся по прочитанному от клиента адресу к серверу телеграма
            newSocketChannel.connect(new InetSocketAddress(InetAddress.getByAddress(addr), p));
            //регестрируем сокет в селекторе
            SelectionKey newSelectionKey = newSocketChannel.register(key.selector(), SelectionKey.OP_CONNECT);
            //теперь настраиваем peer клиента на новый key и peer newSelectionKey на клиента
            //writeBuffer клиента - это readBuffer peer'a и наоборот
            Attachment newAttachment = new Attachment();
            newAttachment.peer = key;
            newAttachment.writeBuffer = clientAttachment.readBuffer;
            newAttachment.readBuffer = clientAttachment.writeBuffer;
            clientAttachment.peer = newSelectionKey;
            newSelectionKey.attach(newAttachment);
        } else {
            //если у клиента уже все есть, то обмениваемся сообщениями
            //читаем из attachment клиента readBuffer
            //и передаем его в attachment пира в writeBuffer
            clientAttachment.readBuffer.clear();
            //читаем запрос от клиента
            int code = clientChannel.read(clientAttachment.readBuffer);
            if (code < 1) {
                //если -1 - то разрыв соединения, 0 - нет места в буфере
                close(key);
                return;
            }
            //печатаем прочитанное в консоль
            System.out.println("Read3 from " + clientChannel.getRemoteAddress() + ": " + Arrays.toString(clientAttachment.readBuffer.array()));
            key.interestOps(key.interestOps() ^ SelectionKey.OP_READ);
            //ставим интерес прочитать пиру клиента
            clientAttachment.peer.interestOps(SelectionKey.OP_WRITE);
        }
        key.attach(clientAttachment);
    }

    /*
    отправить ответ клиенту
     */
    private static void write(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        Attachment clientAttachment = (Attachment) key.attachment();
        //этот метод отправляет серверу или клиенту данные из буфера writeBuffer
        System.out.println("Write to " + clientChannel.getRemoteAddress() + ": " + Arrays.toString(clientAttachment.writeBuffer.array()));
        clientAttachment.writeBuffer.flip();
        if (clientChannel.write(clientAttachment.writeBuffer) == -1) {
            //если -1 - то разрыв соединения, 0 - нет места в буфере
            close(key);
            return;
        }
        if (clientAttachment.writeBuffer.remaining() == 0) {
            if (clientAttachment.peer == null) {
                //если пира еще нет, то готовимся принимать ответ
                key.interestOps(SelectionKey.OP_READ);
            } else {
                //если всё записано, чистим буфер
                clientAttachment.writeBuffer.clear();
                //добавялем пиру интерес на чтение
                clientAttachment.peer.interestOps(SelectionKey.OP_READ);
                //убираем интерес на запись
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private static void close(SelectionKey key) throws IOException {
        key.cancel();
        key.channel().close();
        Attachment attachment = ((Attachment) key.attachment());
        if (attachment != null && attachment.peer != null) {
            Attachment peerKey = ((Attachment) attachment.peer.attachment());
            if (peerKey != null && peerKey.peer != null) {
                peerKey.peer.cancel();
            }
            attachment.peer.cancel();
        }

    }
}
