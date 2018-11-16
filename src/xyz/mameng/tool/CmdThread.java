package xyz.mameng.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdThread implements Runnable{

    private String name;
    private static List<String> devices;
    private static List<String> flashing;
    private static String filepath = "";
    private Cmd cmd = new Cmd();


    public CmdThread(String name){
        this.name = name;
    }

    @Override
    public void run() {

        if (name.equals("devices")){
            devices = new ArrayList<>();
            flashing = new ArrayList<>();
            while (true){
                String result = cmd.CMDCommand("adb devices");
                List<String> results = Arrays.asList(result.split("\n"));
                if (results.size()>1){
                    for (Object device : results){
                        if (device.toString().endsWith("device")){
                            String deviceName = device.toString().split("\t")[0];
                            if (devices.isEmpty()){
                            //    System.out.println("devices为空，添加"+deviceName);
                                devices.add(deviceName);
                            }else if (!devices.contains(deviceName)){
                            //    System.out.println("device不为空，添加："+deviceName);
                                devices.add(deviceName);
                            }
                        }
                    }
                }
                //System.out.println("添加后device数量："+devices.size());
            }
        }

        if (name.equals("flash")){

            //选取最后一行的device进行操作，结束之后删除
            while (true){
               // 每次循环延时1秒
                this.sleep(1000);
                boolean isEmpty = devices.isEmpty();
                if (isEmpty){
                    System.out.println(Thread.currentThread().getName()+"：等待设备连接！");
                    this.sleep(2000);
                }else{
                    String s = devices.get(devices.size()-1).toString();
                    //判断s是否正在其他线程刷机
                    boolean isFlashing = flashing.contains(s);
                    if (!isFlashing){
                        //开始刷机前将这台设备添加到正在刷机的列表，防止其他线程重复刷
                        flashing.add(s);
                        //调用刷机
                        this.flash(s);
                        //将这台设备从列表中移除
                        devices.remove(s);
                        flashing.remove(s);
                        //System.out.println("------------------断开后device数量："+devices.size());
                    }else {
                        System.out.println(Thread.currentThread().getName()+"--------等待.....");
                    }
                }
            }
        }
    }


    public void flash(String s){
        //获取一下线程名称
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName+":-----------------------------开始刷机："+s);

        /*刷机流程*/
        cmd.CMDCommand("adb -s "+s+" push "+filepath+" /sdcard/");
        cmd.CMDCommand("adb -s "+s+" shell am start com.qualcomm.update/com.qualcomm.update.UpdateDialog");
        cmd.CMDCommand("adb -s "+s+" shell input tap 280 680");
        cmd.CMDCommand("adb -s "+s+" shell input tap 600 790");
        cmd.CMDCommand("adb -s "+s+" shell sleep 2");
        cmd.CMDCommand("adb -s "+s+" shell input keyevent 93");
        cmd.CMDCommand("adb -s "+s+" shell input keyevent 93");
        cmd.CMDCommand("adb -s "+s+" shell input keyevent 93");
        cmd.CMDCommand("adb -s "+s+" shell input tap 330 1040");
        cmd.CMDCommand("adb -s "+s+" shell sleep 2");

        //test
//        cmd.CMDCommand("adb -s "+s+" shell input tap 130 780");
//        cmd.CMDCommand("adb -s "+s+" shell am kill com.qualcomm.update");

        cmd.CMDCommand("adb -s "+s+" shell input tap 600 780");
        System.out.println(threadName+":-------------------------结束，请断开连接！---->> "+s);
        this.sleep(5000);
    }



    public static void main(String[] args) throws InterruptedException {
        new Thread(new CmdThread("devices")).start();
        Thread.sleep(3000);
        new Thread(new CmdThread("flash")).start();
        Thread.sleep(3000);
        new Thread(new CmdThread("flash")).start();
        Thread.sleep(3000);
        new Thread(new CmdThread("flash")).start();
    }




    public void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
