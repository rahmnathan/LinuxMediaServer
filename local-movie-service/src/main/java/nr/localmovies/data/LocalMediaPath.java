package nr.localmovies.data;

import java.util.ArrayDeque;
import java.util.Arrays;

public class LocalMediaPath extends ArrayDeque<String> {

    private final LocalMediaPath parentPath;

    public LocalMediaPath(String path){
        parentPath = new LocalMediaPath(path.split("LocalMedia")[1]);
        Arrays.stream(path.split("/")).forEachOrdered(this::add);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        this.forEach(directory -> sb.append(directory).append("/"));

        return sb.toString();
    }

    public boolean isViewingTopLevel(){
        return this.size() == 3;
    }

    public LocalMediaPath getParentPath(){
        return parentPath;
    }
}