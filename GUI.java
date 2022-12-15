import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUI {

    private JFrame frame;
    private FCB Fileroot;
    private JLabel top;
    private JTree jTree;
    private JLabel order;
    private JTextField jtf;
    private JTable jt;
    private  Function function;
    private JPanel diskshow;
    private ArrayList<JButton> JbuttonList = new ArrayList<>();
    private JButton verify;
    DefaultMutableTreeNode root;
    DefaultTreeModel treeModel;
    private FCB anotherfileModel = null;
    public Function getfunction(){
        return function;
    }

    public GUI(Function function){
        this.function = function;
        Fileroot = function.getRoot();
        init();
        firstThread();
    }

    public void init(){
        JFrame frame = new JFrame("os-filemanage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700,500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        top = new JLabel("文件管理系统");
        top.setFont(new Font("黑体", Font.PLAIN, 25));
        top.setBounds(250, 0, 200, 50);
        frame.add(top);

        root = new DefaultMutableTreeNode("root");
        initTree(function.getRoot(),root);
        treeModel = new DefaultTreeModel(root,true);
        jTree = new JTree(treeModel);


        jTree.setFont(new Font("宋体", Font.PLAIN, 25));
        jTree.setBounds(10,50,150,400);
        frame.add(jTree);

        order = new JLabel("命令:");
        order.setFont(new Font("黑体",Font.PLAIN,20));
        order.setBounds(200,50,100,50);
        frame.add(order);
        jtf = new JTextField();
        jtf.setBounds(250,60,250,30);
        frame.add(jtf);

        verify = new JButton("确认");
        verify.setFont(new Font("黑体",Font.PLAIN,15));
        verify.setBounds(520,60,70,30);
        verify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    orderall(jtf.getText().toString());
                } catch (ArrayIndexOutOfBoundsException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "请检查命令！", "提示", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        frame.add(verify);

        diskshow = new JPanel();
        diskshow.setBounds(150,80,550,450);
        diskshow.setLayout(null);
        frame.add(diskshow);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 13; j++) {
                JButton jButton = new JButton();
                JbuttonList.add(jButton);
                diskshow.add(jButton);
                jButton.setBounds(30 + j * 35, 20 + i * 35, 25, 25);
            }
        }
        for (int i = 0; i < 11; i++) {
            JButton jButton = new JButton();
            JbuttonList.add(jButton);
            diskshow.add(jButton);

            jButton.setBounds(30 + i * 35, 335, 25, 25);
        }


        frame.setVisible(true);
    }

    public void orderall(String str){
        String[] strs = input(str);
        switch (strs[0]){
            case "create":
                create(strs);
                break;
            case "copy":
                copy(strs);
                break;
            case "delete":
                delete(strs);
                break;
            case "move":
                move(strs);
                break;
            case "type":
                type(strs);
                break;
            case "edit":
                edit(strs);
                break;
            case "change":
                change(strs);
                break;
            case "format":
                format(strs);
                break;
            case "makdir":
                makdir(strs);
                break;
            case "chadir":
                chadir(strs);
                break;
            case "rdir":
                delete(strs);
                break;
            case "deldir":
                deldir(strs);
                break;
            default:
                showDialogue("输入的指令错误！");
        }
    }

    private void move(String[] strs) {
        if(strs.length < 3){
            JOptionPane.showMessageDialog(null,
                    "您所输入的命令有误，请检查！", "提示", JOptionPane.PLAIN_MESSAGE);
        }else{
            FCB file = function.openFile(strs[1]);
            if(file != null){
                if (file.getAttr() == 3){
                    showDialogue("此为目录文件，请检查输入的命令！");
                }else if(file.getAttr() == 2 ){
                    String[] path = strs[2].split("/");
                    function.searchFile(path);
                    file.getFather().getSubMap().remove(file.getName());
                    file.setFather(function.getNowCatalog());
                    file.getFather().getSubMap().put(file.getName(),file);
                    refreshTree();
                    showDialogue("移动成功!");
                }
            }
        }
    }

    private void copy(String[] strs) {
        if(strs.length < 3){
            JOptionPane.showMessageDialog(null,
                    "您所输入的命令有误，请检查！", "提示", JOptionPane.PLAIN_MESSAGE);
        }else {
                FCB file= function.openFile(strs[1]);
                anotherfileModel = file;
                if(anotherfileModel != null) {
                    int startNum = function.setFat(anotherfileModel.getSize());
                    FCB file1 = new FCB(strs[2], file.getType(), file.getStartNum(), file.getSize());//深拷贝文件对象
                    file1.setFather(file.getFather());
                    file1.setFileContent(file.getFileContent());
                    file1.getFather().subMap.put(file1.getName(), file1);
                    function.getTotalFiles().put(file1.getName(), file1);
                    function.getFAT()[0] -= file1.getSize();
                    refreshTree();
                }
        }
    }


    private void deldir(String[] strs) {
        if (strs.length < 2) {
            JOptionPane.showMessageDialog(null,
                    "您所输入的命令有误，请检查！", "提示", JOptionPane.PLAIN_MESSAGE);
        } else {
            function.deleteNotNullCatalog(strs[1]);
        }
    }


    private void chadir(String[] strs) {
        if (strs.length < 2) {
            JOptionPane.showMessageDialog(null,
                    "您所输入的命令有误，请检查！", "提示", JOptionPane.PLAIN_MESSAGE);
        } else {
                String[] path = strs[1].split("/");
                function.searchFile(path);
                function.changedir();
        }
    }

    private void makdir(String[] strs) {
        if (strs.length < 2) {
            JOptionPane.showMessageDialog(null,
                    "您所输入的命令有误，请检查！", "提示", JOptionPane.PLAIN_MESSAGE);
        } else {
            function.createCatolog(strs[1]);
        }
    }

    private void edit(String[] strs) {
        if (strs.length < 2) {
            JOptionPane.showMessageDialog(null,"您输入的命令有误","提示",JOptionPane.PLAIN_MESSAGE);
        } else {
            FCB file = function.openFile(strs[1]);
            if(file!=null)
            {
                editFrame(file);
            }
        }
    }

    private void editFrame(FCB file) {
            if (file.isAttrIsOnlyRead()) {
                showDialogue("该文件不可编辑");
            }else{
                JFrame editFrame = new JFrame();
                editFrame.setLocationRelativeTo(null);
                JTextArea jT = new JTextArea();
                editFrame.getContentPane().add(jT);
                editFrame.setVisible(true);
                int oldSize = file.getSize();
                jT.setText(file.getFileContent());
                editFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        if (JOptionPane.showConfirmDialog(null,
                                "您确认要保存文件吗", "确认", JOptionPane.YES_NO_OPTION) == 0) {
                            int ByteSize = jT.getText().toString().length() / 2;
                            int size = ByteSize > 64 ? ByteSize % 64 + 1 : 1;
                            file.setFileContent(jT.getText().toString());
                            function.edittxt(file.getName(), size - oldSize);
                        }
                    }
                });
                editFrame.setSize(500,500);
            }

    }

    private void type(String[] strs) {
        if (strs.length < 2) {
            JOptionPane.showMessageDialog(null,"您输入的命令有误","提示",JOptionPane.PLAIN_MESSAGE);
        } else {
            FCB file = function.openFile(strs[1]);
            if (file != null) {
                JFrame jF = new JFrame();
                JTextArea jT = new JTextArea();
                jF.setLocationRelativeTo(null);
                jF.getContentPane().add(jT);
                jF.setVisible(true);
                int oldSize = file.getSize();
                jT.setText(file.getFileContent());
                jF.setSize(500,500);
                refreshTree();
            }else {
                JOptionPane.showMessageDialog(null,"不存在该文件","提示",JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    public void showDialogue(String str){
        JOptionPane.showMessageDialog(null,str,"提示",JOptionPane.PLAIN_MESSAGE);
    }

    private void change(String[] strs) {
        if (strs.length < 3) {
            JOptionPane.showMessageDialog(null,"您输入的命令有误","提示",JOptionPane.PLAIN_MESSAGE);
        } else {
            function.change(strs[1], Integer.parseInt(strs[2]));

        }
    }

    private void format(String[] strs) {
        if(strs.length < 2){
            JOptionPane.showMessageDialog(null,"您输入的命令有误","提示",JOptionPane.PLAIN_MESSAGE);
        }else {
            if (JOptionPane.showConfirmDialog(null,
                    "您确定要格式化磁盘吗", "确认", JOptionPane.YES_NO_OPTION) == 0) {
                function.formatRoot();
                refreshTree();
                JOptionPane.showMessageDialog(null,"已格式化完成！","提示",JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    public void delete(String[] strs) {
        if(strs.length < 2){
            JOptionPane.showMessageDialog(null,"您输入的命令有误","提示",JOptionPane.PLAIN_MESSAGE);
        }else {
            function.deleteFile(strs[1]);
            refreshTree();
        }
    }

    public void create(String[] strs){
        JFrame createjf = new JFrame();
        createjf.setLocationRelativeTo(null);
        createjf.setSize(500,500);
        JTextArea jTextArea = new JTextArea();
        jTextArea.setLineWrap(true);//自动换行
        createjf.getContentPane().setLayout(null);
        createjf.add(jTextArea);
        jTextArea.setBounds(0,0,500,500);
        createjf.setVisible(true);
        createjf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (JOptionPane.showConfirmDialog(null,
                        "是否保存该文件", "提示", JOptionPane.YES_NO_OPTION) == 0) {
                    function.createFile(strs[1], strs[2], jTextArea.getText().toString());
                }
            }
        });

    }

    public static String[] input(String str){
        Pattern pattern = Pattern.compile("([a-zA-Z0-9.\\\\/]*) *");
        Matcher m = pattern.matcher(str);
        ArrayList<String> list = new ArrayList<String>();
        while(m.find()){
            list.add(m.group(1));
        }
        String[] strs = list.toArray(new String[list.size()]);
        for (int i = 1; i < strs.length; i++) {
            int j = strs[i].indexOf(".");
            if (j != -1) {
                String[] index = strs[i].split("\\.");
                strs[i] = index[0];
            }
        }
        return strs;
    }


    public void refreshTree(){
        initTree(function.getRoot(),root);
        treeModel.reload();
    }

    public void initTree(FCB fileroot,DefaultMutableTreeNode root){
        root.removeAllChildren();
        for(String key : fileroot.getSubMap().keySet()){
            FCB file = fileroot.getSubMap().get(key);
            if(!file.isAttrIsHide()){
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
                root.add(node);
                initTree(file,node);
            }
        }
    }

    public void firstThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    JButton jButton = null;
                    JbuttonList.get(0).setBackground(Color.black);
                    JbuttonList.get(1).setBackground(Color.black);
                    for (int i = 2; i < 128; i++) {
                        jButton = JbuttonList.get(i);
                        if (function.getFAT()[i] != 0) {
                            jButton.setBackground(Color.black);
                        } else {
                            jButton.setBackground(Color.lightGray);
                        }
                    }
                }
            }
        }).start();
    }
}

