import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Function {
    private GUI gui;
    public Map<String, FCB> totalFiles = new HashMap<String, FCB>();
    private int SUM_DISK_SIZE = 128;
    //定义FAT表
    private int[] FAT = new int[SUM_DISK_SIZE];
    private FCB root = new FCB("root", 1);
    private FCB nowCatalog = root;

    //初始化FAT表
    public Function() {
        for (int i = 2; i < FAT.length; i++) {
            FAT[i] = 0;
        }
        FAT[0] = 126;
        FAT[1] = -1;
        root.setFather(root);
        totalFiles.put("root", root);
    }


    //向FAT表申请空间
    public int setFat(int size) {
        int[] startNum = new int[128];
        int i = 2;
        for (int j = 0; j < size; i++) {
            if (FAT[i] == 0) {
                startNum[j] = i;
                if (j > 0) {
                    FAT[startNum[j - 1]] = i;
                }
                j++;
            }
        }
        FAT[i - 1] = -1;
        return startNum[0];
    }

    //更改目录路径
    public void changedir() {
        FCB catalog = nowCatalog;
        String str = "";
        Stack<String> stringStack = new Stack<>();
        while (catalog != root) {
            stringStack.push(catalog.getName() + '/');
            catalog = catalog.getFather();
        }
        str += "root/";
        while (!stringStack.empty()) {
            String peek = stringStack.peek();
            stringStack.pop();
            str += peek;
        }
    }

    public void searchFile(String[] path) {
        FCB Catalog = nowCatalog;
        if (totalFiles.containsKey(path[path.length - 1])) {
            nowCatalog = root;
            if (nowCatalog.getName().equals(path[0])) {
                for (int i = 1; i < path.length; i++) {
                    if (nowCatalog.subMap.containsKey(path[i])) {
                        nowCatalog = nowCatalog.subMap.get(path[i]);
                    } else {
                        gui.showDialogue("找不到该路径下的文件或目录，请检查路径是否正确");
                        nowCatalog = Catalog;
                        break;
                    }
                }
            } else {
                nowCatalog = Catalog;
                gui.showDialogue("请输入正确的路径！");
            }
        } else {
            gui.showDialogue("该文件或目录不存在，请输入正确的绝对路径！");
        }
    }
    //释放FAT表
    public void deletFat(int startNum) {
        int next = FAT[startNum];
        int now = startNum;
        int count = 0;
        while (FAT[now] != 0) {
            next = FAT[now];
            if (next == -1) {
                FAT[now] = 0;
                count++;
                break;
            } else {
                FAT[now] = 0;
                count++;
                now = next;
            }
        }
        FAT[0] += count;
    }

    //创建文件
    public void createFile(String name, String type, String contents) {
        int byteSize = contents.length() / 2;
        int size = byteSize + 8 > 64 ? (byteSize % 64 + 1) : 1;
        if (FAT[0] >= size) {
            FCB fileModel = nowCatalog.subMap.get(name);
            if (fileModel != null) {
                if (fileModel.getAttr() == 3) {
                    int startNum = setFat(size);
                    FCB file = new FCB(name, type, startNum, size);
                    file.setFileContent(contents);
                    file.setFather(nowCatalog);
                    nowCatalog.subMap.put(name, file);
                    totalFiles.put(file.getName(), file);
                    FAT[0] -= size;
                    gui.refreshTree();
                } else if (fileModel.getAttr() == 2) {
                    JOptionPane.showConfirmDialog(null, "存在同名目录，您确定要覆盖吗？", "确认", JOptionPane.YES_NO_OPTION);
                }
            } else if (fileModel == null) {
                int startNum = setFat(size);
                FCB file = new FCB(name, type, startNum, size);
                file.setFather(nowCatalog);
                file.setFileContent(contents);
                nowCatalog.subMap.put(name, file);
                totalFiles.put(file.getName(), file);
                FAT[0] -= size;
                gui.refreshTree();
            }
            JOptionPane.showMessageDialog(null, "保存文件成功!", "提示", JOptionPane.PLAIN_MESSAGE);
        } else {
            gui.showDialogue("创建失败，磁盘空间不足！");
        }
    }
    //编辑文件时的FAT表变化
    public void editAddFat(int startNum, int addSize) {
        int now = startNum;
        int next = FAT[startNum];
        while (FAT[now] != -1) {
            now = next;
            next= FAT[now];
        }
        for (int i = 2, count = 0; count < addSize; i++) {
            if (FAT[i] == 0) {
                FAT[now] = i;
                now = i;
                count++;
                FAT[now] = -1;
            }
        }
    }
    //编辑文件
    public void edittxt(String name, int addSize) {
        if (FAT[0] >= addSize) {
            nowCatalog = nowCatalog.getFather();
            if (nowCatalog.subMap.containsKey(name)) {
                FCB file = nowCatalog.subMap.get(name);
                if (file.getAttr() == 2) {
                    file.setSize(file.getSize() + addSize);
                    editAddFat(file.getStartNum(), addSize);
                    gui.showDialogue("编辑成功");
                    openFile(name);
                } else {
                    gui.showDialogue("不可编辑！");
                }
            }
        } else {
            gui.showDialogue("编辑内容失败，磁盘空间不足！");
        }
    }

    //创建目录
    public void createCatolog(String name) {
        if (FAT[0] >= 1) {
            FCB file = nowCatalog.subMap.get(name);
            if (file != null) {
                if (file.getAttr() == 2) {
                    int startNum = setFat(1);
                    FCB catalog = new FCB(name, startNum);
                    catalog.setFather(nowCatalog);
                    nowCatalog.subMap.put(name, catalog);
                    FAT[0]--;
                    totalFiles.put(catalog.getName(), catalog);
                    gui.showDialogue("创建目录成功！");
                    gui.refreshTree();
                } else if (file.getAttr() == 3) {
                    gui.showDialogue("创建目录失败，该目录已存在！");
                }
            } else if (file == null) {
                int startNum = setFat(1);
                FCB catalog = new FCB(name, startNum);
                catalog.setFather(nowCatalog);
                nowCatalog.subMap.put(name, catalog);
                FAT[0]--;
                totalFiles.put(catalog.getName(), catalog);
                gui.showDialogue("创建目录成功！");
                gui.refreshTree();
            }
        } else {
            gui.showDialogue("创建目录失败，磁盘空间不足！");
        }

    }

    //删除空目录和文件
    public void deleteFile(String name) {
        FCB file = nowCatalog.subMap.get(name);
        if (file == null) {
            gui.showDialogue("删除失败，没有该文件或目录!");
        } else if (!file.subMap.isEmpty()) {
            gui.showDialogue("删除失败，该目录内含有文件！");
        } else {
            nowCatalog.subMap.remove(name);
            deletFat(file.getStartNum());
            if (file.getAttr() == 3 || file.getAttr() == 2) {
                gui.showDialogue(file.getName() + " 已成功删除");
                gui.refreshTree();
            }
        }
    }

    //删除非空目录
    public void deleteNotNullCatalog(String name) {
        FCB file = nowCatalog.subMap.get(name);
        if (file == null) {
            gui.showDialogue("删除失败，没有该文件或文件夹!");
        } else if (!file.subMap.isEmpty()) {
            for (String key : file.getSubMap().keySet()) {
                FCB fileModel = file.getSubMap().get(key);
                deletFat(fileModel.getStartNum());
            }
            nowCatalog.subMap.remove(name);
            deletFat(file.getStartNum());
            gui.showDialogue("删除非空目录成功!");
            gui.refreshTree();
        } else {
            deleteFile(name);
            gui.refreshTree();
            deletFat(file.getStartNum());
        }
    }

    //更改文件属性
    public void change(String name, int i) {
        if (!nowCatalog.getSubMap().containsKey(name)) {
                  gui.showDialogue("该文件不存在!");
        } else {
            FCB file = nowCatalog.getSubMap().get(name);
            String flag = "";
            switch (i) {
                case 1:
                    file.setAttrIsOnlyRead(true);
                    flag = "只读";
                    break;
                case 2:
                    file.setAttrIsOnlyRead(false);
                    flag = "非只读";
                    break;
                case 3:
                    file.setAttrIsHide(true);
                    flag = "隐藏";
                    break;
                case 4:
                    file.setAttrIsHide(false);
                    flag = "非隐藏";
                    break;
            }
            gui.refreshTree();
            gui.showDialogue("修改文件属性成功！修改之后的属性为" + flag);
        }
    }

    //格式化磁盘
    public void formatRoot() {
        totalFiles.clear();
        getFAT()[0] = 126;

        FCB root = new FCB("root", 1);
        totalFiles.put("root", root);
        setRoot(root);
        for (int i = 0; i < FAT.length; i++) {
            if (i >= 2) {
                FAT[i] = 0;
            }
        }
    }
    public FCB openFile(String name) {
        if (nowCatalog.subMap.containsKey(name)) {
            FCB file = nowCatalog.subMap.get(name);
            return file;
        } else {
            return null;
        }
    }
    public int[] getFAT() {
        return FAT;
    }

    public FCB getRoot() {
        return root;
    }

    public void setRoot(FCB root) {
        this.root = root;
    }

    public Map<String, FCB> getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Map<String, FCB> totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getSUM_DISK_SIZE() {
        return SUM_DISK_SIZE;
    }

    public void setSUM_DISK_SIZE(int SUM_DISK_SIZE) {
        this.SUM_DISK_SIZE = SUM_DISK_SIZE;
    }

    public FCB getNowCatalog() {
        return nowCatalog;
    }

    public void setNowCatalog(FCB nowCatalog) {
        this.nowCatalog = nowCatalog;
    }

    public void setFAT(int[] FAT) {
        this.FAT = FAT;
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }



}
