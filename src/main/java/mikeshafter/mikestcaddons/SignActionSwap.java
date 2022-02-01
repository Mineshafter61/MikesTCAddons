package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.plugin.Plugin;

import java.util.Set;


public class SignActionSwap extends SignAction {
  
  Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
  
  @Override
  public boolean match(SignActionEvent info) {
    return info.isType("swap", "swapdoor");
  }
  
  private void swap(MinecartMember<?> member) {
    Set<ConfigurationNode> attachments = member.getProperties().getModel().getConfig().getNode("attachments").getNodes();
    if (attachments != null) {
      for (ConfigurationNode node : attachments) swap(node);
    }
  }
  
  private void swap(ConfigurationNode attachmentNode) {
    Set<ConfigurationNode> attachments = attachmentNode.getNode("attachments").getNodes();
    if (attachments != null) {
      for (ConfigurationNode node : attachments) swap(node);
    }
    
    Set<String> animationNames = attachmentNode.getNode("animations").getKeys();
    for (String s : animationNames) plugin.getLogger().info(s);
    
    if (animationNames.contains("door_R")) {
      for (String name : animationNames) {
        if (name.contains("door_R")) {
          // get full name
          ConfigurationNode animation = attachmentNode.getNode("animations").getNode(name).clone();
          for (String s : animation.getKeys()) plugin.getLogger().info(s);
          // create new node with door_R
          ConfigurationNode newAnimation = attachmentNode.getNode("animations").getNode("door_L"+name.substring(6));
          // set to new node
          animation.setTo(newAnimation);
        }
      }
    }
  
    // Swap left doors for right doors
    else if (animationNames.contains("door_L")) {
      for (String name : animationNames) {
        if (name.contains("door_L")) {
          // get full name
          ConfigurationNode animation = attachmentNode.getNode("animations").getNode(name).clone();
          // create new node with door_R
          ConfigurationNode newAnimation = attachmentNode.getNode("animations").getNode("door_R"+name.substring(6));
          // set to new node
          animation.setTo(newAnimation);
        }
      }
    }
  }
  
  @Override
  public void execute(SignActionEvent info) {
    // When a [train] sign is placed, activate when powered by redstone when the train
    // goes over the sign, or when redstone is activated.
    if (info.isTrainSign()
            && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)
            && info.isPowered() && info.hasGroup()) {
      for (MinecartMember<?> member : info.getGroup()) {
        swap(member);
      }
      return;
    }
  
    // When a [cart] sign is placed, activate when powered by redstone when each cart
    // goes over the sign, or when redstone is activated.
    if (info.isCartSign()
            && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)
            && info.isPowered() && info.hasMember()) {
      swap(info.getMember());
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
