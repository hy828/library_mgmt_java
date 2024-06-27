
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage { //首页
    private JPanel HomePagePanel;
    private JButton LogInButton;
    private JButton QueryButton;

    public HomePage() {
        JFrame frame = new JFrame("图书管理系统"); //创建窗口对象以及窗口名字
        frame.setContentPane(HomePagePanel); //把组件添加到窗口
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //用户单击窗口的关闭按钮时程序执行的操作
        frame.pack(); //根据窗口里面的布局及组件的preferredSize来确定窗口的最佳大小
        frame.setSize(600, 400); //窗口大小
        frame.setLocationRelativeTo(null); //居中
        frame.setVisible(true); //设置窗口可见

        LogInButton.addActionListener(new ActionListener() { //点击管理员登录，跳转至该页
            @Override
            public void actionPerformed(ActionEvent e) {
                LogInPage page = new LogInPage();
                frame.dispose(); //清理窗口
            }
        });

        QueryButton.addActionListener(new ActionListener() { //点击查询，跳转至该页
            @Override
            public void actionPerformed(ActionEvent e) {
                QueryPage page = new QueryPage();
                frame.dispose();
            }
        });
    }

    public static void main(String[] args) {
        new HomePage();
    } //主函数
}
