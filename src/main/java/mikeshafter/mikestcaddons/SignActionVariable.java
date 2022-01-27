package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.sl.API.Variable;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.plugin.Plugin;


public class SignActionVariable extends SignAction {
  
  @Override
  public boolean match(SignActionEvent event) {
    return event.isType("variable", "var", "uvar", "updatevar");
  }
  
  @Override
  public void execute(SignActionEvent event) {
    Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
    // When a [train] sign is placed, activate when powered by redstone when the train
    // goes over the sign, or when redstone is activated.
    // When a [cart] sign is placed, activate when powered by redstone when each cart
    // goes over the sign, or when redstone is activated.
    if ((event.isTrainSign() && event.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && event.isPowered() && event.hasGroup()) || (event.isCartSign() && event.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && event.isPowered() && event.hasMember())) {
      
      String v = event.getLine(3);
      String newText = event.getLine(4);
      
      Variable variable = Variables.getIfExists(v);
      variable.set(newText);
      
    }
  }
  
  @Override
  public boolean build(SignChangeActionEvent event) {
    return SignBuildOptions.create()
        .setName("variable updater")
        .setDescription("update variables")
        .handle(event.getPlayer());
  }
}
