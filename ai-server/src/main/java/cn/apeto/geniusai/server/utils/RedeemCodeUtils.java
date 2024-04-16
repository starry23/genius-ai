package cn.apeto.geniusai.server.utils;

public class RedeemCodeUtils {
    public static void main(String[] args) {
        String st1 = createBigSmallLetterStrOrNumberRadom(16);
        String st2 = createSmallStrOrNumberRadom(16);
        String st3 = createBigStrOrNumberRadom(16);
        System.out.println(st1);
        System.out.println(st2);
        System.out.println(st3);
//        for (int i = 0; i < 100; i++) {
//            System.out.println(createBigStrOrNumberRadom(16));
//        }
    }

    /**
     * @param num
     * @return
     * @function 生成num位的随机字符串(数字 、 大写字母随机混排)
     */
    public static String createBigSmallLetterStrOrNumberRadom(int num) {

        StringBuilder str = new StringBuilder();
        for (int i = 1; i <= num; i++) {
            int intVal = (int) (Math.random() * 58 + 65);
            if (intVal >= 91 && intVal <= 96) {
                i--;
            }
            if (intVal < 91 || intVal > 96) {
                if (intVal % 2 == 0) {
                    str.append((char) intVal);
                } else {
                    str.append((int) (Math.random() * 10));
                }
                if (i % 4 == 0) {
                    str.append("-");
                }
            }

        }
        return str.substring(0, str.length() - 1);
    }

    /**
     * @param num
     * @return
     * @function 生成num位的随机字符串(数字 、 小写字母随机混排)
     */
    public static String createSmallStrOrNumberRadom(int num) {

        StringBuilder str = new StringBuilder();
        for (int i = 1; i <= num; i++) {
            int intVal = (int) (Math.random() * 26 + 97);
            if (intVal % 2 == 0) {
                str.append((char) intVal);
            } else {
                str.append((int) (Math.random() * 10));
            }
            if (i % 4 == 0) {
                str.append("-");
            }
        }
        return str.substring(0, str.length() - 1);
    }

    /**
     * @param num
     * @return
     * @function 生成num位的随机字符串(小写字母与数字混排)
     */
    public static String createBigStrOrNumberRadom(int num) {

        StringBuilder str = new StringBuilder();
        for (int i = 1; i <= num; i++) {
            int intVal = (int) (Math.random() * 26 + 65);
            if (intVal % 2 == 0) {
                str.append((char) intVal);
            } else {
                str.append((int) (Math.random() * 10));
            }
            if (i % 4 == 0) {
                str.append("-");
            }
        }
        return str.substring(0, str.length() - 1);
    }
}

