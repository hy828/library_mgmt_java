import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QueryPage {
    private JPanel QueryPagePanel;
    private JButton ExitButton;
    private JTextField 书名textField;
    private JTextField 作者textField;
    private JButton 查询Button;
    private JTextField 价格textField;
    private JTextField 年份textField;
    private JTextField 出版社textField;
    private JButton 清空Button;
    private JTable table1;
    private JScrollPane scrollpane1;
    private JTextField 年份textField2;
    private JTextField 价格textField2;
    private JComboBox 类别comboBox;

    public QueryPage() {
        JFrame frame = new JFrame("图书管理系统-[图书查询]");
        frame.setContentPane(QueryPagePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        table1.setModel(new DefaultTableModel( //建立用来显示的表格
                new Object [][] {},
                new String [] {"书号", "书名", "类别", "作者", "出版社", "年份", "价格", "总藏书量", "库存"}
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false, false, false
            };
            public Class getColumnClass(int columnIndex) { return types [columnIndex]; }
            public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit [columnIndex]; }
        });
        scrollpane1.setViewportView(table1); //可滚动
        try{
            Connection con = Database.getConnection(); //连接数据库
            PreparedStatement ps = con.prepareStatement("select distinct BookType from book");
            //准备sql语句
            ResultSet rs = ps.executeQuery(); //执行sql语句并返回结果
            类别comboBox.addItem("");
            while(rs.next()){ //逐条显示记录
                String type = rs.getString("BookType");
                类别comboBox.addItem(type);
            }
            con.close(); //关闭连接
        }catch(Exception ex){
            System.out.println(ex);
        }

        ExitButton.addActionListener(new ActionListener() { //点击退出
            @Override
            public void actionPerformed(ActionEvent e) {
                if(LogInPage.userid != ""){ //用户名不为空，跳转至管理员菜单
                    AdministratorPage page = new AdministratorPage();
                }else{ //跳转至首页
                    HomePage page = new HomePage();
                }
                frame.dispose();
            }
        });

        清空Button.addActionListener(new ActionListener() { //点击清空，设置每个输入框为空字符串
            @Override
            public void actionPerformed(ActionEvent e) {
                书名textField.setText("");
                作者textField.setText("");
                价格textField.setText("");
                价格textField2.setText("");
                年份textField.setText("");
                年份textField2.setText("");
                出版社textField.setText("");
                类别comboBox.setSelectedIndex(0);
            }
        });

        查询Button.addActionListener(new ActionListener() { //点击查询
            @Override
            public void actionPerformed(ActionEvent e) {
                String p1 = "%" + 书名textField.getText() + "%"; //加分号表示这是部分字符串
                String p2 = "%" + String.valueOf(类别comboBox.getSelectedItem()) + "%";
                if(String.valueOf(类别comboBox.getSelectedItem()) == null){
                    p2 = "%" + "" + "%";
                }
                String p3 = "%" + 作者textField.getText() + "%";
                double p4 = 0, p5 = 10000;
                int p6 = 0, p7 = 2023;
                try{
                    if(!价格textField.getText().isBlank()){
                        p4 = Double.parseDouble(价格textField.getText());
                    }
                    if(!价格textField2.getText().isBlank()){
                        p5 = Double.parseDouble(价格textField2.getText());
                    }
                    if(!年份textField.getText().isBlank()){
                        p6 = Integer.parseInt(年份textField.getText());
                    }
                    if(!年份textField2.getText().isBlank()){
                        p7 = Integer.parseInt(年份textField2.getText());
                    }
                }catch(NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "年份/价格必须为数字");
                }
                String p8 = "%" + 出版社textField.getText() + "%";
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                model.setRowCount(0); //设置行数为零，即清空表格
                table1.setRowSorter(new TableRowSorter<DefaultTableModel>(model)); //设置过滤器
                try{
                    Connection con = Database.getConnection(); //连接数据库
                    PreparedStatement ps = con.prepareStatement("select BookNo, BookName, " +
                                    "BookType, Author, Publisher, Year, Price, Total, Storage " +
                            "from book where BookName like ? and BookType like ? and Author like " +
                            "? and Price >= ? and Price <= ? and Year >= ? and Year <= ? and " +
                            "Publisher like ?"); //准备sql语句
                    ps.setString(1, p1);
                    ps.setString(2, p2);
                    ps.setString(3, p3);
                    ps.setDouble(4, p4);
                    ps.setDouble(5, p5);
                    ps.setInt(6, p6);
                    ps.setInt(7, p7);
                    ps.setString(8, p8);
                    ResultSet rs = ps.executeQuery(); //执行sql语句并返回结果
                    if(!rs.next()){ //返回为空表
                        JOptionPane.showMessageDialog(null, "无记录");
                    }else{
                        do{ //逐条显示记录
                            String bookNo = rs.getString("BookNo");
                            String bookName = rs.getString("BookName");
                            String bookType = rs.getString("BookType");
                            String author = rs.getString("Author");
                            String publisher = rs.getString("Publisher");
                            String year = rs.getString("Year");
                            String price = rs.getString("Price");
                            String total = rs.getString("Total");
                            String storage = rs.getString("Storage");
                            model.addRow(new Object[]{bookNo, bookName, bookType, author, publisher, year, price, total, storage});
                        }while(rs.next());
                    }
                    con.close();
                }catch(Exception ex){
                    System.out.println(ex);
                }
            }
        });
    }
}
