import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdministratorPage {
    private JButton QueryButton;
    private JButton LentReturnButton;
    private JButton CardButton;
    private JButton BookButton;
    private JButton LogOutButton;
    private JPanel AdministratorPagePanel;

    public AdministratorPage() {
        JFrame frame = new JFrame("图书管理系统");
        frame.setContentPane(AdministratorPagePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); //居中
        frame.setVisible(true);

        LogOutButton.addActionListener(new ActionListener() { //点击退出登录，跳转至首页或管理员菜单
            @Override
            public void actionPerformed(ActionEvent e) {
                LogInPage.userid = ""; //设置当前登录用户名为空
                HomePage page = new HomePage();
                frame.dispose();
            }
        });

        QueryButton.addActionListener(new ActionListener() { //点击图书查询，跳转至该页
            @Override
            public void actionPerformed(ActionEvent e) {
                QueryPage page = new QueryPage();
                frame.dispose();
            }
        });

        LentReturnButton.addActionListener(new ActionListener() { //点击借书/还书，跳转至该页
            @Override
            public void actionPerformed(ActionEvent e) {
                LendReturnPage page = new LendReturnPage();
                frame.dispose();
            }
        });

        CardButton.addActionListener(new ActionListener() { //点击借书证管理，跳转至该页
            @Override
            public void actionPerformed(ActionEvent e) {
                CardPage page = new CardPage();
                frame.dispose();
            }
        });

        BookButton.addActionListener(new ActionListener() { //点击图书入库，跳转至该页
            @Override
            public void actionPerformed(ActionEvent e) {
                BookPage page = new BookPage();
                frame.dispose();
            }
        });
    }
}
