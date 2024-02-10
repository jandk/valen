package be.twofold.valen.ui;

import be.twofold.valen.resource.*;

import javax.swing.tree.*;
import java.util.*;

public interface MainView {

    void show();

    void setFileTree(TreeNode root);

    void setResources(List<Resource> resources);

    void addListener(MainViewListener listener);

}
