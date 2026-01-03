package internal.designPattern.compositePattern;

public class MainClient {
    public static void main(String[] args) {
        Folder folder1 = new Folder("root");
        Folder subFolder1 = folder1.addFolder("sub-folder-1");
        Folder subFolder2 = folder1.addFolder("sub-folder-2");
        subFolder2.addFile("File-1", 20);
        Folder subSubFolder1 = subFolder2.addFolder("sub-sub-Folder-1");
        folder1.addFile("File-2", 50);

        folder1.openAll(0);
    }    
}
