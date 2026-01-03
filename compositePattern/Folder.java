package internal.designPattern.compositePattern;

import java.util.ArrayList;
import java.util.List;

public class Folder implements IFileSystem{

    private String name;
    private int size;
    private List<IFileSystem> fileSystems;

    public Folder(String name){
        this.name = name;
        this.size = 0;
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
            System.out.print("");
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

    public void addFile(String fileName, int size){
        File file = new File(fileName, size);
        fileSystems.add(file);
    }
}
