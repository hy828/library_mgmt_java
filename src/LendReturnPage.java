import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LendReturnPage {
    private JPanel LendReturnPagePanel;
    private JTextField 借书证卡号textField;
    private JTextField 书号textField;
    private JButton 借出Button;
    private JButton ExitButton;
    private JTable table1;
    private JScrollPane scrollpane1;
    private JButton 查询Button;
    private JButton 还书Button;

    public LendReturnPage() {
        JFrame frame = new JFrame("图书管理系统-[借书/还书]");
        frame.setContentPane(LendReturnPagePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        table1.setModel(new DefaultTableModel( //建立用来显示的表格
                new Object [][] {},
                new String [] {"借书证卡号", "书号", "书名", "借出日期", "剩余库存", "总藏书量"}
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false
            };
            public Class getColumnClass(int columnIndex) { return types [columnIndex]; }
            public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit [columnIndex]; }
        });
        scrollpane1.setViewportView(table1); //可滚动
        showTable(); //显示表格

        ExitButton.addActionListener(new ActionListener() { //点击退出，跳转至管理员菜单
            @Override
            public void actionPerformed(ActionEvent e) {
                AdministratorPage page = new AdministratorPage();
                frame.dispose();
            }
        });

        借出Button.addActionListener(new ActionListener() { //点击借出
            @Override
            public void actionPerformed(ActionEvent e) {
                String p1 = 借书证卡号textField.getText();
                String p2 = 书号textField.getText();
                int status = 0;
                if (check(p1, p2)) { //检查每一个输入框是否符合规范
                    try{
                        Connection con = Database.getConnection(); //连接数据库
                        int num = -1;
                        PreparedStatement ps = con.prepareStatement("select Storage from book where " +
                                "BookNo = ?"); //准备sql语句：获取库存数
                        ps.setString(1, p2);
                        ResultSet rs = ps.executeQuery(); //执行sql语句并返回结果
                        if(rs.next()){
                            num = rs.getInt("Storage");
                            if(num <= 0){ //库存小于或等于零，无法借出
                                JOptionPane.showMessageDialog(null, "此书已无库存，无法借出！");
                            }else{
                                ps = con.prepareStatement("insert library_record(CardNo, " +
                                        "BookNo, LentDate, Operator) values(?, ?, now(), ?)"); //插入数据
                                ps.setString(1, p1);
                                ps.setString(2, p2);
                                ps.setString(3, LogInPage.userid);
                                status = ps.executeUpdate();
                                if(status == 0){
                                    JOptionPane.showMessageDialog(null, "无法借出！");
                                }else{
                                    ps = con.prepareStatement("update book set Storage = ? where " +
                                            "BookNo = ?"); //更新库存
                                    ps.setInt(1,num-1);
                                    ps.setString(2, p2);
                                    status = ps.executeUpdate();
                                }
                                showTable();
                                书号textField.setText("");
                            }
                        }
                        con.close();
                    }catch(Exception ex){
                        System.out.println(ex);
                    }
                }
            }
        });

        查询Button.addActionListener(new ActionListener() { //点击查询
            @Override
            public void actionPerformed(ActionEvent e) {
                if(借书证卡号textField.getText().isBlank()){
                    JOptionPane.showMessageDialog(null, "请填写借书证卡号");
                }else{
                    showTable();
                }
            }
        });

        还书Button.addActionListener(new ActionListener() { //点击还书
            @Override
            public void actionPerformed(ActionEvent e) {
                String p1 = 借书证卡号textField.getText();
                String p2 = 书号textField.getText();
                int status = 0;
                if(check(p1, p2)){ //检查每一个输入框是否符合规范
                    try{
                        Connection con = Database.getConnection();
                        PreparedStatement ps = con.prepareStatement("update library_record set " +
                                "ReturnDate=now() where CardNo=? and BookNo=? and ReturnDate is " +
                                "null order by LentDate limit 1"); //更新归还日期
                        ps.setString(1, p1);
                        ps.setString(2, p2);
                        status = ps.executeUpdate();
                        if(status == 0){
                            JOptionPane.showMessageDialog(null, "无法还书！");
                        }else{
                            ps = con.prepareStatement("update book set Storage=Storage+1 where " +
                                    "BookNo=?"); //更新库存
                            ps.setString(1, p2);
                            status = ps.executeUpdate();
                            showTable();
                            书号textField.setText("");
                        }
                        con.close();
                    }catch(Exception ex){
                        System.out.println(ex);
                    }
                }
            }});
    }

    public void showTable(){ //显示表格
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(0);
        table1.setRowSorter(new TableRowSorter<DefaultTableModel>(model));
        String p1 = 借书证卡号textField.getText();
        if(!p1.isBlank()){ //卡号不为空
            try{
                Connection con = Database.getConnection();
                PreparedStatement ps = con.prepareStatement("select BookNo, BookName, " +
                        "LentDate, Storage, Total from book natural join library_record where " +
                        "CardNo = ? and ReturnDate is null"); //显示该卡号所借的书并且尚未归还
                ps.setString(1, p1);
                ResultSet rs = ps.executeQuery();
                if(!rs.next()){ //返回为空表
                    JOptionPane.showMessageDialog(null, "无记录");
                }else{
                    do{ //逐条显示记录
                        String bookNo = rs.getString("BookNo");
                        String bookName = rs.getString("BookName");
                        String lentDate = rs.getString("LentDate");
                        String storage = rs.getString("Storage");
                        String total = rs.getString("Total");
                        model.addRow(new Object[]{p1, bookNo, bookName, lentDate, storage, total});
                    }while(rs.next());
                }
                con.close();
            }catch(Exception ex){
                System.out.println(ex);
            }
        }

    }

    public boolean check(String p1, String p2){ //检查每一个字符串都不为空
        if(p1.isBlank()){
            JOptionPane.showMessageDialog(null, "卡号不能为空！");
            return false;
        }else if(p2.isBlank()){
            JOptionPane.showMessageDialog(null, "书号不能为空！");
            return false;
        }else{
            return true;
        }
    }
}
