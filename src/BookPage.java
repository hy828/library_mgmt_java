import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

public class BookPage {
    private JPanel BookPagePanel;
    private JTextField 书号textField;
    private JTextField 类别textField;
    private JTextField 书名textField;
    private JTextField 出版社textField;
    private JTextField 年份textField;
    private JTextField 作者textField;
    private JTextField 价格textField;
    private JTextField 数量textField;
    private JButton 添加Button;
    private JButton ExitButton;
    private JTable table1;
    private JScrollPane scrollpane1;
    private JButton 导入Button;
    private JButton 删除Button;

    public BookPage() {
        JFrame frame = new JFrame("图书管理系统-[图书入库]");
        frame.setContentPane(BookPagePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        table1.setModel(new DefaultTableModel( //建立用来显示的表格
                new Object [][] {},
                new String [] {"书号", "书名", "类别", "作者", "出版社", "年份", "价格", "数量"}
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false, false
            };
            public Class getColumnClass(int columnIndex) { return types [columnIndex]; }
            public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit [columnIndex]; }
        });
        scrollpane1.setViewportView(table1); //可滚动
        showTable();

        ExitButton.addActionListener(new ActionListener() { //点击退出，跳转至管理员菜单
            @Override
            public void actionPerformed(ActionEvent e) {
                AdministratorPage page = new AdministratorPage();
                frame.dispose();
            }
        });

        添加Button.addActionListener(new ActionListener() { //点击添加
            @Override
            public void actionPerformed(ActionEvent e) {
                String p1 = 书号textField.getText();
                String p2 = 类别textField.getText();
                String p3 = 书名textField.getText();
                String p4 = 出版社textField.getText();
                String pp5 = 年份textField.getText();
                String p6 = 作者textField.getText();
                String pp7 = 价格textField.getText();
                String pp8 = 数量textField.getText();
                if(check(p1, p2, p3, p4, pp5, p6, pp7, pp8)){ //检查每一个输入框是否符合规范
                    insert(p1, p2, p3, p4, pp5, p6, pp7, pp8);
                }
            }
        });

