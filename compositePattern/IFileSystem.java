package internal.designPattern.compositePattern;

public interface IFileSystem {

    String getName();

    void ls(int indent);

    int getSize();

    void openAll(int indent);

    IFileSystem cd(String name);

    Boolean isFolder();
}
