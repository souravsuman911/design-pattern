package internal.designPattern.compositePattern;

public class File implements IFileSystem {

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
        String indentSpace = " ".repeat(indent);
        System.out.println(indentSpace + name);
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
        return null;
    }

    @Override
    public Boolean isFolder() {
        return false;
    }
}
