import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FCB  {
    private String name;
    private String type;
    private  int  attr;
    private  int startNum;
    private int size;
    private boolean attrIsOnlyRead = false;
    private boolean attrIsHide = false;
    public Map<String, FCB> subMap = new HashMap<String, FCB>();
    private FCB father = null;
    private String FileContent;

    public Map<String, FCB> getSubMap() {
        return subMap;
    }

    public void setSubMap(Map<String, FCB> subMap) {
        this.subMap = subMap;
    }

    public String getFileContent() {
        return FileContent;
    }

    public void setFileContent(String fileContent) {
        FileContent = fileContent;
    }
   //文件的构造方法
    public FCB(String name, String type, int startNum, int size) {
        this.name = name;
        this.type = type;
        this.attr = 2;
        this.startNum = startNum;
        this.size = size;
    }
    //目录的构造方法
    public FCB(String name, int startNum) {
        this.name = name;
        this.attr = 3;
        this.startNum = startNum;
        this.type = " ";
        this.size = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAttr() {
        return attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isAttrIsOnlyRead() {
        return attrIsOnlyRead;
    }

    public void setAttrIsOnlyRead(boolean attrIsOnlyRead) {
        this.attrIsOnlyRead = attrIsOnlyRead;
    }

    public boolean isAttrIsHide() {
        return attrIsHide;
    }

    public void setAttrIsHide(boolean attrIsHide) {
        this.attrIsHide = attrIsHide;
    }

    public FCB getFather() {
        return father;
    }

    public void setFather(FCB father) {
        this.father = father;
    }
}
