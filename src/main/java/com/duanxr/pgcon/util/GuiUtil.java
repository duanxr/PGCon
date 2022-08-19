package com.duanxr.pgcon.util;

import com.dooapp.fxform.FXForm;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import lombok.experimental.UtilityClass;

/**
 * @author 段然 2022/8/19
 */
@UtilityClass
public class GuiUtil {

  public static void setNodeWithImageSize(FXForm<?> nodeWithImage) {
    double width = 280;
    ArrayList<Node> nodes = getAllNodes(nodeWithImage, null);
    for (Node node : nodes) {
      if (node instanceof ImageView imageView) {
        imageView.setFitWidth(240);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
      }
    }
    nodeWithImage.setMinWidth(width);
    nodeWithImage.setPrefWidth(width);
    nodeWithImage.setMaxWidth(width);
  }

  public static void setNodeSize(FXForm<?> node) {
    ArrayList<Node> nodes = getAllNodes(node, null);
    double width = 180;
    node.setMinWidth(width);
    node.setPrefWidth(width);
    node.setMaxWidth(width);
  }

  public static ArrayList<Node> getAllNodes(Parent parent, ArrayList<Node> nodes) {
    if (nodes == null) {
      nodes = new ArrayList<>();
    }
    for (Node node : parent.getChildrenUnmodifiable()) {
      nodes.add(node);
      if (node instanceof Parent) {
        getAllNodes((Parent) node, nodes);
      }
    }
    return nodes;
  }

}
