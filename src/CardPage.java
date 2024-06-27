import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.sql.*;

public class CardPage {
    private JPanel CardPagePanel;
    private JTextField 卡号textField;
    private JTextField 姓名textField;
    private JButton 添加Button;
    private JTextField 单位textField;
    private JButton 删除Button;
    private JButton ExitButton;
    private JTable table1;
    private JScrollPane scrollpane1;
    private JComboBox comboBox1;

    public CardPage() {
        JFrame frame = new JFrame("图书管理系统-[借书证管理]");
        frame.setContentPane(CardPagePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        table1.setModel(new DefaultTableModel( //建立用来显示的表格
                new Object [][] {},
                new String [] {"卡号", "姓名", "所属单位", "类别"}
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false
            };
            public Class getColumnClass(int columnIndex) { return types [columnIndex]; }
            public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit [columnIndex]; }
        });
        scrollpane1.setViewportView(table1); //可滚动
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(0); //设置行数为零，即清空表格
        table1.setRowSorter(new TableRowSorter<DefaultTableModel>(model)); //设置过滤器
        try{
            Connection con = Database.getConnection(); //连接数据库
            PreparedStatement ps = con.prepareStatement("select CardNo, Name, Department, " +
                    "CardType from library_card"); //准备sql语句
            ResultSet rs = ps.executeQuery(); //执行sql语句并返回结果
            if(!rs.next()){ //返回为空表
                JOptionPane.showMessageDialog(null, "无记录");
            }else{
                do{ //逐条显示记录
                    String cardNo = rs.getString("CardNo");
                    String name = rs.getString("Name");
                    String department = rs.getString("Department");
                    String cardType = rs.getString("CardType");
                    model.addRow(new Object[]{cardNo, name, department, cardType});
                }while(rs.next());
            }
            con.close(); //关闭连接
        }catch(Exception ex){
            System.out.println(ex);
        }

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
                String p1 = 卡号textField.getText();
                String p2 = 姓名textField.getText();
                String p3 = 单位textField.getText();
                String p4 = String.valueOf(comboBox1.getSelectedItem());
                if(check(p1, p2, p3, p4)){ //检查每一个输入框是否符合规范
                    int status = 0;
                    try{
                        Connection con = Database.getConnection();
                        PreparedStatement ps = con.prepareStatement("insert library_card values(?, " +
                                "?, ?, ?, now())"); //插入数据
                        ps.setString(1, p1);
                        ps.setString(2, p2);
                        ps.setString(3, p3);
                        ps.setString(4, p4);
                        status = ps.executeUpdate();
                        if(status == 0){
                            JOptionPane.showMessageDialog(null, "无法添加！");
                        }else{
                            model.addRow(new Object[]{p1, p2, p3, p4}); //添加成功，显示该行
                            卡号textField.setText("");
                            姓名textField.setText("");
                            单位textField.setText("");
                            comboBox1.setSelectedIndex(-1);
                        }
                        con.close();
                    }catch(SQLIntegrityConstraintViolationException ex){ //primary key完整性校验所抛出的异常
                        JOptionPane.showMessageDialog(null, "卡号重复，无法添加！");
                    }catch(SQLException ex){
                        System.out.println(ex);
                    }
                }
            }
        });

        删除Button.addActionListener(new ActionListener() { //点击删除
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table1.getSelectedRow(); //返回选中的行在第几行
                String cardNo = model.getValueAt(row, 0).toString(); //获取第一列的值
                int status = 0;
                int value = JOptionPane.showConfirmDialog(null, "确定要删除此借书证吗？"); //确认是否删除
                if(value == JOptionPane.YES_OPTION){
                    try{
                        Connection con = Database.getConnection();
                        PreparedStatement ps = con.prepareStatement("delete from library_card where " +
                                "CardNo = ?"); //删除数据
                        ps.setString(1, cardNo);
                        status = ps.executeUpdate();
                        if(status == 0){
                            JOptionPane.showMessageDialog(null, "无法删除！");
                        }else{
                            model.removeRow(row); //删除成功，移除该行
                        }
                        con.close();
                    }catch(SQLIntegrityConstraintViolationException ex){ //foreign key完整性校验所抛出的异常
                        JOptionPane.showMessageDialog(null, "已存在记录，无法删除！");
                    }catch(Exception ex){
                        System.out.println(ex);
                    }
                }

            }
        });
    }

    public boolean check(String p1, String p2, String p3, String p4){ //检查每一个字符串都不为空
        if(p1.isBlank()){
            JOptionPane.showMessageDialog(null, "卡号不能为空！");
            return false;
        }else if(p2.isBlank()){
            JOptionPane.showMessageDialog(null, "姓名不能为空！");
            return false;
        }else if(p3.isBlank()){
            JOptionPane.showMessageDialog(null, "所属单位不能为空！");
            return false;
        }else if(p4.isBlank()){
            JOptionPane.showMessageDialog(null, "请选择其中一个类别！");
            return false;
        }else{
            return true;
        }
    }
}
