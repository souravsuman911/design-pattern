package internal.designPattern.compositePattern;

import java.util.ArrayList;
import java.util.List;

interface IFileSystem {

    String getName();

    int getSize();

    default void ls(int indent){
    }

    void openAll(int indent);

    IFileSystem cd(String name);

    Boolean isFolder();
}

class File implements IFileSystem {

    private String name;
    private int size;

    public File(String name, int size){
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void ls(int indent) {
        throw new RuntimeException("ls command is not supported for files");
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void openAll(int indent) {
        String indentSpace = " ".repeat(indent);
        System.out.println(indentSpace + name);
    }

    @Override
    public IFileSystem cd(String name) {
        throw new RuntimeException("cd command is not supported for files");
    }

    @Override
    public Boolean isFolder() {
        return false;
    }
}

class Folder implements IFileSystem{

    private String name;
    private List<IFileSystem> fileSystems;

    public Folder(String name){
        this.name = name;
        fileSystems = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void ls(int indent) {
        for(IFileSystem fileSystem : fileSystems){
            String indentSpace = " ".repeat(indent);
            System.out.print(fileSystem.getName() + " ");
        }
    }

    @Override
    public int getSize() {
        int size = 0;
        for(IFileSystem fileSystem : fileSystems){
            if(fileSystem.isFolder()){
                for(IFileSystem internalFileSystem : fileSystems){
                    size += internalFileSystem.getSize();
                }
            }
            else{
                size += fileSystem.getSize();
            }
        }

        return size;
    }

    @Override
    public void openAll(int indent) {
        String indentSpace = " ".repeat(indent);
        System.out.println(indentSpace + getName());
        for(IFileSystem fileSystem : fileSystems){
            fileSystem.openAll(indent + 4);
        }
    }

    @Override
    public IFileSystem cd(String name) {
        for(IFileSystem fileSystem : fileSystems){
            if(fileSystem.isFolder() && fileSystem.getName().equals(name)){
                return fileSystem;
            }
        }
        
        
        return null;
    }

    @Override
    public Boolean isFolder() {
        return true;
    }

    public Folder addFolder(String folderName){
        Folder folder = new Folder(folderName);
        fileSystems.add(folder);
        return folder;
    }

    public Folder addFolder(Folder folder){
        fileSystems.add(folder);
        return folder;
    }

    public File addFile(String fileName, int size){
        File file = new File(fileName, size);
        fileSystems.add(file);
        return file;
    }

    public File addFile(File file){
        fileSystems.add(file);
        return file;
    }


}

public class MainClient {
    public static void main(String[] args) {
        Folder root = new Folder("root");
        Folder subFolder1 = root.addFolder("sub-folder-1");
        Folder subFolder2 = root.addFolder("sub-folder-2");
        subFolder2.addFile("File-1", 20);
        Folder subSubFolder1 = subFolder2.addFolder("sub-sub-Folder-1");
        root.addFile("File-2", 50);

        root.openAll(0);

        File file = new File("File-3", 10);
        file.ls(4);
    }    
}
