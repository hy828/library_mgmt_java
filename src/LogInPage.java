import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LogInPage {
    private JPanel LogInPagePanel;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton LogInButton;
    private JButton ExitButton;
    public static String userid = "";

    public LogInPage() {
        JFrame frame = new JFrame("图书管理系统-[管理员登录]");
        frame.setContentPane(LogInPagePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(450, 250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        LogInButton.addActionListener(new ActionListener() { //点击登录
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = textField1.getText();
                String password = String.valueOf(passwordField1.getPassword());
                if(validate(name, password)){ //检查用户名和密码是否正确
                    userid = name; //保存用户名
                    AdministratorPage page = new AdministratorPage(); //跳转至管理员菜单
                    frame.dispose();
                }else{
                    JOptionPane.showMessageDialog(null, "用户名或密码错误！");
                }
            }
        });

        ExitButton.addActionListener(new ActionListener() { //点击退出，跳转至首页
            @Override
            public void actionPerformed(ActionEvent e) {
                HomePage page = new HomePage();
                frame.dispose();
            }
        });
    }

    public static boolean validate(String name, String password){
        boolean status = false;
        try{
            Connection con = Database.getConnection(); //连接数据库
            PreparedStatement ps = con.prepareStatement("select * from user where UserID=? and Password=?"); //准备sql语句
            ps.setString(1,name);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery(); //执行sql语句并返回结果
            status = rs.next();
            con.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return status; //返回结果
    }
}