        导入Button.addActionListener(new ActionListener() { //点击导入
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    //读取文件
                    BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\Acer\\Desktop\\library_management_system\\src\\data.txt"));
                    String str;
                    String delimeter = ",";
                    String[] temp;
                    while ((str = in.readLine()) != null) { //逐行读取
                        temp = str.split(delimeter); //以英文逗号为分隔符分为若干个字符串
                        if(temp.length == 8 && isNumeric(temp[4], temp[6], temp[7])){ //检查读入的字符串是否有效
                            insert(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6],
                                    temp[7]); //添加到数据库
                        }
                    }
                }catch(IOException ioe){
                    System.out.println(ioe);
                }
            }
        });

        删除Button.addActionListener(new ActionListener() { //点击删除
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table1.getSelectedRow(); //返回选中的行在第几行
                String bookNo = table1.getModel().getValueAt(row, 0).toString(); //获取第一列的值
                int status = 0;
                int value = JOptionPane.showConfirmDialog(null, "确定要删除此书吗？"); //确认是否删除
                if(value == JOptionPane.YES_OPTION){
                    try{
                        Connection con = Database.getConnection();
                        PreparedStatement ps = con.prepareStatement("delete from book where " +
                                "BookNo = ?"); //删除数据
                        ps.setString(1, bookNo);
                        status = ps.executeUpdate();
                        if(status == 0){
                            JOptionPane.showMessageDialog(null, "无法删除！");
                        }else{
                            showTable(); //删除成功，移除该行
                        }
                        con.close();
                    }catch(SQLIntegrityConstraintViolationException ex){ //foreign key完整性校验所抛出的异常
                        JOptionPane.showMessageDialog(null, "已存在记录，无法删除！");
                    }catch(Exception ex){
                        System.out.println(ex);
                    }
            }
        }});
    }

    public boolean check(String p1, String p2, String p3, String p4, String pp5, String p6,
                         String pp7, String pp8){ //检查每一个字符串都不为空，且部分字符串必须为数字
        if(p1.isBlank()){
            JOptionPane.showMessageDialog(null, "书号不能为空！");
            return false;
        }else if(p2.isBlank()){
            JOptionPane.showMessageDialog(null, "类别不能为空！");
            return false;
        }else if(p3.isBlank()){
            JOptionPane.showMessageDialog(null, "书名不能为空！");
            return false;
        }else if(p4.isBlank()){
            JOptionPane.showMessageDialog(null, "出版社不能为空！");
            return false;
        }else if(pp5.isBlank()){
            JOptionPane.showMessageDialog(null, "年份不能为空！");
            return false;
        }else if(p6.isBlank()){
            JOptionPane.showMessageDialog(null, "作者不能为空！");
            return false;
        }else if(pp7.isBlank()){
            JOptionPane.showMessageDialog(null, "价格不能为空！");
            return false;
        }else if(pp8.isBlank()){
            JOptionPane.showMessageDialog(null, "数量不能为空！");
            return false;
        }else{
            if(isNumeric(pp5, pp7, pp8) == false){
                JOptionPane.showMessageDialog(null, "年份/价格/数量输入错误！");
                return false;
            }else{
                return true;
            }
        }
    }

    public boolean isNumeric(String pp5, String pp7, String pp8){
        try{
            Integer.parseInt(pp5);
            double price = Double.parseDouble(pp7);
            if(price < 0) return false;
            int qty = Integer.parseInt(pp8);
            if(qty < 0) return false;
            return true;
        }catch(NumberFormatException e){ //通过捕捉异常来检查是否为数字
            return false;
        }
    }

    public void insert(String p1, String p2, String p3, String p4, String pp5, String p6,
                       String pp7, String pp8){
        int p5 = Integer.parseInt(pp5);
        double p7 = Double.parseDouble(pp7);
        int p8 = Integer.parseInt(pp8);
        int status = 0;
        try{
            Connection con = Database.getConnection();
            PreparedStatement ps = con.prepareStatement("insert book values(?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, now())"); //插入数据
            ps.setString(1, p1);
            ps.setString(2, p2);
            ps.setString(3, p3);
            ps.setString(4, p4);
            ps.setInt(5, p5);
            ps.setString(6, p6);
            ps.setDouble(7, p7);
            ps.setInt(8, p8);
            ps.setInt(9, p8);
            status = ps.executeUpdate();
            if(status == 0){
                JOptionPane.showMessageDialog(null, "无法添加！");
            }else{
                showTable(); //添加成功，显示该行
                书号textField.setText("");
                类别textField.setText("");
                书名textField.setText("");
                出版社textField.setText("");
                年份textField.setText("");
                作者textField.setText("");
                价格textField.setText("");
                数量textField.setText("");
            }
            con.close();
        }catch(SQLIntegrityConstraintViolationException ex){ //primary key完整性校验所抛出的异常
            JOptionPane.showMessageDialog(null, "书号重复，无法添加！");
        }catch(Exception ex){
            System.out.println(ex);
        }
    }

    public void showTable(){ //显示表格
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(0); //设置行数为零，即清空表格
        table1.setRowSorter(new TableRowSorter<DefaultTableModel>(model)); //设置过滤器
        try{
            Connection con = Database.getConnection(); //连接数据库
            PreparedStatement ps = con.prepareStatement("select BookNo, BookName, " +
                    "BookType, Author, Publisher, Year, Price, Total from book order by UpdateTime");
            //准备sql语句
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
                    model.addRow(new Object[]{bookNo, bookName, bookType, author, publisher, year, price, total});
                }while(rs.next());
            }
            con.close(); //关闭连接
        }catch(Exception ex){
            System.out.println(ex); //打印异常
        }
    }
}
