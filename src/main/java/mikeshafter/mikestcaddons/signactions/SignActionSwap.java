package mikeshafter.mikestcaddons.signactions;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import static mikeshafter.mikestcaddons.util.Util.swapMember;

public class SignActionSwap extends SignAction {
  
  @Override
  public boolean match(SignActionEvent info) {
    return info.isType("swap", "swapdoor");
  }

  @Override
  public void execute(SignActionEvent info) {
    // When a [train] sign is placed, activate when powered by redstone when the train
    // goes over the sign, or when redstone is activated.
    if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && info.isPowered() && info.hasGroup()) {
      for (MinecartMember<?> member : info.getGroup()) swapMember(member);
      return;
    }
    
    // When a [cart] sign is placed, activate when powered by redstone when each cart
    // goes over the sign, or when redstone is activated.
    if (info.isCartSign() && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && info.isPowered() && info.hasMember()) {
      swapMember(info.getMember());
    }
  }
  
  @Override
  public boolean build(SignChangeActionEvent event) {
    return SignBuildOptions.create()
        .setName("door swapper")
        .setDescription("swaps left and right doors")
        .handle(event.getPlayer());
  }
}
