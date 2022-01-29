package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;

import java.util.List;
import java.util.Map;


public class SignActionSwap extends SignAction {
  
  @Override
  public boolean match(SignActionEvent info) {
    return info.isType("swap", "swapdoor");
  }
  
  private void swap(Attachment attachment) {
    // Swap animations for children
    if (!attachment.getChildren().isEmpty()) for (Attachment child : attachment.getChildren()) swap(child);
  
    // Swap for current attachment
    ConfigurationNode node = attachment.getConfig().getNode("animations");
    List<String> animationNames = attachment.getAnimationNamesRecursive();
    Map<String, Animation> animationMap = attachment.getInternalState().animations;
  
    // Swap right doors for left doors
    if (animationNames.contains("door_R")) {
      for (String name : animationNames) {
        if (name.contains("door_R")) {
          // get full name
          Animation animation = animationMap.get(name);
          // change "door_R" to "door_L"
          animationMap.put("door_L"+name.substring(6), animation);
          // remove original name
          animationMap.remove(name);
        }
      }
    }
  
    // Swap left doors for right doors
    else if (animationNames.contains("door_L")) {
      for (String name : animationNames) {
        if (name.contains("door_L")) {
          // get full name
          Animation animation = animationMap.get(name);
          // change "door_L" to "door_R"
          animationMap.put("door_R"+name.substring(6), animation);
          // remove original name
          animationMap.remove(name);
        }
      }
    }
  
    attachment.onLoad(node);
//    for (Iterator<Animation> iterator = animations.iterator(); iterator.hasNext(); ) {
//      Animation animation = iterator.next();
//
//      if (animation.getOptions().getName().equals("door_R")) {
//        AnimationOptions options = animation.getOptions();
//        options.setName("door_L");
//        animation.setOptions(options);
//        attachment.clearAnimations();
//        attachment.addAnimation(animation);
//      } else if (animation.getOptions().getName().equals("door_L")) {
//        AnimationOptions options = animation.getOptions();
//        options.setName("door_R");
//        animation.setOptions(options);
//        attachment.clearAnimations();
//        attachment.addAnimation(animation);
//      }
//    }
  
  
  }
  
  @Override
  public void execute(SignActionEvent info) {
    // When a [train] sign is placed, activate when powered by redstone when the train
    // goes over the sign, or when redstone is activated.
    if (info.isTrainSign()
            && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)
            && info.isPowered() && info.hasGroup()) {
      for (MinecartMember<?> member : info.getGroup()) {
        swap(member.getAttachments().getRootAttachment());
      }
      return;
    }
  
    // When a [cart] sign is placed, activate when powered by redstone when each cart
    // goes over the sign, or when redstone is activated.
    if (info.isCartSign()
            && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)
            && info.isPowered() && info.hasMember()) {
      swap(info.getMember().getAttachments().getRootAttachment());
    }
  }
  
  @Override
  public boolean build(SignChangeActionEvent event) {
    return SignBuildOptions.create()
        .setName("door swapper")
        .setDescription("swap left and right door animation names")
        .handle(event.getPlayer());
  }
}
