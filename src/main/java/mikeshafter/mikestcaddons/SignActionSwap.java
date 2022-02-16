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
  
  @Override
  public boolean match(SignActionEvent info) {
    return info.isType("swap", "swapdoor");
  }
  
  public static void swap(MinecartMember<?> member) {
    ConfigurationNode fullConfig = member.getProperties().getModel().getConfig();
    
    Set<ConfigurationNode> attachments = fullConfig.getNode("attachments").getNodes();
    if (attachments != null) {
      for (ConfigurationNode node : attachments) {
        
        ConfigurationNode attachment = member.getProperties().getModel().getConfig().getNode("attachments");
        attachment.set(node.getPath(), swap(node));
        fullConfig.set("attachments", attachment);
        member.getProperties().getModel().update(fullConfig);
      }
    }
  }
  
  public static ConfigurationNode swap(ConfigurationNode attachmentNode) {
    Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
    Set<ConfigurationNode> attachments = attachmentNode.getNode("attachments").getNodes();
    if (attachments != null) {
      for (ConfigurationNode node : attachments) swap(node);
    }
    
    ConfigurationNode animations = attachmentNode.getNode("animations");
    Set<String> animationNames = animations.getKeys();
    
    if (animationNames.contains("door_R")) {
      animationNames.forEach((name) -> {
        plugin.getLogger().info(name);
        if (name.startsWith("door_R")) {
          // set to new node
          animations.set("door_L"+name.substring(6), animations.getNode(name));
          // remove
          animations.remove(name);
          attachmentNode.set("animations", animations);
        }
      });
    }

    // Swap left doors for right doors
    else if (animationNames.contains("door_L")) {
      animationNames.forEach((name) -> {
        plugin.getLogger().info(name);
        if (name.startsWith("door_L")) {
          // set to new node
          animations.set("door_R"+name.substring(6), animations.getNode(name));
          // remove
          animations.remove(name);
          attachmentNode.set("animations", animations);
        }
      });
    }
    
    return attachmentNode;
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
