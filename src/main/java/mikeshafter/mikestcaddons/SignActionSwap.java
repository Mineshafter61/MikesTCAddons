package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;

import java.util.Collection;


public class SignActionSwap extends SignAction {
  
  @Override
  public boolean match(SignActionEvent info) {
    return info.isType("swap", "swapdoor");
  }
  
  private void swap(Attachment attachment) {
    // Swap for current attachment
    Collection<Animation> animations = attachment.getAnimations();
    for (Animation animation : animations) {
      if (animation.getOptions().getName().equals("door_R")) {
        AnimationOptions options = animation.getOptions();
        options.setName("door_L");
        animation.setOptions(options);
        attachment.clearAnimations();
        attachment.addAnimation(animation);
      } else if (animation.getOptions().getName().equals("door_L")) {
        AnimationOptions options = animation.getOptions();
        options.setName("door_R");
        animation.setOptions(options);
        attachment.clearAnimations();
        attachment.addAnimation(animation);
      }
    }
    
    // Swap animations for children as well
    if (!attachment.getChildren().isEmpty()) for (Attachment child : attachment.getChildren()) swap(child);
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
