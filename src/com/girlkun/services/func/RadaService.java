package com.girlkun.services.func;

import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.utils.Util;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class RadaService {

    private static RadaService i;

    private RadaService() {

    }

    public static RadaService gI() {
        if (i == null) {
            i = new RadaService();
        }
        return i;
    }

    public void controller(Player player, Message msg) {
        try {
            byte type = msg.reader().readByte();
            switch (type) {
                case 0:
                    openRada(player);
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        try {
//            DataInputStream dis = new DataInputStream(new FileInputStream("C:\\Users\\adm\\.microemulator\\filesystem\\e\\radasuutam"));
//            System.out.println(dis.readByte());
//            System.out.println("-------------------------------------");
//            int n = dis.readShort();
//            for (int i = 0; i < n; i++) {
//                System.out.println("+++++++++++++++++++++++++++++\n\n\n");
//                System.out.println("id: " + dis.readShort());
//                System.out.println("icon: " + dis.readShort());
//                System.out.println("rank: " + dis.readByte());
//                System.out.println("amount: " + dis.readByte());
//                System.out.println("max_amount: " + dis.readByte());
//
//                System.out.println("------------------------------");
//                int check = dis.readByte();
//                if (check == 0) {
//                    System.out.println("mob id: " + dis.readShort());
//                } else {
//                    System.out.println("head: " + dis.readShort());
//                    System.out.println("body: " + dis.readShort());
//                    System.out.println("leg: " + dis.readShort());
//                    System.out.println("bag: " + dis.readShort());
//                }
//                System.out.println("------------------------------");
//                System.out.println("name: " + dis.readUTF());
//                System.out.println("info: " + dis.readUTF());
//                System.out.println("level: " + dis.readByte());
//                System.out.println("use: " + dis.readByte());
//                System.out.println("------------------------------mmmmmmmmmm");
//                check = dis.readByte();
//                if (check != 0) {
//                    for (int j = 0; j < check; j++) {
//                        System.out.println("itemoption: " + dis.readByte());
//                        System.out.println("param: " + dis.readShort());
//                        System.out.println("active: " + dis.readByte());
//                        System.out.println("------------------------------");
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void openRada(Player player) {
        Message msg;
        try {
            msg = new Message(127);
            msg.writer().writeByte(0);
            msg.writer().writeShort(77);

            for (int i = 0; i < 77; i++) {
                msg.writer().writeShort(i); //id
                msg.writer().writeShort(15000); //icon
                msg.writer().writeByte(i % 7); //rank
                msg.writer().writeByte(2); //amount
                msg.writer().writeByte(7 + i); //max_amount
                int check = Util.nextInt(0, 0);
                msg.writer().writeByte(check); //check
                if (check == 0) {
                    msg.writer().writeShort(0 + i); //tempid
                } else {
                    msg.writer().writeShort(player.getHead());
                    msg.writer().writeShort(player.getBody());
                    msg.writer().writeShort(player.getLeg());
                    msg.writer().writeShort(player.getFlagBag());
                }
                msg.writer().writeUTF("đây là cái tên của cái thẻ sưu tập này");
                msg.writer().writeUTF("đây là thông tin dài dằng dặc mà bạn phải đọc hết mới hiểu được công dụng của cái này là cái gì");

                msg.writer().writeByte(1); //level
                msg.writer().writeByte(1); //use
                int noption = 3;
                msg.writer().writeByte(noption); //count option
                for (int k = 0; k < noption; k++) {
                    msg.writer().writeByte(22); //id option
                    msg.writer().writeShort(4444 + k); //param
                    msg.writer().writeByte(k); //active
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

}
